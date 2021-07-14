package com.example.garbagesorting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.garbagesorting.dao.FindDao;
import com.example.garbagesorting.dao.UserDao;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.BitmapUtils;
import com.example.garbagesorting.utils.CameraUtils;
import com.example.garbagesorting.utils.SPUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.annotations.NonNull;
import pub.devrel.easypermissions.EasyPermissions;

public class AddActivity extends AppCompatActivity {
    private ImageView iv_recyclable_garbage;
    private EditText et_text;
    private Button btn_add;
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
    //拍照和相册获取图片的Bitmap
    private Bitmap orc_bitmap;
    SharedPreferences sp=null;
    private FindDao findDao=new FindDao();
    private UserDao userDao=new UserDao();
    private ArrayList<find> list=new ArrayList<>();
    Date date=new Date();
    SimpleDateFormat simpleDateFormat  =new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_add);
        initUI();
        initData();
    }
    private void initUI(){
        iv_recyclable_garbage=findViewById(R.id.iv_recyclable_garbage);
        et_text=findViewById(R.id.et_text);
        btn_add=findViewById(R.id.btn_add);
        sp=getSharedPreferences("User",MODE_PRIVATE);
    }
    private void initData(){
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        iv_recyclable_garbage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=et_text.getText().toString();
                String time = simpleDateFormat.format(date);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("时间"+time);
                        find f=new find(sp.getString("phone", null),text,userDao.getIcon(sp.getString("phone", null)),base64Pic,time);
                        if (findDao.insert(f)) {
                            System.out.println(sp.getString("phone", null));
                        }
                        Intent intent1=new Intent();
                        intent1.putExtra("find",f);
                        setResult(8,intent1);
                        finish();
                    }
                }).start();
            }
        });
    }
    /**
     * 更换头像
     *
     * @param
     */
    public void changeAvatar() {
        bottomSheetDialog = new BottomSheetDialog(AddActivity.this);
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
        outputImagePath = new File(getExternalCacheDir(),
                filename + ".jpg");
        Intent takePhotoIntent = CameraUtils.getTakePhotoIntent(AddActivity.this, outputImagePath);
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
                SPUtils.putString("imageUrl", imagePath, AddActivity.this);

                //显示图片
                Glide.with(this).load(imagePath).into(iv_recyclable_garbage);

                //压缩图片
                orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
                //转Base64
                base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);
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
        Toast.makeText(AddActivity.this, msg, Toast.LENGTH_SHORT).show();
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

        if (Build.MANUFACTURER.equals("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
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
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            //显示图片
            displayImage(outputImagePath.getAbsolutePath());
        }
        //打开相册后返回
        //显示图片
        else if (requestCode == OPEN_ALBUM_CODE && resultCode == RESULT_OK) {
            //打开相册返回
            final Uri imageUri = Objects.requireNonNull(data).getData();
            System.out.println(imageUri + "45123");
            //图片剪裁
            pictureCropping(imageUri);
        } else if (requestCode == PICTURE_CROPPING_CODE && resultCode == RESULT_OK) {
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
                Glide.with(this).load(image).into(iv_recyclable_garbage);
                //iv_login.setImageBitmap(image);
                base64Pic = BitmapUtils.bitmapToBase64(image);
            } else {
                showMsg("图片获取失败");
            }
        }
    }
}