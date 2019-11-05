package com.space.care.objects;

/**
 * Created by SPACE on 2017/5/12.
 */

public class ProvidersItem {
    public int pvdId,pvdDistance,pvdDiseMark,pvdFunctionId;
    public double pvdRate;
    public String pvdName,pvdIntroduction,pvdLocation,pvdPrice,pvdUserNum;

    public ProvidersItem(int id,double rate,String name,String intro,String loc,int dis,String price,String user,int Func)
    {
        pvdId=id;
        pvdRate=rate;
        pvdName=name;
        pvdIntroduction=intro;
        pvdLocation=loc;
        pvdDistance=dis;
        pvdPrice=price;
        pvdUserNum=user;

        pvdFunctionId=Func;

        if (0<pvdDistance && pvdDistance<=1000) {pvdDiseMark=5;}
        else if (1000<pvdDistance && pvdDistance<=3000) {pvdDiseMark=4;}
        else if (3000<pvdDistance && pvdDistance<=5000) {pvdDiseMark=3;}
        else if (5000<pvdDistance && pvdDistance<=8000) {pvdDiseMark=2;}
        else {pvdDiseMark=1;}
    }
}
