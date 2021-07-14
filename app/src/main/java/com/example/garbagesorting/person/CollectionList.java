package com.example.garbagesorting.person;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.garbagesorting.adapter.collectionAdapter;
import com.example.garbagesorting.dao.collectionDao;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.clusterutil.MarkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionList extends AppCompatActivity {
    ListView listView;
    List<find> finds;
    private collectionAdapter adapter;
    SharedPreferences sp=null;
    Handler h = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.person_list_collection);
        listView = findViewById(R.id.collection_list_list);
        finds = new ArrayList<>();
        adapter = new collectionAdapter(this, finds);
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
                find clickAccount = finds.get(position);
                System.out.println(clickAccount.getText());
                showDeleteItem(clickAccount);
            }
        });
    }

    private void showDeleteItem(final find clickAccount) {
        AlertDialog alertDialog = new AlertDialog.Builder(CollectionList.this)
                .setTitle("再次确认")
                .setMessage("是否取消收藏")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone=sp.getString("phone",null);
                        String collectionphone=clickAccount.getPhone();
                        String date=clickAccount.getTime();
                        //Toast.makeText(CollectionList.this, collectionphone+"  "+phone+"  "+date, Toast.LENGTH_SHORT).show();
                        collectionDao collectdao = new collectionDao();
                        String a=collectdao.deleteByPhone(phone,collectionphone,date);
                        Toast.makeText(CollectionList.this, a, Toast.LENGTH_SHORT).show();
                        finds.remove(clickAccount);
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
                List<find> list = new ArrayList<>();
                super.run();
                Looper.prepare();
                try {
                    collectionDao collectdao = new collectionDao();
                    if (!phone.equals("")) {
                        System.out.println("CollectionList");
                        list = collectdao.getInfoByPhone(phone);//查询结果放到list<Garbage>中
                        finds.clear();
                        finds.addAll(list);
                        //Toast.makeText(CollectionList.this, list.size()+"hhhh", Toast.LENGTH_SHORT).show();
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


