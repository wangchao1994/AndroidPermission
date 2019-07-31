package com.android.androidaudiolearning;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.androidaudiolearning.persmisson.GlobalPermission;
import com.android.androidaudiolearning.persmisson.OnPermissionListener;
import com.android.androidaudiolearning.persmisson.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {
    private static final int DEFAULT_INVALID_SOUND_ID = -1;
    private static final int DEFAULT_INVALID_STREAM_ID = -1;
    private SoundPool mSoundPool;
    private int mSoundId;
    private int mStreamID;
    private float mCruLeftVolume;
    private float mCurRightVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initSoundPool
        initSoundPool();
    }

    private void initSoundPool() {
        if (mSoundPool == null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes mAdudioAttributes = null;
                mAdudioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mSoundPool = new SoundPool.Builder()
                        .setAudioAttributes(mAdudioAttributes)
                        .setMaxStreams(16)
                        .build();
            } else {
                mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 6);
            }
            mSoundId = mSoundPool.load("", 1);
            mSoundPool.setOnLoadCompleteListener(this);
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
        if (mSoundPool != null){
            if (mStreamID == DEFAULT_INVALID_STREAM_ID){
                mStreamID = mSoundPool.play(mSoundId, 1.0f, 1.0f, 16, -1, 1.0f);
            }
        }
    }
}
