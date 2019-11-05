package com.space.care.internet;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by SPACE on 2017/5/11.
 */

public class WaitDialog extends ProgressDialog {
    public WaitDialog(Context context) {
        super(context);
        //设置当前的Activity无Tilte,并且全屏(调用这个方法必须在setContentView之前调用)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置在对话框的外面点击,是否让对话框消失,false是可以取消
        setCanceledOnTouchOutside(false);
        //设置对话框的样式
        setProgressStyle(STYLE_SPINNER);
        //设置进度条对话框显示的内容
        setMessage("正在请求网络,请稍后....");
    }

    public WaitDialog(Context context, int theme) {
        super(context, theme);
    }
}
