package com.icomm_semi.xuan.babystore.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.icomm_semi.xuan.babystore.HttpControl.AudioSrc;
import com.icomm_semi.xuan.babystore.IdaView.CategoryItem;
import com.icomm_semi.xuan.babystore.IdaView.SublistActivity;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioViewFragment extends Fragment {
    private ArrayList<CategoryItem> catList = null;
    private GridView gridView;
    ArrayList<Map<String, Object>> dataList;
    SimpleAdapter adapter;
    MyHandler mHandler = null;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.i("rx","refresh grid view");
            catList = (ArrayList<CategoryItem>) msg.obj;
            for (int i = 0; i < catList.size(); i++) {
                Map<String, Object> map=new HashMap<>();
                map.put("img", catList.get(i).icon);
                map.put("text",catList.get(i).name);
                map.put("id",catList.get(i).id);
                dataList.add(map);
            }
            adapter.notifyDataSetChanged();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.audio_fragment_layout,container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (GridView)view.findViewById(R.id.gridView);
        mHandler = new MyHandler();

        initData();
        String[] from={"img","text"};
        int[] to={R.id.gridImage,R.id.gridText};
        adapter=new SimpleAdapter(this.getContext(), dataList, R.layout.sub_list_item, from, to);
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
                Log.i("rx","Click："+ dataList.get(arg2).get("text").toString()+"  ID:"+dataList.get(arg2).get("id"));
                Intent intent = new Intent();
                intent.putExtra("id",String.valueOf(dataList.get(arg2).get("id")));
                intent.setClass(getContext(),SublistActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData(){
        AudioSrc.getInstance().getCategoryList(mHandler);
        dataList = new ArrayList<Map<String, Object>>();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("rx","On start");
    }

    @Override
    public void onResume() {
        if(catList != null && catList.size() > 0) {
            Log.i("rx","catlist has member");
            adapter.notifyDataSetChanged();
        }
        Log.i("rx","On resume");
        super.onResume();

    }

    @Override
    public void onPause() {
        Log.i("rx","On onPause");
        super.onPause();
    }
}
