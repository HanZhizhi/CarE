package com.space.care.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.Text;
import com.space.care.R;
import com.space.care.adapter.ProvidersAdapter;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.objects.ProvidersItem;
import com.space.care.utils.Utils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonArrayRequest;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.amap.api.maps2d.LocationSource;

public class ProvidersActivity extends AppCompatActivity implements HttpListener {
    private Toolbar toolbar;
    private ListView lvProviders;
    private SwipeRefreshLayout swiper;
    private ProvidersAdapter adapter;
    private ArrayList<ProvidersItem> pvdList;
    private TextView tvSortComplex,tvSortRate,tvSortDIstance;
    private TextView[] sorters;

    private String serviceName="搜索结果";
    private int serviceId=-1;
    private Comparator<ProvidersItem> normalComparator,rateComparator,disComparator;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //if (mLocationListener != null && aMapLocation != null) {
            Log.i("providerloc","okfinidh"+aMapLocation.toString());
                if (aMapLocation != null
                        && aMapLocation.getErrorCode() == 0) {
                    userLat = aMapLocation.getLatitude();
                    userLon = aMapLocation.getLongitude();
                    //mLocationListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                    //Toast.makeText(getApplicationContext(),"定位成功"+userLat+"**"+userLon,Toast.LENGTH_SHORT).show();
                    Log.i("AmapLoc","**"+userLat+"**"+userLon);
                    getProviders();
                } else {
                    String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr",errText);
                    Toast.makeText(getApplicationContext(),"定位失败"+errText,Toast.LENGTH_SHORT).show();
                }
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            //}
        }
    };

    public AMapLocationClientOption mLocationOption = null;


    private double userLat,userLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_providers);

        Intent intent=getIntent();
        if (intent!=null)
        {
            Bundle bundle=intent.getExtras();
            serviceId=bundle.getInt("service_id");
            serviceName=bundle.getString("service_name");
        }
        //Toast.makeText(this,serviceId+"**"+serviceName,Toast.LENGTH_SHORT).show();

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setHttpTimeOut(8000);

        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();

        initViews();

    }

    private void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(serviceName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvProviders= (ListView) findViewById(R.id.services_providers_listview);
        TextView footer=new TextView(getApplicationContext());
        footer.setGravity(Gravity.CENTER);
        footer.setText("暂无更多~~");
        lvProviders.addFooterView(footer);
        swiper= (SwipeRefreshLayout) findViewById(R.id.services_pullrefresher);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiper.setRefreshing(false);
                    }
                },2500);
            }
        });

        tvSortComplex= (TextView) findViewById(R.id.providers_sort_complex);
        tvSortRate= (TextView) findViewById(R.id.providers_sort_rating);
        tvSortDIstance= (TextView) findViewById(R.id.providers_sort_distance);
        sorters= new TextView[]{tvSortComplex, tvSortRate, tvSortDIstance};
        setSorting(0);

        TextView.OnClickListener sorterListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.providers_sort_complex:
                        setSorting(0);
                        Collections.sort(pvdList,normalComparator);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.providers_sort_rating:
                        setSorting(1);
                        Collections.sort(pvdList,rateComparator);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.providers_sort_distance:
                        setSorting(2);
                        Collections.sort(pvdList,disComparator);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        tvSortComplex.setOnClickListener(sorterListener);
        tvSortRate.setOnClickListener(sorterListener);
        tvSortDIstance.setOnClickListener(sorterListener);

        rateComparator=new Comparator<ProvidersItem>() {
            @Override
            public int compare(ProvidersItem o1, ProvidersItem o2) {
                return (int) (o2.pvdRate-o1.pvdRate);
            }
        };

        disComparator=new Comparator<ProvidersItem>() {
            @Override
            public int compare(ProvidersItem o1, ProvidersItem o2) {
                return o1.pvdDistance - o2.pvdDistance;
            }
        };

        normalComparator=new Comparator<ProvidersItem>() {
            @Override
            public int compare(ProvidersItem o1, ProvidersItem o2) {
                if (o1.pvdDiseMark != o2.pvdDiseMark)
                {
                    return o2.pvdDiseMark-o1.pvdDiseMark;
                }
                else
                {
                    if (o1.pvdRate!=o2.pvdRate)
                    {
                        return (int) (o2.pvdRate-o1.pvdRate);
                    }
                    else return o1.pvdDistance-o2.pvdDistance;
                }
            }
        };
    }

    public void setSorting(int k)
    {
        for(int i=0;i<sorters.length;++i)
        {
            if(i==k) sorters[i].setTextColor(Color.RED);
            else sorters[i].setTextColor(Color.BLACK);
        }
    }

    private void getProviders()
    {
        JsonArrayRequest providersRequest= (JsonArrayRequest) NoHttp.createJsonArrayRequest(NetConsts.getUrl()+"getProviders.php", RequestMethod.POST);
        providersRequest.add("service_item_id",serviceId);
        CallServer caller=CallServer.getRequestInstance();
        caller.add(ProvidersActivity.this,9,providersRequest,this,true,true);
    }

    private void setListData(JSONArray pvdArray)
    {
        pvdList=new ArrayList<>();
        try {
            for (int i=0;i<pvdArray.length();++i)
            {
                JSONObject tempObg=pvdArray.getJSONObject(i);
                String pName,pIntro,pLoc,pPrice,pUserNum;
                int pId,pDis,pFuncId;
                double pRate,pLat,pLon;
                pId=tempObg.getInt("service_provider_id");
                double ptRate=tempObg.getDouble("provider_rate");
                BigDecimal bg1 = new BigDecimal(ptRate);
                pRate = bg1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                pName=tempObg.getString("sp_name");
                pIntro=tempObg.getString("sp_introduction");
                pLoc=tempObg.getString("sp_loc_desc");
                pPrice=tempObg.getString("function_price");
                pUserNum=tempObg.getString("function_used_users");
                pLat=tempObg.getDouble("sp_loc_lat");
                pLon=tempObg.getDouble("sp_loc_lon");
                pDis= (int) calDistance(pLat,pLon,userLat,userLon);
                pFuncId=tempObg.getInt("function_id");
                Log.i("AmapLoc","location  used");
                ProvidersItem pItem=new ProvidersItem(pId,pRate,pName,pIntro,pLoc,pDis,pPrice,pUserNum,pFuncId);
                pvdList.add(pItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(pvdList,normalComparator);
        adapter=new ProvidersAdapter(pvdList,this);
        lvProviders.setAdapter(adapter);
    }

    private double EARTH_RADIUS = 6378.137;

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public double calDistance(double lat1, double lng1, double lat2,double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONArray pvdJsonArray= (JSONArray) response.get();
        Log.i("providers",pvdJsonArray.toString());
        if (pvdJsonArray.length()>0) setListData(pvdJsonArray);
        else Utils.showToast(this,"暂无提供该服务的商家！");
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("providers",exception.toString());
    }
}
