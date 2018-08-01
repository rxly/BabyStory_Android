package com.icomm_semi.xuan.babystore.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.system.Os;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.icomm_semi.xuan.babystore.Controler;
import com.icomm_semi.xuan.babystore.DevInfo;
import com.icomm_semi.xuan.babystore.GlobalInfo;
import com.icomm_semi.xuan.babystore.R;
import com.icomm_semi.xuan.babystore.View.fragment.AccountFragment;
import com.icomm_semi.xuan.babystore.View.fragment.AudioViewFragment;
import com.icomm_semi.xuan.babystore.View.fragment.DeviceFragment;

public class MainActivity extends AppCompatActivity {
    private DeviceFragment devFragment;
    private Fragment audioFragment;
    private Fragment accountFragment;
    private Fragment fragmentList[]= null;
    private int actionFragment = 0;
    private Controler mMqttCtrl;
    private Handler mHandler;
    private long startTime = 0;

    private class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GlobalInfo.MSG_MQTT_CONNECT_START:
                    Log.i("rx","start conncet MQTT");
                    devFragment.setConState(R.string.connceting_server);
                    break;
                case GlobalInfo.MSG_MQTT_CONNECT_SUCCESS:
                    Log.i("rx","conncet MQTT success");
                    devFragment.setConState(R.string.connceted_server);
                    mMqttCtrl.scanDev();
                    break;
                case GlobalInfo.MSG_MQTT_CONNECT_FAIL:
                    Log.i("rx","start conncet MQTT false");
                    devFragment.setConState(R.string.disconnceted_server);
                    break;
                case GlobalInfo.MSG_MQTT_SCAN_START:
                    devFragment.setConState(R.string.connceting_dev);
                    Log.i("rx","start scan dev ");
                    break;
                case GlobalInfo.MSG_MQTT_SCAN_END:
                    DevInfo dev = (DevInfo)msg.obj;
                    String sta = new String("已连接到："+dev.getDevName());
                    devFragment.setConState(sta);
                    Log.i("rx","stop scan dev ");
                    mMqttCtrl.scanLocalList();
                    break;
                case GlobalInfo.MSG_MQTT_DEV_DISCONNECT:
                    Toast.makeText(MainActivity.this,"故事机已经断线",Toast.LENGTH_SHORT).show();
                    break;
                case GlobalInfo.MSG_MQTT_DEV_ONLINE:
                    mMqttCtrl.scanDev();
                    Toast.makeText(MainActivity.this,(String)msg.obj+"上线",Toast.LENGTH_SHORT).show();
                    break;
                case GlobalInfo.MSG_MQTT_DOWNLOAD_START:
                    startTime = System.currentTimeMillis();
                    Toast.makeText(MainActivity.this,"文件:"+(String)msg.obj+"开始下载...",Toast.LENGTH_SHORT).show();
                    break;
                case GlobalInfo.MSG_MQTT_DOWNLOAD_COMPLETE:
                    long endTime = System.currentTimeMillis();
                    long costTime = (endTime - startTime)/1000;
                    Toast.makeText(MainActivity.this,"文件:"+(String)msg.obj+"下载完成\n耗时:"+costTime,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragment(0);
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(1);
                    return true;
                case R.id.navigation_notifications:
                    switchFragment(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MainHandler();
        initFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initControl();
    }

    private void initFragment(){
        devFragment = new DeviceFragment();
        accountFragment = new AccountFragment();
        audioFragment = new AudioViewFragment();
        fragmentList = new Fragment[]{devFragment,audioFragment,accountFragment};

        actionFragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,fragmentList[actionFragment]).show(fragmentList[actionFragment]).commit();
    }

    public void switchFragment(int idx){
        if(idx == actionFragment){
            return;
        }
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragmentList[actionFragment]);
        if(fragmentList[idx].isAdded()==false)
        {
            transaction.add(R.id.fragmentLayout,fragmentList[idx]);
        }
        actionFragment = idx;
        transaction.show(fragmentList[idx]).commitAllowingStateLoss();
    }

    public void initControl(){
        mMqttCtrl = Controler.getInstance();
        mMqttCtrl.setHandler(mHandler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMqttCtrl.conncet();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if(mMqttCtrl.isAlive())
            mMqttCtrl.disconncet();
        super.onDestroy();
    }
}
