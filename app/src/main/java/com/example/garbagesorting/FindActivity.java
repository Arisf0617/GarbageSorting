package com.example.garbagesorting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbagesorting.dao.FindDao;
import com.example.garbagesorting.dao.collectionDao;
import com.example.garbagesorting.dao.followDao;
import com.example.garbagesorting.model.Collect;
import com.example.garbagesorting.model.Follow;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.BitmapUtils;

public class FindActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView btn_back;
    private ImageView iv_touxiang;
    private TextView tv_name;
    private find edititem;
    private ImageView iv_garbage;
    private TextView tv_text;
    private LinearLayout line_follow;
    private TextView tv_follow;
    private TextView tv_collect;
    private ImageView iv_collect;
    private LinearLayout line_collect;
    private followDao follow=new followDao();
    private collectionDao collect=new collectionDao();
    private static int flag=1;
    SharedPreferences sp=null;
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_find);
        initUI();
        initData();
    }
    private void initUI(){
        iv_touxiang=findViewById(R.id.iv_touxiang);
        btn_back=findViewById(R.id.btn_back);
        tv_name=findViewById(R.id.tv_name);
        iv_garbage=findViewById(R.id.iv_garbage);
        tv_text=findViewById(R.id.tv_text);
        line_follow=findViewById(R.id.line_follow);
        tv_follow=findViewById(R.id.tv_follow);
        iv_collect=findViewById(R.id.iv_collect);
        tv_collect=findViewById(R.id.tv_collect);
        line_collect=findViewById(R.id.line_collect);
    }
    private void initData(){
        btn_back.setOnClickListener(this);
        line_follow.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        line_collect.setOnClickListener(this);
        sp=getSharedPreferences("User",MODE_PRIVATE);
        edititem=(find)getIntent().getSerializableExtra("edititem");
        String bb = edititem.getPhone().substring(0, 3) + "****" + edititem.getPhone().substring(7, 11);
        tv_name.setText(bb);
        tv_text.setText(edititem.getText());

        final String encodedString = "data:image/png;base64,";
        //放图片
        String pureBase64Encoded = edititem.getRecyclable_garbage().replace(encodedString, "");
        Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
        System.out.println(pic);
        Glide.with(FindActivity.this).load(pic).into(iv_garbage);
        //放头像
        String pureBase64Encodeds = edititem.getPic().replace(encodedString, "");
        Bitmap pics = BitmapUtils.base64ToBitmap(pureBase64Encodeds);
        System.out.println(pics);
        Glide.with(FindActivity.this).load(pics).apply(requestOptions).into(iv_touxiang);
        if(sp.getString("phone",null)==null||sp.getString("phone",null)=="") {
            tv_follow.setText("+关注");
            line_follow.setBackgroundResource(R.drawable.shurukuang3_botton);
            tv_follow.setTextColor(getResources().getColor(R.color.black));
        }
        else {

                    if(getIntent().getStringExtra("judge").equals("已关注")) {
                        line_follow.setBackgroundResource(R.drawable.bac_blue);
                        tv_follow.setText("已关注");
                        tv_follow.setTextColor(getResources().getColor(R.color.white));
                    }
                    else if(getIntent().getStringExtra("judge").equals("+关注")){
                        tv_follow.setText("+关注");
                        line_follow.setBackgroundResource(R.drawable.shurukuang3_botton);
                        tv_follow.setTextColor(getResources().getColor(R.color.black));
                    }
//            if(getIntent().getStringExtra("judge2").equals("已收藏")) {
//                iv_collect.setImageResource(R.drawable.collect1);
//                tv_collect.setText("已收藏");
//                tv_collect.setTextColor(getResources().getColor(R.color.collect));
//            }
//            else if(getIntent().getStringExtra("judge2").equals("取消收藏")){
//                iv_collect.setImageResource(R.drawable.collect);
//                tv_collect.setText("收藏");
//                tv_collect.setTextColor(getResources().getColor(R.color.collect_quxiao));
//            }
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();

        switch (id){
            case R.id.btn_back:finish();break;
            case R.id.line_follow:
                if(sp.getString("phone",null)==null||sp.getString("phone",null)=="") {
                    Toast.makeText(FindActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (getIntent().getStringExtra("judge").equals("+关注")) {
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            public void handleMessage(Message msg) {
                                //一些work线程不能处理的UI逻辑....例如 自动点击view

                                line_follow.setBackgroundResource(R.drawable.bac_blue);
                                tv_follow.setText("已关注");
                                tv_follow.setTextColor(getResources().getColor(R.color.white));
                                Toast.makeText(FindActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                                follow.followByPhone(sp.getString("phone", null), edititem.getPhone());

                            }
                        }).start();
                    } else if (getIntent().getStringExtra("judge").equals("已关注")) {
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            public void handleMessage(Message msg) {
                                //一些work线程不能处理的UI逻辑....例如 自动点击view
                                tv_follow.setText("+关注");
                                line_follow.setBackgroundResource(R.drawable.shurukuang3_botton);
                                tv_follow.setTextColor(getResources().getColor(R.color.black));
                                Toast.makeText(FindActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                                follow.deleteByPhone(sp.getString("phone", null), edititem.getPhone());
                            }
                        }).start();
                    }
                }
                break;
            case R.id.line_collect:
                if(sp.getString("phone",null)==null||sp.getString("phone",null)=="") {
                    Toast.makeText(FindActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (getIntent().getStringExtra("judge").equals("+关注")) {
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            public void handleMessage(Message msg) {
                                //一些work线程不能处理的UI逻辑....例如 自动点击view
                                iv_collect.setImageResource(R.drawable.collect1);
                                tv_collect.setText("已收藏");
                                tv_collect.setTextColor(getResources().getColor(R.color.collect));
                                Toast.makeText(FindActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                                Collect c=new Collect(sp.getString("phone",null),edititem.getPhone(),edititem.getTime());
                                collect.insert(c);
                            }
                        }).start();
                    } else if (getIntent().getStringExtra("judge").equals("已关注")) {
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            public void handleMessage(Message msg) {
                                //一些work线程不能处理的UI逻辑....例如 自动点击view
                                iv_collect.setImageResource(R.drawable.collect);
                                tv_collect.setText("收藏");
                                tv_collect.setTextColor(getResources().getColor(R.color.collect_quxiao));
                                Toast.makeText(FindActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                                collect.deleteByPhone(sp.getString("phone",null),edititem.getPhone(),edititem.getTime());
                            }
                        }).start();
                    }
                }

                break;
        }
    }
}