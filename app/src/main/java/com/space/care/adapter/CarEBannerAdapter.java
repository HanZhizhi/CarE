package com.space.care.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by SPACE on 2017/5/10.
 */

public class CarEBannerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imgUrlLists;

    public CarEBannerAdapter(Context ctx,ArrayList<String> lists)
    {
        context=ctx;
        imgUrlLists=lists;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int newposition=position%imgUrlLists.size();
        ImageView iv=new ImageView(context);
        Glide.with(context).load(imgUrlLists.get(newposition)).into(iv);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(iv);
        Log.i("image","created");
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}