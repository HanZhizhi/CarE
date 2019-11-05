package com.space.care.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.space.care.R;
import com.space.care.activity.RateActivity;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.objects.OrderObject;
import com.space.care.utils.Utils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

import java.util.ArrayList;

/**
 * Created by SPACE on 2017/5/19.
 */

public class FinishedAdapter extends BaseAdapter implements HttpListener{
    private static final int Delete=1;
    private ArrayList<OrderObject> dataList;
    private Context context;

    public FinishedAdapter(ArrayList<OrderObject> list,Context ctx)
    {
        dataList=list;
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
        ViewHolder viewHolder=null;
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.order_finished_item,null);
            viewHolder.vhProvider= (TextView) convertView.findViewById(R.id.finished_provider_name);
            viewHolder.vhService= (TextView) convertView.findViewById(R.id.finished_service_name);
            viewHolder.vhRemark= (TextView) convertView.findViewById(R.id.finished_tv_remark);
            viewHolder.vhFinishTime= (TextView) convertView.findViewById(R.id.finished_finish_time);
            viewHolder.vhDelete= (TextView) convertView.findViewById(R.id.finished_delete_order);
            viewHolder.vhRate= (TextView) convertView.findViewById(R.id.finished_rate_order);
            convertView.setTag(viewHolder);
        }
        else viewHolder= (ViewHolder) convertView.getTag();

        viewHolder.vhProvider.setText(dataList.get(position).order_provider);
        viewHolder.vhService.setText(dataList.get(position).order_service);
        //String remark=dataList.get(position).order_remarks;
        //viewHolder.vhRemark.setText("".equals(remark)?"备注："+remark:"备注：无");
        viewHolder.vhRemark.setText("备注："+dataList.get(position).order_remarks);
        viewHolder.vhFinishTime.setText("完成时间："+dataList.get(position).order_finished_time);

        viewHolder.vhRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle rbundle=new Bundle();
                rbundle.putInt("function",dataList.get(position).order_function_id);
                rbundle.putInt("order",dataList.get(position).order_id);
                Intent rateIntent=new Intent(context, RateActivity.class);
                rateIntent.putExtras(rbundle);
                context.startActivity(rateIntent);
            }
        });

        viewHolder.vhDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest delRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"deleteFinishedOrder.php", RequestMethod.POST);
                delRequest.add("order_id",dataList.get(position).order_id);
                delRequest.add("position",position);
                CallServer caller=CallServer.getRequestInstance();
                caller.add(context,Delete,delRequest,FinishedAdapter.this,true,true);
            }
        });

        return convertView;
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what)
        {
            case Delete:
                String res=response.get().toString();
                Log.i("cancelordr",res);
                if(res.startsWith("1"))
                {
                    //Log.i("cancelordr",res.substring(1));
                    int pos=Integer.parseInt(res.substring(1));
                    dataList.remove(pos);
                    notifyDataSetChanged();
                    Utils.showToast(context,"删除订单成功！");
                }
                break;
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }

    class ViewHolder
    {
        public TextView vhProvider,vhService,vhRemark,vhFinishTime,vhDelete,vhRate;
    }
}
