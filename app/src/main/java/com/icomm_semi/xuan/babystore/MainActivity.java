package com.icomm_semi.xuan.babystore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    private MainHandler mHandler;
    private FloatingActionButton addBtn;
    private LinearLayout addMenuLayout;
    private FloatingActionButton wifiCfgBtn;

    private Boolean isAddMenuShow = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dev:
                    mHandler.obtainMessage(GlobalInfo.MSG_NAVIGATION_DEV).sendToTarget();
                    return true;
                case R.id.navigation_content:
                    mHandler.obtainMessage(GlobalInfo.MSG_NAVIGATION_CONTENT).sendToTarget();
                    return true;
                case R.id.navigation_account:
                    mHandler.obtainMessage(GlobalInfo.MSG_NAVIGATION_ACCOUNT).sendToTarget();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        addMenuLayout = (LinearLayout)findViewById(R.id.wifiConfigLayout);

        addBtn = (FloatingActionButton)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new MainOnClickListen());
        wifiCfgBtn = (FloatingActionButton)findViewById(R.id.wifiCfgBtn);
        wifiCfgBtn.setOnClickListener(new MainOnClickListen());

        mHandler = new MainHandler();
    }

    private class MainOnClickListen implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addBtn:
                    if(isAddMenuShow){
                        addMenuLayout.setVisibility(View.GONE);
                        addBtn.setImageResource(R.mipmap.add);
                        isAddMenuShow = false;
                    }
                    else {
                        addMenuLayout.setVisibility(View.VISIBLE);
                        addBtn.setImageResource(R.mipmap.close);
                        isAddMenuShow = true;
                    }
                    break;
                case R.id.wifiCfgBtn:
                    addMenuLayout.setVisibility(View.GONE);
                    //addBtn.hide();
                    Snackbar.make(view, "No implete \nStart AirKiss wifi config", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                default:
                    break;
            }
        }
    }


    private class  MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GlobalInfo.MSG_NAVIGATION_DEV:
                    Log.i("rx","Get msg:MSG_NAVIGATION_DEV");
                    addBtn.show();
                    break;
                case GlobalInfo.MSG_NAVIGATION_CONTENT:
                    Log.i("rx","Get msg:MSG_NAVIGATION_CONTENT");
                    addBtn.hide();
                    break;
                case GlobalInfo.MSG_NAVIGATION_ACCOUNT:
                    Log.i("rx","Get msg:MSG_NAVIGATION_ACCOUNT");
                    addBtn.hide();
                    break;
                case GlobalInfo.MSG_MQTT_CONNECT_SUCCESS:

                    break;
                default:
                    break;
            }
        }
    }

}
