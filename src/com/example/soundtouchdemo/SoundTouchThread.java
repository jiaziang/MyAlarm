package com.example.soundtouchdemo;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.jeremyfeinstein.slidingmenu.example.SetAlarmFragment;

import android.annotation.SuppressLint;
import android.os.Handler;

public class SoundTouchThread extends Thread {

	private BlockingQueue<short[]> recordQueue;
	private Handler handler;
	private static final long TIME_WAIT_RECORDING = 100;
	private volatile boolean setToStopped = false;
	private JNISoundTouch soundtouch = new JNISoundTouch();
	private JNIMp3Encode mp3Encode = new JNIMp3Encode();
	private LinkedList<byte[]> wavDatas = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas2 = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas3 = new LinkedList<byte[]>();
	private LinkedList<byte[]> wavDatas4 = new LinkedList<byte[]>();
	private ArrayList<short[]>  recordAllDatas = new ArrayList<short[]>(); 
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
		soundtouch.setPitchSemiTones(0);
		soundtouch.setRateChange(0.0f);
		soundtouch.setTempoChange(0.0f);
				
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
					recordAllDatas.add(recordingData);					
				}

				if (setToStopped && recordQueue.size() == 0) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(short[] element:recordAllDatas){
			
			soundtouch.putSamples(element, element.length);
			short[] buffer1;	
			byte[] mp3Datas;
			do {
				buffer1 = soundtouch.receiveSamples();
				mp3Datas = mp3Encode.encode(buffer1, buffer1.length);
				wavDatas.add(mp3Datas);
			} while (buffer1.length > 0);
		}
		
		soundtouch.setSampleRate(16000);
		soundtouch.setChannels(1);
		soundtouch.setPitchSemiTones(5);
		soundtouch.setRateChange(-0.2f);	
		soundtouch.setTempoChange(20.0f);
		
		for(short[] element:recordAllDatas){
			
			soundtouch.putSamples(element, element.length);
			short[] buffer1;	
			byte[] mp3Datas;
			do {
				buffer1 = soundtouch.receiveSamples();
				mp3Datas = mp3Encode.encode(buffer1, buffer1.length);
				wavDatas2.add(mp3Datas);
			} while (buffer1.length > 0);
		}
		
		soundtouch.setSampleRate(16000);
		soundtouch.setChannels(1);
		soundtouch.setPitchSemiTones(-4);
		soundtouch.setRateChange(-0.2f);
		soundtouch.setTempoChange(-0.2f);
		
		for(short[] element:recordAllDatas){
			
			soundtouch.putSamples(element, element.length);
			short[] buffer1;	
			byte[] mp3Datas;
			do {
				buffer1 = soundtouch.receiveSamples();
				mp3Datas = mp3Encode.encode(buffer1, buffer1.length);
				wavDatas3.add(mp3Datas);
			} while (buffer1.length > 0);
		}
		
		soundtouch.setSampleRate(16000);
		soundtouch.setChannels(1);
		soundtouch.setPitchSemiTones(5);
		soundtouch.setRateChange(-40f);
		soundtouch.setTempoChange(10f);
		
		for(short[] element:recordAllDatas){
			
			soundtouch.putSamples(element, element.length);
			short[] buffer1;	
			byte[] mp3Datas;
			do {
				buffer1 = soundtouch.receiveSamples();
				mp3Datas = mp3Encode.encode(buffer1, buffer1.length);
				wavDatas4.add(mp3Datas);
			} while (buffer1.length > 0);
		}
		

		try {
			
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

			handler.sendEmptyMessage(SetAlarmFragment.RECORD_FINISHED);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}
}
