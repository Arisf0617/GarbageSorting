package com.example.garbagesorting.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbagesorting.FindActivity;
import com.example.garbagesorting.MainActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.dao.followDao;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.BitmapUtils;

import java.util.List;

/**
 * 发现页面find适配器*/

public class findAdapter extends RecyclerView.Adapter<findAdapter.findAdapterViewHolder>{

    private Context mContext;
    private List<find> list;
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存
    SharedPreferences sp=null;
    followDao follow=new followDao();
    public findAdapter(Context context, List<find> list){
        this.mContext=context;
        this.list=list;
    }

    public void setList(List<find> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public findAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        findAdapterViewHolder holder=new findAdapterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.findmodel,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull findAdapterViewHolder holder, int position) {
        find f=list.get(position);
        holder.tv_name.setText(f.getPhone().substring(0, 3) + "****" + f.getPhone().substring(7, 11));
        holder.tv_text.setText(f.getText());
        final String encodedString = "data:image/png;base64,";
        if(f.getRecyclable_garbage()!=null) {
            String pureBase64Encoded = f.getRecyclable_garbage().replace(encodedString, "");
            Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
            System.out.println(pic);
            Glide.with(mContext).load(pic).into(holder.iv_find);
        }
        if(f.getPic()!=null) {
            String pureBase64Encoded = f.getPic().replace(encodedString, "");
            Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
            System.out.println(pic);
            Glide.with(mContext).load(pic).apply(requestOptions).into(holder.iv_touxiang);
        }
        holder.rela_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(mContext, FindActivity.class);
                intent.putExtra("edititem",f);
                Handler handler = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message msg) {
                        //一些work线程不能处理的UI逻辑....例如 自动点击view
                        if(msg.what==1) {
                          intent.putExtra("judge","已关注");
                        }
                        else if(msg.what==0){
                            intent.putExtra("judge","+关注");
                        }
                        mContext.startActivity(intent);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();

                        if(follow.getFollowByPhone(sp.getString("phone",null),f.getPhone())){
                            msg.what = 1;
                            System.out.println(f.getPhone()+"这是电话");
                        }
                        else{
                            msg.what = 0;
                        }
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }


    //绑定单元格空间
    class findAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_find;
        TextView tv_name;
        TextView tv_text;
        ImageView iv_touxiang;
        RelativeLayout rela_find;
        public findAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            sp=mContext.getSharedPreferences("User",mContext.MODE_PRIVATE);
            iv_find=itemView.findViewById(R.id.iv_find);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_text=itemView.findViewById(R.id.tv_text);
            iv_touxiang=itemView.findViewById(R.id.iv_touxiang);
            rela_find=itemView.findViewById(R.id.rela_find);
        }
    }

}