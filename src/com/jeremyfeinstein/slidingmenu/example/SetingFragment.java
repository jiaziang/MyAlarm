package com.jeremyfeinstein.slidingmenu.example;

import java.io.File;
import java.io.IOException;

import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.RepeatPreference;
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
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SetingFragment extends Fragment {
	private RelativeLayout homepageRelativeLayout;
	private RelativeLayout notificationTextView;
	private RelativeLayout reviewRelativeLayout;
	private RelativeLayout settingsRelativeLayout;
	private Preference mTimePref; // 时间
	private RepeatPreference mRepeatPref; // 重复
	private SharedPreferences sharedPreferences;
	private ImageView userheadImageView;
	private File cache;
	public static Activity instance = null;
	public static Handler handler;
	public static final int REFRESHIMAGE = 1;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		instance = getActivity();
		sharedPreferences = getActivity().getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
		cache = new File(Environment.getExternalStorageDirectory(),
				"MyRecord/cache");
		if (!cache.exists()) {
			cache.mkdirs();
		}
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REFRESHIMAGE:
					refreshUserHead();
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings, null);
		notificationTextView = (RelativeLayout) view
				.findViewById(R.id.notification);
		reviewRelativeLayout = (RelativeLayout) view.findViewById(R.id.review);
		settingsRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.setting);
		userheadImageView = (ImageView) view.findViewById(R.id.setting_head);
		homepageRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.firstpage);
		homepageRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ViewPagerActivity.changeToHomepage();
				try {
					Runtime runtime = Runtime.getRuntime();
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				} catch (IOException e) {
					Log.e("Exception when doBack", e.toString());
				}
			}
		});

		notificationTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ViewPagerActivity.account);
				bundle.putBoolean("hasMainActivity", true);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), NotificationActivity.class);
				startActivity(intent);
			}
		});

		reviewRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ViewPagerActivity.account);
				bundle.putBoolean("hasMainActivity", true);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), ReviewActivity.class);
				startActivity(intent);

			}
		});
		settingsRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ViewPagerActivity.account);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});
		userheadImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ViewPagerActivity.account);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), SettingActivity.class);
				startActivity(intent);

			}
		});

		asyncloadImage(userheadImageView);
		return view;
	}

	private void asyncloadImage(ImageView imageView) {
		MyService imageMyService = new MyService();
		AsyncImageTask task = new AsyncImageTask(imageMyService, imageView);
		task.execute(ViewPagerActivity.account);
	}

	private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
		private MyService service;
		private ImageView imageView;

		public AsyncImageTask(MyService myService, ImageView imageView) {
			this.service = myService;
			this.imageView = imageView;
		}

		@Override
		protected Uri doInBackground(String... params) {
			try {
				String path = service.getHeadPathByAccount(params[0]);
				if (path.equals(""))
					return null;
				path = MyService.GETHEADURL
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

	public static Handler getHandler() {
		return handler;
	}

	public void refreshUserHead() {
		asyncloadImage(userheadImageView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
