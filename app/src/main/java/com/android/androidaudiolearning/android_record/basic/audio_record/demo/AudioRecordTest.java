package com.android.androidaudiolearning.android_record.basic.audio_record.demo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * 音频录制
 */
public class AudioRecordTest {

    private static final String TAG = AudioRecordTest.class.getSimpleName();
    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord mAudioRecord;
    private int mMinBufferSize = 0;
    private Thread mAudioRecordThread;
    private boolean mIsRecordStart;
    private volatile boolean isLoopExist;

    /**
     * 开始录制
     * @return
     */
    public boolean startRecord(){
        return startRecord(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT);
    }

    public boolean startRecord(int defaultSource, int defaultSampleRate, int defaultChannelConfig, int defaultAudioFormat) {
        if (mIsRecordStart){
            Log.d("audio_record","record already start------------->");
            return false;
        }
        mMinBufferSize = AudioRecord.getMinBufferSize(defaultSampleRate,defaultChannelConfig,defaultAudioFormat);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE){
            Log.d("audio_record","invalid params------------->");
            return false;
        }
        mAudioRecord = new AudioRecord(defaultSource,defaultSampleRate,defaultChannelConfig,defaultAudioFormat,mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
            Log.d("audio_record","AudioRecord initialize fail------------->");
            return false;
        }
        mAudioRecord.startRecording();
        isLoopExist = false;
        mAudioRecordThread = new AudioRecordThread();
        mAudioRecordThread.start();
        mIsRecordStart = true;
        Log.d("audio_record","AudioRecord start success ------------->");
        return false;
    }

    /**
     * 停止录制
     */
    public void stopRecord(){
        if (!mIsRecordStart){
            Log.d("audio_record","AudioRecord already stop ------------->");
            return;
        }
        isLoopExist = true;
        try {
            mAudioRecordThread.interrupt();
            mAudioRecordThread.join(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord.stop();
        }
        mAudioRecord.release();
        mIsRecordStart = false;
        onAudioFrameDataListener = null;
    }

    class AudioRecordThread extends Thread{
        @Override
        public void run() {
            while (!isLoopExist){
                byte[] buffer = new byte[mMinBufferSize];
                int read = mAudioRecord.read(buffer, 0, /*buffer.length*/mMinBufferSize);
                if (read == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.d("audio_record","AudioRecord ERROR_INVALID_OPERATION------------->");
                } else if (read == AudioRecord.ERROR_BAD_VALUE) {
                    Log.d("audio_record","AudioRecord ERROR_BAD_VALUE ------------->");
                }else {
                    if (onAudioFrameDataListener != null){
                        onAudioFrameDataListener.outAudioDataFrame(buffer);
                    }
                }
            }
        }
    }

    public interface OnAudioFrameDataListener{
        void outAudioDataFrame(byte[] frame);
    }
    private OnAudioFrameDataListener onAudioFrameDataListener;
    public void setOnAudioFrameDataListener(OnAudioFrameDataListener audioFrameDataListener){
        this.onAudioFrameDataListener = audioFrameDataListener;
    }
}
