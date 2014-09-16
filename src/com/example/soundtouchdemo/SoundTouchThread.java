package com.example.soundtouchdemo;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


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
	private JNIMp3Encode mp3Encode = new JNIMp3Encode();
	private LinkedList<byte[]> wavDatas = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas2 = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas3 = new LinkedList<byte[]>();
	private int channel = 1;
	private int sampleRate = 16000;
	private int brate = 64;

	public SoundTouchThread(Handler handler, BlockingQueue<short[]> recordQueue) {
		this.handler = handler;
		this.recordQueue = recordQueue;
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

		wavDatas.clear();
		wavDatas2.clear();
		wavDatas3.clear();

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
						 Log.v("aaa", "buffer:"+buffer.length);
						//byte[] bytes = Utils.shortToByteSmall(buffer);
						//wavDatas.add(bytes);

					} while (buffer.length > 0);
					
					
					soundtouch2.putSamples(recordingData, recordingData.length);
					short[] buffer2;
					do {
						buffer2 = soundtouch2.receiveSamples();
						Log.v("aaa", "buffer2:"+buffer2.length);
						//byte[] bytes2 = Utils.shortToByteSmall(buffer2);
						//wavDatas2.add(bytes2);
					} while (buffer2.length > 0);
					
					soundtouch3.putSamples(recordingData, recordingData.length);
					short[] buffer3;
					short[] queueHeadBuffer = null;
					do {
						buffer3 = soundtouch3.receiveSamples();
						Log.v("aaa", "buffer3:"+buffer3.length);
						byte[] mp3Datas = mp3Encode.encode(buffer3, buffer3.length);
						wavDatas3.add(mp3Datas);
						//byte[] bytes3 = Utils.shortToByteSmall(buffer3);						
					} while (buffer3.length > 0);
					if(buffer3!=null){

					}					
					
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
			// 先写入文件头
			//WaveHeader header = new WaveHeader(fileLength);
			//byte[] headers = header.getHeader();

			// 保存文件
			FileOutputStream out = new FileOutputStream(Settings.recordingPath
					+ "soundtouch.wav");
			//out.write(headers);
			// 写入数据
			for (byte[] bytes : wavDatas) {
				out.write(bytes);
			}

			out.close();

			// 先写入文件头
			//WaveHeader header2 = new WaveHeader(fileLength2);
			//byte[] headers2 = header2.getHeader();
			FileOutputStream out2 = new FileOutputStream(Settings.recordingPath
					+ "soundtouch2.wav");
			//out2.write(headers2);
			// 写入数据
			for (byte[] bytes : wavDatas2) {
				out2.write(bytes);
			}
			out2.close();
			
			
			
			// 先写入文件头
/*			WaveHeader header3 = new WaveHeader(fileLength2);
			byte[] headers3 = header3.getHeader();*/
			FileOutputStream out3 = new FileOutputStream(Settings.recordingPath
					+ "soundtouch3.mp3");
			//out3.write(headers3);
			// 写入数据
			for (byte[] bytes : wavDatas3) {
				out3.write(bytes);
			}
			out3.close();

			handler.sendEmptyMessage(Settings.MSG_FILE_SAVE_SUCCESS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}
}
