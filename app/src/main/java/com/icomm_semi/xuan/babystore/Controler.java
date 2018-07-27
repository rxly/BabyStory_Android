package com.icomm_semi.xuan.babystore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Controler {
    private final String clientid = "gushiji_demo";
    private static Controler gInstance = null;
    private Handler mHandler;
    private int qos = 2;

    private String TOPIC_PUB_SCAN_DEV = "ScanDev";
    private String TOPIC_SUB_DEV_STATE = "DevState";

    private MqttClient mClient;

    private Controler(Handler handler){
        mHandler = handler;
    }

    public static Controler getInstance(Handler handler){
        if(gInstance == null){
            gInstance = new Controler(handler);
            gInstance.init();
        }
        return gInstance;
    }

    public void init(){
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mClient = new MqttClient(Key.MQTT_SERVER,clientid,persistence);
            MqttConnectOptions con = new MqttConnectOptions();
            con.setKeepAliveInterval(10);
            con.setUserName(Key.MQTT_USER_NAME);
            con.setPassword(Key.MQTT_PWD.toCharArray());
            con.setCleanSession(true);
            mClient.connect(con);
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_CONNECT_SUCCESS).sendToTarget();
        } catch (MqttException e) {
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_CONNECT_FAIL).sendToTarget();
            e.printStackTrace();
        }
    }

    public boolean isAlive(){
        return mClient.isConnected();
    }

    public boolean publish(String topic, String content){
        MqttMessage msg = new MqttMessage(content.getBytes());
        try {
            mClient.publish(topic,msg);
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_PUB_SUCCESS).sendToTarget();
            return true;
        } catch (MqttException e) {
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_PUB_FAIL).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }

    public boolean subscribe(String topic, IMqttMessageListener listener){
        try {
            mClient.subscribe(topic, qos,listener);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconncet(){
        try {
            mClient.disconnect();
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_CONNECT_FAIL);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        gInstance = null;
    }

    public boolean scanDev(){
        ArrayList<Dev>  devList = new ArrayList<>();
        mHandler.obtainMessage(GlobalInfo.MSG_MQTT_SCAN_START).sendToTarget();
        gInstance.subscribe(TOPIC_SUB_DEV_STATE,new mqttMsgArray());
        JSONObject obj = new JSONObject();
        try {
            obj.put("name","Android_app");
            gInstance.publish(TOPIC_PUB_SCAN_DEV,obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private class mqttMsgArray implements IMqttMessageListener{

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            if(s.equals(TOPIC_SUB_DEV_STATE)){
                Dev dev = new Dev();
                JSONObject obj = new JSONObject(new String(mqttMessage.getPayload()));
                dev.setDevName(obj.getString("name"));
                dev.setBatteryLevel(obj.getInt("battery_level"));
                Message msg = new Message();
                msg.obj = dev;
                mHandler.sendMessage(msg);
            }
        }
    }
}
