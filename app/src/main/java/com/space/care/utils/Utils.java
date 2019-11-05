package com.space.care.utils;

import android.content.Context;
import android.widget.Toast;

import com.space.care.R;

/**
 * Created by SPACE on 2017/5/30.
 */

public class Utils {
    public static void showToast(Context context, String string)
    {
        Toast.makeText(context,string,Toast.LENGTH_LONG).show();
    }
}
