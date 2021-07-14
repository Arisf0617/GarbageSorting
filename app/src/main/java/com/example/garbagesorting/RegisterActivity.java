package com.example.garbagesorting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbagesorting.dao.UserDao;
import com.example.garbagesorting.model.User;
import com.example.garbagesorting.utils.CountDownTimerUtils;
import com.example.garbagesorting.view.WaveView;
import com.mob.MobSDK;
import com.mob.tools.utils.ResHelper;

import org.json.JSONObject;


import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * 注册*/
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Callback {
    private static final String TAG = "RegisterActivity";
    private LinearLayout line_phone;
    private LinearLayout line_validation;
    private LinearLayout line_new_password;
    private LinearLayout line_confirm_password;
    private LinearLayout line_agree;
    private TextView tv_agree;
    private EditText etPhone;
    private EditText etCode;
    private TextView tvCode;
    private Button btn_register;
    private String phonenumber;
    private EditText et_password;
    private EditText et_password_confirm;
    private CheckBox cb_agree;
    private String password;
    private TextView tvToast;
    private Toast toast;
    private static int flag=1;
    private UserDao userDao=new UserDao();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_register);
        flag=1;
        initViews();
        initData();
        initSDK();
        line_confirm_password.setVisibility(View.GONE);
        line_new_password.setVisibility(View.GONE);
    }
    private void  initData(){
        String info="《垃圾分类用户协议》";
        SpannableString sps=new SpannableString(info);
        //对字符串拆分并注册点击事件
        sps.setSpan(new ClickableSpan(){
            public void onClick(View view){
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }, 0, info.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_agree.setText(sps);
        tv_agree.setMovementMethod(LinkMovementMethod.getInstance());

//        final WaveView viewById = (WaveView) findViewById(R.id.view_speech_listen);
//        viewById.post(new Runnable() {
//            @Override
//            public void run() {
//                viewById.start();
//            }
//        });

    }
    //初始化控件
    private void initViews() {
        line_phone=findViewById(R.id.line_phone);
        line_validation=findViewById(R.id.line_validation);
        line_new_password=findViewById(R.id.line_new_password);
        line_confirm_password=findViewById(R.id.line_confirm_password);
        line_agree=findViewById(R.id.line_agree);
        tv_agree=findViewById(R.id.tv_agree);
        cb_agree=findViewById(R.id.cb_agree);
        et_password=findViewById(R.id.et_password);
        et_password_confirm=findViewById(R.id.et_password_confirm);
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        tvCode = findViewById(R.id.tvCode);
        tvToast=findViewById(R.id.tvToast);
        btn_register=findViewById(R.id.btn_register);
        tvCode.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        setResult(8);
        etPhone.setFocusable(true);
        etPhone.setFocusableInTouchMode(true);
        etPhone.requestFocus();
        Timer timer =new Timer();
        timer.schedule(new TimerTask(){
                           @Override
                           public void run() {
                               InputMethodManager inputManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(etPhone,0);
                           }},
                200);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //手机号输入大于5位，获取验证码按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCode.setEnabled(etPhone.getText() != null && etPhone.getText().length() > 5);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            //验证码输入6位并且手机大于5位，验证按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_register.setEnabled(etCode.getText() != null && etCode.getText().length() >= 6 && etPhone.getText() != null && etPhone.getText().length() > 5);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    // 注册短信回调
    private void initSDK() {
        try {
            MobSDK.submitPolicyGrantResult(true, null);
            final Handler handler = new Handler(this);
            EventHandler eventHandler = new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            };
            SMSSDK.registerEventHandler(eventHandler); // 注册短信回调
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // 销毁回调监听接口
    protected void onDestroy() {

        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();

    }
    public boolean handleMessage(Message msg) {

        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;
        if (result == SMSSDK.RESULT_COMPLETE) {
            System.out.println("--------result"+event);
            //回调完成
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                Toast.makeText(RegisterActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
                line_confirm_password.setVisibility(View.VISIBLE);
                line_new_password.setVisibility(View.VISIBLE);
                etPhone.setFocusable(false);
                etCode.setFocusable(false);
                btn_register.setText("立即注册");
                btn_register.setTextColor(getResources().getColor(R.color.white));
                // btn_register.setTextSize(20);
                System.out.println("救命救命");
                flag = 2;
            }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                //获取验证码成功
                Toast.makeText(RegisterActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(tvCode, 60000, 1000);
                mCountDownTimerUtils.start();
            }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){

                //返回支持发送验证码的国家列表
            }

        }else{
            //((Throwable) data).printStackTrace();
            //Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            //Toast.makeText(RegisterActivity.this, "123", Toast.LENGTH_SHORT).show();
            int status = 0;
            try {
                ((Throwable) data).printStackTrace();
                Throwable throwable = (Throwable) data;

                JSONObject object = new JSONObject(throwable.getMessage());
                String des = object.optString("detail");
                status = object.optInt("status");
                if (!TextUtils.isEmpty(des)) {
                    // Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                    processError(data);
                    return false;
                }
            } catch (Exception e) {
                SMSLog.getInstance().w(e);
            }

        }
        return false;
    }
    private void showErrorToast(String text) {
        if (toast == null) {
            toast = new Toast(this);
            View rootView = LayoutInflater.from(this).inflate(R.layout.smssdk_error_toast_layout, null);
            tvToast = rootView.findViewById(R.id.tvToast);
            toast.setView(rootView);
            toast.setGravity(Gravity.CENTER, 0, ResHelper.dipToPx(this, -100));
        }
        tvToast.setText(text);
        toast.show();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void processError(Object data) {
        int status = 0;
        // 根据服务器返回的网络错误，给toast提示
        try {
            ((Throwable) data).printStackTrace();
            Throwable throwable = (Throwable) data;

            JSONObject object = new JSONObject(
                    throwable.getMessage());
            String des = object.optString("detail");
            status = object.optInt("status");
            if (!TextUtils.isEmpty(des)) {
                showErrorToast(des);
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()) {
            case R.id.tvCode:
                phonenumber = etPhone.getText().toString().trim();
                if (!isNetworkConnected()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!TextUtils.isEmpty(phonenumber)) {
                    SMSSDK.getVerificationCode("86", phonenumber);//获取短信
                    //SMSSDK.getVoiceVerifyCode("86", phonenumber);
                }else {
                    Toast.makeText(RegisterActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.btn_register:
                phonenumber=etPhone.getText().toString().trim();
                password=et_password.getText().toString();
                String password_confirm=et_password_confirm.getText().toString();
                String number = etCode.getText().toString().trim();
                if(flag==1) {
                    if (phonenumber.length() == 0) {
                        Toast.makeText(RegisterActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern pattern = Pattern.compile("^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$");
                    boolean matcher = pattern.matcher(phonenumber).find();
                    if (!matcher) {
                        Toast.makeText(RegisterActivity.this, "手机号码输入不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(number)) {
                        Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();
                            try {
                                if(!userDao.isExistPhone(phonenumber)){
                                    if (!TextUtils.isEmpty(number)) {
                                        SMSSDK.submitVerificationCode("86", phonenumber, number);//验证短信
                                    }
                                    System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle("").setIcon(R.mipmap.ic_launcher).setMessage("账号" + phonenumber + "已经存在,无需重复注册")
                                            .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                    AlertDialog ad = builder.create();
                                    ad.show();
                                }
                            }catch (Exception e) {
                                Log.e("error",e.toString());
                            }
                            Looper.loop();
                        }
                    }.start();
                }
                else if(flag==2) {
                    if (password.length() == 0) {
                        Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.equals(password_confirm)) {
                        Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!cb_agree.isChecked()) {
                        Toast.makeText(RegisterActivity.this, "请勾选《垃圾分类用户协议》", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            User u = new User(phonenumber, password);
                            Looper.prepare();
                            try {
                                if (userDao.insert(u)) {
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                    //  return;
                                }
                            } catch (Exception e) {
                                Log.e("error", e.toString());
                            }
                            Looper.loop();
                        }
                    }.start();
                }
                break;
            default:
                break;
        }
    }

}