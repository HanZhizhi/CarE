package com.space.care.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.space.care.R;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.profile.Profile;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class OrderActivity extends AppCompatActivity implements HttpListener{
    private int funcId=-1;
    private Toolbar toolbar;
    private TextView tvProvider,tvService,tvPrice,tvOrderDate;
    private Button btOrder;
    private EditText etRemark;
    private String funcProvider,funcServiceName,funcPrice;
    private int year,month,day;
    private String orderRemark="",orderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order);

        Intent intent=getIntent();
        if(intent!=null)
        {
            Bundle bundle=intent.getExtras();
            funcId=bundle.getInt("funcId");
            Log.i("functionid",funcId+"");
            funcProvider=bundle.getString("provider");
            funcServiceName=bundle.getString("service");
            funcPrice=bundle.getString("price");
        }

        initViews();
    }

    private void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("确认订单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvProvider= (TextView) findViewById(R.id.order_tv_provider_name);
        tvService= (TextView) findViewById(R.id.order_tv_service_item);
        tvPrice= (TextView) findViewById(R.id.order_tv_price);
        tvOrderDate= (TextView) findViewById(R.id.order_tv_service_date);
        btOrder= (Button) findViewById(R.id.order_bt_order);
        etRemark= (EditText) findViewById(R.id.order_et_service_remarks);

        tvProvider.setText(funcProvider);
        tvService.setText(funcServiceName);
        tvPrice.setText("价格："+funcPrice);

        tvOrderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profile.getName(OrderActivity.this)==null)
                {
                    Toast.makeText(OrderActivity.this,"请先登录或注册",Toast.LENGTH_SHORT).show();
                    Intent logIntent=new Intent(OrderActivity.this,LoginActivity.class);
                    startActivity(logIntent);
                    return;
                }

                orderRemark=etRemark.getText().toString();
                Log.i("orderremark",etRemark.getText().toString());
                if (year==0 && month==0 && day==0)
                {
                    showCalendar();
                    return;
                }
                Log.i("orderdate",year+"year"+month+day);
                orderDate=year+"-"+month+"-"+day;
                StringRequest orderRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"addOrder.php",RequestMethod.POST);
                Log.i("functionid",funcId+"");
                orderRequest.add("function_id",funcId);
                orderRequest.add("remark",orderRemark);
                orderRequest.add("date",orderDate);
                CallServer caller=CallServer.getRequestInstance();
                caller.add(OrderActivity.this,0,orderRequest,OrderActivity.this,true,true);
            }
        });
    }

    private void showCalendar()
    {
        Calendar mycalendar=Calendar.getInstance();

        year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天

        DatePickerDialog.OnDateSetListener dateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                year=y;
                month=m+1;
                day=d;

                tvOrderDate.setText(year+"年"+month+"月"+day+"日");
            }
        };

        DatePickerDialog dpd=new DatePickerDialog(OrderActivity.this,dateListener,year,month,day);
        dpd.show();
    }

    @Override
    public void onSucceed(int what, Response response) {
        String l=response.get().toString();
        Log.i("funcdata",l);
        if (l.equals("ok"))
        {
            //Toast.makeText(this,"下单完成~~",Toast.LENGTH_LONG).show();
            showDialoge();
        }
        else
        {
            Toast.makeText(this,"系统异常，请重新尝试！",Toast.LENGTH_LONG).show();
        }
    }

    private void showDialoge()
    {
        AlertDialog.Builder okDialog=new AlertDialog.Builder(OrderActivity.this);
        okDialog.setIcon(R.mipmap.ic_launcher);
        okDialog.setTitle("下单成功");
        okDialog.setMessage("请按照预约时间完成订单");
        okDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        okDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        okDialog.show();
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("funcdata",exception.toString());
        Toast.makeText(this,"系统异常，请重新尝试！",Toast.LENGTH_LONG).show();
    }
}
