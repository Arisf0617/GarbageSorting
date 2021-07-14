package com.example.garbagesorting.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.garbagesorting.AddActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.adapter.findAdapter;
import com.example.garbagesorting.dao.FindDao;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.BitmapUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FindFragment extends Fragment {
    private RecyclerView rec_find;
    private View root;
    private ArrayList<find> list=new ArrayList<>();
    private findAdapter adapter;
    private FindDao findDao=new FindDao();
    public static final int ADD = 100;
    SharedPreferences sp=null;
    private LocalBroadcastManager broadcastManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.fragment_find, container, false);

        initUI();
        initData();
        new UpdateDateThd(updateDateHandler).start();
        return root;
    }
    private void initUI(){
        rec_find=root.findViewById(R.id.rec_find);
        sp=getContext().getSharedPreferences("User", getContext().MODE_PRIVATE);
    }
    private void initData(){
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if(sp.getString("phone",null)!=null){
                    intent.setClass(getActivity(), AddActivity.class);
                    getActivity().startActivityForResult(intent, ADD);
                }
                else {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler updateDateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取日期,显示
            Bundle bundle = msg.getData();
            ArrayList date = (ArrayList) bundle.getSerializable("value");
            //dateView.setText(date);
            rec_find.setLayoutManager (new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
            adapter=new findAdapter(getContext(),date);
            rec_find.setAdapter(adapter);
        }
    };

    //发送消息给UI线程的子线程.
    class UpdateDateThd extends Thread{
        private Handler handler;
        private boolean bool=true;

        public UpdateDateThd(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run () {

            while (bool) {
//                try {
                    //每秒向主线程发送一次消息
//                    TimeUnit.MILLISECONDS.sleep(10000);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String date = sdf.format(new Date());

                try {
                    list=findDao.queryAll();
                    sendMessage(list);
                    TimeUnit.MILLISECONDS.sleep(10000);
                    bool=false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bool=false;
            }
        }

        private void sendMessage(ArrayList msg){
            Bundle data = new Bundle();
            data.putSerializable("value",list);
            Message message = new Message();
            message.setData(data);

            this.handler.sendMessage(message);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照后返回
        if (resultCode==8) {
            //显示图片
            System.out.println("秋明");
            find fd=(find) data.getSerializableExtra("find");
            System.out.println(fd.getPhone());
            rec_find.setLayoutManager (new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
            adapter.notifyItemInserted(0);
            list.add(0,fd);
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            rec_find.scrollToPosition(0);
            return;
        }

    }
}