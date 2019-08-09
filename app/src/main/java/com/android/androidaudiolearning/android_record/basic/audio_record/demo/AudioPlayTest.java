package com.android.androidaudiolearning.android_record.basic.audio_record.demo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * 音频播放
 */
public class AudioPlayTest {
    /**默认播放类型*/
    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    /**默认音频帧率*/
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    /**默认声道配置*/
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    /**默认声音编码格式*/
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    /**默认播放模式*/
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;
    private boolean mIsPlayStarted = false;
    private int mMinBufferSize = 0;
    private AudioTrack mAudioTrack;

    /**
     * 开始播放
     * @return
     */
    public boolean startPlay(){
        return startPlay(DEFAULT_STREAM_TYPE,DEFAULT_SAMPLE_RATE,DEFAULT_CHANNEL_CONFIG,DEFAULT_AUDIO_FORMAT);
    }

    private boolean startPlay(int defaultStreamType, int defaultSampleRate, int defaultChannelConfig, int defaultAudioFormat) {
        if (mIsPlayStarted){
            Log.d("audio_record","AudioTrack already start play ------------->");
            return false;
        }
        mMinBufferSize = AudioTrack.getMinBufferSize(defaultSampleRate,defaultChannelConfig,defaultAudioFormat);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE){
            Log.d("audio_record","invalid params------------->");
            return false;
        }
        mAudioTrack = new AudioTrack(defaultStreamType,defaultSampleRate,defaultChannelConfig,defaultAudioFormat,mMinBufferSize,DEFAULT_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED){
            Log.d("audio_record","AudioTrack initialize fail------------->");
        }
        mIsPlayStarted = true;
        return false;
    }

    /**
     * 停止录制
     */
    public void stopPlay(){
        if (!mIsPlayStarted){
            Log.d("audio_record","AudioTrack already stop------------->");
            return;
        }
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            mAudioTrack.stop();
        }
        mAudioTrack.release();
        mIsPlayStarted = false;
    }

    /**
     * 开始播放
     * @param audioFrame
     * @param offsetInBytesde
     * @param sizeInBytes
     * @return
     */
    public boolean play(byte[] audioFrame,int offsetInBytes, int sizeInBytes){
        if (!mIsPlayStarted){
            Log.d("audio_record","AudioTrack play not started!");
            return false;
        }
        //AudioTrack内部音频缓冲区的大小，该值不能低于一帧音频祯数据的大小
        if (sizeInBytes < mMinBufferSize){
            Log.d("audio_record","audio data not enough!");
            return false;
        }
        if (mAudioTrack.write(audioFrame,offsetInBytes,sizeInBytes) != sizeInBytes) {
            Log.e("audio_record", "Could not write all the samples to the audio device !");
        }
        mAudioTrack.play();
        return true;
    }
}
