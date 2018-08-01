package com.icomm_semi.xuan.babystore.IdaView;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.icomm_semi.xuan.babystore.HttpControl.AudioSrc;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {
    RecyclerView recyclerView = null;
    AudioRecyclerAdapter adapter = null;
    MyHandler myHandler = null;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            List<AudioItem> audioList = (ArrayList)msg.obj;
            Log.i("rx","refresh grid view");
//            List<AudioItem> adapterList = adapter.getList();
            adapter.setList(audioList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        int id = Integer.valueOf(getIntent().getStringExtra("id"));
        Log.i("rx","get List id "+id);
        myHandler = new MyHandler();

        recyclerView = (RecyclerView)findViewById(R.id.audioList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//
        adapter = new AudioRecyclerAdapter(AudioListActivity.this);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        AudioSrc.getInstance().getAudioList(myHandler,id);
    }
}
