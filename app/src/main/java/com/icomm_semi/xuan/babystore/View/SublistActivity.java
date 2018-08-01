package com.icomm_semi.xuan.babystore.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.icomm_semi.xuan.babystore.AudioSrc;
import com.icomm_semi.xuan.babystore.HttpGetListen;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SublistActivity extends AppCompatActivity {
    private GridView gridView;
    ArrayList<Map<String, Object>> subDataList;
    private ArrayList<CategoryItem> subCatList = null;
    SimpleAdapter adapter;
    MyHandler mHandler = null;
    private Toolbar toolbar;
    private ImageView back;
    private TextView titleText;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            subCatList = (ArrayList<CategoryItem>) msg.obj;
            for (int i = 0; i < subCatList.size(); i++) {
                Map<String, Object> map=new HashMap<>();
                map.put("img", subCatList.get(i).icon);
                map.put("text",subCatList.get(i).name);
                map.put("id",subCatList.get(i).id);
                map.put("cnt",subCatList.get(i).cat_count);
                subDataList.add(map);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sublist);
        toolbar = (Toolbar)findViewById(R.id.sublistToolbar);
        setSupportActionBar(toolbar);
        back = (ImageView)toolbar.findViewById(R.id.toolbarBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SublistActivity.this.finish();
            }
        });

        titleText = (TextView)toolbar.findViewById(R.id.sublistTitle);
        titleText.setText(getIntent().getStringExtra("name"));

        gridView = (GridView) findViewById(R.id.childListGrid);
        mHandler = new MyHandler();

        initData(Integer.valueOf(getIntent().getStringExtra("id")));
        String[] from={"img","text","cnt"};
        int[] to={R.id.gridImage,R.id.gridText,R.id.gridCnt};
        adapter=new SimpleAdapter(this, subDataList, R.layout.cat_list_item, from, to);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;

            }
        });

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Log.i("rx","Click："+ subDataList.get(arg2).get("text").toString());

                //AudioSrc.getInstance().getSubCategoryList(mHandler,(int)subDataList.get(arg2).get("id"));

                Intent intent = new Intent();
                intent.putExtra("id",String.valueOf(subDataList.get(arg2).get("id")));
                intent.putExtra("name",String.valueOf(subDataList.get(arg2).get("text")));
                intent.setClass(SublistActivity.this, AudioListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initData(int catId){
        Log.i("rx","Cat id:"+catId);
        subDataList = new ArrayList<Map<String, Object>>();
        AudioSrc.getInstance().getSubCategoryList(catId, new HttpGetListen() {
            @Override
            public void OnCompletionListener(List list) {
                Message msg = new Message();
                msg.obj = list;
                mHandler.sendMessage(msg);
            }

            @Override
            public void OnStartListener(List list) {

            }
        });


//        }
    }
}
