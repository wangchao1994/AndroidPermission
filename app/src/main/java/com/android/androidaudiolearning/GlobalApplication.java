package com.android.androidaudiolearning;

import android.app.Application;

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
