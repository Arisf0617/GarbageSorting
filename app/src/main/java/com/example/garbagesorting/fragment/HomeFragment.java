package com.example.garbagesorting.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.garbagesorting.R;
import com.example.garbagesorting.SearchList;
import com.example.garbagesorting.SelectPicPopupWindow;
import com.example.garbagesorting.utils.LQRPhotoSelectUtils;
import com.example.garbagesorting.utils.ToastUtil;
import com.example.garbagesorting.BaiduAdvancedGeneral.*;
import com.example.garbagesorting.view.LoadingView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import kr.co.namee.permissiongen.PermissionGen;

import java.io.File;
import java.util.ArrayList;


/**
 * 主页面
 * 添加了拍照和图片选择功能,有bug——by haoyu 2021/4/19 21:48 add
 * 修复了拍照和图片选择功能，完善代码注释——by yuxuan，haoyu 2021/4/20 17:12 update
 * 增加了python调用识别函数，已配置好python环境(chaquopy已配置license)，记得更改build.gradle中的python路径——by haoyu 2021/4/20 22:00 update
 * 精简了代码量，注释掉未利用的方法，保留或注释掉某些输出语句，以便之后调试——by yuxuan,haoyu 2021/4/20 22:25 update
 * 添加了查询方法——by     2021/4/24 22：00 add
 * 添加了语音识别功能，使用讯飞的SDK——by haoyu 2021/4/25 16:48 add
 *      注：assets/iflytek为讯飞UI图片资源
 * 修改为动态获取权限 2021/4/25 17:15 update
 * 图像识别接口改为Java，删除python相关文件 2021/4/25 17:15 update&delete
 *
 */
/**
 * 目前存在的bug(2021/4/20 22:25)：
 * 在调用拍照或图库结束后，会卡顿，初步分析是由于识别的调用，可以尝试添加过渡动画（转圈动画）[未解决]
 * 只有从图库选择图片才能调用裁剪方法，拍照调用裁剪会闪退[未解决]
 * */
public class HomeFragment extends Fragment{
    private LinearLayout line_camera;
    private SelectPicPopupWindow menuWindow;
    private String filePath = null;
    private String fileUri = null;
    private File file;

    private Dialog progressDialog;

    //定义控件
    private EditText et_search;
    private ImageView iv_pic;
    private Button btn_hidden;//这是一个自动点击按钮
    private ImageView iv_search;
    private LinearLayout line_voice;

    private static final int REQUEST_CODE = 0x001;//请求码
    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //显示布局
        View root=inflater.inflate(R.layout.fragment_home, container, false);
        //绑定控件
        line_camera=root.findViewById(R.id.line_camera);
        et_search = root.findViewById(R.id.et_search);
        iv_pic = root.findViewById(R.id.iv_pic);
        btn_hidden = root.findViewById(R.id.btn_hidden);
        btn_hidden.setVisibility(View.GONE);
        iv_search = root.findViewById(R.id.iv_search);
        line_voice=root.findViewById(R.id.line_voice);

        progressDialog = new Dialog(getActivity(),R.style.progress_dialog);
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("加载中");

//        忽略android.os.NetworkOnMainThreadException异常(弃用)
//        if (Build.VERSION.SDK_INT >= 11) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
//        }

