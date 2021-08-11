package com.k.calendar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class WaveView extends View {
    //路径
    private Path mPath;
    private Path circlePath;
    //画笔
    private Paint mPaint;
    //屏幕高度
    private int screenHeight;
    //屏幕宽度
    private int screenWidth;
    //波长 自己控制
    private int waveLength = 400;
    //波长的数量
    private int waveCount;
    //贝塞尔曲线的控制点
    private int centerY;
    private ValueAnimator mValueAnimator;
    //偏移量
    private int mOffset;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#f85b5b"));
        mPath = new Path();
        circlePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;// 获取屏幕高度
        screenWidth = w;//获取屏幕宽度
        centerY = getHeight();//设置中心点
        waveCount = (int) Math.round(screenWidth / waveLength + 1.5);//波长的数量
        circlePath.reset();
        circlePath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipPath(circlePath);
        mPath.reset();
        //首先移动到最左边 offset是偏移量
        mPath.moveTo(-waveLength + mOffset, centerY);
        for (int i = 0; i < waveCount; i++) {
            //先画底部的曲线   负的波长的3/4  第二次加一个波长的长度 以及加偏移量    曲线是向下的，控制点加60        终点则为负的波长的1/2 + 波长的长度 和偏移量
            mPath.quadTo(-waveLength * 3 / 4 + i * waveLength + mOffset, centerY + 60, -waveLength / 2 + i * waveLength + mOffset, centerY);
            //再画顶部的曲线   负的波长的1/4                                 曲线是向上的，控制点减60
            mPath.quadTo(-waveLength * 1 / 4 + i * waveLength + mOffset, centerY - 60, 0 + i * waveLength + mOffset, centerY);
        }
        //闭合路径
        mPath.lineTo(screenWidth, screenHeight);
        mPath.lineTo(0, screenHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        //设置点击时间只是为了点击以后才开始循环

        canvas.restore();
    }

    public void start() {
        //主要是为了获取偏移量
        mValueAnimator = ValueAnimator.ofInt(0, waveLength);
        mValueAnimator.setDuration(1000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mOffset = (int) valueAnimator.getAnimatedValue();
                //重绘
                invalidate();
            }
        });
        mValueAnimator.start();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0)
                .setDuration(10000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                centerY = (int) (getHeight() * v);
            }
        });
        animator.start();
    }
}
