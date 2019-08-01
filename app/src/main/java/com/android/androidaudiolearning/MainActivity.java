package com.android.androidaudiolearning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playAudio();
    }

    private SoundPool mSoundPool;
    private static final int DEFAULT_INVALID_SOUND_ID = -1;
    private int mSoundId = -1;
    private int mStreamId = -1;
    private void playAudio(){
        SoundPool mSoundPool = createSoundPool();
        if (mSoundPool == null)return;
        mSoundPool.setOnLoadCompleteListener(this);
        if (mSoundId == DEFAULT_INVALID_SOUND_ID){
            mSoundId = mSoundPool.load(getApplicationContext(),R.raw.speaker_ring_custom,1/*0*/);
        }else{
            if (mStreamId == DEFAULT_INVALID_SOUND_ID)
            onLoadComplete(mSoundPool,0,0);
        }
    }
    private SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes mAudioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(mAudioAttributes)
                    .build();
        }else{
            mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC,0);
        }
        return mSoundPool;
    }

    /*
    1.mSoundId load方法返回的值,指向某个已加载的音频资源
    2.leftVolume\rightVolume 用来这种左右声道的值.范围 0.0f ~ 1.0f
    3.priority 流的优先级
    4.loop 循环播放的次数, -1 表示无限循环
    5.rate 播放的速率 , 2 表示2倍速度
    */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (mSoundPool != null){
            mStreamId = mSoundPool.play(mSoundId, 1.0f, 1.0f, 16, -1, 1.0f);
        }
    }

    public void pause(){
        if (mSoundPool != null){
            mSoundPool.pause(mStreamId);
        }
    }
    public void resume(){
        if (mSoundPool != null){
            mSoundPool.resume(mStreamId);
        }
    }

}
