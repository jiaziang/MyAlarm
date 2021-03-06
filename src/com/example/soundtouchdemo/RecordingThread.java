package com.example.soundtouchdemo;

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class RecordingThread extends Thread {
	
	private static int FREQUENCY = 16000;
	private static int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	

	private volatile boolean setToStopped = false;
	private Handler handler;
	
	
	private static int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL, ENCODING);
	private BlockingQueue<short[]> recordQueue;
	
	public RecordingThread(Handler handler, BlockingQueue<short[]> recordQueue){
		this.handler = handler;
		this.recordQueue = recordQueue;
	}
	
	public void stopRecording(){
		this.setToStopped = true;
	}
	
	
	@Override
	public void run(){	
			AudioRecord audioRecord = null;		
			try {					
				//Log.v("Alarm", "bufferLength:"+bufferSize);
				short[] buffer = new short[bufferSize];  //------1280
				audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, FREQUENCY,
						CHANNEL, ENCODING, bufferSize*2);
				
				int state = audioRecord.getState();				
				if (state == AudioRecord.STATE_INITIALIZED) {
					
					audioRecord.startRecording();			
					//shandler.obtainMessage(Settings.MSG_RECORDING_START).sendToTarget();										
					boolean flag = true;
					
					while (!setToStopped) {
						
						int len = audioRecord.read(buffer, 0, buffer.length);	
						Log.v("Alarm", "hufferLength~~~~~~~~~~~~~"+buffer.length);
						
						// 去掉全0数据
						if(flag){
							
							double sum = 0.0;
							for(int i = 0; i < len; i++){
								sum += buffer[i];
							}
							if(sum == 0.0){
								continue;
								
							}else{													
								flag = false;												
							}
						}						
						
						Log.v("Alarm", "len:"+len);
						short[] data = new short[len]; 
						System.arraycopy(buffer, 0, data, 0, len);							
						recordQueue.add(data);												
					}					
					
					//handler.sendEmptyMessage(Settings.MSG_RECORDING_STOP);
					audioRecord.stop();

				} else {
					
					//handler.sendEmptyMessage(Settings.MSG_RECORDING_STATE_ERROR);

				}

			} catch (Exception e) {
				
				//handler.sendEmptyMessage(Settings.MSG_RECORDING_EXCEPTION);	
				
			} finally {
							
				try {								
					audioRecord.release();
					audioRecord = null;					
					//handler.sendEmptyMessage(Settings.MSG_RECORDING_RELEASE);												
				} catch (Exception e) {
					
				}
			}
			
		}
	
}
