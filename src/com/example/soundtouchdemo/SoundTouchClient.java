package com.example.soundtouchdemo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class SoundTouchClient implements OnCompletionListener{

	private BlockingQueue<short[]> recordQueue = new LinkedBlockingQueue<short[]>();
	private RecordingThread recordingThread;
	private SoundTouchThread soundTouchThread;
	private MediaPlayer mediaPlayer;
	private Handler mainHandler;
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case Settings.MSG_FILE_SAVE_SUCCESS:				
				play();
				
				break;
				
			}
		};
		
	};
	
	public SoundTouchClient(Handler mainHandler){
		this.mainHandler = mainHandler;
	}
	
	public void start(){
		recordingThread = new RecordingThread(handler, recordQueue);
		recordingThread.start();
		
		soundTouchThread = new SoundTouchThread(handler, recordQueue);
		soundTouchThread.start();
	}
	
	public void stop(){
		recordingThread.stopRecording();
		soundTouchThread.stopSoundTounch();
	}
	
	public void play() {
		
		try {
			
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setDataSource(Settings.recordingPath  + "soundtouch.wav");
			mediaPlayer.prepare();
			mediaPlayer.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("soundtouch", e.getMessage());
		} 
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mainHandler.sendEmptyMessage(0);
		mediaPlayer.release();
	}
}
