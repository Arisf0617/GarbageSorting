package com.example.garbagesorting.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * 此工具类用来显示Toast
 * 示例：ToastUtil.showMsg(getApplicationContext(), "您还没有选择图片呢!");
 * wuhaoyu 2021/4/19 21:18 add
 */
public class ToastUtil {
    public static Toast mToast;
    public static void showMsg(Context context, String msg){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }
}
