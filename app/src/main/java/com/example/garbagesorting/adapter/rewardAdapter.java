package com.example.garbagesorting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbagesorting.R;
import com.example.garbagesorting.model.Reward;

import java.util.List;

public class rewardAdapter extends BaseAdapter {
    Context context;
    List<Reward> mData;
    LayoutInflater inflater;

    public rewardAdapter (Context context,List<Reward> mdata){
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
            convertView=inflater.inflate(R.layout.list_item_reward,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Reward bean=mData.get(position);
        switch (bean.getReward()){
            case "香皂":
                holder.headIV.setImageResource(R.drawable.gift_soap);
                break;
            case "湿巾":
                holder.headIV.setImageResource(R.drawable.gift_wet);
                break;
            case "卫生纸":
                holder.headIV.setImageResource(R.drawable.gift_paper);
                break;
            case "洗手液":
                holder.headIV.setImageResource(R.drawable.gift_liquit_soap);
                break;
        }
        holder.rewardTV.setText(bean.getReward());
        holder.dateTV.setText(bean.getDate());
        holder.integralTV.setText(bean.getIntegral());
        return convertView;
    }
    class ViewHolder{
        ImageView headIV;
        TextView idTV,rewardTV,dateTV,integralTV;
        public ViewHolder(View view){
            headIV=view.findViewById(R.id.iv_head);
            idTV=view.findViewById(R.id.item_id);
            idTV.setVisibility(View.GONE);
            rewardTV=view.findViewById(R.id.item_reward);
            dateTV=view.findViewById(R.id.item_date);
            integralTV=view.findViewById(R.id.item_integral);
        }
    }
}
