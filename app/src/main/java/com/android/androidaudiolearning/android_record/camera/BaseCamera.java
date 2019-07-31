package com.android.androidaudiolearning.android_record.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Camera 基类,可采用google camera事例
 */
public abstract class BaseCamera {
    private static final String TAG = BaseCamera.class.getSimpleName();
    protected Context mContext;
    protected SurfaceHolder mSurfaceHolder;
    protected SurfaceTexture mSurfaceTexture;
    protected int mCameraId;
    protected String mPicturePath;
    protected String mVideoPath;
    protected MediaRecorder mMediaRecorder;
    /**
     * 设置上下文
     * @param context
     */
    public void setContext(Context context){
        this.mContext = context;
    }

    /**
     * 设置cameraId
     * @param cameraId
     */
    public void setCameraId(int cameraId){
        if (mCameraId != -1 && mCameraId != 0){
            Log.d(TAG,"CameraId only support 0 or 1");
            return;
        }
        mCameraId = cameraId;
    }

    /**
     * SurfaceView 预览
     * @param surfaceHolder
     */
    public void setDisplay(SurfaceHolder surfaceHolder){
        this.mSurfaceHolder = surfaceHolder;
    }

    /**
     * TextureView预览
     * @param surfaceTexture
     */
    public void setDisplay(SurfaceTexture surfaceTexture){
        this.mSurfaceTexture = surfaceTexture;
    }

    protected abstract void openCamera();
    protected abstract void destroyCamera();
    protected abstract void takePicture(String mPicturePath);
    protected abstract void startRecord(String mVideoPath);
    protected abstract void stopRecord();
}
