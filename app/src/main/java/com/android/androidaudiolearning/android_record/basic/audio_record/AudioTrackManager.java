package com.android.androidaudiolearning.android_record.basic.audio_record;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 音频播放类
 */
public class AudioTrackManager {
    private AudioTrack mAudioTrack;
    private DataInputStream mDis;//播放文件的数据流
    private Thread mRecordThread;
    private boolean isStart = false;
    private volatile static AudioTrackManager mInstance;
    //音频流类型
    private static final int mStreamType = AudioManager.STREAM_MUSIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz = 44100;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mMinBufferSize;
    //STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。这个和我们在socket中发送数据一样，
    // 应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
    private static int mMode = AudioTrack.MODE_STREAM;

    public AudioTrackManager(){
        initData();
    }

    private void initData() {
        //根据采样率，采样精度，单双声道来得到frame的大小。
        mMinBufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//计算最小缓冲区
        //注意，按照数字音频的知识，这个算出来的是一秒钟buffer的大小。
        //创建AudioTrack
        mAudioTrack = new AudioTrack(mStreamType, mSampleRateInHz, mChannelConfig,
                mAudioFormat, mMinBufferSize, mMode);
    }

    /**
     * 获取当前单例模式
     * @return
     */
    public static AudioTrackManager getInstance(){
        if (mInstance == null){
            synchronized (AudioTrackManager.class){
                if (mInstance == null){
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }

    public void setPath(String path){
        File file = new File(path);
        try {
            mDis = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startPlay(String path){
        Log.d("wangchao_log","path----------------wav->"+path);
        setPath(path);
        startThread();
    }
    private void destroyThread(){
        isStart = false;
        if (mRecordThread != null && Thread.State.RUNNABLE == mRecordThread.getState()){
            try {
                Thread.sleep(500);
                mRecordThread.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                mRecordThread = null;
            }
        }
    }

    private void startThread(){
        destroyThread();
        isStart = true;
        if (mRecordThread == null){
            mRecordThread = new Thread(mRecordRunnable);
            mRecordThread.start();
        }
    }

    private Runnable mRecordRunnable = new Runnable() {
        @Override
        public void run() {
            //设置线程的优先级
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            byte[] tempBuffer = new byte[mMinBufferSize];
            int readCount = 0;
            try {
                while (!(mDis.available() > 0)) {
                    readCount = mDis.read(tempBuffer);
                    if (readCount == AudioRecord.ERROR_INVALID_OPERATION || readCount == AudioRecord.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {//一边播放一边写入语音数据
                        if (mAudioTrack.getState() == AudioRecord.STATE_UNINITIALIZED){
                            initData();
                        }
                        mAudioTrack.play();
                        mAudioTrack.write(tempBuffer, 0, readCount);
                    }
                }
                stopPlay();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void stopPlay() {
        try {
            destroyThread();
            if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED){
                mAudioTrack.stop();
            }
            if (mAudioTrack != null){
                mAudioTrack.release();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mDis != null){
                try {
                    mDis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
