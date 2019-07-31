package com.android.androidaudiolearning.android_record.basic.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * 自定义SurfaceView
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Context mContext;
    private Paint mPaint;
    private boolean isRunning;
    private Thread mThread;
    public static final String PICTURE_NAME = "prettygirl.png";

    public CustomSurfaceView(Context context) {
        this(context, null);    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        //获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        mThread = new Thread();
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        //循环绘制
        while (isRunning) {
            drawView();
        }
    }

    private void drawView() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null){
                mCanvas.drawBitmap(Util.getImageFromAssetsFile(mContext, PICTURE_NAME), 0, 0, mPaint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCanvas != null) {
                //释放canvas
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
