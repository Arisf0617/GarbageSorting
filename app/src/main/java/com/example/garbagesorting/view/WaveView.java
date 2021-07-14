package com.example.garbagesorting.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.example.garbagesorting.R;

/**
 * desc: todo 描述本类功能
 * author: pdog
 * email: pdog@qq.com
 * time: 2017/11/8  12 :34
 */
public class WaveView extends View {

    private Paint mPaint;
    private Path mPath;
    private int mWidth;
    private int mHeight;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //当前移动的距离
    float tranlateWidth;

    ValueAnimator mValueAnimator;


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.wave_green));
        mPaint.setStrokeWidth(2);

        mPath = new Path();


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mValueAnimator = ValueAnimator.ofInt(0,1);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tranlateWidth = valueAnimator.getAnimatedFraction() * mWidth;
                invalidate();
                System.out.println("update...");
            }
        });
        mValueAnimator.setDuration(1 << 10);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        mValueAnimator = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getWidth();
        mHeight = getHeight();
        float waveHeight = (float) (mWidth / 5);


        mPath.moveTo(-mWidth, 0);

        mPath.rCubicTo(mWidth / 4 * 1, -waveHeight, mWidth / 4 * 3, waveHeight, mWidth, 0);
        mPath.rCubicTo(mWidth / 4 * 1, -waveHeight, mWidth / 4 * 3, waveHeight, mWidth, 0);

        mPath.rLineTo(0, mHeight >> 1);
        mPath.rLineTo(-mWidth << 1, 0);
        mPath.close();
    }

    public void start() {
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(tranlateWidth, getHeight() >> 1);

        canvas.drawPath(mPath, mPaint);
    }
}
