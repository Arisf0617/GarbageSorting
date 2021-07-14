package com.example.garbagesorting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.garbagesorting.R;
import com.example.garbagesorting.model.Follow;
import com.example.garbagesorting.utils.BitmapUtils;

import java.util.List;


/**
 * 垃圾搜索结果页面适配器*/

public class followAdapter extends BaseAdapter {
    Context context;
    List<Follow> mData;
    LayoutInflater inflater;

    public followAdapter (Context context,List<Follow> mdata){
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
            convertView=inflater.inflate(R.layout.list_item_follow,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Follow bean=mData.get(position);
        holder.phoneTV.setText(bean.getPhone());
        holder.followTV.setText(bean.getFollow().substring(0, 3) + "****" + bean.getFollow().substring(7, 11));
        final String encodedString = "data:image/png;base64,";
        if(bean.getIcon()!=null) {
            String pureBase64Encoded = bean.getIcon().replace(encodedString, "");
            Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
            System.out.println(pic);
            Glide.with(context).load(pic).into(holder.personIV);
        }
        return convertView;
    }
    class ViewHolder{
        ImageView personIV;
        TextView phoneTV,followTV;
        public ViewHolder(View view){
            personIV=view.findViewById(R.id.iv_head);
            phoneTV=view.findViewById(R.id.item_phone);
            phoneTV.setVisibility(View.GONE);
            followTV=view.findViewById(R.id.item_follow);
        }
    }
}
