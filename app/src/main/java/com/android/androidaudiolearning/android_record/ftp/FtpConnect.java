package com.android.androidaudiolearning.android_record.ftp;

public class FtpConnect {
    public static final String TAG = FtpConnect.class.getSimpleName();
    private volatile static FtpConnect mFtpConnect;
    public static FtpConnect getInstance(){
        if (mFtpConnect == null){
            synchronized (FtpConnect.class){
                if (mFtpConnect == null){
                    mFtpConnect = new FtpConnect();
                }
            }
        }
        return mFtpConnect;
    }
}
