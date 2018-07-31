package com.icomm_semi.xuan.babystore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataModel {

    public static DevInfo getDevInfo(String json){
        DevInfo devInfo = new DevInfo();
        try {
            JSONObject jroot = new JSONObject(json);
            JSONObject jitem = jroot.getJSONObject("deviceinfo");
            devInfo.setDevName(jitem.getString("name"));
            devInfo.setBatteryLevel(jitem.getInt("battery"));
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return devInfo;
        }
    }

}
