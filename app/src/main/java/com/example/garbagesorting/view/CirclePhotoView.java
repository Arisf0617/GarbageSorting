package com.example.garbagesorting.view;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by yds
 * on 2020/3/10.
 * 个人页面用户圆形头像
 */
public class CirclePhotoView extends Drawable {
    private Paint mPaint;
    private BitmapShader mBitmapShader;
    private int mSize;
    private int mRadius;
    public CirclePhotoView(Bitmap bitmap){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        mPaint.setShader(mBitmapShader);
        mSize = Math.min(bitmap.getWidth(),bitmap.getHeight());
        mRadius = mSize/2;
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }
}