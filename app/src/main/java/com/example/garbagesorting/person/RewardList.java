package com.example.garbagesorting.person;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.rewardAdapter;
import com.example.garbagesorting.model.Reward;
import com.example.garbagesorting.dao.collectionDao;
import com.example.garbagesorting.dao.rewardDao;

import java.util.ArrayList;
import java.util.List;

public class RewardList extends AppCompatActivity {
    ListView listView;
    List<Reward> rewards;
    private rewardAdapter adapter;
    SharedPreferences sp=null;
    Handler h = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.person_list_reward);
        listView = findViewById(R.id.reward_list_list);
        rewards = new ArrayList<>();
        adapter = new rewardAdapter(this, rewards);
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
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                String phone=sp.getString("phone",null);
                List<Reward> list = new ArrayList<>();
                super.run();
                Looper.prepare();
                try {
                    if (!phone.equals("")) {
                        System.out.println("RewardList");
                        list = rewardDao.getInfoByPhone(phone);//查询结果放到list<Garbage>中
                        rewards.clear();
                        rewards.addAll(list);
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


