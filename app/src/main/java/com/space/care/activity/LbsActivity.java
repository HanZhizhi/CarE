package com.space.care.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.space.care.R;
import com.space.care.WelActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LbsActivity extends AppCompatActivity implements LocationSource, AMapLocationListener,AMap.InfoWindowAdapter,AMap.OnMapClickListener,HttpListener{
    class ObjProvider
    {
        public String pvdName,pvdIntroduction;
        public int pvdDistance;
        public double pvdLat,pvdLon;

        public ObjProvider(String name,String intro,int dis,double lat,double lon)
        {
            pvdName=name;
            pvdIntroduction=intro;
            pvdDistance=dis;
            pvdLat=lat;
            pvdLon=lon;
        }
    }

    private Toolbar toolbar;
    private AMap aMap;
    private MapView mapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private double lat,lon;
    private ArrayList<ObjProvider> pvds;
    private Comparator<ObjProvider> disComparator;
    private Marker marker1,currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lbs);

        initViews();

        mapView = (MapView) findViewById(R.id.lbs_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initLbs();
    }

    private void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("附近服务");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        disComparator=new Comparator<ObjProvider>() {
            @Override
            public int compare(ObjProvider o1, ObjProvider o2) {
                return o1.pvdDistance-o2.pvdDistance;
            }
        };
    }

    private void initLbs()
    {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.lbs_loc_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapClickListener(this);

        /*MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(34.2781057709,108.8606866176);
        markerOption.position(latLng);
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.mipmap.lbs_indicater)));
// 将Marker设置为贴地显示，可以双指下拉地图查看效果
        //markerOption.setFlat(true);//设置marker平贴地图效果
        marker1=aMap.addMarker(markerOption);
        JSONObject markerObject=new JSONObject();
        try {
            markerObject.put("label","markerlabel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        marker1.setObject(markerObject);*/


    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                Log.i("location",amapLocation.toString());
                lat = amapLocation.getLatitude();
                lon = amapLocation.getLongitude();
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                //Toast.makeText(this,"定位成功"+lat+"**"+lon+amapLocation.getDistrict(),Toast.LENGTH_SHORT).show();
                askProviders(amapLocation.getDistrict());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                finish();
            }
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow= LayoutInflater.from(this).inflate(R.layout.lbs_info_window,null);
        TextView tv1= (TextView) infoWindow.findViewById(R.id.tv1);
        TextView tv2= (TextView) infoWindow.findViewById(R.id.tv2);
        /*try {
            tv1.setText(marker.getId()+new JSONObject(marker.getObject().toString()).get("label"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        tv1.setText(marker.getSnippet());
        tv2.setText(marker.getTitle());
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("markercur",currentMarker.toString());
        if (currentMarker != null) {
            currentMarker.hideInfoWindow();
            Log.i("markercur","hidden");
        }
        else Log.i("markercur","null");
    }

    private void askProviders(String district)
    {
        JsonArrayRequest providersRequest= (JsonArrayRequest) NoHttp.createJsonArrayRequest(NetConsts.getUrl()+"getLBSproviders.php", RequestMethod.POST);
        providersRequest.add("district",district);
        CallServer caller=CallServer.getRequestInstance();
        caller.add(LbsActivity.this,9,providersRequest,this,true,true);
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what)
        {
            case 9:
                JSONArray providers= (JSONArray) response.get();
                Log.i("hello","hi"+providers.length()+providers.toString());
                //便利算距离选出前几个
                if (providers.length()>0)
                {
                    pvds=new ArrayList<>();
                    try {
                        for (int i=0;i<providers.length();++i)
                        {
                            Log.i("hello","ind");
                            JSONObject tempObg=providers.getJSONObject(i);
                            String pName,pIntro;
                            int pDis;
                            double pLat,pLon;
                            pName=tempObg.getString("sp_name");
                            pIntro=tempObg.getString("sp_introduction");
                            pLat=tempObg.getDouble("sp_loc_lat");
                            pLon=tempObg.getDouble("sp_loc_lon");
                            pDis= (int) calDistance(pLat,pLon,lat,lon);
                            Log.i("AmapLoc","location  used");
                            ObjProvider tempPvd=new ObjProvider(pName,pIntro,pDis,pLat,pLon);
                            pvds.add(tempPvd);
                            Log.i("hello","annded");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("hello",""+pvds.size());
                    Collections.sort(pvds,disComparator);
                    markProviders(pvds);
                }
                else Utils.showToast(this,"附近无提供商！！！");
                break;
        }
    }

    private void markProviders(ArrayList<ObjProvider> list)
    {
        for (int i=0;i<list.size();i++)
        {
            ObjProvider prov=list.get(i);
            double later,loner;
            String title,content;
            later=prov.pvdLat;loner=prov.pvdLon;
            title=prov.pvdName;content=prov.pvdIntroduction;
            Log.i("content",content);
            aMap.addMarker(new MarkerOptions().position(new LatLng(later,loner)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.mipmap.lbs_indicater))).title(title)).setSnippet(content);//setObject(content);
        }


        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker=marker;
                Log.i("markerclick",marker.getId()+marker.getSnippet());
                //Toast.makeText(getApplicationContext(),"点击",Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("lbs",marker.getId()+marker.getPosition()+marker.getObject());
                marker.setObject("");
                Intent intent=new Intent(LbsActivity.this,WelActivity.class);
                intent.putExtras(bundle);
                //startActivity(intent);
                return false;
                //返回:true 表示点击marker 后marker 不会移动到地图中心；返回false 表示点击marker 后marker 会自动移动到地图中心
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("error",exception.toString());
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
}
