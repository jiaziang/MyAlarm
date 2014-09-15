package com.jeremyfeinstein.slidingmenu.example;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.cn.daming.deskclock.Alarm.DaysOfWeek;
import com.cn.daming.deskclock.SetAlarm;
import com.jiaziang8.alarm.service.Contacts;
import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.AcceptAddFriendListener;
import com.jiaziang8.alarm.ui.AlarmDetailListener;
import com.jiaziang8.alarm.ui.NotificationButton;
import com.jiaziang8.alarm.ui.RefuseAddFriendListener;
import com.jiaziang8.alarm.ui.RefuseAlarmListener;
import com.jiaziang8.alarm.util.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationActivity extends Activity {
	static String account = "";
	public static final int LIST_OK = 1;
	public static final int REFRESH_LIST = 2;
	public static final int DOWNLOAD_COMPLET = 3;
	public static final int FINISH_ACTIVITY = 4;
	private ListView notificatinoListView;
	private static List<HashMap<String, Object>> mData;
	private Myadapter myadapter;
	private boolean hasMainActivity;
	private SharedPreferences sharedPreferences;
	private Context context;
	private File cache;
	private Contacts contacts;
	// private ImageButton back_button;
	private RelativeLayout backRelativeLayout;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LIST_OK:
				if (myadapter == null) {
					myadapter = new Myadapter(NotificationActivity.this);
				}
				notificatinoListView.setAdapter(myadapter);
				myadapter.notifyDataSetChanged();
				break;
			case REFRESH_LIST:
				refreshNotification();
				break;

			case DOWNLOAD_COMPLET:
				int mHour = msg.arg1;
				int mMunite = msg.arg2;
				DaysOfWeek daysOfWeek = (DaysOfWeek) msg.obj;
				SetAlarm.popAlarmSetToast(context, mHour, mMunite, daysOfWeek);
				// Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
				break;
			case FINISH_ACTIVITY:
				if (!hasMainActivity) {
					Intent contentIntent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("account", account);
					contentIntent.setClass(NotificationActivity.this,
							ViewPagerActivity.class);
					contentIntent.putExtras(bundle);
					startActivity(contentIntent);
				}
				NotificationActivity.this.finish();
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notification);
		backRelativeLayout = (RelativeLayout) this
				.findViewById(R.id.back_layout);
		sharedPreferences = this.getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		account = bundle.getString("account");
		cache = new File(Environment.getExternalStorageDirectory(),
				"MyRecord/cache");
		if (!cache.exists()) {
			cache.mkdirs();
		}

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

		// 判断由通知栏或者主界面进入
		if (bundle.getBoolean("hasMainActivity") == true) {
			hasMainActivity = true;
		} else
			hasMainActivity = false;

		notificatinoListView = (ListView) this
				.findViewById(R.id.notification_listview);
		if (ViewPagerActivity.contacts != null) {
			ViewPagerActivity.contacts.getPhoneContacts();
		} else {
			contacts = new Contacts(getApplicationContext());
			contacts.getPhoneContacts();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				mData = MyService.getNotification(account);
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
			String filename;
			String time;
			String wordsToSay;
			String path;

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
			path = (String) mData.get(position).get("path");

			// 添加好友的请求
			if (mDays == -1) {
				if (convertView == null)
					convertView = mInflater.inflate(R.layout.notification_item,
							null);
				TextView notificationTextView = (TextView) convertView
						.findViewById(R.id.notification_info);
				TextView notificationTextView2 = (TextView) convertView
						.findViewById(R.id.notification_text);
				TextView notificationtimeTextView = (TextView) convertView
						.findViewById(R.id.notification_time);
				NotificationButton refuseButton = (NotificationButton) convertView
						.findViewById(R.id.refuse_button);
				NotificationButton acceptButton = (NotificationButton) convertView
						.findViewById(R.id.accept_button);
				ImageView notification_headImageView = (ImageView) convertView
						.findViewById(R.id.notification_head);

				refuseButton.setId(id);
				refuseButton.setAccount(account);
				refuseButton.setName(name);
				refuseButton.setselfAccount(selfAccount);
				refuseButton.setmDays(mDays);
				refuseButton.setmHour(mHour);
				refuseButton.setmMinute(mMinute);
				refuseButton.setFilename(filename);
				refuseButton.setTime(time);
				refuseButton.setWordsToSay(wordsToSay);
				refuseButton.setHandler(handler);

				acceptButton.setId(id);
				acceptButton.setAccount(account);
				acceptButton.setName(name);
				acceptButton.setselfAccount(selfAccount);
				acceptButton.setmDays(mDays);
				acceptButton.setmHour(mHour);
				acceptButton.setmMinute(mMinute);
				acceptButton.setFilename(filename);
				acceptButton.setTime(time);
				acceptButton.setWordsToSay(wordsToSay);
				acceptButton.setHandler(handler);

				notificationtimeTextView.setText(time);

				notificationTextView.setText(name);
				notificationTextView2.setText("请求添加你为好友");
				refuseButton.setOnClickListener(RefuseAddFriendListener
						.getInstance());
				acceptButton.setOnClickListener(AcceptAddFriendListener
						.getInstance());
				asyncloadImage(notification_headImageView, path);
			} else {                  //设置闹钟的请求
				if (convertView == null)
					convertView = mInflater.inflate(
							R.layout.notification_alarm_item, null);
				TextView notificationTextView = (TextView) convertView
						.findViewById(R.id.notification_info);
				/*
				 * TextView notificationTextView2 = (TextView)convertView
				 * .findViewById(R.id.notification_text);
				 */
				TextView notificationtimeTextView = (TextView) convertView
						.findViewById(R.id.notification_time);
				TextView notificationAlarmTimeTextView = (TextView) convertView
						.findViewById(R.id.notification_alarm_time);
				TextView notificationRepeatInfoTextView = (TextView) convertView
						.findViewById(R.id.notification_repeat_info);
				NotificationButton refuseButton = (NotificationButton) convertView
						.findViewById(R.id.refuse_button);
				NotificationButton acceptButton = (NotificationButton) convertView
						.findViewById(R.id.accept_button);
				ImageView notification_headImageView = (ImageView) convertView
						.findViewById(R.id.notification_head);

				refuseButton.setId(id);
				refuseButton.setAccount(account);
				refuseButton.setName(name);
				refuseButton.setselfAccount(selfAccount);
				refuseButton.setmDays(mDays);
				refuseButton.setmHour(mHour);
				refuseButton.setmMinute(mMinute);
				refuseButton.setFilename(filename);
				refuseButton.setTime(time);
				refuseButton.setWordsToSay(wordsToSay);
				refuseButton.setHandler(handler);

				acceptButton.setId(id);
				acceptButton.setAccount(account);
				acceptButton.setName(name);
				acceptButton.setselfAccount(selfAccount);
				acceptButton.setmDays(mDays);
				acceptButton.setmHour(mHour);
				acceptButton.setmMinute(mMinute);
				acceptButton.setFilename(filename);
				acceptButton.setTime(time);
				acceptButton.setWordsToSay(wordsToSay);
				acceptButton.setHandler(handler);

				notificationtimeTextView.setText(time);
				notificationTextView.setText(name + "给你设置了一个闹钟");
				// notificationTextView2.setText("给你设置了一个闹钟");
				String mHourString = "";
				String mMinuteString = "";
				if (mHour < 10) {
					mHourString = "0" + String.valueOf(mHour);
				} else
					mHourString = String.valueOf(mHour);
				if (mMinute < 10) {
					mMinuteString = "0" + String.valueOf(mMinute);
				} else
					mMinuteString = String.valueOf(mMinute);

				notificationAlarmTimeTextView.setText("时间 " + mHourString + ":"
						+ mMinuteString);
				DaysOfWeek daysOfWeek = new DaysOfWeek(mDays);
				String daysOfWeekStr = daysOfWeek.toString(
						NotificationActivity.this, true);
				notificationRepeatInfoTextView.setText(daysOfWeekStr);

				refuseButton.setOnClickListener(RefuseAlarmListener
						.getInstance());
				acceptButton.setOnClickListener(AlarmDetailListener
						.getInstance());
				asyncloadImage(notification_headImageView, path);
			}
			return convertView;
		}

		private void asyncloadImage(ImageView imageView, String path) {
			MyService imageMyService = new MyService();
			AsyncImageTask task = new AsyncImageTask(imageMyService, imageView);
			task.execute(path);
		}

		private final class AsyncImageTask extends
				AsyncTask<String, Integer, Uri> {
			private MyService service;
			private ImageView imageView;

			public AsyncImageTask(MyService myService, ImageView imageView) {
				this.service = myService;
				this.imageView = imageView;
			}

			@Override
			protected Uri doInBackground(String... params) {
				try {
					String path = params[0];
					if (path.equals(""))
						return null;
					path = Constants.url + "/AlarmServer/Head/"
							+ path.substring(path.lastIndexOf("/"));
					return service.getImageURI(path, cache);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Uri result) {
				super.onPostExecute(result);
				if (imageView != null && result != null) {
					imageView.setImageURI(result);
				}
			}

		}
	}

	public void refreshNotification() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData = MyService.getNotification(account);
				Message message = new Message();
				message.what = LIST_OK;
				handler.sendMessage(message);
			}
		}).start();
	}

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
						if (account.indexOf("+86") == 0) {
							account = account.substring(3);
						}
						JPushInterface.setAlias(getApplicationContext(),
								account, null);

						Intent contentIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("account", account);
						contentIntent.setClass(NotificationActivity.this,
								ViewPagerActivity.class);
						contentIntent.putExtras(bundle);
						startActivity(contentIntent);
						NotificationActivity.this.finish();
					}
				}
			}).start();
			/*
			 * else //NotificationActivity.this.finish();
			 * super.onKeyDown(keyCode, event);
			 */
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
