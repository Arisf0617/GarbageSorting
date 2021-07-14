package com.example.garbagesorting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbagesorting.dao.UserDao;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * 登录*/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btn_register;
    private TextView btn_forget;
    private Button btn_login;
    private EditText et_phone;
    private EditText et_password;
    private ImageView btn_back;
    private UserDao userDao=new UserDao();
    private final int REG=111;
    private final int FOG=222;
    SharedPreferences sp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.light_blue_theme);
        setContentView(R.layout.activity_login);
        initUI();
        initData();
    }
    private void initUI(){
        btn_register=findViewById(R.id.btn_register);
        btn_forget=findViewById(R.id.btn_forget);
        et_phone=findViewById(R.id.et_phone);
        et_password=findViewById(R.id.et_password);
        btn_login=findViewById(R.id.btn_login);
        btn_back=findViewById(R.id.btn_back);
//        final WaveView viewById = (WaveView) findViewById(R.id.view_speech_listen);
//        viewById.post(new Runnable() {
//            @Override
//            public void run() {
//                viewById.start();
//            }
//        });
        et_phone.setFocusable(true);
        et_phone.setFocusableInTouchMode(true);
        et_phone.requestFocus();
        Timer timer =new Timer();
        timer.schedule(new TimerTask(){
                           @Override
                           public void run() {
                               InputMethodManager inputManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.showSoftInput(et_phone,0);
                           }}, 200);
        sp= getSharedPreferences("User",MODE_PRIVATE);
    }
    private void  initData(){
        btn_register.setOnClickListener(this);
        btn_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        Intent intent = getIntent();
        String phone_intent=intent.getStringExtra("null");
        Intent intent1=new Intent();
        intent1.putExtra("null_phone",phone_intent);
        setResult(4,intent1);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        int id=v.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_register:
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REG);
                break;
            case R.id.btn_forget:
                intent.setClass(LoginActivity.this,ForgetActivity.class);
                startActivityForResult(intent,FOG);
                break;
            case R.id.btn_login:
                String phone=et_phone.getText().toString().trim();
                String password=et_password.getText().toString();
                if(phone.length()==0){
                    Toast.makeText(LoginActivity.this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern pattern = Pattern.compile("^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$");
                boolean matcher = pattern.matcher(phone).find();
                if(!matcher){
                    Toast.makeText(LoginActivity.this,"手机号码输入不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()==0){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();
                        try {
                            if(userDao.login(phone,password)){
                                System.out.println(et_phone);
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                //存储手机号和姓名，方便调用
                                SharedPreferences.Editor edit=sp.edit();
                                edit.putString("phone",phone);
                                // edit.putString("name",name);
                                edit.commit();
                                intent.putExtra("phone",phone);
                                setResult(3, intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "手机号码或密码错误", Toast.LENGTH_SHORT).show();
                                //  return;
                            }
                        }catch (Exception e) {
                            Log.e("error",e.toString());
                        }
                        Looper.loop();
                    }
                }.start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FOG&&resultCode==4) {
            et_phone.setFocusable(true);
            et_phone.setFocusableInTouchMode(true);
            et_phone.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(et_phone, 0);
                }
            }, 200);
        }
        else if (requestCode==REG&&resultCode==8) {
            et_phone.setFocusable(true);
            et_phone.setFocusableInTouchMode(true);
            et_phone.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(et_phone, 0);
                }
            }, 200);
        }
    }
}