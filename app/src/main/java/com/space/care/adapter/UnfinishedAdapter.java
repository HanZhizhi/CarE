package com.space.care.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.space.care.R;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.mainFrag.OrderFrag;
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

public class UnfinishedAdapter extends BaseAdapter implements HttpListener{
    private ArrayList<OrderObject> orderList;
    private Context context;
    public UnfinishedAdapter(ArrayList<OrderObject> data, Context ctx) {
        orderList = data;
        context = ctx;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.order_unfinished_item, null);
            viewHolder.vhProvider = (TextView) convertView.findViewById(R.id.unfinished_provider_name);
            viewHolder.vhService = (TextView) convertView.findViewById(R.id.unfinished_service_name);
            viewHolder.vhremarks = (TextView) convertView.findViewById(R.id.unfinished_remarks);
            viewHolder.vhContact = (TextView) convertView.findViewById(R.id.unfinished_contact);
            viewHolder.tvScheduledDate= (TextView) convertView.findViewById(R.id.unfinished_scheduled_date);
            viewHolder.vhcancel = (TextView) convertView.findViewById(R.id.unfinished_cancel_order);
            viewHolder.vhFinishOrder= (TextView) convertView.findViewById(R.id.unfinished_finish_order);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.vhProvider.setText(orderList.get(position).order_provider);
        viewHolder.vhService.setText(orderList.get(position).order_service);
        //String remark=orderList.get(position).order_remarks;
        // viewHolder.vhremarks.setText("".equals(remark)?"备注："+remark:"备注：无");
        viewHolder.vhremarks.setText("备注："+orderList.get(position).order_remarks);
        viewHolder.tvScheduledDate.setText("预约日期："+orderList.get(position).order_scheduled_date);

        viewHolder.vhcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest delRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"cancelUnfinishedOrder.php", RequestMethod.POST);
                delRequest.add("order_id",orderList.get(position).order_id);
                delRequest.add("position",position);
                CallServer caller=CallServer.getRequestInstance();
                caller.add(context,1,delRequest,UnfinishedAdapter.this,true,true);
            }
        });

        viewHolder.vhFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest fnRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"finishOrder.php", RequestMethod.POST);
                fnRequest.add("orderid",orderList.get(position).order_id);
                fnRequest.add("position",position);
                CallServer caller=CallServer.getRequestInstance();
                caller.add(context,2,fnRequest,UnfinishedAdapter.this,true,true);
            }
        });

        viewHolder.vhContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,orderList.get(position).order_provider_manager+orderList.get(position).order_provider_phone,Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder callAlert = new AlertDialog.Builder(context);
                callAlert.setTitle(orderList.get(position).order_provider_manager);
                callAlert.setMessage(orderList.get(position).order_provider_phone);
                callAlert.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderList.get(position).order_provider_phone));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(intent);
                    }
                });
                callAlert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                callAlert.show();
            }
        });

        return convertView;
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what)
        {
            case 1:
                String res=response.get().toString();
                Log.i("cancelordr",res);
                if(res.startsWith("1"))
                {
                    //Log.i("cancelordr",res.substring(1));
                    int pos=Integer.parseInt(res.substring(1));
                    orderList.remove(pos);
                    notifyDataSetChanged();
                    Utils.showToast(context,"取消预约成功！");
                }
                break;
            case 2:
                String fnCode=response.get().toString();
                Log.i("cancelordr",fnCode);
                if (fnCode.startsWith("1"))
                {
                    int pos=Integer.parseInt(fnCode.substring(1));
                    orderList.remove(pos);
                    notifyDataSetChanged();
                    Utils.showToast(context,"订单完成已确认！");
                }
                else if ("0".equals(fnCode))
                {
                    Utils.showToast(context,"系统异常，请重新尝试！");
                }
                break;
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }

    class ViewHolder
    {
        public TextView vhProvider,vhService,vhremarks,vhContact,vhcancel,vhFinishOrder,tvScheduledDate;
    }
}
