package com.space.care;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * Created by SPACE on 2017/5/11.
 */

public class CarEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }
}
