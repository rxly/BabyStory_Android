package com.icomm_semi.xuan.babystore.HttpControl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.icomm_semi.xuan.babystore.IdaView.AudioItem;
import com.icomm_semi.xuan.babystore.IdaView.CategoryItem;
import com.icomm_semi.xuan.babystore.Key;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioSrc {
    private static AudioSrc instance = null;
    private ArrayList<CategoryItem> dataList;
    private ArrayList<CategoryItem> subDataList;
    private ArrayList<AudioItem> audioList;

    private Handler mHandler;

    public static AudioSrc getInstance(){
        if (instance == null){
            instance = new AudioSrc();
        }
        return instance;
    }

    private AudioSrc(){
        dataList = new ArrayList<>();
        subDataList = new ArrayList<>();
        audioList = new ArrayList<>();
    }

    public String getSignature(String ts){

        return SHA1.encode(Key.IDA_APP_ID+Key.IDA_SRC+ts);
    }

    private String getList(int catId){
        String ts = String.valueOf(System.currentTimeMillis()/1000);
        Random random = new Random();
        String nonce = String.valueOf(random.nextInt(1000000000));
        Log.i("rx","Nonce:"+nonce);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("app_id",Key.IDA_APP_ID));
        params.add(new BasicNameValuePair("device_id",Key.IDA_DEV_ID));
        params.add(new BasicNameValuePair("timestamp",ts));
        params.add(new BasicNameValuePair("nonce",nonce));
        params.add(new BasicNameValuePair("signature",getSignature(ts)));
        params.add(new BasicNameValuePair("offset","0"));
        params.add(new BasicNameValuePair("limit","20"));
        params.add(new BasicNameValuePair("cat_ids",catId == 0? "[]":("["+catId+"]")));
        Log.i("rx",params.toString());

        UrlEncodedFormEntity entity= null;
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Key.IDA_HOST);
        try {

            entity = new UrlEncodedFormEntity(params);
            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity httpentity=response.getEntity();
                return EntityUtils.toString(httpentity, "utf-8");
            }else{
                return null;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }

    public Bitmap getImageBitmap(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse resp = client.execute(httpget);
            // 判断是否正确执行
            if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                // 将返回内容转换为bitmap
                HttpEntity entity = resp.getEntity();
                InputStream in = entity.getContent();
                Bitmap mBitmap = BitmapFactory.decodeStream(in);
                // 向handler发送消息，执行显示图片操作
                Log.i("rx","Bitmap W:" +mBitmap.getWidth()+"   H:"+mBitmap.getHeight());
                return mBitmap;
            }

        } catch (Exception e) {
            Log.i("rx","获取图片出错");
        } finally {
            client.getConnectionManager().shutdown();
        }

        return null;
    }

    private ArrayList<CategoryItem> decCategory(String json){
        JSONObject jroot;
        JSONObject jcats;
        JSONArray  jarray;
        try {
            Log.i("rx",json);
            jroot = new JSONObject(json);
            jcats = jroot.getJSONObject("audioinfos");
            jarray = jcats.getJSONArray("cats");
            for(int i = 0; i < jarray.length(); i++){
                CategoryItem categoryItem = new CategoryItem();
                JSONObject item = jarray.getJSONObject(i);
                categoryItem.name = item.getString("cat_name");
                categoryItem.icon_url = item.getString("cat_icon_url");
                categoryItem.id = item.getInt("cat_id");
                categoryItem.icon = getImageBitmap(categoryItem.icon_url);
                Log.i("rx","Name:"+categoryItem.name+"  id:"+categoryItem.id);
                dataList.add(categoryItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.obj = dataList;
        mHandler.sendMessage(msg);
        return dataList;
    }

    private ArrayList<CategoryItem> decSubCategory(String json){
        JSONObject jroot;
        JSONObject jcats;
        JSONArray  jarray;
        try {
            Log.i("rx",json);
            jroot = new JSONObject(json);
            jcats = jroot.getJSONObject("audioinfos");
            jarray = jcats.getJSONArray("cats");
            for(int i = 0; i < jarray.length(); i++){
                CategoryItem categoryItem = new CategoryItem();
                JSONObject item = jarray.getJSONObject(i);
                categoryItem.cat_count = item.getInt("cat_count");
                if(categoryItem.cat_count == 0)
                    continue;
                categoryItem.name = item.getString("cat_name");
                categoryItem.icon_url = item.getString("cat_icon_url");
                categoryItem.id = item.getInt("cat_id");
                categoryItem.icon = getImageBitmap(categoryItem.icon_url);

                Log.i("rx","Name:"+categoryItem.name);
                subDataList.add(categoryItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.obj = subDataList;
        mHandler.sendMessage(msg);
        return subDataList;
    }

    private ArrayList<AudioItem> decAudioList(String json){
        JSONObject jroot;
        JSONObject jcats;
        JSONArray  jarray;
        try {
            Log.i("rx",json);
            jroot = new JSONObject(json);
            jcats = jroot.getJSONObject("audioinfos");
            jarray = jcats.getJSONArray("contents");
            for(int i = 0; i < jarray.length(); i++){
                AudioItem audioItem = new AudioItem();
                JSONObject item = jarray.getJSONObject(i);
                audioItem.name = item.getString("name");
                audioItem.icon_url = item.getString("icon");
                audioItem.id = item.getString("id");
                audioItem.content = item.getString("taxonomys");
                audioItem.price = item.getInt("price");
                audioItem.play_url = item.getString("play_url_with_token");
//                audioItem.duration = item.getInt("duration");
//                audioItem.size = item.getInt("filesize");
                audioItem.icon = getImageBitmap(audioItem.icon_url);

                Log.i("rx","Name:"+audioItem.name);
                audioList.add(audioItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.obj = audioList;
        mHandler.sendMessage(msg);
        return audioList;
    }

    public void getCategoryList(Handler handler){
        if(dataList.size() > 0){
            Message msg = new Message();
            msg.obj = dataList;
            mHandler.sendMessage(msg);
            return;
        }

        mHandler = handler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                decCategory(getList(0));
            }
        }).start();
    }

    public void getSubCategoryList(Handler handler, final int catId){

        if(subDataList.size() > 0){
            subDataList.clear();
        }

        mHandler = handler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                decSubCategory(getList(catId));
            }
        }).start();
    }

    public void getAudioList(Handler handler,final int catId){
        if(audioList.size() > 0){
            audioList.clear();
        }

        mHandler = handler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                decAudioList(getList(catId));
            }
        }).start();
    }
}
