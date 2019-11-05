package com.space.care.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.space.care.R;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.utils.Utils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class RateActivity extends AppCompatActivity implements HttpListener{
    private Button btRate;
    private RatingBar ratingBar;
    private EditText etRemark;
    private int orderid,functionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("评价");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle=getIntent().getExtras();
        orderid=bundle.getInt("order");
        functionid=bundle.getInt("function");

        Log.i("data",orderid+"  "+functionid);

        initViews();
    }

    private void initViews()
    {
        btRate= (Button) findViewById(R.id.rate_btrate);
        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        etRemark= (EditText) findViewById(R.id.rate_editText);

        btRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("rating",""+ratingBar.getRating()+etRemark.getText().toString());
                double rate=ratingBar.getRating();
                String remark=etRemark.getText().toString();
                StringRequest rateRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"rate.php", RequestMethod.POST);
                rateRequest.add("order",orderid);
                rateRequest.add("function",functionid);
                rateRequest.add("rate",rate);
                rateRequest.add("remark",remark);
                CallServer caller=CallServer.getRequestInstance();
                caller.add(RateActivity.this,0,rateRequest,RateActivity.this,true,true);
            }
        });
    }

    @Override
    public void onSucceed(int what, Response response) {
        int code=Integer.parseInt(response.get().toString());
        Log.i("respon",code+"");
        switch (code)
        {
            case 0:
                Utils.showToast(this,"评价失败，请重试！");
                break;
            case 1:
                Utils.showToast(this,"评价成功！");
                finish();
                break;
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("respon",exception.toString());
    }
}
