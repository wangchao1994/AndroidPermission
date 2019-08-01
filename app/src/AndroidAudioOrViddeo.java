音视频笔记:

1.模拟信号数字化:一般要经过采样,量化,编码(这里的编码值信道编码,而非信源编码)
2.PCM数据描述:量化格式（sampleFormat）、采样率（sampleRate）、声道数（channel）
     以CD的音质为例：量化格式（有的地方描述为位深度）为16比特（2字节），采样率为44100，声道数为2
     比特率:44100 * 16 * 2 = 1378.125kbps,如此CD音质所占内存空间大小约:1378.125 * 60 / 8 / 1024 = 10.09MB
3.音频编码:其基本指标为压缩比,一般压缩比小于1
     压缩算法:有损压缩和无损压缩  常见压缩编码算法，如PCM、WAV、AAC、MP3、Ogg等
     音频压缩主要是去除冗余信息，从而实现数据量的压缩
4.YUV视频帧数据:
     YUV主要应用与优化彩色视频信号的传输
     YUV主要应用于优化彩色视频信号的传输，使其向后兼容老式黑白电视。与RGB视频信号传输相比，它最大的优点在于只需要占用极少的频宽（RGB要求三个独立的视频信号同时传输）。其中“Y”表示明亮度	（Luminance或Luma），也称灰阶值；而“U”和“V”表示的则是色度（Chrominance或Chroma），它们的作用是描述影像的色彩及饱和度，用于指定像素的颜色
     Y的取值范围都是16～235，UV的取值范围都是16～240 , YUV最常用的采样格式是4：2：0
     RGB相比而言:对于计算一帧1280x720的视频数据,如果用YUV420P的格式来表示,720P:其数据大小：1280 * 720 * 1 + 1280 * 720 * 0.5 ~= 1.318MB
				每个Y/U/V的采样点用8bit来表示，即1Bytes。
				720p: 1280 * 720 * 8 *(1 + 1/4 + 1/4) / 8 ～= 1.318359375 M
				1080p:1920 * 1080 * 8 *(1 + 1/4 + 1/4) / 8 ～= 2.966308594 M
	 那么对于一部一小时的电影来说：一秒钟需要24张这样大小的图片,按照YUV420P的格式来计算：其数据量大小：1.318MB * 24fps * 60 * 60 = 166.8GB,很显然用这种方式进行数据存储明显不行。那么视频的编码与解码就变得固然重要。


对于音频编码压缩，我们知道其主要是去除冗余信息，从而实现数据量的压缩,可是对于视频压缩，该如何实现？
	视频数据具有极强的相关性,也就是说具有大量的冗余信息，包括空间上的和时间上的冗余信息。
	帧间编码技术可以去除空间上或时间上的冗余信息:
		·运动补偿：运动补偿是通过先前的局部图像来预测、补偿当前的局部图像，它是减少帧序列冗余信息的有效方法。
		·运动表示：不同区域的图像需要使用不同的运动矢量来描述运动信息。
		·运动估计：运动估计是从视频序列中抽取运动信息的一整套技术。
	对于视频，ISO同样也制定了标准：Motion JPEG即MPEG，MPEG算法是适用于动态视频的压缩算法，它除了对单幅图像进行编码外，还利用图像序列中的相关原则去除冗余，这样可以大大提高视频的压缩比。截至目前，MPEG的版本一直在不断更新中，主要包括这样几个版本：Mpeg1（用于VCD）、Mpeg2（用于DVD）、Mpeg4 AVC（现在流媒体使用最多的就是它）。
	相比较于ISO制定的MPEG的视频压缩标准，ITU-T制定的H.261、H.262、H.263、H.264一系列视频编码标准是一套单独的体系。其中，H.264集中了以往标准的所有优点，并吸取了以往标准的经验，采用的是简洁设计，这使得它比Mpeg4更容易推广。现在使用最多的就是H.264标准，H.264创造了多参考帧、多块类型、整数变换、帧内预测等新的压缩技术，使用了更精细的分像素运动矢量（1/4、1/8）和新一代的环路滤波器，这使得压缩性能得到大大提高，系统也变得更加完善。
			
	
