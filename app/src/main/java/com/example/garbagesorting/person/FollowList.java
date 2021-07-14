package com.example.garbagesorting.person;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.followAdapter;
import com.example.garbagesorting.dao.collectionDao;
import com.example.garbagesorting.model.Follow;
import com.example.garbagesorting.dao.followDao;
import com.example.garbagesorting.model.find;

import java.util.ArrayList;
import java.util.List;

public class FollowList extends AppCompatActivity {
    ListView listView;
    List<Follow> follows;
    private followAdapter adapter;
    SharedPreferences sp=null;
    Handler h = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.person_list_follow);
        listView = findViewById(R.id.follow_list_list);
        follows = new ArrayList<>();
        adapter = new followAdapter(this, follows);
        listView.setAdapter(adapter);
        h = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                // call update gui method.
                adapter.notifyDataSetChanged();
            }
        };
        initUiti();
        initData();
    }

    private void initUiti() {
        if(sp==null){
            sp=getSharedPreferences("User", Context.MODE_PRIVATE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Follow clickAccount = follows.get(position);
                gotoItem(clickAccount);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Follow clickAccount = follows.get(position);
                showDeleteItem(clickAccount);
                return true;
            }
        });
    }

    private void gotoItem(Follow clickAccount) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", clickAccount.getFollow());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(FollowList.this, Visit.class);
        startActivity(intent);
    }

    private void showDeleteItem(final Follow clickAccount) {
        AlertDialog alertDialog = new AlertDialog.Builder(FollowList.this)
                .setTitle("再次确认")
                .setMessage("是否取消关注")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone=sp.getString("phone",null);
                        String followphone=clickAccount.getFollow();
                        //Toast.makeText(CollectionList.this, collectionphone+"  "+phone+"  "+date, Toast.LENGTH_SHORT).show();
                        followDao followdao = new followDao();
                        String a=followdao.deleteByPhone(phone,followphone);
                        Toast.makeText(FollowList.this, a, Toast.LENGTH_SHORT).show();
                        follows.remove(clickAccount);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).create();
        alertDialog.show();
    }


    private void initData() {
        new Thread(){
            @Override
            public void run() {
                String phone=sp.getString("phone",null);
                List<Follow> list;
                super.run();
                Looper.prepare();
                try {
                    followDao followDao = new followDao();
                    if (!phone.equals("")) {
                        System.out.println("FollowList");
                        list = followDao.getInfoByPhone(phone);//查询结果放到list<Garbage>中
                        follows.clear();
                        follows.addAll(list);
                        //Toast.makeText(FollowList.this, list.size()+"jjjj", Toast.LENGTH_SHORT).show();
                        //在线程中不能直接修改ui，调用runnable，通过post修改ui
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        };
                        h.post(runnable);
                    }
                }catch (Exception e) {
                    Log.e("error",e.toString());
                }
                Looper.loop();
            }
        }.start();
    }
}


