package com.space.care.mainFrag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.space.care.R;
import com.space.care.activity.LoginActivity;
import com.space.care.adapter.FinishedAdapter;
import com.space.care.adapter.UnfinishedAdapter;
import com.space.care.internet.CallServer;
import com.space.care.internet.HttpListener;
import com.space.care.internet.NetConsts;
import com.space.care.objects.OrderObject;
import com.space.care.profile.Profile;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static com.space.care.R.id.forder_top_finished;

/**
 * Created by SPACE on 2017/5/9.
 */

public class OrderFrag extends Fragment implements HttpListener{
    private RelativeLayout unlogedRelativeLayout;
    private LinearLayout logedLinearLayout;
    private Button btFinished,btUnfinished,btLog;
    private ImageView ivPay;
    private TextView tvLeft,tvRight;
    private ListView lvUnfinished,lvFinished;
    private UnfinishedAdapter unAdapter;
    private FinishedAdapter fnAdapter;
    private ArrayList<OrderObject> unfinishedOrdersArrayList=new ArrayList<>(),finishedOrderArrayList=new ArrayList<>();
    private static final int NO_GETUNFINISHED=0,NO_GETFINISHED=1,NO_DELETE_ORDER=3,NO_FINISH_ORDER=4;

    private void getUnfinishedOrderDatas()
    {
        JsonObjectRequest ordersRequest= (JsonObjectRequest) NoHttp.createJsonObjectRequest(NetConsts.getUrl()+"getUnfinishedOrders.php", RequestMethod.POST);
        CallServer caller=CallServer.getRequestInstance();
        caller.add(getActivity(),NO_GETUNFINISHED,ordersRequest,this,true,true);
    }

