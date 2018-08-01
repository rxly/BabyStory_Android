package com.icomm_semi.xuan.babystore;

import android.util.Log;

import com.icomm_semi.xuan.babystore.DevInfo;
import com.icomm_semi.xuan.babystore.View.AudioItem;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String getDownloadInfo(AudioItem audio){
        JSONObject jroot = new JSONObject();
        JSONObject jitem = new JSONObject();
        String s = new String();
        Log.i("rx",audio.play_url);
        try {

            jitem.put("name",audio.name);
//            jitem.put("duration",audio.duration);
            jitem.put("play_url", audio.play_url);
//            jitem.put("size",audio.size);
            jroot.put("downloadinfo",jitem);
            jroot.put("retcode",0);


//            s = jroot.toString(1).replace("\\","");
          s = jroot.toString(1);
            Log.i("rx","Download:");
            Log.i("rx",s);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return s;
        }
    }

}
