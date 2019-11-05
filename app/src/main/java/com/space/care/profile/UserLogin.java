package com.space.care.profile;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.cloud.CloudImage;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * Created by SPACE on 2017/5/15.
 */

public class UserLogin implements HttpListener {
    private Context context;
    private String userName;
    private String userPassWord;

    public UserLogin(Context ctx,String name,String pswd)
    {
        context=ctx;
        userName=name;
        userPassWord=pswd;
    }

    public boolean login()
    {
        StringRequest loginRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"user_login.php", RequestMethod.POST);
        loginRequest.add("login_user_name",userName);
        loginRequest.add("login_user_password",userPassWord);
        CallServer caller= CallServer.getRequestInstance();
        caller.add(context,0,loginRequest,this,true,true);
        return true;
    }

    @Override
    public void onSucceed(int what, Response response) {
        Log.i("loginclass",response.get().toString());
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("loginclass",exception.toString());
    }
}