    private void getFinishedOrderDatas()
    {
        JsonObjectRequest ordersRequest= (JsonObjectRequest) NoHttp.createJsonObjectRequest(NetConsts.getUrl()+"getFinishedOrders.php", RequestMethod.POST);
        CallServer caller=CallServer.getRequestInstance();
        caller.add(getActivity(),NO_GETFINISHED,ordersRequest,this,true,true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_order,container,false);

        unlogedRelativeLayout= (RelativeLayout) view.findViewById(R.id.forder_unloged_ralative_layout);
        logedLinearLayout= (LinearLayout) view.findViewById(R.id.forder_loged_linearlayout);
        btUnfinished= (Button) view.findViewById(R.id.forder_top_unfinished);
        btFinished= (Button) view.findViewById(forder_top_finished);
        tvLeft= (TextView) view.findViewById(R.id.forder_indicator_left);
        tvRight= (TextView) view.findViewById(R.id.forder_indicator_right);
        btLog= (Button) view.findViewById(R.id.forder_login_button);
        ivPay= (ImageView) view.findViewById(R.id.forder_image_pay);
        lvUnfinished= (ListView) view.findViewById(R.id.forder_unfinished_listview);
        lvFinished= (ListView) view.findViewById(R.id.forder_finished_listview);

        TextView unFoot=new TextView(getActivity());
        unFoot.setText("暂无更多未完成订单");
        unFoot.setGravity(Gravity.CENTER);
        lvUnfinished.addFooterView(unFoot);
        unAdapter=new UnfinishedAdapter(unfinishedOrdersArrayList,getActivity());
        lvUnfinished.setAdapter(unAdapter);

        TextView fnFoot=new TextView(getActivity());
        fnFoot.setText("暂无更多已完成订单");
        fnFoot.setGravity(Gravity.CENTER);
        lvFinished.addFooterView(fnFoot);
        fnAdapter=new FinishedAdapter(finishedOrderArrayList,getActivity());
        lvFinished.setAdapter(fnAdapter);

        ivPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "未找到支付宝！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btUnfinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonColor(0);
            }
        });
        btFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonColor(1);
            }
        });

        if (Profile.getName(getActivity().getApplicationContext())!=null)
        {
            ivPay.setVisibility(View.VISIBLE);
            getUnfinishedOrderDatas();
            logedLinearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            btLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
            unlogedRelativeLayout.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void setButtonColor(int i)
    {
        switch (i)
        {
            case 0:
                btUnfinished.setTextColor(Color.parseColor("#e53935"));
                btFinished.setTextColor(Color.WHITE);
                tvLeft.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.INVISIBLE);
                getUnfinishedOrderDatas();
                break;
            case 1:
                btUnfinished.setTextColor(Color.WHITE);
                btFinished.setTextColor(Color.parseColor("#e53935"));
                tvLeft.setVisibility(View.INVISIBLE);
                tvRight.setVisibility(View.VISIBLE);
                getFinishedOrderDatas();
                break;
        }
    }

    @Override
    public void onSucceed(int what, Response response) {
        switch (what)
        {
            case NO_GETUNFINISHED:
                JSONObject orderObject= (JSONObject) response.get();
                Log.i("orders",orderObject.toString());
                try {
                    if (orderObject.has("noOrder") && orderObject.getString("noOrder").equals("true"))
                    {
                        Log.i("orders","noorder");
                        lvUnfinished.setVisibility(View.VISIBLE);
                        lvFinished.setVisibility(View.GONE);
                    }
                    else
                    {
                        JSONArray unfinishedArray=orderObject.getJSONArray("unfinishedOrders");
                        ArrayList<OrderObject> tempArray=setUnfinishedListView(unfinishedArray);
                        unfinishedOrdersArrayList.removeAll(unfinishedOrdersArrayList);
                        Log.i("shujuchangdu",unfinishedOrdersArrayList.size()+"");
                        unfinishedOrdersArrayList.addAll(tempArray);
                        Log.i("shujuchangdu",unfinishedOrdersArrayList.size()+"");

                        unAdapter.notifyDataSetChanged();

                        lvUnfinished.setVisibility(View.VISIBLE);
                        lvFinished.setVisibility(View.GONE);
                        /*lvUnfinished.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                StringRequest delRequest= (StringRequest) NoHttp.createStringRequest(NetConsts.getUrl()+"cancelUnfinishedOrder.php", RequestMethod.POST);
                                delRequest.add("order_id",unfinishedOrdersArrayList.get(position).order_id);
                                CallServer caller=CallServer.getRequestInstance();
                                caller.add(getActivity(),1,delRequest,OrderFrag.this,true,true);
                                return false;
                            }
                        });
                        lvUnfinished.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0,0,0,"完成订单");
                            }
                        });*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case NO_GETFINISHED:
                JSONObject fnOrderObject= (JSONObject) response.get();
                Log.i("orders",fnOrderObject.toString());
                try {
                    if (fnOrderObject.has("noOrder") && fnOrderObject.getString("noOrder").equals("true"))
                    {
                        Log.i("orders","noorder");
                        lvUnfinished.setVisibility(View.GONE);
                        lvFinished.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        JSONArray finishedArray=fnOrderObject.getJSONArray("finishedOrders");
                        ArrayList<OrderObject> tempArray=setUnfinishedListView(finishedArray);
                        finishedOrderArrayList.removeAll(finishedOrderArrayList);
                        Log.i("shujuchangdu",finishedOrderArrayList.size()+"");
                        finishedOrderArrayList.addAll(tempArray);
                        Log.i("shujuchangdu",finishedOrderArrayList.size()+"");

                        fnAdapter.notifyDataSetChanged();

                        lvUnfinished.setVisibility(View.GONE);
                        lvFinished.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 0:

                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Log.i("orders",exception.toString());
    }

    private ArrayList<OrderObject> setUnfinishedListView(JSONArray array)
    {
        ArrayList<OrderObject> orderObjectArrayList=new ArrayList<>();
        for(int i=0;i<array.length();++i)
        {
            int order_id,order_function_id,order_price,order_rated;
            String order_remarks,order_scheduled_date,order_provider,order_service,order_provider_manager,order_provider_phone,order_finished_time;
            try {
                order_id=array.getJSONObject(i).getInt("service_order_id");
                order_function_id=array.getJSONObject(i).getInt("order_to_function");
                order_price=array.getJSONObject(i).getInt("order_paid_price");
                order_rated=array.getJSONObject(i).getInt("order_rated");
                order_remarks=array.getJSONObject(i).getString("order_remarks");
                order_scheduled_date=array.getJSONObject(i).getString("order_scheduled_time");
                order_provider=array.getJSONObject(i).getString("sp_name");
                order_provider_manager=array.getJSONObject(i).getString("sp_manager_name");
                order_service=array.getJSONObject(i).getString("service_item_name");
                order_provider_phone=array.getJSONObject(i).getString("sp_phone_number");
                order_finished_time=array.getJSONObject(i).getString("order_confirmed_time");

                OrderObject orderItem=new OrderObject(order_id,order_function_id,order_price,order_rated,order_remarks,order_scheduled_date,order_provider,order_service,order_provider_manager,order_provider_phone,order_finished_time);
                orderObjectArrayList.add(orderItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return orderObjectArrayList;
    }

}
