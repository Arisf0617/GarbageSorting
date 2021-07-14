package com.example.garbagesorting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagesorting.adapter.garAdapter;
import com.example.garbagesorting.dao.garbageDao;
import com.example.garbagesorting.bean.Garbage;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果*/
public class SearchList extends AppCompatActivity{
    private SharedPreferences sharedPreferences;
    ListView listView;
    List<Garbage> garbage;
    private EditText et_search;
    private ImageView iv_search;
    private garAdapter adapter;
    Handler h = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_search_list);
        listView = findViewById(R.id.search_list_list);
        et_search = findViewById(R.id.et_search);
        iv_search = findViewById(R.id.iv_search);
        garbage = new ArrayList<>();
        adapter = new garAdapter(this, garbage);
        listView.setAdapter(adapter);

        if(sharedPreferences==null){
            sharedPreferences=getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        h = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                // call update gui method.
                adapter.notifyDataSetChanged();
            }
        };

        Intent intent = getIntent();
        // 通过key得到值 值为字符串类型
        String name = intent.getStringExtra("name");
        et_search.setText(name);
        initData(name);
        initUiti();
    }

    private void initUiti() {
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_search.getText().toString(); //获取搜索内容
                System.out.println(name);
                initData(name);
            }
        });
    }

    private void initData(String name) {

        new Thread(){
            @Override
            public void run() {
                List<Garbage> list = new ArrayList<>();
                super.run();
                Looper.prepare();
                try {
                    garbageDao gardao = new garbageDao();
                    if (!name.equals("")) {
                        System.out.println("SearchList");
                        list = gardao.getInfoByName(name);//查询结果放到list<Garbage>中
                        garbage.clear();
                        garbage.addAll(list);
                        for(Garbage s:garbage)
                            System.out.println(s.getCategory()+name+"]");
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
