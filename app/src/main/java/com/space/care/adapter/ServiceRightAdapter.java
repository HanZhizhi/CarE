package com.space.care.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.space.care.R;
import com.space.care.objects.ServiceItem;

import java.util.ArrayList;

/**
 * Created by SPACE on 2017/5/11.
 */


public class ServiceRightAdapter extends BaseAdapter {
    private ArrayList<ServiceItem>[] allData;
    private Context context;
    private  int selectIndex;

    public ServiceRightAdapter(ArrayList<ServiceItem>[] allData, Context context, int selectIndex) {
        this.allData=allData;
        this.context=context;
        this.selectIndex=selectIndex;
    }

    @Override
    public int getCount() {
        return allData[selectIndex].size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.service_right_layout,null);
            vh=new ViewHolder();
            vh.tv= (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }

        vh.tv.setText(allData[selectIndex].get(position).itemName);

        return convertView;
    }

    public void setIndex(int index){
        selectIndex=index;
    }

    class ViewHolder{
        TextView tv;
    }
}