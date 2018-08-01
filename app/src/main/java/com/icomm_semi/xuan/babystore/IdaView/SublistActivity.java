package com.icomm_semi.xuan.babystore.IdaView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.icomm_semi.xuan.babystore.HttpControl.AudioSrc;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SublistActivity extends AppCompatActivity {
    private GridView gridView;
    ArrayList<Map<String, Object>> subDataList;
    private ArrayList<CategoryItem> subCatList = null;
    SimpleAdapter adapter;
    MyHandler mHandler = null;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.i("rx","refresh grid view");
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
                intent.setClass(SublistActivity.this, AudioListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initData(int catId){
        Log.i("rx","Cat id:"+catId);
        AudioSrc.getInstance().getSubCategoryList(mHandler,catId);
        subDataList = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < catList.size(); i++) {
//            Map<String, Object> map=new HashMap<>();
//            map.put("img", catList.get(i).icon);
//            map.put("text",catList.get(i).name);
//            dataList.add(map);
//        }
    }
}
