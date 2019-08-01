package com.android.androidaudiolearning;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.nio.ByteBuffer;

public class GlobalApplication extends Application {
    public static GlobalApplication mGlobalApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalApplication = this;
    }
    public synchronized static GlobalApplication getInstance(){
        return mGlobalApplication;
    }



}
