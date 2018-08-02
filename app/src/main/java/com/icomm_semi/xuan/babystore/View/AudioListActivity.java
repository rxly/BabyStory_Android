package com.icomm_semi.xuan.babystore.View;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icomm_semi.xuan.babystore.AudioSrc;
import com.icomm_semi.xuan.babystore.HttpAudioSrc.HttpSpider;
import com.icomm_semi.xuan.babystore.HttpGetListen;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {
    RecyclerView recyclerView = null;
    AudioRecyclerAdapter adapter = null;
    MyHandler myHandler = null;
    private Toolbar toolbar;
    private ImageView backImage;
    private TextView titleText;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            List<AudioItem> audioList = (ArrayList)msg.obj;
            adapter.setList(audioList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        toolbar = (Toolbar)findViewById(R.id.audiolistToolbar);
        setSupportActionBar(toolbar);
        backImage = (ImageView)toolbar.findViewById(R.id.audioBarBack);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioListActivity.this.finish();
            }
        });
        titleText = (TextView)toolbar.findViewById(R.id.audiolistTitle);
        titleText.setText(getIntent().getStringExtra("name"));

        int id = Integer.valueOf(getIntent().getStringExtra("id"));
        Log.i("rx","get List id "+id);
        myHandler = new MyHandler();

        recyclerView = (RecyclerView)findViewById(R.id.audioList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AudioRecyclerAdapter(AudioListActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        HttpSpider.getInstance().getAudioList(id, new HttpGetListen() {
            @Override
            public void OnCompletionListener(List list) {
                Message msg = new Message();
                msg.obj = list;
                myHandler.sendMessage(msg);
            }

            @Override
            public void OnStartListener(List list) {

            }
        });
    }
}
