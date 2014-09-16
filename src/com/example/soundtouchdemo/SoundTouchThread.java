package com.example.soundtouchdemo;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.jeremyfeinstein.slidingmenu.example.SetAlarmFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

public class SoundTouchThread extends Thread {

	private BlockingQueue<short[]> recordQueue;
	private Handler handler;
	private static final long TIME_WAIT_RECORDING = 100;
	private volatile boolean setToStopped = false;
	private JNISoundTouch soundtouch = new JNISoundTouch();
	private JNISoundTouch soundtouch2 = new JNISoundTouch();
	private JNISoundTouch soundtouch3 = new JNISoundTouch();
	private JNISoundTouch soundtouch4 = new JNISoundTouch();
	private JNIMp3Encode mp3Encode = new JNIMp3Encode();
	private LinkedList<byte[]> wavDatas = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas2 = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas3 = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas4 = new LinkedList<byte[]>();
	public String Filename_1;
	public String Filename_2;
	public String Filename_3;
	public String Filename_4;
	private int channel = 1;
	private int sampleRate = 16000;
	private int brate = 64;

	public SoundTouchThread(Handler handler, BlockingQueue<short[]> recordQueue) {
		this.handler = handler;
		this.recordQueue = recordQueue;
	}
	
	public SoundTouchThread(Handler handler, BlockingQueue<short[]> recordQueue,String Filename1,String Filename2,String Filename3,String Filename4) {
		this.handler = handler;
		this.recordQueue = recordQueue;
		this.Filename_1 = Filename1;
		this.Filename_2 = Filename2;
		this.Filename_3 = Filename3;
		this.Filename_4 = Filename4;
	}

	public void stopSoundTounch() {
		setToStopped = true;
	}

	@SuppressLint("NewApi")
	@Override
	public void run() {
		mp3Encode.init(channel, sampleRate, brate);

		soundtouch.setSampleRate(16000);
		soundtouch.setChannels(1);
		soundtouch.setPitchSemiTones(10);
		soundtouch.setRateChange(-0.7f);
		soundtouch.setTempoChange(0.5f);

		soundtouch2.setSampleRate(16000);
		soundtouch2.setChannels(1);
		soundtouch2.setPitchSemiTones(10);
		soundtouch2.setRateChange(-0.7f);
		soundtouch2.setTempoChange(0.5f);
		
		soundtouch3.setSampleRate(16000);
		soundtouch3.setChannels(1);
		soundtouch3.setPitchSemiTones(5);
		soundtouch3.setRateChange(-0.2f);
		soundtouch3.setTempoChange(0.7f);
		
		soundtouch4.setSampleRate(16000);
		soundtouch4.setChannels(1);
		soundtouch4.setPitchSemiTones(5);
		soundtouch4.setRateChange(-0.2f);
		soundtouch4.setTempoChange(0.7f);
		
		
		wavDatas.clear();
		wavDatas2.clear();
		wavDatas3.clear();
		wavDatas4.clear();

		short[] recordingData;
		while (true) {

			try {
				// 获取的音频数据
				recordingData = recordQueue.poll(TIME_WAIT_RECORDING,
						TimeUnit.MILLISECONDS);
				
				if (recordingData != null) {
					
					soundtouch.putSamples(recordingData, recordingData.length);					
					short[] buffer;					
					do {
						buffer = soundtouch.receiveSamples();
						byte[] mp3Datas = mp3Encode.encode(buffer, buffer.length);
						wavDatas.add(mp3Datas);
					} while (buffer.length > 0);
					
					
					soundtouch2.putSamples(recordingData, recordingData.length);
					short[] buffer2;
					do {
						buffer2 = soundtouch2.receiveSamples();
						byte[] mp3Datas = mp3Encode.encode(buffer2, buffer2.length);
						wavDatas2.add(mp3Datas);	
					} while (buffer2.length > 0);
					
					
					soundtouch3.putSamples(recordingData, recordingData.length);
					short[] buffer3;
					do {
						buffer3 = soundtouch3.receiveSamples();
						byte[] mp3Datas = mp3Encode.encode(buffer3, buffer3.length);
						wavDatas3.add(mp3Datas);					
					} while (buffer3.length > 0);			
					
					soundtouch4.putSamples(recordingData, recordingData.length);
					short[] buffer4;
					do {
						buffer4 = soundtouch4.receiveSamples();
						byte[] mp3Datas = mp3Encode.encode(buffer4, buffer4.length);
						wavDatas4.add(mp3Datas);					
					} while (buffer4.length > 0);		
					
				}

				if (setToStopped && recordQueue.size() == 0) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 计算文件长度
		int fileLength = 0;
		int fileLength2 = 0;
		for (byte[] bytes : wavDatas) {
			fileLength += bytes.length;
		}
		for (byte[] bytes : wavDatas2) {
			fileLength2 += bytes.length;
		}
		Log.v("aaa", "length:" + fileLength + "length2:" + fileLength2);

		try {
			
			Log.v("Alarm", "Filename:"+Filename_1);
			FileOutputStream out = new FileOutputStream(Filename_1);
			for (byte[] bytes : wavDatas) {
				out.write(bytes);
			}
			out.close();

			FileOutputStream out2 = new FileOutputStream(Filename_2);
			for (byte[] bytes : wavDatas2) {
				out2.write(bytes);
			}
			out2.close();
			
			
			
			FileOutputStream out3 = new FileOutputStream(Filename_3);
			for (byte[] bytes : wavDatas3) {
				out3.write(bytes);
			}
			out3.close();
			
			FileOutputStream out4 = new FileOutputStream(Filename_4);
			for (byte[] bytes : wavDatas4) {
				out4.write(bytes);
			}
			out4.close();
			SetAlarmFragment.ISRECORDED = true;

			//handler.sendEmptyMessage(Settings.MSG_FILE_SAVE_SUCCESS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}
}
