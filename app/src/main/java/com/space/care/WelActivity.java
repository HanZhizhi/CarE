package com.space.care;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;

import com.space.care.activity.LoginActivity;
import com.space.care.activity.MainActivity;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.profile.Profile;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class WelActivity extends Activity implements HttpListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tryLogin();
            }
        },1800);
    }

    private void tryLogin()
    {
        String account= getSharedPreferences("CarEProfile",MODE_PRIVATE).getString("userName",null);
        String password=getSharedPreferences("CarEProfile",MODE_PRIVATE).getString("userPassword",null);
        if (account!=null && password!=null)
        {
            StringRequest loginRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"user_login.php", RequestMethod.POST);
            loginRequest.add("login_user_name",account);
            loginRequest.add("login_user_password",password);
            CallServer caller= CallServer.getRequestInstance();
            caller.add(this,0,loginRequest,this,true,true);
        }
        else
        {
            startActivity(new Intent(WelActivity.this,LoginActivity.class));
            finish();
        }

    }

    @Override
    public void onSucceed(int what, Response response) {
        int code=Integer.parseInt(response.get().toString());
        if (code==1)
        {
            startActivity(new Intent(WelActivity.this,MainActivity.class));
            finish();
        }
        else
        {
            startActivity(new Intent(WelActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }
}
