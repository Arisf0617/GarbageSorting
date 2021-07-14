package com.example.garbagesorting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbagesorting.dao.UserDao;
import com.example.garbagesorting.utils.CountDownTimerUtils;
import com.mob.tools.utils.ResHelper;

import org.json.JSONObject;
import android.os.Handler.Callback;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * 忘记密码*/
public class ForgetActivity extends AppCompatActivity implements View.OnClickListener, Callback {
    private static final String TAG = "ForgetActivity";
    private LinearLayout line_phone;
    private LinearLayout line_validation;
    private LinearLayout line_new_password;
    private LinearLayout line_confirm_password;
    private Button btn_next;
    private ImageView iv_back;
    private EditText et_phone;
    private EditText et_code;
    private EditText new_password;
    private EditText confirm_password;
    private TextView tvCode;
    private Toast toast;
    private TextView tvToast;
    private String phonenumber;
    private static int flag=1;
    private UserDao userDao=new UserDao();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_forget);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        flag=1;
        initUI();
        initData();
        initViews();
        initSDK();
        line_confirm_password.setVisibility(View.GONE);
        line_new_password.setVisibility(View.GONE);
    }
    private void initUI(){
        line_phone=findViewById(R.id.line_phone);
        line_validation=findViewById(R.id.line_validation);
        line_new_password=findViewById(R.id.line_new_password);
        line_confirm_password=findViewById(R.id.line_confirm_password);
        btn_next=findViewById(R.id.btn_next);
        iv_back=findViewById(R.id.iv_back);
        et_phone=findViewById(R.id.et_phone);
        et_code=findViewById(R.id.et_code);
        new_password=findViewById(R.id.new_password);
        confirm_password=findViewById(R.id.confirm_password);
        tvCode=findViewById(R.id.tvCode);
        et_phone.setFocusable(true);
        et_phone.setFocusableInTouchMode(true);
        et_phone.requestFocus();
        Timer timer =new Timer();
        timer.schedule(new TimerTask(){
                           @Override
                           public void run() {
                               InputMethodManager inputManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(et_phone,0);
                           }},
                200);
//        final WaveView viewById = (WaveView) findViewById(R.id.view_speech_listen);
//        viewById.post(new Runnable() {
//            @Override
//            public void run() {
//                viewById.start();
//            }
//        });

    }
    private void initData(){
        btn_next.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tvCode.setOnClickListener(this);
        setResult(4);
    }

    //初始化控件
    private void initViews() {
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //手机号输入大于5位，获取验证码按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCode.setEnabled(et_phone.getText() != null && et_phone.getText().length() > 5);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            //验证码输入6位并且手机大于5位，验证按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_next.setEnabled(et_code.getText() != null && et_code.getText().length() >= 6 && et_code.getText() != null && et_code.getText().length() > 5);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    // 注册短信回调
    private void initSDK() {
        try {

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
                Toast.makeText(ForgetActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();

                line_confirm_password.setVisibility(View.VISIBLE);
                line_new_password.setVisibility(View.VISIBLE);
                et_phone.setFocusable(false);
                et_code.setFocusable(false);
                btn_next.setText("修改密码");
                btn_next.setTextColor(getResources().getColor(R.color.white));
                System.out.println("救命救命");
                flag = 2;
            }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                //获取验证码成功
                Toast.makeText(ForgetActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(tvCode, 60000, 1000);
                mCountDownTimerUtils.start();
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
                //return;
            }
        } catch (Exception e) {
            Log.w(TAG, "", e);
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        int id=v.getId();
        switch (id){
            case R.id.tvCode:
                phonenumber = et_phone.getText().toString().trim();
                if (!isNetworkConnected()) {
                    Toast.makeText(ForgetActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!TextUtils.isEmpty(phonenumber)) {
                    SMSSDK.getVerificationCode("86", phonenumber);//获取短信

                    //SMSSDK.getVoiceVerifyCode("86", phonenumber);
                }else {
                    Toast.makeText(ForgetActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back: finish();flag=1;break;
            case R.id.btn_next:
                if(flag==1){
                    phonenumber=et_phone.getText().toString().trim();
                    String number = et_code.getText().toString().trim();
                    if(phonenumber.length()==0){
                        Toast.makeText(ForgetActivity.this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern pattern = Pattern.compile("^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$");
                    boolean matcher = pattern.matcher(phonenumber).find();
                    if(!matcher){
                        Toast.makeText(ForgetActivity.this,"手机号码输入不正确",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(number)){
                        Toast.makeText(ForgetActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    }
                    ExecutorService singleThreadExecutor = Executors.newCachedThreadPool(); //顺序一起执行, 结果不一定按顺序
                    Thread r1=new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();
                            try {
                                if(userDao.isExistPhone(phonenumber)){
                                    if (!TextUtils.isEmpty(number)) {
                                        SMSSDK.submitVerificationCode("86", phonenumber, number);//验证短信
                                    }
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetActivity.this);
                                    builder.setTitle("").setIcon(R.mipmap.ic_launcher).setMessage("账号"+phonenumber+"未注册,请先注册")
                                            .setPositiveButton("立即注册", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    intent.setClass(ForgetActivity.this,RegisterActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    AlertDialog ad=builder.create();
                                    ad.show();
                                    //Toast.makeText(ForgetActivity.this, "账号"+phonenumber+"未注册,请先注册", Toast.LENGTH_SHORT).show();
                                    //  return;
                                }
                            }catch (Exception e) {
                                Log.e("error",e.toString());
                            }
                            Looper.loop();
                        }
                    };
                    singleThreadExecutor.execute(r1);
                }
                else if(flag==2){

                    String password=new_password.getText().toString();
                    String password_confirm=confirm_password.getText().toString();
                    if(password.length()==0){
                        Toast.makeText(ForgetActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!password.equals(password_confirm)){
                        Toast.makeText(ForgetActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();
                            try {
                                if(userDao.updatePassword(password,phonenumber)){
                                    Toast.makeText(ForgetActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ForgetActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                    //  return;
                                }
                            }catch (Exception e) {
                                Log.e("error",e.toString());
                            }
                            Looper.loop();
                        }
                    }.start();
                    finish();
                    flag=1;
                }
                break;
        }
    }
}