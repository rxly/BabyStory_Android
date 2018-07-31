package com.icomm_semi.xuan.babystore.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icomm_semi.xuan.babystore.Controler;
import com.icomm_semi.xuan.babystore.R;

public class DeviceFragment extends Fragment {
    private Controler mControl;
    private TextView conText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.device_fragment_layout,container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Toolbar devToolbar = (Toolbar) view.findViewById(R.id.devToolbar);
        devToolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(devToolbar);
        conText = (TextView)view.findViewById(R.id.conText);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("rx","Device fragment create Tollbar");
        menu.clear();
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void setConState(int id){
            conText.setText(id);
    }
    public void setConState(String content){
        conText.setText(content);
    }
}
