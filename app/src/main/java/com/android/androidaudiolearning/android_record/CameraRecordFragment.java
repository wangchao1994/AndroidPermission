package com.android.androidaudiolearning.android_record;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;

import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.android_record.encoder.MediaAudioEncoder;
import com.android.androidaudiolearning.android_record.encoder.MediaEncoder;
import com.android.androidaudiolearning.android_record.encoder.MediaMuxerWrapper;
import com.android.androidaudiolearning.android_record.encoder.MediaVideoEncoder;
import com.android.androidaudiolearning.android_record.widget.RotateProgress;

import java.io.IOException;

/**
 * record
 */
public class CameraRecordFragment extends Fragment implements View.OnClickListener {
    private static final boolean DEBUG = false;
    private static final String TAG = CameraRecordFragment.class.getSimpleName();
    private RotateProgress mRotateProgress;
    private View mCameraView;
    private CameraGLView mCameraGLView;
    private ImageButton mRecordButton;
    private static final int VIDEO_SIZE_WIDTH = 1280;
    private static final int VIDEO_SIZE_HEIGHT = 720;
    /**
     * MediaMuxerWrapper for audio/video recording
     */
    private MediaMuxerWrapper mMediaMuxerWrapper;
    public CameraRecordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRotateProgress == null){
            mRotateProgress = new RotateProgress(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //避免onCreateView多次执行创建cameraView
        if (mCameraView == null){
            mCameraView = inflater.inflate(R.layout.fragment_camera_record, container, false);
        }else{
            ViewGroup viewGroup = (ViewGroup) mCameraView.getParent();
            if (viewGroup != null){
                viewGroup.removeView(mCameraView);
            }
        }
        initView(mCameraView);
        return mCameraView;
    }

    /**
     * initView
     * @param cameraView
     */
    private void initView(View cameraView) {
        mCameraGLView = cameraView.findViewById(R.id.cameraView);
        mRecordButton = cameraView.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVideoParams();
    }

    private void initVideoParams() {
        if (mCameraGLView == null)return;
        mCameraGLView.setVideoSize(VIDEO_SIZE_WIDTH,VIDEO_SIZE_HEIGHT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraGLView != null){
            mCameraGLView.onResume();
        }
    }

    @Override
    public void onPause() {
        //停止录像
        stopRecording();
        if (mCameraGLView != null){
            mCameraGLView.onPause();
        }
        super.onPause();
    }

    /**
     * stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMediaMuxerWrapper);
        mRecordButton.setColorFilter(0);	// return to default color
        if (mMediaMuxerWrapper != null) {
            mMediaMuxerWrapper.stopRecording();
            mMediaMuxerWrapper = null;
        }
    }

    /**
     * start recording
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        try {
            mRecordButton.setColorFilter(0xffff0000);	// turn red
            mMediaMuxerWrapper = new MediaMuxerWrapper(".mp4");	// if you record audio only, ".m4a" is also OK.
            // for video capturing
            new MediaVideoEncoder(mMediaMuxerWrapper, mMediaEncoderListener, mCameraGLView.getVideoWidth(), mCameraGLView.getVideoHeight());
            // for audio capturing
            new MediaAudioEncoder(mMediaMuxerWrapper, mMediaEncoderListener);
            mMediaMuxerWrapper.prepare();
            mMediaMuxerWrapper.startRecording();
        } catch (final IOException e) {
            mRecordButton.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
    }

    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder){
                mCameraGLView.setVideoEncoder((MediaVideoEncoder) encoder);
            }
        }

        @Override
        public void onStopped(MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder){
                mCameraGLView.setVideoEncoder(null);
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view == mRecordButton){
            if (mMediaMuxerWrapper == null){
                startRecording();
                mRecordButton.setImageResource(R.drawable.btn_shutter_video_stop);
            }else{
                mRotateProgress.show();
                mRecordButton.setImageResource(R.drawable.btn_shutter_video);
                stopRecording();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Test
                        mRotateProgress.hide();
                    }
                },1500);
            }
        }
    }
}
