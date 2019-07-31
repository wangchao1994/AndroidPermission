package com.android.androidaudiolearning.android_record.basic.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class BallView extends View {

    private Paint mPaint;
    private float pointX = 30;//x 坐标
    private float pointY = 30;//y 坐标
    private float radius = 60;//小球半径
    // 自定义颜色数组
    private int[] colorArray = {Color.BLACK, Color.RED, Color.GREEN};
    // 默认画笔颜色
    private int paintColor = colorArray[0];
    private int screenWidth;//屏幕宽度
    private int screenHeight;// 屏幕高度

    public BallView(Context context,int width, int height) {
        super(context);
        this.screenWidth = width;
        this.screenHeight = height;
        iniPaint();
    }

    private void iniPaint() {
        mPaint = new Paint();
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置画笔颜色
        mPaint.setColor(paintColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置屏幕为白色
        canvas.drawColor(Color.WHITE);
        //修正坐标
        revise();
        //随机设置颜色
        setPaintRandomColor();
        //绘制圆球
        canvas.drawCircle(pointX, pointY, radius, mPaint);
    }

    private void setPaintRandomColor() {
        Random random = new Random();
        paintColor = colorArray[random.nextInt(colorArray.length)];
        mPaint.setColor(paintColor);
    }

    private void revise() {
        if (pointX < radius) {
            pointX = radius;
        } else if (pointX > (screenWidth - radius)) {
            pointX = screenWidth - radius;
        }
        if (pointY < radius) {
            pointY = radius;
        } else if (pointY > (screenHeight - radius)) {
            pointY = screenHeight - radius;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                pointX = event.getX();
                pointY = event.getY();
                postInvalidate();
                break;
        }
        return true;
    }
}
