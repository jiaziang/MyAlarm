package com.jeremyfeinstein.slidingmenu.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.jiaziang8.alarm.service.Contacts;
import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.DeleteReviewListener;
import com.jiaziang8.alarm.ui.deleteReviewButton;
import com.jiaziang8.alarm.util.Constants;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewActivity extends Activity implements IXListViewListener,
		MediaPlayer.OnPreparedListener , OnCompletionListener{
	private String account = "";
	public static final int LIST_OK = 1;
	public static final int DELETE_OK = 2;
	private XListView reviewListView;
	private Handler mHandler;
	private Myadapter myadapter;
	private Context context;
	private static List<HashMap<String, Object>> mData;
	private SharedPreferences sharedPreferences;
	private int start = 0;
	private int countOnce = 10;
	private MediaPlayer mediaPlayer;
	private boolean isPlaying = false;
	private static final String LISTENURL_STRING = Constants.url
			+ "/AlarmServer/Head/recordFile/";
	private boolean hasMainActivity;
	private Contacts contacts;
	private RelativeLayout backRelativeLayout;
	private Button temButton =null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LIST_OK:
				if (myadapter == null) {
					myadapter = new Myadapter(ReviewActivity.this);
				}
				reviewListView.setAdapter(myadapter);
				myadapter.notifyDataSetChanged();
				start = mData.size();
				break;
			case DELETE_OK:
				refreshList();
				Toast.makeText(ReviewActivity.this, "删除成功", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review_activity);
		mData = new ArrayList<HashMap<String, Object>>();
		context = this;

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		account = bundle.getString("account");
		hasMainActivity = bundle.getBoolean("hasMainActivity");
		getItems(0);
		backRelativeLayout = (RelativeLayout)this.findViewById(R.id.back_layout);
		backRelativeLayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				try {
					Runtime runtime = Runtime.getRuntime();
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				} catch (IOException e) {
					Log.e("Exception when doBack", e.toString());
				}
			}
		});
		reviewListView = (XListView) findViewById(R.id.review_listview);
		reviewListView.setPullLoadEnable(true);
		reviewListView.setPullRefreshEnable(true);
		if (myadapter == null) {
			myadapter = new Myadapter(context);
		}
		sharedPreferences = this.getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
		reviewListView.setXListViewListener(this);
		mHandler = new Handler();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);

		if (ViewPagerActivity.contacts != null) {
			ViewPagerActivity.contacts.getPhoneContacts();
		} else {
			contacts = new Contacts(getApplicationContext());
			contacts.getPhoneContacts();
		}

	}

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String audioUrl,Button thisButton) {
		if(temButton!=null){
			return;
		}
		try {
			if(mediaPlayer==null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnCompletionListener(this);
				
			}
			temButton = thisButton;
			/*
			 * mediaPlayer = new MediaPlayer();
			 * mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 * mediaPlayer.setOnPreparedListener(this);
			 */

			mediaPlayer.reset();
			mediaPlayer.setDataSource(audioUrl);
			mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mediaPlayer = new MediaPlayer();
		temButton = null;
	}

	public void getItems(int start) {
		final int startvalue = start;
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData.addAll(MyService.getItems(startvalue, account));
				Message message = new Message();
				message.what = LIST_OK;
				handler.sendMessage(message);
			}
		}).start();
	}

	public class Myadapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Myadapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int id;
			String account;
			String name;
			String selfAccount;
			int mDays;
			int mHour;
			int mMinute;
			 final String filename;
			String time;
			String wordsToSay;

			id = (int) mData.get(position).get("id");
			mDays = (int) mData.get(position).get("mDays");
			account = (String) mData.get(position).get("account");
			selfAccount = (String) mData.get(position).get("friend");
			name = (String) mData.get(position).get("name");
			mHour = (int) mData.get(position).get("mHour");
			mMinute = (int) mData.get(position).get("mMinute");
			filename = (String) mData.get(position).get("filename");
			time = (String) mData.get(position).get("time");
			wordsToSay = (String) mData.get(position).get("wordsToSay");

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.review_item, null);
			}
			TextView reviewInfoTextView = (TextView) convertView
					.findViewById(R.id.review_info);
			TextView reviewTimeTextView = (TextView) convertView
					.findViewById(R.id.review_time);
			TextView reviewWordsTextView = (TextView) convertView
					.findViewById(R.id.review_words);
			Button playButton = (Button) convertView.findViewById(R.id.play);
			deleteReviewButton deleteReviewButton = (deleteReviewButton) convertView
					.findViewById(R.id.delete_review);
			deleteReviewButton.setId(id);
			deleteReviewButton.setHandler(handler);
			deleteReviewButton.setOnClickListener(DeleteReviewListener
					.getInstance());

			// account是设闹钟的人 selfAccount是被设的人 name 是设闹钟人的name
			if (account.equals(ReviewActivity.this.account)) {
				reviewInfoTextView.setText("我给"
						+ Contacts.getNamaByNumber(selfAccount) + "设了闹钟");
			} else if (selfAccount.equals(ReviewActivity.this.account)) {
				reviewInfoTextView.setText(Contacts.getNamaByNumber(account)
						+ "给我设了闹钟");
			}
			playButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Button buttonView = (Button) v;
					String listenurlString = filename.substring( // 获取文件名
							filename.lastIndexOf("/") + 1);
					if (!mediaPlayer.isPlaying()) {
						buttonView.setBackgroundResource(R.drawable.review_pause);
						// String urlString =
						// "http://192.168.1.105:8080/AlarmServer/tempFile719657307.amr";
						playUrl(LISTENURL_STRING + listenurlString,buttonView);
						//Log.v("nimei", LISTENURL_STRING + listenurlString);
						
					} else if(mediaPlayer.isPlaying()&&temButton==buttonView){
						buttonView.setBackgroundResource(R.drawable.review_play);
						stop();
					}
				}
			});
			reviewTimeTextView.setText(time);
			reviewWordsTextView.setText(wordsToSay);

			return convertView;
		}
	}

	private void onLoad() {
		reviewListView.stopRefresh();
		reviewListView.stopLoadMore();
		reviewListView.setRefreshTime("刚刚");
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mData.clear();
				start = 0;
				getItems(start);
				onLoad();
			}
		}, 2000);
	}

	// 下拉加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getItems(start);
				onLoad();
			}
		}, 2000);
	}

	@Override
	/**  
	 * 通过onPrepared播放  
	 */
	public void onPrepared(MediaPlayer arg0) {
		arg0.start();

		Log.e("mediaPlayer", "onPrepared");
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0){
		try {
			mediaPlayer.stop();
			mediaPlayer.reset();
			isPlaying = false;
			temButton.setBackgroundResource(R.drawable.review_play);
			temButton = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /////////////////////////////////////////////////
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !hasMainActivity) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String accountString = sharedPreferences.getString(
							"account", "");
					String passwordString = sharedPreferences.getString(
							"password", "");
					if (StartActivity.login(accountString, passwordString)) {
						if (JPushInterface
								.isPushStopped(getApplicationContext())) {
							JPushInterface.resumePush(getApplicationContext());
						}
						if (accountString.indexOf("+86") == 0) {
							accountString = accountString.substring(3);
						}
						JPushInterface.setAlias(getApplicationContext(),
								accountString, null);

						Intent contentIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("account", accountString);
						contentIntent.setClass(ReviewActivity.this,
								ViewPagerActivity.class);
						contentIntent.putExtras(bundle);
						startActivity(contentIntent);
						ReviewActivity.this.finish();
					}
				}
			}).start();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void refreshList() {
		mData.clear();
		start = 0;
		getItems(0);
	}

}
