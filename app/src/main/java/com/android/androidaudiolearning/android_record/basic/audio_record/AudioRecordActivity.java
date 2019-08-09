package com.android.androidaudiolearning.android_record.basic.audio_record;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.androidaudiolearning.R;
import com.android.androidaudiolearning.persmisson.GlobalPermission;
import com.android.androidaudiolearning.persmisson.OnPermissionListener;
import com.android.androidaudiolearning.persmisson.Permission;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    private static final String TAG = AudioRecordActivity.class.getSimpleName();
    private Button mBtStartRecord;
    private Button mBtStopRecord;
    private Button mBtPlayRecord;
    private Button mBtStopPlayRecord;
    private Button mBtPlayWAV;
    private Button mBtPcm2wav;
    private Button mBtPlayAAC;
    private Button mBtPcm2aac;
    private TextView mTvEncodeProcess;
    //Params
    //----------------------------------------------------------------------------------------------
    //指定音源
    private static final int AUDIO_RESOURCE = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int SAMPLERATEHZ = 44100;
    //指定音频捕获的声道数
    private static final int CHANNAL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mMinBufferSize;
    //STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。这个和我们在socket中发送数据一样，
    // 应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
    private static int mMode = AudioTrack.MODE_STREAM;

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord = null;// 声明 AudioRecord 对象
    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudiioRecordFile";
    //保存的音频文件名
    private static final String mFileName = "audiorecordtest.pcm";
    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        Log.d("wangchao_log","onCreate------------------------------->");

        //initPermission();
        initData();
        initView();
        initEventListener();
    }

    private void initData() {
        //初始化数据
        mMinBufferSize = AudioRecord.getMinBufferSize(SAMPLERATEHZ, CHANNAL_CONFIG, mAudioFormat);//计算最小缓冲区
        //创建AudioRecorder对象
        mAudioRecord = new AudioRecord(AUDIO_RESOURCE, SAMPLERATEHZ, CHANNAL_CONFIG, mAudioFormat, mMinBufferSize);

        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists())
            mFileRoot.mkdirs();
    }

    private void initPermission() {
        Log.d("wangchao_log","initPermission------------------------------->");
        GlobalPermission.with(this)
                .permission(Permission.Group.STORAGE, Permission.Group.RECORD_AUDIO)
                .request(new OnPermissionListener() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll){
                            startRecord();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                    }
                });
    }

    private void initEventListener() {
        mBtStartRecord.setOnClickListener(this);
        mBtStopRecord.setOnClickListener(this);
        mBtPlayRecord.setOnClickListener(this);
        mBtStopPlayRecord.setOnClickListener(this);
        mBtPlayWAV.setOnClickListener(this);
        mBtPcm2wav.setOnClickListener(this);
        mBtPlayAAC.setOnClickListener(this);
        mBtPcm2aac.setOnClickListener(this);
        mTvEncodeProcess.setOnClickListener(this);
    }

    private void initView() {
        mBtStartRecord = findViewById(R.id.bt_start_record);
        mBtStopRecord = findViewById(R.id.bt_stop_record);
        mBtPlayRecord = findViewById(R.id.bt_play_record);
        mBtStopPlayRecord = findViewById(R.id.bt_stop_play_record);
        mBtPlayWAV = findViewById(R.id.bt_play_wav);
        mBtPcm2wav = findViewById(R.id.bt_pcm2wav);
        mBtPlayAAC = findViewById(R.id.bt_play_aac);
        mBtPcm2aac = findViewById(R.id.bt_pcm2aac);
        mTvEncodeProcess = findViewById(R.id.tv_encode_process);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_record:
                initPermission();
                break;
            case R.id.bt_stop_record:
                stopRecord();
                break;
            case R.id.bt_play_record:
                playRecord();
                break;
            case R.id.bt_stop_play_record:
                stopPlayRecord();
                break;
            case R.id.bt_play_wav:
                playWav();
                break;
            case R.id.bt_pcm2wav:
                playPcm2Wav();
                break;
            case R.id.bt_play_aac:
                playAac();
                break;
            case R.id.bt_pcm2aac:
                playPCM2Aac();
                break;
        }
    }

    private void playPCM2Aac() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + "wav";
        final AACUtil aacUtil = AACUtil.newInstance();
        aacUtil.setIOPath(path,result);
        aacUtil.prepare();
        aacUtil.startAsync();
        aacUtil.setOnCompleteListener(new AACUtil.OnCompleteListener() {
            @Override
            public void completed() {
                aacUtil.release();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
        final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.applyPattern("##.##%");
        aacUtil.setOnProgressListener(new AACUtil.OnProgressListener() {
            @Override
            public void progress(final long current, final long total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvEncodeProcess.setText(current + "/" + total + "  " + df.format((double) current / total));
                    }
                });
            }
        });
    }

    private void playAac() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + "aac";
        AudioTrackManager.getInstance().startPlay(result);
    }

    private void playPcm2Wav() {
        String path = mFileRoot + File.separator + mFileName;
        String s = path.substring(0, path.lastIndexOf(".")) + "wav";
        WAVUtil.convertPcm2Wav(path,s,SAMPLERATEHZ,CHANNAL_CONFIG,mAudioFormat);
    }

    private void playWav() {
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".wav";
        AudioTrackManager.getInstance().startPlay(result);
    }

    private void stopPlayRecord() {
        AudioTrackManager.getInstance().stopPlay();
    }

    private void playRecord() {

        String path = mFileRoot + File.separator + mFileName;
        Log.d("wangchao_log","playRecord path------------------------------->"+path);

        AudioTrackManager.getInstance().startPlay(path);
    }

    private void stopRecord() {
        Log.d("wangchao_log","stopRecord------------------------------->");
        isRecording = false;
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.stop();
            }
            if (mAudioRecord != null){
                mAudioRecord.release();
            }
        }
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        Log.d("wangchao_log","startRecord------------------------------->");
        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize || AudioRecord.ERROR == mMinBufferSize) {
                throw new RuntimeException("Unable to getMinBufferSize");
        }else{
            //首先销毁线程
            destroyThread();
            if (mThread == null){
                mThread = new Thread(this);
                mThread.start();
            }
        }
    }

    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                Thread.sleep(500);
                mThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mThread = null;
        } finally {
            mThread = null;
        }
    }

    @Override
    public void run() {
        Log.d("wangchao_log","startRecord run------------------------------->");
        //开始录音状态
        isRecording = true;
        //创建本地保存文件
        createLocalFile();
        //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            initData();
        }
        //最小缓冲区
        byte[] buffer = new byte[mMinBufferSize];
        //获取文件的数据流
        try {
            mDataOutputStream = new DataOutputStream(new FileOutputStream(mRecordingFile));
            //开始录音
            mAudioRecord.startRecording();
            //getRecordingState获取当前AudioRecording是否正在采集数据的状态
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mMinBufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);
                }
                mDataOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stopRecord();
                if (mDataOutputStream != null) {
                    mDataOutputStream.close();
                    mDataOutputStream = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createLocalFile() {
        mRecordingFile = new File(mFileRoot, mFileName);
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "创建储存音频文件出错");
        }
    }
}
