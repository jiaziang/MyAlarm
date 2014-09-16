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
	public String Filename_1;
	public String Filename_2;
	public String Filename_3;
	public String Filename_4;
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			
/*			case Settings.MSG_FILE_SAVE_SUCCESS:				
				play();
				
				break;*/
				
			}
		};
		
	};
	
	public SoundTouchClient(Handler mainHandler){
		this.mainHandler = mainHandler;
	}
	
	public SoundTouchClient(Handler mainHandler,String Filename1,String Filename2,String Filename3,String Filename4){
		this.mainHandler = mainHandler;
		this.Filename_1 = Filename1;
		this.Filename_2 = Filename2;
		this.Filename_3 = Filename3;
		this.Filename_4 = Filename4;
		
	}
	
	public void start(){
		recordingThread = new RecordingThread(handler, recordQueue);
		recordingThread.start();
		
		soundTouchThread = new SoundTouchThread(handler, recordQueue, Filename_1,Filename_2,Filename_3,Filename_4);
		soundTouchThread.start();
	}
	
	public void stop(){
		recordingThread.stopRecording();
		soundTouchThread.stopSoundTounch();
	}
	
/*	public void play() {
		
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
		
	}*/
	
	public void setfilename(String Filename1,String Filename2,String Filename3,String Filename4){
		this.Filename_1 = Filename1;
		this.Filename_2 = Filename2;
		this.Filename_3 = Filename3;
		this.Filename_4 = Filename4;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mainHandler.sendEmptyMessage(0);
		mediaPlayer.release();
	}
}
