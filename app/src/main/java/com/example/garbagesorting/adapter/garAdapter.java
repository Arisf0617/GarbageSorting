package com.example.garbagesorting.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbagesorting.R;
import com.example.garbagesorting.bean.Garbage;

import java.util.ArrayList;
import java.util.List;


/**
 * 垃圾搜索结果页面适配器*/

public class garAdapter extends BaseAdapter {
    private String[] category_count=new String[]{"","可回收垃圾","有害垃圾","","湿垃圾","","","","干垃圾","","","","","","","","大件垃圾"};
    Context context;
    List<Garbage> mData;
    LayoutInflater inflater;

    public garAdapter (Context context,List<Garbage> mdata){
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
            convertView=inflater.inflate(R.layout.garbage_list_item,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Garbage bean=mData.get(position);
        holder.nameTV.setText(bean.getName());
//        holder.noteTV.setText(category_count[Integer.parseInt(bean.getCategory())]);
//        holder.garbageIV.setImageResource(R.drawable.food);
        String gar_temp=category_count[Integer.parseInt(bean.getCategory())];
        holder.noteTV.setText(gar_temp);
        if(gar_temp.equals("可回收垃圾")) holder.garbageIV.setImageResource(R.drawable.recyclable);
        else if(gar_temp.equals("有害垃圾")) holder.garbageIV.setImageResource(R.drawable.harm);
        else if(gar_temp.equals("湿垃圾")) holder.garbageIV.setImageResource(R.drawable.food);
        else if(gar_temp.equals("干垃圾")) holder.garbageIV.setImageResource(R.drawable.other);
        else holder.garbageIV.setImageResource(R.drawable.biggar);
        return convertView;
    }
    class ViewHolder{
        TextView idTV,nameTV,noteTV;
        ImageView garbageIV;
        public ViewHolder(View view){
            idTV=view.findViewById(R.id.item_id);
            idTV.setVisibility(View.GONE);
            nameTV=view.findViewById(R.id.item_name);
            noteTV=view.findViewById(R.id.item_note);
            garbageIV=view.findViewById(R.id.iv_garbage);
        }
    }
}
