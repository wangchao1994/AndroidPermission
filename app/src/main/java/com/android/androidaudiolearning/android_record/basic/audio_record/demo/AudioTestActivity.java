package com.android.androidaudiolearning.android_record.basic.audio_record.demo;

import android.Manifest;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.android_record.basic.audio_record.demo.wav.WavFileReader;
import com.android.androidaudiolearning.android_record.basic.audio_record.demo.wav.WavFileWriter;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.io.IOException;

public class AudioTestActivity extends AppCompatActivity implements AudioRecordTest.OnAudioFrameDataListener {
    private static final String DEFAULT_TEST_FILE = Environment.getExternalStorageDirectory() + "/test.wav";
    private AudioRecordTest audioRecordTest;
    private WavFileWriter wavFileWriter;
    private static final int SAMPLES_PER_FRAME = 1024;
    private AudioPlayTest audioPlayTest;
    private volatile boolean mIsTestingExit = false;
    private WavFileReader wavFileReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_test_laout);
    }

    private void initPermission() {
        Permissions permissions = Permissions.build(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        SoulPermission.getInstance().checkAndRequestPermissions(permissions, new CheckRequestPermissionsListener() {
            @Override
            public void onAllPermissionOk(Permission[] allPermissions) {
                AudioRecordClick();
                Log.d("audio_log","onAllPermissionOk----------------------->");
            }

            @Override
            public void onPermissionDenied(Permission[] refusedPermissions) {
                Log.d("audio_log","onPermissionDenied----------------------->");
            }
        });
    }


    public void AudioRecordClick() {
        audioRecordTest = new AudioRecordTest();
        wavFileWriter = new WavFileWriter();
        try {
            wavFileWriter.openFile(DEFAULT_TEST_FILE, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioRecordTest.setOnAudioFrameDataListener(this);
        audioRecordTest.startRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }
    /**
     * 开始采集
     * @param view
     */
    public void AudioRecordClick(View view) {
        Log.d("audio_log","start----------------------->");
        initPermission();
    }

    @Override
    public void outAudioDataFrame(byte[] frame) {
        wavFileWriter.writeData(frame, 0, frame.length);
    }

    /**
     * 停止采集
     * @param view
     */
    public void AudioStopClick(View view) {
        audioRecordTest.stopRecord();
        try {
            wavFileWriter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放
     * @param view
     */
    public void AudioPlayClick(View view) {
        wavFileReader = new WavFileReader();
        audioPlayTest = new AudioPlayTest();
        try {
            wavFileReader.openFile(DEFAULT_TEST_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioPlayTest.startPlay();
        new Thread(AudioPlayRunnable).start();
    }

    /**
     * 停止播放
     * @param view
     */
    public void AudioPlayStopClick(View view) {
        mIsTestingExit = true;
    }

    private Runnable AudioPlayRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[SAMPLES_PER_FRAME * 2];
            while (!mIsTestingExit && wavFileReader.readData(buffer, 0, buffer.length) > 0) {
                audioPlayTest.play(buffer, 0, buffer.length);
            }
            audioPlayTest.stopPlay();
            try {
                wavFileReader.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
