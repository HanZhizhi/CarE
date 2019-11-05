package com.space.care.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.space.care.R;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceActivity extends AppCompatActivity implements HttpListener{
    private Toolbar toolbar;
    private WebView introWebView;
    private ScrollView introScrollView;
    private TextView introTextView;
    private Button btnBack,btnOrder;
    private int function_id=-1;
    private String serviceProvider,serviceItem,servicePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_service);

        Intent intent=getIntent();
        if(intent!=null)
        {
            Bundle bundle=intent.getExtras();
            function_id=bundle.getInt("function_id");
        }

        initViews();
        getData();
    }

    private void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("服务详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        introWebView= (WebView) findViewById(R.id.service_introduction_webview);
        introScrollView= (ScrollView) findViewById(R.id.service_introduction_scrollview);
        introTextView= (TextView) findViewById(R.id.service_introduction_textview);

        btnBack= (Button) findViewById(R.id.service_button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOrder= (Button) findViewById(R.id.service_button_order);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Log.i("functionid",function_id+"");
                bundle.putInt("funcId",function_id);
                bundle.putString("provider",serviceProvider);
                bundle.putString("service",serviceItem);
                bundle.putString("price",servicePrice);
                Intent orderIntent=new Intent(ServiceActivity.this,OrderActivity.class);
                orderIntent.putExtras(bundle);
                startActivity(orderIntent);
            }
        });
    }

    private void getData()
    {
        Request<JSONObject> funcRequest= NoHttp.createJsonObjectRequest(NetConsts.getUrl()+"getFunctionInfo.php", RequestMethod.POST);
        funcRequest.add("function_id",function_id);
        CallServer funcer=CallServer.getRequestInstance();
        funcer.add(this,0,funcRequest,this,true,true);
    }

    private void decorViews(JSONObject object)
    {
        try {
            String introductionString=object.getString("function_introduction");
            if (introductionString.startsWith("http://") || introductionString.startsWith("https://"))
            {
                introWebView.loadUrl(introductionString);
                introWebView.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                introWebView.setVisibility(View.VISIBLE);
            }
            else
            {
                introTextView.setText(introductionString);
                introScrollView.setVisibility(View.VISIBLE);
            }

            serviceProvider=object.getString("sp_name");
            serviceItem=object.getString("service_item_name");
            servicePrice=object.getString("function_price");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        JSONObject funcObject= (JSONObject) response.get();
        Log.i("funcobject",funcObject.toString());
        decorViews(funcObject);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("funcobject",exception.toString());
    }
}
