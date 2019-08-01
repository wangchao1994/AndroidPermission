package com.android.androidaudiolearning.android_record.record;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static android.content.Context.STORAGE_SERVICE;

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
    public AudioRecordTest(Context context){
        startRecord(context,DEFAULT_SOURCE,DEFAULT_RATE,DEFAULT_CHANNEL,DEFAULT_FORMAT);
    }
    /**
     * 开始录制
     * @param default_source
     * @param default_rate
     * @param default_channel
     * @param default_format
     */
    private void startRecord(Context context,int default_source, int default_rate, int default_channel, int default_format) {
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            Log.d("audio_record","This device doesn't have a mic!");
            return;
        }
        setCurrentDirPcm();
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

    private void setCurrentDirPcm() {
        File mFile = new File(Environment.getExternalStorageDirectory()+"/PCMDemo");
        try {
            File mAudioFile = File.createTempFile("recording", ".pcm", mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    audioFrameListener.outAudioFrame(result);//写入文件
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

    // 其实只要判断第二张卡在挂载状态
    public String getSecondaryStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, null);
            if (paths != null){
                // second element in paths[] is secondary storage path
                return paths.length <= 1 ? null : paths[1];
            }
        } catch (Exception e) {
            Log.e("audio_record", "getSecondaryStoragePath() failed", e);
        }
        return null;
    }

    // 获取存储卡的挂载状态. path 参数传入上方法得到的路径
    public String getStorageState(Context context,String path) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
            String state = (String) getVolumeStateMethod.invoke(sm, path);
            return state;
        } catch (Exception e) {
            Log.e("audio_record", "getStorageState() failed", e);
        }
        return null;
    }
}
