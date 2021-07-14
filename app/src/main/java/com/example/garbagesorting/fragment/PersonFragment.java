package com.example.garbagesorting.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbagesorting.adapter.rewardAdapter;
import com.example.garbagesorting.dao.garbageDao;
import com.example.garbagesorting.dao.rewardDao;
import com.example.garbagesorting.model.Reward;
import com.example.garbagesorting.person.CollectionList;
import com.example.garbagesorting.person.FollowList;
import com.example.garbagesorting.person.IntegralList;
import com.example.garbagesorting.person.RewardList;
import com.example.garbagesorting.view.CirclePhotoView;
import com.example.garbagesorting.LoginActivity;
import com.example.garbagesorting.R;
import com.example.garbagesorting.SelectPicPopupWindow;
import com.example.garbagesorting.dao.UserDao;
import com.example.garbagesorting.utils.BitmapUtils;
import com.example.garbagesorting.utils.CameraUtils;
import com.example.garbagesorting.utils.LQRPhotoSelectUtils;
import com.example.garbagesorting.utils.SPUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.annotations.NonNull;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PersonFragment extends Fragment implements View.OnClickListener{

    /**
     * 外部存储权限请求码
     */
    public static final int REQUEST_EXTERNAL_STORAGE_CODE = 9527;
    /**
     * 打开相册请求码
     */
    private static final int OPEN_ALBUM_CODE = 100;
    /**
     * 图片剪裁请求码
     */
    public static final int PICTURE_CROPPING_CODE = 200;

    //权限请求
    private RxPermissions rxPermissions;

    //是否拥有权限
    private boolean hasPermissions = false;

    //底部弹窗
    private BottomSheetDialog bottomSheetDialog;
    //弹窗视图
    private View bottomView;
    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;
    //注册
    public static final int LOGIN= 3;
    //Base64
    private String base64Pic;
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;

    private UserDao userDao=new UserDao();
    private garbageDao garbageDao=new garbageDao();
    private TextView tv_login;
    private ImageView iv_login;
    private TextView bt_integral;
    private TextView bt_reward;
    private TextView bt_collection;
    private TextView bt_follow;
    private ListView lv_reward;
    List<Reward> rewards;
    private rewardAdapter adapter;
    Handler h = null;
    private LinearLayout line_login;
    private SelectPicPopupWindow menuWindow;
    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    private View root;
    SharedPreferences sp=null;
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
            .skipMemoryCache(true);//不做内存缓存
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.fragment_person, container, false);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        intUI();
        intData();
        //checkVersion();
        //取出缓存
        requestPermission();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData1();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        initData1();
    }

    private void initData1(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                try {
                    String phone=sp.getString("phone",null);
                    if(phone!=null){
                        garbageDao gardao = new garbageDao();
                        System.out.println("PersonFragment");
                        String integral = gardao.getNumByPhone(phone,"integral");
                        String reward = gardao.getNumByPhone(phone,"reward");
                        String collection = gardao.getNumByPhone(phone,"collection");
                        String follow = gardao.getNumByPhone(phone,"follow");
                        bt_integral.setText(integral+"\n积分");
                        bt_reward.setText(reward+"\n奖励");
                        bt_collection.setText(collection+"\n收藏");
                        bt_follow.setText(follow+"\n关注");
                    }
                }catch (Exception e) {
                    Log.e("error",e.toString());
                }
                Looper.loop();
            }
        }.start();
    }
    private void intUI(){
        tv_login=root.findViewById(R.id.tv_login);
        iv_login=root.findViewById(R.id.iv_login);
        line_login=root.findViewById(R.id.line_login);
        bt_integral=root.findViewById(R.id.bt_integral);
        bt_reward=root.findViewById(R.id.bt_reward);
        bt_collection=root.findViewById(R.id.bt_collection);
        bt_follow=root.findViewById(R.id.bt_follow);
        lv_reward=root.findViewById(R.id.list_reward);
        sp = getContext().getSharedPreferences("User", getContext().MODE_PRIVATE);
        rewards = new ArrayList<>();
        adapter = new rewardAdapter(getActivity(), rewards);
        lv_reward.setAdapter(adapter);
        h = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                // call update gui method.
                adapter.notifyDataSetChanged();
            }
        };
    }
    private void intData(){
        tv_login.setOnClickListener(this);
        //iv_login.setOnClickListener(this);
        iv_login.setOnClickListener(this);
        bt_integral.setOnClickListener(this);
        bt_reward.setOnClickListener(this);
        bt_collection.setOnClickListener(this);
        bt_follow.setOnClickListener(this);
//        SharedPreferences.Editor editor=sp.edit();
//        editor.clear();
//        editor.commit();
        System.out.println(sp.getAll());
        //  sp=null;
        if(sp.getString("phone",null)!=null){
            System.out.println("asdsadsaasd");
            String aa =sp.getString("phone",null);
            String icon=userDao.getIcon(aa);
            System.out.println("是这个吗"+icon);
            if(icon==null){
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.smssdk_failure_bg);
                Drawable drawable = new CirclePhotoView(bitmap);
                iv_login.setImageDrawable(drawable);
            }
            else {
                final String encodedString = "data:image/png;base64,";
                String pureBase64Encoded = icon.replace(encodedString, "");
                Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
                System.out.println(pic);
                Glide.with(getActivity()).load(pic).apply(requestOptions).into(iv_login);
            }
            String bb = aa.substring(0, 3) + "****" + aa.substring(7, 11);
            tv_login.setText(bb);
        }
        else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_none);
            Drawable drawable = new CirclePhotoView(bitmap);
            iv_login.setImageDrawable(drawable);
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                try {
                    String phone=sp.getString("phone",null);
                    if(phone!=null){
                        garbageDao gardao = new garbageDao();
                        System.out.println("PersonFragment");
                        String integral = gardao.getNumByPhone(phone,"integral");
                        String reward = gardao.getNumByPhone(phone,"reward");
                        String collection = gardao.getNumByPhone(phone,"collection");
                        String follow = gardao.getNumByPhone(phone,"follow");
                        bt_integral.setText(integral+"\n积分");
                        bt_reward.setText(reward+"\n奖励");
                        bt_collection.setText(collection+"\n收藏");
                        bt_follow.setText(follow+"\n关注");
                    }
                    System.out.println("RewardList");
                    List<Reward> list = rewardDao.getAllReward();//查询结果放到list<Garbage>中
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
                }catch (Exception e) {
                    Log.e("error",e.toString());
                }
                Looper.loop();
            }
        }.start();
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent=new Intent();
        switch (id){
            case R.id.tv_login:
                if(sp.getString("phone",null)!=null){
                    return;
                }
                intent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(intent,LOGIN);
                break;
            case R.id.iv_login:
                if(sp.getString("phone",null)==null){
                    return;
                }
                changeAvatar();
                break;
            case R.id.bt_integral:
                intent.setClass(getActivity(), IntegralList.class);
                startActivity(intent);
                break;
            case R.id.bt_reward:
                intent.setClass(getActivity(), RewardList.class);
                startActivity(intent);
                break;
            case R.id.bt_collection:
                intent.setClass(getActivity(), CollectionList.class);
                startActivity(intent);
                break;
            case R.id.bt_follow:
                intent.setClass(getActivity(), FollowList.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 更换头像
     *
     * @param
     */
    public void changeAvatar() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        TextView tvTakePictures = bottomView.findViewById(R.id.tv_take_pictures);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //拍照
        tvTakePictures.setOnClickListener(v -> {
            takePhoto();
            showMsg("拍照");
            bottomSheetDialog.cancel();
        });
        //打开相册
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            showMsg("打开相册");
            bottomSheetDialog.cancel();
        });
        //取消
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        //底部弹窗显示
        bottomSheetDialog.show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        outputImagePath = new File(getContext().getExternalCacheDir(),
                filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(getContext(), outputImagePath);
        // 开启一个带有返回值的Activity，请求码为TAKE_PHOTO
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
    }

    /**
     * 打开相册
     */
    /**
     * 打开相册
     */
    public void openAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_ALBUM_CODE);
    }

    /**
     * 通过图片路径显示图片
     */
    private void displayImage(String imagePath) {
        if(sp!=null) {
            if (!TextUtils.isEmpty(imagePath)) {

                //放入缓存
                SPUtils.putString("imageUrl", imagePath, getContext());

                //显示图片
                Glide.with(this).load(imagePath).apply(requestOptions).into(iv_login);

                //压缩图片
                orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
                //转Base64
                base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (userDao.updateIcon(base64Pic,sp.getString("phone",null) )) {
//                            System.out.println("123456");
//                        }
//                    }
//                }).start();
            } else {
                showMsg("图片获取失败");
            }
        }
    }
    /**
     * Toast提示
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
    /**
     * 权限请求
     */
    @AfterPermissionGranted(REQUEST_EXTERNAL_STORAGE_CODE)
    private void requestPermission() {
        String[] param = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), param)) {
            //已有权限
            showMsg("已获得权限");
        } else {
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions(this, "请求权限", REQUEST_EXTERNAL_STORAGE_CODE, param);
        }
    }
    /**
     * 图片剪裁
     *
     * @param uri 图片uri
     */
    private void pictureCropping(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        // 返回裁剪后的数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICTURE_CROPPING_CODE);
    }
    /**
     * 权限请求结果
     *
     * @param requestCode  请求码
     * @param permissions  请求权限
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    /**
     * 返回Activity结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照后返回
        if (requestCode == TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            //显示图片
            displayImage(outputImagePath.getAbsolutePath());
        }
        //打开相册后返回
        //显示图片
        else if (requestCode == OPEN_ALBUM_CODE && resultCode == getActivity().RESULT_OK) {
            //打开相册返回
            final Uri imageUri = Objects.requireNonNull(data).getData();
            System.out.println(imageUri + "45123");
            //图片剪裁
            pictureCropping(imageUri);
        } else if (requestCode == PICTURE_CROPPING_CODE && resultCode == getActivity().RESULT_OK) {
            //图片剪裁返回
            Bundle bundle = data.getExtras();
            System.out.println(bundle);
            if (bundle != null) {
                //System.out.println("123456");
                //在这里获得了剪裁后的Bitmap对象，可以用于上传
                Bitmap image = bundle.getParcelable("data");
                //设置到ImageView上
                System.out.println(image);
                //Glide.with(this).load(image).apply(requestOptions).into(iv_login);
                Glide.with(this).load(image).apply(requestOptions).into(iv_login);
                //iv_login.setImageBitmap(image);
                base64Pic = BitmapUtils.bitmapToBase64(image);
                System.out.println(base64Pic + "太长");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (userDao.updateIcon(base64Pic, sp.getString("phone", null))) {
                            System.out.println("123456");
                        }
                    }
                }).start();
            } else {
                showMsg("图片获取失败");
            }
        } else if (resultCode == 3) {
            //设置结果显示框的显示数值
            iv_login.setClickable(true);
            String icon=userDao.getIcon(data.getStringExtra("phone"));
            System.out.println("是这个吗"+icon);
            if(icon==null){
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.smssdk_failure_bg);
                Drawable drawable = new CirclePhotoView(bitmap);
                iv_login.setImageDrawable(drawable);
            }
            else {
                final String encodedString = "data:image/png;base64,";
                String pureBase64Encoded = icon.replace(encodedString, "");
                Bitmap pic = BitmapUtils.base64ToBitmap(pureBase64Encoded);
                System.out.println(pic);
                Glide.with(getActivity()).load(pic).apply(requestOptions).into(iv_login);
            }
            String bb = data.getStringExtra("phone").substring(0, 3) + "****" + data.getStringExtra("phone").substring(7, 11);
            tv_login.setText(bb);
        }
        else if(resultCode==4){
            iv_login.setClickable(false);
            tv_login.setClickable(true);
            tv_login.setText("登录/注册");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_none);
            Drawable drawable = new CirclePhotoView(bitmap);
            iv_login.setImageDrawable(drawable);
        }
    }
}

