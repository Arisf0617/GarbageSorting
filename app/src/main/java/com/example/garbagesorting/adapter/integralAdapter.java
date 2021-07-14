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
import com.example.garbagesorting.model.Integral;

import java.util.List;

public class integralAdapter extends BaseAdapter {
    Context context;
    List<Integral> mData;
    LayoutInflater inflater;

    public integralAdapter (Context context,List<Integral> mdata){
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
            convertView=inflater.inflate(R.layout.list_item_integral,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Integral bean=mData.get(position);
        holder.placeTV.setText(bean.getPlace());
        if(!bean.getIn_out().equals("+")) {
            holder.headIV.setImageResource(R.drawable.list_gift);
            holder.integralTV.setTextColor(R.color.list_black);
        }
        holder.integralTV.setText(bean.getIn_out()+bean.getIntegral());
        holder.dateTV.setText(bean.getDate());
        return convertView;
    }
    class ViewHolder{
        ImageView headIV;
        TextView idTV,placeTV,integralTV,dateTV;
        public ViewHolder(View view){
            headIV=view.findViewById(R.id.iv_head);
            idTV=view.findViewById(R.id.item_id);
            idTV.setVisibility(View.GONE);
            placeTV=view.findViewById(R.id.item_place);
            integralTV=view.findViewById(R.id.item_integral);
            dateTV=view.findViewById(R.id.item_date);
        }
    }
}
