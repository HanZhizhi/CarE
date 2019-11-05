package com.space.care.mainFrag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.space.care.R;
import com.space.care.activity.ProvidersActivity;
import com.space.care.activity.ServicesActivity;
import com.space.care.adapter.CarEBannerAdapter;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonArrayRequest;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by SPACE on 2017/5/8.
 */

public class ServeFrag extends Fragment implements ViewPager.OnPageChangeListener{
    private ListView listServices;
    private ViewPager banner;
    private TextView bannerText;
    private LinearLayout bannerDotGroup;
    private RelativeLayout rlRepair,rlMaintain,rlWashing,rlBehalf,rlDecorate,rlBeauty,rlInssurance,rlServices;
    private ArrayList<String> bannerUrlList=new ArrayList<String>();
    private int previousPosition=0;
    private boolean isSwitching=false;  //用户切换
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            banner.setCurrentItem(banner.getCurrentItem()+1);
        }
    };

    private RequestQueue mainQueen;
    private JsonArrayRequest bannerRequest;
    private OnResponseListener<JSONArray> requestListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_serve,container,false);

        listServices= (ListView) view.findViewById(R.id.frag_serve_servicesList);


        String[] strs={"基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统","基于LBS技术的网上修车预约与管理系统"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,strs);

        listServices.setAdapter(adapter);
        View header=inflater.inflate(R.layout.main_header,null);
        listServices.addHeaderView(header);
        initHeader(header);

        return view;
    }

    private void initHeader(View view)
    {
        TextView tvHead= (TextView) view.findViewById(R.id.tv);
        banner= (ViewPager) view.findViewById(R.id.main_title_banner);
        bannerText= (TextView) view.findViewById(R.id.main_banner_title);
        bannerDotGroup= (LinearLayout) view.findViewById(R.id.main_banner_dotgroup);
        rlRepair= (RelativeLayout) view.findViewById(R.id.main_title_service_repair);
        rlMaintain= (RelativeLayout) view.findViewById(R.id.main_title_service_maintain);
        rlWashing= (RelativeLayout) view.findViewById(R.id.main_title_service_washing);
        rlBehalf= (RelativeLayout) view.findViewById(R.id.main_title_service_behalf);
        rlDecorate= (RelativeLayout) view.findViewById(R.id.main_title_service_decoration);
        rlBeauty= (RelativeLayout) view.findViewById(R.id.main_title_service_beauty);
        rlInssurance= (RelativeLayout) view.findViewById(R.id.main_title_service_insurance);
        rlServices= (RelativeLayout) view.findViewById(R.id.main_title_service_services);

        tvHead.setText("哈哈哈");

        NoHttp.initialize(getActivity().getApplication());
        mainQueen=NoHttp.newRequestQueue();
        //getBannerData();

        RelativeLayout.OnClickListener funcsListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.main_title_service_repair:
                        Bundle bundle1=new Bundle();
                        bundle1.putInt("service_id",1);
                        bundle1.putString("service_name","检修");
                        Intent pvdIntent1=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent1.putExtras(bundle1);
                        startActivity(pvdIntent1);
                        break;
                    case R.id.main_title_service_maintain:
                        Bundle bundle2=new Bundle();
                        bundle2.putInt("service_id",13);
                        bundle2.putString("service_name","保养");
                        Intent pvdIntent2=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent2.putExtras(bundle2);
                        startActivity(pvdIntent2);
                        break;
                    case R.id.main_title_service_washing:
                        Bundle bundle3=new Bundle();
                        bundle3.putInt("service_id",30);
                        bundle3.putString("service_name","洗车");
                        Intent pvdIntent3=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent3.putExtras(bundle3);
                        startActivity(pvdIntent3);
                        break;
                    case R.id.main_title_service_behalf:
                        Bundle bundle4=new Bundle();
                        bundle4.putInt("service_id",29);
                        bundle4.putString("service_name","代驾");
                        Intent pvdIntent4=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent4.putExtras(bundle4);
                        startActivity(pvdIntent4);
                        break;
                    case R.id.main_title_service_decoration:
                        Bundle bundle5=new Bundle();
                        bundle5.putInt("service_id",43);
                        bundle5.putString("service_name","车饰");
                        Intent pvdIntent5=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent5.putExtras(bundle5);
                        startActivity(pvdIntent5);
                        break;
                    case R.id.main_title_service_beauty:
                        Bundle bundle6=new Bundle();
                        bundle6.putInt("service_id",19);
                        bundle6.putString("service_name","美容");
                        Intent pvdIntent6=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent6.putExtras(bundle6);
                        startActivity(pvdIntent6);
                        break;
                    case R.id.main_title_service_insurance:
                        Bundle bundle7=new Bundle();
                        bundle7.putInt("service_id",34);
                        bundle7.putString("service_name","车险");
                        Intent pvdIntent7=new Intent(getActivity(),ProvidersActivity.class);
                        pvdIntent7.putExtras(bundle7);
                        startActivity(pvdIntent7);
                        break;
                    case R.id.main_title_service_services:
                        getActivity().startActivity(new Intent(getActivity(), ServicesActivity.class));
                        break;
                }
            }
        };
        rlRepair.setOnClickListener(funcsListener);
        rlMaintain.setOnClickListener(funcsListener);
        rlWashing.setOnClickListener(funcsListener);
        rlBehalf.setOnClickListener(funcsListener);
        rlDecorate.setOnClickListener(funcsListener);
        rlBeauty.setOnClickListener(funcsListener);
        rlInssurance.setOnClickListener(funcsListener);
        rlServices.setOnClickListener(funcsListener);
    }

    private void initViewPager()
    {
        View dotView;

        for(int i=0;i<bannerUrlList.size();++i)
        {
            dotView=new View(getActivity());
            dotView.setBackgroundResource(R.drawable.banner_dot_selector);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(15,15);
            if(i!=0) params.leftMargin=15;
            dotView.setLayoutParams(params);
            dotView.setEnabled(false);
            bannerDotGroup.addView(dotView);
        }

        banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        isSwitching=true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isSwitching=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isSwitching=false;
                        break;
                }
                return false;
            }
        });

        banner.setAdapter(new CarEBannerAdapter(getActivity(),bannerUrlList));
        int curItem=Integer.MAX_VALUE/2-(Integer.MAX_VALUE / 2 % bannerUrlList.size());
        banner.setCurrentItem(curItem);

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("vpclicked",bannerUrlList.get(banner.getCurrentItem()));
            }
        });

        bannerDotGroup.getChildAt(previousPosition).setEnabled(true);
        bannerText.setText(bannerUrlList.get(previousPosition));
        banner.setOnPageChangeListener(this);

        new Thread()
        {
            @Override
            public void run() {
                while(true)
                {
                    SystemClock.sleep(4000);
                    if(!isSwitching)
                    {
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }.start();
    }

    private void getBannerData()
    {
        requestListener=new OnResponseListener<JSONArray>() {
            @Override
            public void onStart(int what) {
                Log.i("data","start");
            }

            @Override
            public void onSucceed(int what, Response<JSONArray> response) {
                try {
                    Log.i("data",response.get().getJSONObject(0).getString("pic"));
                    JSONArray bannerDataArray=response.get();
                    for(int i=0;i<bannerDataArray.length();i++)
                    {
                        String curUrl=bannerDataArray.getJSONObject(i).getString("pic");
                        bannerUrlList.add(curUrl);
                        Log.i("data",curUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initViewPager();
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                Log.i("data",exception.toString());
            }

            @Override
            public void onFinish(int what) {
                Log.i("data","finish");
            }
        };
        bannerRequest=(JsonArrayRequest)NoHttp.createJsonArrayRequest("http://192.168.199.183/CarEGradProj/Index/pics.php", RequestMethod.POST);
        mainQueen.add(9,bannerRequest,requestListener);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int newposition=position%bannerUrlList.size();
        bannerDotGroup.getChildAt(newposition).setEnabled(true);
        bannerDotGroup.getChildAt(previousPosition).setEnabled(false);
        bannerText.setText(bannerUrlList.get(newposition));
        previousPosition=newposition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
