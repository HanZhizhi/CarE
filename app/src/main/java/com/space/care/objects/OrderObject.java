package com.space.care.objects;

/**
 * Created by SPACE on 2017/5/19.
 */

public class OrderObject {
    public int order_id,order_function_id,order_price,order_rated;
    public String order_remarks,order_scheduled_date,order_provider,order_service,order_provider_manager,order_provider_phone,order_finished_time;

    public OrderObject(int order_id, int order_function_id, int order_price, int order_rated, String order_remarks, String order_scheduled_date, String order_provider, String order_service, String order_provider_manager, String order_provider_phone,String order_finished_time) {
        this.order_id = order_id;
        this.order_function_id = order_function_id;
        this.order_price = order_price;
        this.order_rated = order_rated;
        this.order_remarks = order_remarks;
        this.order_scheduled_date = order_scheduled_date;
        this.order_provider = order_provider;
        this.order_service = order_service;
        this.order_provider_manager = order_provider_manager;
        this.order_provider_phone = order_provider_phone;
        this.order_finished_time=order_finished_time;
    }
}
