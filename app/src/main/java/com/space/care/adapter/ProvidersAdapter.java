package com.space.care.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.space.care.R;
import com.space.care.activity.ProviderActivity;
import com.space.care.activity.ServiceActivity;
import com.space.care.activity.ServicesActivity;
import com.space.care.objects.ProvidersItem;

import java.util.ArrayList;

/**
 * Created by SPACE on 2017/5/12.
 */

public class ProvidersAdapter extends BaseAdapter {
    private ArrayList<ProvidersItem> dataList;
    private Context context;

    public ProvidersAdapter(ArrayList<ProvidersItem> data,Context ctx)
    {
        dataList=data;
        context=ctx;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHoler viewHoler=null;
        if (convertView==null)
        {
            viewHoler=new ViewHoler();
            convertView= LayoutInflater.from(context).inflate(R.layout.providers_listitem,null);
            viewHoler.vhAvator= (ImageView) convertView.findViewById(R.id.pvd_item_avator);
            viewHoler.vhName= (TextView) convertView.findViewById(R.id.pvd_item_name);
            viewHoler.vhLocation= (TextView) convertView.findViewById(R.id.pvd_item_location);
            viewHoler.vhDistance= (TextView) convertView.findViewById(R.id.pvd_item_distance);
            viewHoler.vhRate= (TextView) convertView.findViewById(R.id.pvd_item_grade);
            viewHoler.vhPrice= (TextView) convertView.findViewById(R.id.pvd_item_price);
            viewHoler.vhPeopleNum= (TextView) convertView.findViewById(R.id.pvd_item_peoplenum);
            viewHoler.vhIntroduction= (TextView) convertView.findViewById(R.id.pvd_item_introduction);
            convertView.setTag(viewHoler);
        }
        else viewHoler= (ViewHoler) convertView.getTag();

        viewHoler.vhName.setText(dataList.get(position).pvdName);
        viewHoler.vhLocation.setText("位置："+dataList.get(position).pvdLocation);

        int distance=dataList.get(position).pvdDistance;
        if (distance>=1000) viewHoler.vhDistance.setText("距离："+distance/1000+"千"+distance%1000+"米");
        else viewHoler.vhDistance.setText("距离："+distance+"米");

        viewHoler.vhRate.setText("评价："+dataList.get(position).pvdRate);
        viewHoler.vhPrice.setText("参考价格：￥"+dataList.get(position).pvdPrice);
        viewHoler.vhPeopleNum.setText("服务人次："+dataList.get(position).pvdUserNum);
        viewHoler.vhIntroduction.setText(dataList.get(position).pvdIntroduction);

        viewHoler.vhAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProviderActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("provider_id",dataList.get(position).pvdId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clickedpvd","**"+dataList.get(position).pvdFunctionId);
                Intent intent=new Intent(context, ServiceActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("function_id",dataList.get(position).pvdFunctionId);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHoler
    {
        public ImageView vhAvator;
        public TextView vhName,vhLocation,vhDistance,vhRate,vhPrice,vhPeopleNum,vhIntroduction;
    }
}
