package com.android.androidaudiolearning.media_extractor;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 封装音频，视频
 */
public class MuxerVideoAndAudio {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void muxVideoAndAudio(String path, String outPath){
        try {
            MediaMuxer mediaMuxer = new MediaMuxer(outPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            //获取视频的MediaExtractor
            MediaExtractor mVideoExtractor = new MediaExtractor();
            mVideoExtractor.setDataSource(path);
            int videoTrackIndex = -1;
            for (int i = 0; i < mVideoExtractor.getTrackCount(); i++) {
                MediaFormat trackFormat = mVideoExtractor.getTrackFormat(i);
                String formatString = trackFormat.getString(MediaFormat.KEY_MIME);
                if (formatString != null && formatString.startsWith("video/")){
                    mVideoExtractor.selectTrack(i);
                    videoTrackIndex = mediaMuxer.addTrack(trackFormat);
                    break;
                }
            }

            //获取音频的MediaExtractor
            MediaExtractor mAudioExtractor = new MediaExtractor();
            mAudioExtractor.setDataSource(path);
            int audioTrackIndex = -1;
            for (int i = 0; i < mAudioExtractor.getTrackCount(); i++) {
                MediaFormat trackFormat = mAudioExtractor.getTrackFormat(i);
                String formatString = trackFormat.getString(MediaFormat.KEY_MIME);
                if (formatString != null && formatString.startsWith("audio/")){
                    mAudioExtractor.selectTrack(i);
                    audioTrackIndex = mediaMuxer.addTrack(trackFormat);
                    break;
                }
            }

            //封装视频
            if (-1 != videoTrackIndex){
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                bufferInfo.presentationTimeUs = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(100 * 1024);
                while (true){
                    int sampleData = mVideoExtractor.readSampleData(byteBuffer, 0);
                    if (sampleData < 0){
                        break;
                    }
                    bufferInfo.offset = 0;
                    bufferInfo.size = sampleData;
                    bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    bufferInfo.presentationTimeUs = mVideoExtractor.getSampleTime();
                    mediaMuxer.writeSampleData(videoTrackIndex,byteBuffer,bufferInfo);
                    mVideoExtractor.advance();
                }
            }

            //封装音频
            if (-1 != audioTrackIndex){
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                bufferInfo.presentationTimeUs = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(100 * 1024);
                while (true){
                    int sampleData = mAudioExtractor.readSampleData(byteBuffer, 0);
                    if (sampleData < 0)break;
                    bufferInfo.offset = 0;
                    bufferInfo.size = sampleData;
                    bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    bufferInfo.presentationTimeUs = mAudioExtractor.getSampleTime();
                    mediaMuxer.writeSampleData(audioTrackIndex,byteBuffer,bufferInfo);
                    mAudioExtractor.advance();
                }
            }
            //释放资源
            mVideoExtractor.release();
            mAudioExtractor.release();

            mediaMuxer.stop();
            mediaMuxer.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
