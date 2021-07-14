package com.example.garbagesorting.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingView extends View {
    public LoadingView(Context context) {
        this(context, null);
    }
    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //对画笔的设置
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        mPaint.setColor(Color.WHITE);
    }

    private Paint mPaint;

    //默认view的宽度
    private int mDefaultWidth = dp2px(100);
    private int mDefaultHeight =mDefaultWidth;
    private int mDefaultPadding = dp2px(10);
    //绘制进度条的实际宽度
    private int mMeasureHeight;
    private int mMeasureWidth;
    //绘制直线的索引
    private int mCurrentIndex = 0;
    //圆圈组成线段的个数
    private int count = 12;
    private boolean mIsSart=false;
    Timer mTimer;
    TimerTask mTimerTask;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量计算宽度
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            mMeasureWidth = widthSpecSize;
        } else {
            //指定默认大小
            mMeasureWidth = mDefaultWidth;
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                mMeasureWidth = Math.min(mMeasureWidth, widthSpecSize);
            }
        }

        //测量计算View的高
        int heightSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            mMeasureHeight = heightSpecSize;
        } else {
            //指定默认大小
            mMeasureHeight = mDefaultHeight;
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                mMeasureHeight = Math.min(mMeasureHeight, heightSpecSize);
            }
        }
        mMeasureHeight=mMeasureHeight-getPaddingBottom()-getPaddingTop();
        mMeasureWidth=mMeasureWidth-getPaddingLeft()-getPaddingBottom();
        //重新测量
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //将画面平移到View的中心
        canvas.translate(mMeasureWidth/2,mMeasureHeight/2);
        if (mCurrentIndex >= count) {
            mCurrentIndex = 0;
        }
        int endAlpha = 255 / count;
        for (int i = 0; i < count; i++) {
            int alpha;
            //计算将要绘制直线的颜色透明度
            if (mCurrentIndex - i > 0) {
                alpha = endAlpha * (mCurrentIndex - i);
            } else {
                alpha = 255 - 255 / count * (i - mCurrentIndex);
            }
            mPaint.setColor(Color.argb(alpha, 255, 255, 255));
            //绘制直线
            canvas.drawLine(-mMeasureWidth / 5, 0, -mMeasureWidth / 11, 0, mPaint);
            //旋转画布
            canvas.rotate(360 / count, 0, 0);
        }
        mCurrentIndex++;

        canvas.restore();
    }

    //将设置的db转为屏幕像素
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    //开始旋转
    public void start(){
        mTimer = new Timer();
        mIsSart=true;
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    postInvalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTimer.schedule(mTimerTask,0,100);
    }

    //停止旋转
    public void stop(){
        mIsSart=false;
        mTimerTask.cancel();
        mTimer.cancel();
    }

    //是否开始旋转
    public boolean isStart() {
        return mIsSart;
    }

}
