package com.icomm_semi.xuan.babystore;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.icomm_semi.xuan.babystore.View.AudioItem;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MqttControler {
    private final String clientid = "gushiji_demo";
    private static MqttControler gInstance = null;
    private Handler mHandler;
    private int qos = 1;

    private String TOPIC_REQ_SCAN_DEV = "DeviceScanReq";
    private String TOPIC_REQ_DOWNLOAD = "DownloadFileReq";
    private String TOPIC_REQ_LOCAL_LIST = "PlayListReq";

    private String TOPIC_RESP_SCAN_DEV = "DeviceScanResp";
    private String TOPIC_RESP_DOWNLOAD = "DownloadStateResp";
    private String TOPIC_RESP_LOCAL_LIST = "PlayListResp";
    private String TOPIC_RESP_DISCONNECT = "DevDisconnect";
    private String TOPIC_RESP_ONLINE = "OnlineNotification";

    private MqttClient mClient;

    private MqttControler(){}

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public static MqttControler getInstance(){
        if(gInstance == null){
            gInstance = new MqttControler();
        }
        return gInstance;
    }

    public void conncet(){
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_CONNECT_START).sendToTarget();
            mClient = new MqttClient(Key.MQTT_SERVER,clientid,persistence);
            MqttConnectOptions con = new MqttConnectOptions();
            con.setKeepAliveInterval(10);
            con.setUserName(Key.MQTT_USER_NAME);
            con.setPassword(Key.MQTT_PWD.toCharArray());
            con.setCleanSession(true);
            mClient.connect(con);
            mHandler.obtainMessage(GlobalInfo.MSG_MQTT_CONNECT_SUCCESS).sendToTarget();
            mqttMsgArray msgArray = new mqttMsgArray();
            gInstance.subscribe(TOPIC_RESP_SCAN_DEV,msgArray);
            gInstance.subscribe(TOPIC_RESP_LOCAL_LIST,msgArray);
            gInstance.subscribe(TOPIC_RESP_DISCONNECT,msgArray);
            gInstance.subscribe(TOPIC_RESP_ONLINE,msgArray);
            gInstance.subscribe(TOPIC_RESP_DOWNLOAD,msgArray);

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
        ArrayList<DevInfo>  devList = new ArrayList<>();
        mHandler.obtainMessage(GlobalInfo.MSG_MQTT_SCAN_START).sendToTarget();
        JSONObject obj = new JSONObject();
        try {
            obj.put("name","Android_app");
            gInstance.publish(TOPIC_REQ_SCAN_DEV,obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean scanLocalList(){
        ArrayList<DevInfo>  devList = new ArrayList<>();
//        mHandler.obtainMessage(GlobalInfo.MSG_MQTT_SCAN_).sendToTarget();
        JSONObject obj = new JSONObject();
        try {
            obj.put("locallist","Android_app");
            gInstance.publish(TOPIC_REQ_LOCAL_LIST,obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean downloadFile(final AudioItem audio){
        new Thread(new Runnable() {
            @Override
            public void run() {
                gInstance.publish(TOPIC_REQ_DOWNLOAD, DataModel.getDownloadInfo(audio));
            }
        }).start();

        return true;
    }

    private class mqttMsgArray implements IMqttMessageListener{
        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

            if(s.equals(TOPIC_RESP_SCAN_DEV)){
                String content = new String(mqttMessage.getPayload());
                Log.i("rx","Topic:"+ s + "   payload:\n"+content);
                DevInfo dev = DataModel.getDevInfo(content);
                Message msg = new Message();
                msg.obj = dev;
                msg.what = GlobalInfo.MSG_MQTT_SCAN_END;
                mHandler.sendMessage(msg);
            }else if(s.equals(TOPIC_RESP_LOCAL_LIST)){
                String content = new String(mqttMessage.getPayload());
                Log.i("rx","Topic:"+ s + "   payload:\n"+content);

            }else if(s.equals(TOPIC_RESP_DISCONNECT)){
                String content = new String(mqttMessage.getPayload());
                Log.i("rx","Topic:"+ s + "   payload:\n"+content);
                mHandler.obtainMessage(GlobalInfo.MSG_MQTT_DEV_DISCONNECT).sendToTarget();
            }else if(s.equals(TOPIC_RESP_ONLINE)){
                String content = new String(mqttMessage.getPayload());
                Log.i("rx","Topic:"+ s + "   payload:\n"+content);
                Message msg = new Message();
                msg.what = GlobalInfo.MSG_MQTT_DEV_ONLINE;
                msg.obj = content;
                mHandler.sendMessage(msg);
            }else if(s.equals(TOPIC_RESP_DOWNLOAD)){
                String content = new String(mqttMessage.getPayload());
                Log.i("rx","Topic:"+ s + "   payload:\n"+content);
                JSONObject jroot= new JSONObject(content);
                JSONObject jitem = jroot.getJSONObject("download");
                String state = jitem.getString("state");
                String name = jitem.getString("name");
                Message msg = new Message();
                Log.i("rx","state:"+state);
                Log.i("rx","name:"+name);
                if(state.equals("start")){
                    msg.what = GlobalInfo.MSG_MQTT_DOWNLOAD_START;
                }else if (state.equals("end")){
                    msg.what = GlobalInfo.MSG_MQTT_DOWNLOAD_COMPLETE;
                }
                msg.obj = name;
                mHandler.sendMessage(msg);
            }
        }
    }
}