        btn_hidden.setOnClickListener(new View.OnClickListener() {//一个隐藏按钮，用来获取结果
            @Override
            public void onClick(View v) {
                Glide.with(getActivity()).load(fileUri).into(iv_pic);//使用Glide进行图片展示
                AdvancedGeneral();
                ToastUtil.showMsg(getActivity(), "正在识别...请稍后");
                //toSearch();


                //加载框出现
                //progressDialog.show();

                //加载框消失
                //progressDialog.dismiss();

                //startAnim();
                //isEmpty();

                //deleteDirectory("/storage/emulated/0/Android/data/com.fruitbasket.audioplatform");//删除和第二次图片展示不能同时存在
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_search.getText().toString(); //获取搜索内容
                    System.out.println(name);
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), SearchList.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
            }
        });

        //讯飞初始化创建语音配置对象
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(getContext(), SpeechConstant.APPID +"=eb7b8284");

        line_camera.setOnClickListener(new View.OnClickListener() {//相机监听：子菜单，拍照or从图库选择
            @Override
            public void onClick(View view) {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(HomeFragment.this.line_camera, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });

        line_voice.setOnClickListener(new View.OnClickListener() {//按钮监听：语音输入
            @Override
            public void onClick(View v) {
                initSpeech();
            }
        });

        init();
        return root;
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    //获取权限，调用拍照方法
                    PermissionGen.with(getActivity())
                            .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            ).request();
                    //拍照
                    mLqrPhotoSelectUtils.takePhoto();
                    break;
                case R.id.btn_pick_photo:
                    //选择图片
                    mLqrPhotoSelectUtils.selectPhoto();
                    break;
                default:
                    break;
            }
        }

    };


    private void init() {//初始化
        //创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(getActivity(), new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri){
                progressDialog.setCancelable(false);
                progressDialog.show();

                //当拍照或从图库选取图片成功后回调
//              mTvPath.setText(outputFile.getAbsolutePath());//显示图片路径
//              mTvUri.setText(outputUri.toString());//显示图片uri
                filePath = outputFile.getAbsolutePath();
                fileUri = (outputUri.toString());
                //Glide.with(getActivity()).load(fileUri).into(iv_pic);//使用Glide进行图片展示
                //Toast.makeText(getActivity(),"识别中，请稍后...",Toast.LENGTH_LONG);
                //String result = callPythonCode();
                //et_search.setText(result);
                //et_search.setText(callPythonCode());
                //et_search.setText(filePath);
                //System.out.println(outputUri.toString());
                //Glide.with(getActivity()).load(outputUri).into(iv_pic);//使用Glide进行图片展示
                btn_hidden.performClick();
                //btn_hidden.performClick();
                //et_search.setText(callPythonCode());
            }
        }, true);//是否裁剪。true裁剪，false不裁剪。 2021/4/20有bug，拍照不能裁剪，从图库选择可以
        //检查权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, REQUEST_CODE);
        }else{
            ToastUtil.showMsg(getActivity(), "已经获得了所有权限");
        }

//        final LoadingView loading_View = loading_view;
//        loading_View.start();
//        final LoadingView loading_View = loading_view;
//        loading_View.post(new Runnable() {
//            @Override
//            public void run() {
//                loading_View.start();
//            }
//        });



    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //initListener();
//                ToastUtil.showMsg(getActivity(), "您已授予权限");
//            } else {
//                ToastUtil.showMsg(getActivity(), "您已拒绝授予权限");
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }


    //启用子线程进行网络通讯
    public void AdvancedGeneral() {//图像识别
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                et_search.setText(AdvancedGeneral.advancedGeneral(filePath));//需要在子线程中处理的逻辑

                progressDialog.dismiss();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
//        Thread thread2 = getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                iv_search.performClick();
//            }
//        });
        thread1.start();
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            iv_search.performClick();
        }
    };


//    public void toSearch(){
//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                iv_search.performClick();
//            }
//        });
//
//    }




    public void initSpeech(){//初始化语音识别
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(getActivity(), null);
        //2.设置accent、language等参数，更多参数详见https://www.xfyun.cn/doc/asr/voicedictation/Android-SDK.html#_2%E3%80%81sdk%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    //返回的result为识别后的汉字,直接赋值到TextView上即可
                    String result = parseVoice(recognizerResult.getResultString());
                    et_search.setText(result);

                    //获取焦点
                    et_search.requestFocus();
                    //将光标定位在文字最后以便修改
                    et_search.setSelection(result.length());
                }
            }
            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);
        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    /**
     * 语音对象封装
     */
    public class Voice {
        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }


 /**
  * 根据路径删除指定的目录或文件，无论存在与否
  *@param sPath  要删除的目录或文件
  *@return 删除成功返回 true，否则返回 false。
  */
    public boolean DeleteFile(String sPath) {
        boolean flag = false;
        file = new File(sPath);
        // 判断目录或文件是否存在 
        if (!file.exists()) {// 不存在返回 false 
            return flag;
        } else {
            // 判断是否为文件 
            if (file.isFile()) {// 为文件时调用删除文件方法 
                return deleteFile(sPath);
            }
        }
        return false;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length-2; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
//        //删除当前目录
//        if (dirFile.delete()) {
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }



}