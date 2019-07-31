package com.android.androidaudiolearning.android_record.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraFactory {
    /**
     * 检查是否有摄像头
     * @param context
     * @return
     */
    public static boolean checkCameraHardWare(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }
}
