package com.android.androidaudiolearning.android_record.record;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * 音频播放
 */
public class AudioPlayerTest {
    private final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;  //流音乐
    private final int DEFAULT_RATE = 44100;    //采样率
    private final int DEFAULT_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;   //双通道(左右声道)
    private final int DEFAULT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;   //数据位宽16位
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;
    private boolean isPlaying = false;
    private int minBufferSize;
    private AudioTrack audioTrack;
    public AudioPlayerTest(){
        startPlay(DEFAULT_STREAM_TYPE,DEFAULT_RATE,DEFAULT_CHANNEL,DEFAULT_FORMAT,DEFAULT_PLAY_MODE);
    }

    private void startPlay(int default_stream_type, int default_rate, int default_channel, int default_format, int defaultPlayMode) {
        if (isPlaying){
            Log.d("audio_play","isPlaying---->");
            return;
        }
        minBufferSize = AudioTrack.getMinBufferSize(default_rate, default_channel, default_format);
        if (minBufferSize == AudioTrack.ERROR_BAD_VALUE){
            return;
        }
        audioTrack = new AudioTrack(DEFAULT_STREAM_TYPE,DEFAULT_RATE,DEFAULT_CHANNEL,DEFAULT_FORMAT,minBufferSize,DEFAULT_PLAY_MODE);
        if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED){
            return;
        }
        isPlaying = true;
    }

    private void play(byte[] audioData,int offsetInBytes, int sizeInBytes){
        if (!isPlaying){
            Log.d("audio_play", "AudioTrack not start");
            return;
        }
        if (sizeInBytes < minBufferSize){
            //AudioTrack内部音频缓冲区的大小，该值不能低于一帧音频祯数据的大小
        }
        if(audioTrack.write(audioData,offsetInBytes,sizeInBytes)!=minBufferSize){
            Log.d("audio_play", "AudioTrack can not write all the data");
        }
        audioTrack.play();
    }

    public void stop(){
        if (!isPlaying){
            Log.d("audio_play", "AudioTrack not start");
            return;
        }
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            audioTrack.stop();
        }
        audioTrack.release();
    }


}
