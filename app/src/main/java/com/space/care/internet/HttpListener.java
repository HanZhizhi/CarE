package com.space.care.internet;

import com.yolanda.nohttp.rest.Response;

/**
 * Created by SPACE on 2017/5/11.
 */

public interface HttpListener<T> {
    //请求网络成功回调的方法
    void onSucceed(int what, Response<String> response);
    //请求网络失败回调的监听方法
    void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis);
}
