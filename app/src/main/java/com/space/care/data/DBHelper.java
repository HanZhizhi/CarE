package com.space.care.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SPACE on 2017/1/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="care.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE user (userid INTEGER PRIMARY KEY AUTOINCREMENT,usernameTEXT,userpassword TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor selectUser()
    {
        return getReadableDatabase().rawQuery("select * from workrecord order by wrdate desc",null);
    }

    public Cursor selectContents()
    {
        return getReadableDatabase().rawQuery("select wrcd from workrecord order by wrdate desc",null);
    }

    public void addItem(String content,int done,int left)
    {
        ContentValues addCv=new ContentValues();
        Date dater=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        addCv.put("wrdate",dateFormat.format(dater));
        addCv.put("wraddedtime",timeFormat.format(dater));
        addCv.put("wrcd",content);
        addCv.put("wrdone",done);
        addCv.put("wrleft",left);
        getWritableDatabase().insert("workrecord",null,addCv);
    }
}
