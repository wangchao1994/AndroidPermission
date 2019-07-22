package com.android.androidaudiolearning.android_record;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.android.androidaudiolearning.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraRecordFragment extends Fragment {

    private View mCameraView;
    public CameraRecordFragment() {
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
        return mCameraView;
    }




}
