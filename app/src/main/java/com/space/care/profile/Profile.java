package com.space.care.profile;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SPACE on 2017/5/15.
 */

public class Profile {
    private static SharedPreferences sp;
    //private static String userName,userPassword;

    public static void saveProfile(Context context, String name, String pwd)
    {
        sp=context.getSharedPreferences("CarEProfile",Context.MODE_PRIVATE);
        sp.edit().putString("userName",name).putString("userPassword",pwd).commit();
    }

    public static String getName(Context context)
    {
        sp=context.getSharedPreferences("CarEProfile",Context.MODE_PRIVATE);
        return sp.getString("userName",null);
    }

    public static String getUserPassword(Context context)
    {
        sp=context.getSharedPreferences("CarEProfile",Context.MODE_PRIVATE);
        return sp.getString("userPassword",null);
    }


}
