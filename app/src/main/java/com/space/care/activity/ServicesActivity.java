package com.space.care.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.space.care.R;
import com.space.care.adapter.ServiceLeftAdapter;
import com.space.care.adapter.ServiceRightAdapter;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.objects.ServiceItem;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity implements HttpListener{
    private int selectIndex=0;
    private ListView lvLeft,lvRight;
    private ServiceLeftAdapter leftAdapter;
    private ServiceRightAdapter rightAdapter;
    private Toolbar toolbar;

    private static String[] mMenus;/* = { "常用分类", "服饰内衣", "鞋靴", "手机",
            //"家用电器", "数码", "电脑办公", "个护化妆", "图书" ,"二手手机", "数码", "电脑办公", "个护化妆", "图书" ,"二手手机"};
    private String[] strs1={"常用分类1","常用分类2","常用分类3","常用分类4","常用分类5","常用分类6","常用分类7","常用分类8","常用分类9","常用分类10"};
    private String[] strs2={"服饰内衣1","服饰内衣2","服饰内衣3","服饰内衣4","服饰内衣5","服饰内衣6","服饰内衣7","服饰内衣8","服饰内衣9","服饰内衣10","服饰内衣11","服饰内衣12","服饰内衣13","服饰内衣14","服饰内衣15","服饰内衣16"};
    private String[] strs3={"鞋靴1","鞋靴2","鞋靴3","鞋靴4","鞋靴5","鞋靴6"};
    private String[] strs4={"手机1","手机2","手机3","手机4"};
    private String[] strs5={"家用电器1","家用电器2","家用电器3","家用电器4","家用电器5","家用电器6","家用电器7","家用电器8"};
    private String[][] allData={strs1,strs2,strs3,strs4,strs5,strs1,strs2,strs3,strs4,strs5,strs1,strs2,strs3,strs4,strs5};*/

    private ArrayList<ServiceItem>[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_services);

        lvLeft=(ListView) findViewById(R.id.list_item_1);
        lvRight=(ListView) findViewById(R.id.list_item_2);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("全部服务");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String url = NetConsts.getUrl()+"allTypesAndServices.php";
        //创建NoHttp的请求对象,设置加载的网络路径和请求
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.GET);
        //获取核心的NoHttp网络工具类对象
        CallServer callServerInstance = CallServer.getRequestInstance();
        //把队列添加进去
        callServerInstance.add(ServicesActivity.this,0,request,this,true,true);
    }

    private void initViews()
    {
        leftAdapter=new ServiceLeftAdapter(mMenus,this,selectIndex);
        rightAdapter=new ServiceRightAdapter(items,this,selectIndex);
        lvLeft.setAdapter(leftAdapter);
        lvRight.setAdapter(rightAdapter);

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectIndex=position;
                //把下标传过去，然后刷新adapter
                leftAdapter.setIndex(position);
                leftAdapter.notifyDataSetChanged();
                //当点击某个item的时候让这个item自动滑动到listview的顶部(下面item够多，如果点击的是最后一个就不能到达顶部了)
                lvLeft.smoothScrollToPositionFromTop(position,0);
                rightAdapter.setIndex(position);
                lvRight.setAdapter(rightAdapter);
            }
        });

        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ServicesActivity.this,items[selectIndex].get(position).itemName,Toast.LENGTH_LONG).show();
                Bundle bundle1=new Bundle();
                bundle1.putInt("service_id",items[selectIndex].get(position).itemId);
                bundle1.putString("service_name",items[selectIndex].get(position).itemName);
                Intent pvdIntent=new Intent(ServicesActivity.this,ProvidersActivity.class);
                pvdIntent.putExtras(bundle1);
                startActivity(pvdIntent);
            }
        });
    }

    private void initData(JSONObject object){
        //Toast.makeText(ServicesActivity.this,""+object.toString(), Toast.LENGTH_SHORT).show();
        try {
            JSONArray typesJsonArray=object.getJSONArray("types");
            int typeNum=typesJsonArray.length();
            mMenus=new String[typeNum];
            for(int i=0;i<typesJsonArray.length();i++)
            {
                mMenus[i]=typesJsonArray.getJSONObject(i).getString("service_type_name");
            }

            JSONArray itemsJsonArray=object.getJSONArray("items");
            items = new ArrayList[typeNum];
            for (int i=0;i<typeNum;++i)
            {
                items[i]=new ArrayList();
            }
            for (int i=0;i<itemsJsonArray.length();i++)
            {
                JSONObject oItem=itemsJsonArray.getJSONObject(i);
                int oId=oItem.getInt("service_item_id");
                String oName=oItem.getString("service_item_name");
                int oType=oItem.getInt("service_item_belonged");
                ServiceItem sItem=new ServiceItem(oId,oName,oType);
                items[oType-1].add(sItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        initData((JSONObject) response.get());
        initViews();
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Toast.makeText(ServicesActivity.this,""+exception.toString(), Toast.LENGTH_SHORT).show();
    }
}