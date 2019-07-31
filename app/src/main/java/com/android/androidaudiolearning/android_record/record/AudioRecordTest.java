package com.android.androidaudiolearning.android_record.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * 音频录制
 */
public class AudioRecordTest {
    private final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;  //麦克风
    private final int DEFAULT_RATE = 44100;    //采样率
    private final int DEFAULT_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;   //双通道(左右声道)
    private final int DEFAULT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;   //数据位宽16位
    private int mMinBufferSize;
    private AudioRecord mAudioRecord;
    private boolean isRecording = false;
    private AudioFrameListener audioFrameListener;
    public AudioRecordTest(){
        startRecord(DEFAULT_SOURCE,DEFAULT_RATE,DEFAULT_CHANNEL,DEFAULT_FORMAT);
    }
    /**
     * 开始录制
     * @param default_source
     * @param default_rate
     * @param default_channel
     * @param default_format
     */
    private void startRecord(int default_source, int default_rate, int default_channel, int default_format) {
        //获取内部音频缓冲区大小
        mMinBufferSize = AudioRecord.getMinBufferSize(default_rate,default_channel,default_format);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE){
            return;
        }
        mAudioRecord = new AudioRecord(default_source,default_rate,default_channel,default_format,mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
            return;
        }
        //初始化完成后进行录制音频操作
        mAudioRecord.startRecording();
        isRecording = true;
        AudioRecordThread audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }
    public void stopAudioRecording(){
        isRecording = false;
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord.stop();
        }
        mAudioRecord.release();
    }
    class AudioRecordThread extends Thread{
        @Override
        public void run() {
            while (isRecording){
                byte[] buffer = new byte[mMinBufferSize];
                int result = mAudioRecord.read(buffer, 0, buffer.length);
                if (audioFrameListener != null){
                    audioFrameListener.outAudioFrame(result);
                }
            }
        }
    }
    public interface AudioFrameListener{
        void outAudioFrame(int frame);
    }
    public void setAudioFrameListener(AudioFrameListener mAudioFrameListener){
        audioFrameListener = mAudioFrameListener;
    }
}