//-------------------------------------------------------------------------------------------------------------
编码：对原始音视频文件进行压缩
解码：对压缩过的音视频文件解压缩

Android音视频开发流程：
	播放：获取流->解码->播放
	录制：录制音视频->剪辑->编码->上传
	直播：录制音视频-->编码-->流媒体传输-->服务器--->流媒体传输到其他app-->解码-->播放

	这其中就涉及到几个重要的过程：音视频的录制，音视频的编码与解码，流媒体传输，音视频的播放

//------------------------------------------------------------------------------------------------------------------
一.音频采集
	AudioRecord与MediaRecorder区别：AudioRecord采集的是原始音频数据,MediaRecorder是直接对采集的音频数据进行编码压缩并存储为文件
	1.AudioRecord录制：
		参数配置：public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,int bufferSizeInBytes)
		audioSource:音频采集的输入源
		sampleRateInHz:采样率,目前44100Hz是唯一可以保证兼容所有Android手机。人耳能听到的声音频率范围在20Hz到20KHz之间，为了不失真，采样频率应该在40KHz以上。
		channelConfig:AudioFormat中常量定义
					public static final int CHANNEL_IN_LEFT = 0x4;
					public static final int CHANNEL_IN_RIGHT = 0x8;
					public static final int CHANNEL_IN_FRONT = 0x10;
					//单通道
					public static final int CHANNEL_IN_MONO = CHANNEL_IN_FRONT;   
					//双通道
					public static final int CHANNEL_IN_STEREO = (CHANNEL_IN_LEFT | CHANNEL_IN_RIGHT);
		
		audioFormat:用来配置数据位宽
					public static final int ENCODING_PCM_16BIT = 2;
					public static final int ENCODING_PCM_8BIT = 3;
		bufferSizeInBytes:配置的是AudioRecord内部音频缓冲区的大小，该值不能低于一帧音频祯数据的大小，int size=采样率 * 采样时间 * 位宽 * 通道数
		代码实例：
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
							//直接将录制的结果保存至文件中
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

		MediaRecorder录制：
	    private final String RECORD_PATH = Environment.getExternalStorageDirectory()+File.separator+ "test.amr";
		//开始录制
		private void startRecord() {
		    File mOutRecordFile = new File(RECORD_PATH);
		    if (!mOutRecordFile.exists()){
		        try {
		            mOutRecordFile.createNewFile();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    if (mMediaRecorder == null){
		        mMediaRecorder = new MediaRecorder();
		    }
		    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		    //AudioSystem.setParameters("LRChannelSwitch=1");
		    AudioSystem.setParameters("SET_MIC_CHOOSE=1");
		    mMediaRecorder.setOutputFile(mOutRecordFile.getAbsolutePath());
		    try {
		        mMediaRecorder.prepare();
		        mMediaRecorder.start();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		//停止录制
		private void stopRecorder(){
		    if (mMediaRecorder != null) {
				//避免初始化录制错误
		        mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.setPreviewDisplay(null);
		        try {
		            mMediaRecorder.stop();
		        } catch (IllegalStateException e) {
		            e.printStackTrace();
		        } catch (RuntimeException e) {
		            e.printStackTrace();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}
		
	2.音频播放AudioTrack
		android中常用的音频播放api,MediaPlayer,SoundPool,AudioTrack
		MediaPlayer适用于长音频文件的播放
		SoundPool适用于音频片段的播放
		AudioTrack更接近于底层，播放PCM原始数据

		AudioTrack的使用: public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat,int bufferSizeInBytes, int mode)

		streamType:音频策略管理，参数可选值在AudioManager类中。
					STREAM_MUSCI：音乐声
					STREAM_RING：铃声
					STREAM_NOTIFICATION：通知声
		sampleRateInHz:采样率，范围在4000~192000
		channelConfig:通道数的配置
		audioFormat:用来配置数据位宽
					public static final int ENCODING_PCM_16BIT = 2;//兼容所有Android手机
					public static final int ENCODING_PCM_8BIT = 3;
		bufferSizeInBytes:配置的是AudioRecord内部音频缓冲区的大小
		mode:
			AudioTrack有两种播放方式 MODE_STATIC和MODE_STREAM
			前者是一次性将所有数据写入播放缓冲区，然后播放
			后者是一边写入一边播放

		代码实例：AudioTrack播放音频
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

		MediaPlayer播放音频：
			1.//直接播放文件音频
				private void playRecord(){
					if (mMediaPlayer == null){
						mMediaPlayer = new MediaPlayer();
					}
					mMediaPlayer.reset();
					try{
						mMediaPlayer.setDataSource(RECORD_PATH);
						mMediaPlayer.prepare();
						mMediaPlayer.start();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			
			2.直接播放目录音频资源
				mMediaPlayer = MediaPlayer.create(this,R.raw.test);
				mMediaPlayer.setLooping(false);
				mMediaPlayer.start();
			//停止播放
			private void stopPlay() {
				if (mMediaPlayer != null) {
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}
			}
			
		SoundPool播放音频：
			private SoundPool mSoundPool;
			private static final int DEFAULT_INVALID_SOUND_ID = -1;
			private int mSoundId = -1;
			private int mStreamId = -1;

			private void playAudio() {
				SoundPool mSoundPool = createSoundPool();
				if (mSoundPool == null) return;
				mSoundPool.setOnLoadCompleteListener(this);
				if (mSoundId == DEFAULT_INVALID_SOUND_ID) {
				    mSoundId = mSoundPool.load(getApplicationContext(), R.raw.speaker_ring, 1/*0*/);
				} else {
				    if (mStreamId == DEFAULT_INVALID_SOUND_ID)
				        onLoadComplete(mSoundPool, 0, 0);
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
				} else {
				    mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
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
				if (mSoundPool != null) {
				    mStreamId = mSoundPool.play(mSoundId, 1.0f, 1.0f, 16, -1, 1.0f);
				}
			}
			public void pause() {
				if (mSoundPool != null) {
				    mSoundPool.pause(mStreamId);
				}
			}
			public void resume() {
				if (mSoundPool != null) {
				    mSoundPool.resume(mStreamId);
				}
			}
			public void stop() {
				if (mSoundPool != null) {
				    mSoundPool.stop(mStreamId);
				    mStreamId = DEFAULT_INVALID_SOUND_ID;
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
			private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				    if(mSoundPool == null) return;
				    float volume = progress * 1.0f / seekBar.getMax();
				    switch (seekBar.getId()) {
				        case R.id.left_vloume:  //设置左volume
				            mCruLeftVolume = volume;
				            mSoundPool.setVolume(mStreamID, mCruLeftVolume, mCurRightVolume);
				            break;
				        case R.id.right_vloume: // 设置右volume
				            mCurRightVolume = volume;
				            mSoundPool.setVolume(mStreamID, mCruLeftVolume, mCurRightVolume);
				            break;
				    }
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			};

二：wav文件的存储和解析
	wav:一种无损的音频文件格式	,pcm是无损wav文件中音频数据的一种编码方式，wav还可以用其它方式编码。	
	pcm是一种未经压缩的编码方式
	wav是一种无损的音频文件格式
	
	将录音文件保存为wav格式，需要手动填充wav的文件头信息

	代码实例：
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;

	/**
	 * pcm数据转wav文件
	 */
	public class WAVUtil {
		/**
		 * Pcm转wav文件
		 * @param inPcmFile
		 * @param outWavFile
		 * @param sameRate
		 * @param channels
		 * @param bitNum
		 */
		public static void convertPcm2Wav(String inPcmFile , String outWavFile,int sameRate, int channels,int bitNum){
		    FileInputStream mFileInputStream = null;
		    FileOutputStream mFileOutputStream = null;
		    byte[] data = new  byte[1024];
		    //采样率字节数
		    long byteSizes = sameRate * channels * bitNum / 8;
		    try {
		        mFileInputStream = new FileInputStream(inPcmFile);
		        mFileOutputStream = new FileOutputStream(outWavFile);
		        long totalAudioLen = mFileInputStream.getChannel().size();
		        long totalDataLen = totalAudioLen + 36;
		        writeWaveFileHeader(mFileOutputStream, totalAudioLen, totalDataLen, sameRate, channels, byteSizes);
		        int length = 0;
		        while ((length = mFileInputStream.read(data)) > 0) {
		            mFileOutputStream.write(data, 0, length);
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		/**
		 * 输出WAV文件
		 *
		 * @param mFileOutputStream           WAV输出文件流
		 * @param totalAudioLen 整个音频PCM数据大小
		 * @param totalDataLen  整个数据大小
		 * @param sampleRate    采样率
		 * @param channels      声道数
		 * @param byteRate      采样字节byte率
		 * @throws IOException
		 */
		private static void writeWaveFileHeader(FileOutputStream mFileOutputStream, long totalAudioLen, long totalDataLen, int sampleRate, int channels, long byteRate) throws IOException {
		    byte[] header = new byte[44];
		    header[0] = 'R'; // RIFF
		    header[1] = 'I';
		    header[2] = 'F';
		    header[3] = 'F';
		    header[4] = (byte) (totalDataLen & 0xff);//数据大小
		    header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		    header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		    header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		    header[8] = 'W';//WAVE
		    header[9] = 'A';
		    header[10] = 'V';
		    header[11] = 'E';
		    //FMT Chunk
		    header[12] = 'f'; // 'fmt '
		    header[13] = 'm';
		    header[14] = 't';
		    header[15] = ' ';//过渡字节
		    //数据大小
		    header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		    header[17] = 0;
		    header[18] = 0;
		    header[19] = 0;
		    //编码方式 10H为PCM编码格式
		    header[20] = 1; // format = 1
		    header[21] = 0;
		    //通道数
		    header[22] = (byte) channels;
		    header[23] = 0;
		    //采样率，每个通道的播放速度
		    header[24] = (byte) (sampleRate & 0xff);
		    header[25] = (byte) ((sampleRate >> 8) & 0xff);
		    header[26] = (byte) ((sampleRate >> 16) & 0xff);
		    header[27] = (byte) ((sampleRate >> 24) & 0xff);
		    //音频数据传送速率,采样率*通道数*采样深度/8
		    header[28] = (byte) (byteRate & 0xff);
		    header[29] = (byte) ((byteRate >> 8) & 0xff);
		    header[30] = (byte) ((byteRate >> 16) & 0xff);
		    header[31] = (byte) ((byteRate >> 24) & 0xff);
		    // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
		    header[32] = (byte) (channels * 16 / 8);
		    header[33] = 0;
		    //每个样本的数据位数
		    header[34] = 16;
		    header[35] = 0;
		    //Data chunk
		    header[36] = 'd';//data
		    header[37] = 'a';
		    header[38] = 't';
		    header[39] = 'a';
		    header[40] = (byte) (totalAudioLen & 0xff);
		    header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		    header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		    header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		    mFileOutputStream.write(header, 0, 44);
		}
	}


三：视频的采集（Camera预览数据,GoogleCamera）

四：音视频的分离
	代码实例：
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
			mediaMuxer.start();
            //封装视频
            if (-1 != videoTrackIndex){
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                bufferInfo.presentationTimeUs = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(100 * 1024);
                while (true){
                    int sampleData = mVideoExtractor.readSampleData(byteBuffer, 0);
                    if (sampleData < 0) break;
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
		
//------------------------------------------------------------------------------------------------------
开源框架FFmpeg:
1.引用头文件
	在Android的C++环境下，那么可直接以下面这种方式引用头文件：
	extern "C" {
		#include "3rdparty/ffmpeg/include/libavformat/avformat.h"
		#include "3rdparty/ffmpeg/include/libswscale/swscale.h"
		#include "3rdparty/ffmpeg/include/libswresample/swresample.h"
		#include "3rdparty/ffmpeg/include/libavutil/pixdesc.h"
	}
	作为一种面向对象的语言，C++支持函数的重载，而面向过程的C语言是不支持函数重载的。同一个函数在C++中编译后与其在C中编译后，在符号表中的签名是不同的，假如对于同一个函数：
	void decode(float position, float duration)
	在C语言中编译出来的签名是_decoder，而在C++语言中，一般编译器的生成则类似于_decode_float_float。虽然在编译阶段是没有问题的，但是在链接阶段，如果不加extern“C”关键字的话，那么将会链接_decoder_float_float这个方法签名；而如果加了extern“C”关键字的话，那么寻找的方法签名就是_decoder。而FFmpeg就是C语言书写的，编译FFmpeg的时候所产生的方法签名都是C语言类型的签名，所以在C++中引用FFmpeg必须要加extern“C”关键字。

2.注册协议、格式与编解码器
	avformat_network_init();
	av_register_all();	

3.打开媒体文件源，并设置超时回调
	AVFormatContext *formatCtx = avformat_alloc_context();
	AVIOInterruptCB int_cb = {interrupt_callback, (__bridge void *)(self)};
	formatCtx->interrupt_callback = int_cb;
	avformat_open_input(formatCtx, path, NULL, NULL);
	avformat_find_stream_info(formatCtx, NULL);

4.寻找各个流，并且打开对应的解码器

	寻找音视频流：
	for(int i = 0; i < formatCtx->nb_streams; i++) {
		AVStream* stream = formatCtx->streams[i];
		if(AVMEDIA_TYPE_VIDEO == stream->codec->codec_type) {
			// 视频流
			videoStreamIndex = i;
		} else if(AVMEDIA_TYPE_AUDIO == stream->codec->codec_type ){
			// 音频流
			audioStreamIndex = i;
		}
	}

	打开音频流解码器：
	AVCodecContext * audioCodecCtx = audioStream->codec;
	AVCodec *codec = avcodec_find_decoder(audioCodecCtx ->codec_id);
	if(!codec){
		// 找不到对应的音频解码器
	}
	int openCodecErrCode = 0;
	if ((openCodecErrCode = avcodec_open2(codecCtx, codec, NULL)) < 0){
		// 打开音频解码器失败
	}


	打开视频流解码器：
	AVCodecContext *videoCodecCtx = videoStream->codec;
	AVCodec *codec = avcodec_find_decoder(videoCodecCtx->codec_id);
	if(!codec) {
		// 找不到对应的视频解码器
	}
	int openCodecErrCode = 0;
	if ((openCodecErrCode = avcodec_open2(codecCtx, codec, NULL)) < 0) {
		// 打开视频解码器失败
	}

5.初始化解码后数据的结构体
	构建音频的格式转换对象以及音频解码后数据存放的对象：
	SwrContext *swrContext = NULL;
	if(audioCodecCtx->sample_fmt ！= AV_SAMPLE_FMT_S16) {
		// 如果不是我们需要的数据格式
		swrContext = swr_alloc_set_opts(NULL,
		        outputChannel, AV_SAMPLE_FMT_S16, outSampleRate,
		        in_ch_layout, in_sample_fmt, in_sample_rate, 0, NULL);
		if(!swrContext || swr_init(swrContext)) {
		    if(swrContext) {
		        swr_free(&swrContext);
		    }
		}
		audioFrame = avcodec_alloc_frame();
	}
	构建视频的格式转换对象以及视频解码后数据存放的对象：
	AVPicture picture;
	bool pictureValid = avpicture_alloc(&picture,
		    PIX_FMT_YUV420P,
		    videoCodecCtx->width,
		     videoCodecCtx->height) == 0;
	if (!pictureValid){
		// 分配失败
		return false;
	}
	swsContext = sws_getCachedContext(swsContext,
		    videoCodecCtx->width,
		    videoCodecCtx->height,
		    videoCodecCtx->pix_fmt,
		    videoCodecCtx->width,
		    videoCodecCtx->height,
		    PIX_FMT_YUV420P,
		    SWS_FAST_BILINEAR,
		     NULL, NULL, NULL);
	videoFrame = avcodec_alloc_frame();

6.读取流内容并且解码
	AVPacket packet;
	int gotFrame = 0;
	while(true) {
		if(av_read_frame(formatContext, &packet)) {
		    // End Of File
		    break;
		}
		int packetStreamIndex = packet.stream_index;
		if(packetStreamIndex == videoStreamIndex) {
		    int len = avcodec_decode_video2(videoCodecCtx, videoFrame,
		            &gotFrame, &packet);
		    if(len < 0) {
		        break;
		    }
		    if(gotFrame) {
		        self->handleVideoFrame();
		    }
		} else if(packetStreamIndex == audioStreamIndex) {
		    int len = avcodec_decode_audio4(audioCodecCtx, audioFrame,
		            &gotFrame, &packet);
		    if(len < 0) {
		        break;
		    }
		    if(gotFrame) {
		        self->handleVideoFrame();
		    }
		}
	}

7.处理解码后的裸数据
	音频裸数据处理：
	void* audioData;
	int numFrames;
		if(swrContext) {
		    int bufSize = av_samples_get_buffer_size(NULL, channels,
		            (int)(audioFrame->nb_samples * channels),
		            AV_SAMPLE_FMT_S16, 1);
		    if (!_swrBuffer || _swrBufferSize < bufSize) {
		        swrBufferSize = bufSize;
		        swrBuffer = realloc(_swrBuffer, _swrBufferSize);
		    }
		    Byte *outbuf[2] = { _swrBuffer, 0 };
		    numFrames = swr_convert(_swrContext, outbuf,
		            (int)(audioFrame->nb_samples * channels),
		            (const uint8_t **)_audioFrame->data,
		            audioFrame->nb_samples);
		audioData = swrBuffer;
	} else {
		    audioData = audioFrame->data[0];
		    numFrames = audioFrame->nb_samples;
	}
	
	视频裸数据处理：
	uint8_t* luma;
	uint8_t* chromaB;
	uint8_t* chromaR;
	if(videoCodecCtx->pix_fmt == AV_PIX_FMT_YUV420P ||
		    videoCodecCtx->pix_fmt == AV_PIX_FMT_YUVJ420P){
		luma = copyFrameData(videoFrame->data[0],
		        videoFrame->linesize[0],
		        videoCodecCtx->width,
		        videoCodecCtx->height);
		chromaB = copyFrameData(videoFrame->data[1],
		        videoFrame->linesize[1],
		        videoCodecCtx->width / 2,
		        videoCodecCtx->height / 2);
		chromaR = copyFrameData(videoFrame->data[2],
		        videoFrame->linesize[2],
		        videoCodecCtx->width / 2,
		        videoCodecCtx->height / 2);
	} else{
		sws_scale(_swsContext,
		         (const uint8_t **)videoFrame->data,
		        videoFrame->linesize,
		        0,
		        videoCodecCtx->height,
		        picture.data,
		        picture.linesize);
		luma = copyFrameData(picture.data[0],
		        picture.linesize[0],
		        videoCodecCtx->width,
		        videoCodecCtx->height);
		chromaB = copyFrameData(picture.data[1],
		        picture.linesize[1],
		        videoCodecCtx->width / 2,
		        videoCodecCtx->height / 2);
		chromaR = copyFrameData(picture.data[2],
		        picture.linesize[2],
		        videoCodecCtx->width / 2,
		        videoCodecCtx->height / 2);
	}

8.关闭所有资源

	关闭音频资源:
	if (swrBuffer) {
		free(swrBuffer);
		swrBuffer = NULL;
		swrBufferSize = 0;
	}
	if (swrContext) {
		swr_free(&swrContext);
		swrContext = NULL;
	}
	if (audioFrame) {
		av_free(audioFrame);
		audioFrame = NULL;
	}
	if (audioCodecCtx) {
		avcodec_close(audioCodecCtx);
		audioCodecCtx = NULL;
	}

	关闭视频资源：

	if (swsContext) {
		sws_freeContext(swsContext);
		swsContext = NULL;
	}
	if (pictureValid) {
		avpicture_free(&picture);
		pictureValid = false;
	}
	if (videoFrame) {
		av_free(videoFrame);
		videoFrame = NULL;
	}
	if (videoCodecCtx) {
		avcodec_close(videoCodecCtx);
		videoCodecCtx = NULL;
	}

	关闭连接资源：

	if (formatCtx) {
		avformat_close_input(&formatCtx);
		formatCtx = NULL;
	}

//------------------------------------------------------------------------------------------------------------------------------------------------------------------
图像处理：(明度,色相,饱和度)
	色相：色相通俗的说就是“颜色”，色相的改变就是颜色的改变，色相的调节伴随着红橙黄绿蓝紫的变化。
	明度: 明度通俗的来说就是“光照度”，明度的改变就是光照在物体上带来的改变，明度的调节伴随着越高，光越强，越泛白（就像过曝一样，往白色上偏离）；越低，光越弱，越往黑里偏
	饱和度:饱和度通俗的说就是“色彩的纯度”，饱和度的改变会影响颜色的鲜艳程度，以红色为例子，越高，越接近红色，越低则越接近灰色（黑白）

	亮度调节：一种方法是非线性亮度调节，另外一种方法是线性亮度调节
		非线性亮度调节：对于图像的RGB通道，每个通道增加相同的增量
			伪代码：
				byte* image = loadImage();
				byte* r,g,b = interlaceImage(image);
				int brightness = 3;
				r += brightness;
				g += brightness;
				b += brightness;
		第一步调用loadImage方法将一张图片加载到内存；
		第二步将RGB通道分离开后，再对这三个通道分别增加相应的亮度值。
		这种亮度调节方法的优点是，代码简单，亮度调整速度快；缺点是图像信息损失比较大，调整过的图像平淡，无层次感。
	
		线性亮度调节：HSL色彩模式 。HSL是工业界的一种颜色标准，代表色相（Hue）、饱和度（Saturation）、明度（Lightness）三个通道的颜色，每个通道都可使用0～255的数值来表示。这种调节是通过对色相、饱和度、明度三个颜色通道的变化及其相互之间的叠加来得到各种颜色。线性亮度调节就是先将RGB表示的图像转换为HSL的颜色空间，然后对L通道进行调节，得到新的L值，再与HS通道合并为新的HSL，最终转换为RGB得到新的图像。
		下面用伪代码来实现上述的过程：
		第一步先用RGB计算出L值：L = (max(r, max(g, b)) + min(r, min(g, b))) / 2;
		L的取值范围是[0，255]，然后利用L值与RGB分别求出HS部分的值：
			if(L > 128) {
				rHS = (r * 128 – (L - 128) * 256) / (256 - L);
				gHS = (g * 128 – (L - 128) * 256) / (256 - L);
				bHS = (b * 128 – (L - 128) * 256) / (256 - L);
			} else {
				rHS = r * 128 / L;
				gHS = g * 128 / L;
				bHS = b * 128 / L;
			}

		再调整L值的亮度得到新的L值，并用新的L值和上面计算出的HS的值求出新的RGB，代码如下：
		int delta = 20;// [0-255]
		newL = L + delta – 128;
		if(newL > 0) {
			newR = rHS + (256 - rHS) * newL / 128;
			newG = gHS + (256 - gHS) * newL / 128;
			newB = bHS + (256 - bHS) * newL / 128;
		} else {
			newR = rHS + rHS * newL / 128;
			newG = gHS + gHS * newL / 128;
			newB = bHS + bHS * newL / 128;
		}
		得到新的RGB像素点就是调节亮度之后的像素点。综上所述，线性亮度调节的优点是调节过的图像层次感很强；缺点是代码复杂，调节速度慢，而且当亮度增减量较大时图像有很大失真。
	
	Android中代码处理：(对于照片处理的话，可能效率不是很高)
		//设置色相,饱和度,明度
		public Bitmap bitmapOperation (Bitmap bitmap , float hueValues, float saturationValues, float lightValues){
		    Bitmap blankBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		    Canvas canvas=new Canvas(blankBitmap);
		    Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		    ColorMatrix mHColorMatrix =new ColorMatrix();
		    mHColorMatrix.setRotate(0,hueValues);
		    mHColorMatrix.setRotate(1,hueValues);
		    mHColorMatrix.setRotate(2, hueValues);
		    ColorMatrix mSColorMatrix=new ColorMatrix();
		    mSColorMatrix.setSaturation(saturationValues);
		    ColorMatrix mLightColorMatrix=new ColorMatrix();
		    mLightColorMatrix.setScale(lightValues,lightValues,lightValues,1);
		    ColorMatrix mImageMatrix=new ColorMatrix();
		    mImageMatrix.postConcat(mHColorMatrix);
		    mImageMatrix.postConcat(mSColorMatrix);
		    mImageMatrix.postConcat(mLightColorMatrix);
		    mPaint.setColorFilter(new ColorMatrixColorFilter(mImageMatrix));
		    canvas.drawBitmap(bitmap,0,0,mPaint);
		    return blankBitmap;
		}
		当前也可以直接在画笔中设置色相，明度，饱和度(未验证)
		private float[] direction = new float[]{1, 1, 1};
		private float light = 0.4f;
		private float specular = 6;
		private float blur = 3.5f;
		private Paint initPaint() {
		    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		    paint.setColor(Color.WHITE);
		    EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
		    paint.setMaskFilter(emboss);
		    paint.setTextSize(PAINT_TEXT_SIZE);
		    paint.setDither(true);
		    paint.setFilterBitmap(true);
		    return paint;
		}
		canvas.drawBitmap(bitmap,new Matrix(),initPaint());

添加自定义文字：
	1.创建和原图一样大小的Bitmap
    public void createBitmap(Bitmap mPhotoBitmap) {
        mDrawBitmap = Bitmap.createBitmap(mPhotoBitmap.getWidth(), mPhotoBitmap.getHeight(), mPhotoBitmap.getConfig());
        mCanvas = new Canvas(mDrawBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
        mCanvas.setBitmap(mDrawBitmap);
    }
    private float[] direction = new float[]{1, 1, 1};
    private float light = 0.4f;
    private float specular = 6;
    private float blur = 3.5f;
    private Paint initPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
        paint.setMaskFilter(emboss);
        paint.setTextSize(PAINT_TEXT_SIZE);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        return paint;
    }
	2.绘制原图：
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    	mCanvas.drawBitmap(mSrcPhotoBitmap, 0, 0, null);
	3.绘制自定义文字：
		private Bitmap drawMeasureValuesToBitmap(Bitmap alterBitmap, String sDrawValuesText) {
		    Canvas canvas = new Canvas(alterBitmap);
		    if (!TextUtils.isEmpty(sDrawValuesText)) {
		        canvas.drawText(sDrawValuesText, 100,100,100,100, paint);
		    }
		    canvas.save(Canvas.ALL_SAVE_FLAG);
		    canvas.restore();
		    return alterBitmap;
		}
	
//---------------------------------------------------------------------------------------------------------------------------------
	1.创建和原始图片同样大小的bitmap:
			Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);	
	2.创建画布：
			Canvas localCanvas = new Canvas(textBitmap);
	3.创建画笔：
			localPaint.setColor(Color.argb(255, Color.red(textColor),
		            Color.green(textColor), Color.blue(textColor)));
		    localPaint.setShadowLayer(shadowRadius, textShadowXOffset, textShadowYOffset,
		            Color.argb(255, Color.red(shadowColor),
		                    Color.green(shadowColor), Color.blue(shadowColor)));
		    localPaint.setTextSize(textSize);
		    localPaint.setAntiAlias(true);
		    localPaint.setTextAlign(Paint.Align.CENTER);
	4.在规定区域内添加文字：
		    Rect targetRect = new Rect(textLabelLeft, textLabelTop,
		            textLabelLeft + textLabelWidth, textLabelTop + textLabelHeight);
		    Paint.FontMetricsInt fontMetrics = localPaint.getFontMetricsInt();
		    int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
		    localCanvas.drawText(text, targetRect.centerX(), baseline, localPaint);
	5.将画好的Bitmap复制到内存区域，返回给调用者：
			int capacity = width * height * 4;
			ByteBuffer dst = ByteBuffer.allocate(capacity);
			textBitmap.copyPixelsToBuffer(dst);
			dst.position(0);
			dst.get(buffer, 0, capacity);
//------------------------------------------------------------------------------------------------------------------------------------


	













	


