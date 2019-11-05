package com.space.care.objects;

/**
 * Created by SPACE on 2017/5/12.
 */

public class ServiceItem {
    public int itemId;
    public String itemName;
    public int itemType;

    public ServiceItem(int id,String name,int type)
    {
        itemId=id;
        itemName=name;
        itemType=type;
    }
}
