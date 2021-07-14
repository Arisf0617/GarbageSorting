package com.example.garbagesorting.adapter;

import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.garbagesorting.FindActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.dao.collectionDao;
import com.example.garbagesorting.dao.followDao;
import com.example.garbagesorting.model.Collect;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.BitmapUtils;

import java.util.List;


/**
 * 垃圾搜索结果页面适配器*/

public class collectionAdapter extends BaseAdapter {
    Context context;
    List<find> mData;
    LayoutInflater inflater;
    followDao follow = new followDao();
    collectionDao collect=new collectionDao();
    SharedPreferences sp=null;
    public collectionAdapter (Context context,List<find> mdata){
        this.context=context;
        this.mData=mdata;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_collection,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        find f=mData.get(position);
        holder.phoneTV.setText("");
        holder.collectionphoneTV.setText(f.getPhone());
        holder.textTV.setText(f.getText());
        holder.dateTV.setText(f.getTime());
        final String encodedString = "data:image/png;base64,";
        if(f.getRecyclable_garbage()!=null) {
            String pureBase64Encoded = f.getRecyclable_garbage().replace(encodedString, "");
            Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
            System.out.println(pic);
            Glide.with(context).load(pic).into(holder.things_IV);
        }
        holder.line_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(context, FindActivity.class);
                intent.putExtra("edititem",f);
                Handler handler = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message msg) {
                        //一些work线程不能处理的UI逻辑....例如 自动点击view
                        System.out.println("msg"+msg.what);
                        if((boolean)msg.obj==true) {
                            intent.putExtra("judge","已关注");
                        }
                        else if((boolean)msg.obj==false){
                            intent.putExtra("judge","+关注");
                        }

                        if(msg.what == 2){
                            intent.putExtra("judge2","已收藏");
                        }
                        else if(msg.what == 3){
                            intent.putExtra("judge2","取消收藏");
                        }
                        context.startActivity(intent);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                       msg.obj=follow.getFollowByPhone(sp.getString("phone",null),f.getPhone());
                        if(collect.getCollectionByPhone(sp.getString("phone",null),f.getPhone(),f.getTime())){
                            msg.what= 2;
                        }
                        else{
                            msg.what = 3;
                        }
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        return convertView;
    }
    class ViewHolder{
        ImageView things_IV;
        TextView phoneTV,collectionphoneTV,textTV,dateTV;
        LinearLayout line_list;
        LinearLayout line_collect;
        private TextView tv_collect;
        private ImageView iv_collect;
        public ViewHolder(View view){
            sp=context.getSharedPreferences("User",context.MODE_PRIVATE);
            things_IV=view.findViewById(R.id.iv_things);
            phoneTV=view.findViewById(R.id.item_phone);
            phoneTV.setVisibility(View.GONE);
            collectionphoneTV=view.findViewById(R.id.item_collectionphone);
            collectionphoneTV.setVisibility(View.GONE);
            textTV=view.findViewById(R.id.item_text);
            dateTV=view.findViewById(R.id.item_date);
            line_list=view.findViewById(R.id.line_list);
            line_collect=view.findViewById(R.id.line_collect);
            iv_collect=view.findViewById(R.id.iv_collect);
            tv_collect=view.findViewById(R.id.tv_collect);
        }
    }
}
