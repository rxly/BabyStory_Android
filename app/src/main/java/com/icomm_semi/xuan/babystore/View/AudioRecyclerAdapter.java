package com.icomm_semi.xuan.babystore.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icomm_semi.xuan.babystore.MqttControler;
import com.icomm_semi.xuan.babystore.R;

import java.util.ArrayList;
import java.util.List;

public class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder> {
    private List<AudioItem> list = null;
    private Context context;

    public AudioRecyclerAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.audio_item,parent,false);
        AudioViewHolder holder = new AudioViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, final int position) {
        holder.nameText.setText(list.get(position).name);
        holder.content.setText(list.get(position).content);
        holder.icon.setImageBitmap(list.get(position).icon);

        holder.dowloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MqttControler.getInstance().downloadFile(list.get(position));
//                Snackbar.make(view,"开始下载..."+list.get(position).name+"   "+list.get(position).play_url,Snackbar.LENGTH_LONG).show();
            }
        });

    }

    public void setList(List<AudioItem> list) {
        this.list = list;
        notifyItemRangeChanged(0,20);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<AudioItem> getList() {
        return list;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView nameText = null;
        TextView content = null;
        ImageView icon = null;
        ImageView dowloadIcon = null;
        ImageView likeIcon = null;

        private MqttControler mMqttCtrl;


        public AudioViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.audioName);
            content = itemView.findViewById(R.id.audioContent);

            icon=itemView.findViewById(R.id.audioIcon);
            dowloadIcon = itemView.findViewById(R.id.audioDownload);

            likeIcon = itemView.findViewById(R.id.audioLike);
            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeIcon.setImageResource(R.mipmap.like_fill);
                    Snackbar.make(v,"已经添加到收藏列表",Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
