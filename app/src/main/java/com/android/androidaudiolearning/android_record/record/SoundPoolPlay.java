package com.android.androidaudiolearning.android_record.record;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import com.android.androidaudiolearning.R;

public class SoundPoolPlay implements SoundPool.OnLoadCompleteListener{
    private SoundPool mSoundPool;
    private static final int DEFAULT_INVALID_SOUND_ID = -1;
    private int mSoundId = -1;
    private int mStreamId = -1;

    public void playAudio(Context context){
        SoundPool mSoundPool = createSoundPool();
        if (mSoundPool == null)return;
        mSoundPool.setOnLoadCompleteListener(SoundPoolPlay.this);
        if (mSoundId == DEFAULT_INVALID_SOUND_ID){
            mSoundId = mSoundPool.load(context.getApplicationContext(), R.raw.speaker_ring_custom,0);
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
    public void releaseSound() {
        if (mSoundPool != null) {
            mSoundPool.autoPause();
            mSoundPool.unload(mSoundId);
            mSoundId = DEFAULT_INVALID_SOUND_ID;
            mSoundPool.release();
            mSoundPool = null;
        }

    }
}
