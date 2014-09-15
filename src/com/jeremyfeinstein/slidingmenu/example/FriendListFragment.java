package com.jeremyfeinstein.slidingmenu.example;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.MyButton;
import com.jiaziang8.alarm.ui.MyOnClickListener;
import com.jiaziang8.alarm.util.Constants;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendListFragment extends Fragment {
	String account = "";
	private static final int LIST_OK = 1;
	private static final int DELETE_OK = 2;
	private static final int REFRESH_DATA = 3;
	private ListView friendList;
	private static List<HashMap<String, Object>> mData;
	private File cache;
	private Button addfriend;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LIST_OK:
				MyAdapter adapter = new MyAdapter(getActivity());
				friendList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				// addfriend.setEnabled(true);
				break;
			case DELETE_OK:
				Toast.makeText(getActivity(), "删除好友成功~", Toast.LENGTH_SHORT)
						.show();
				break;
			case REFRESH_DATA:
				// addfriend.setEnabled(false);
				refreshData();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_friends_list, null);
		account = ViewPagerActivity.account;
		cache = new File(Environment.getExternalStorageDirectory(),
				"MyRecord/cache");

		if (!cache.exists()) {
			cache.mkdirs();
		}
		friendList = (ListView) view.findViewById(R.id.friends_listview);
		/*
		 * addfriend = (Button)view.findViewById(R.id.addfriend);
		 * addfriend.setEnabled(false); addfriend.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { Intent add_friendIntent =
		 * new Intent(); Bundle bundle = new Bundle();
		 * bundle.putString("account", account);
		 * add_friendIntent.setClass(getActivity(), ContactsActivity.class);
		 * add_friendIntent.putExtras(bundle); startActivity(add_friendIntent);
		 * } });
		 */

		new Thread(new Runnable() {
			@Override
			public void run() {
				mData = MyService.getData(account);
				Message msg = new Message();
				//Log.v(Constants.LOG_TAG, "mData is" + mData);
				msg.what = LIST_OK;
				handler.sendMessage(msg);
			}
		}).start();
		return view;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
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
			if (convertView == null)
				convertView = mInflater
						.inflate(R.layout.friend_list_item, null);

			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.name);
			TextView numberTextView = (TextView) convertView
					.findViewById(R.id.number);
			ImageView uerheadImageView = (ImageView) convertView
					.findViewById(R.id.friend_head);
			MyButton addthis = (MyButton) convertView
					.findViewById(R.id.addthis);

			nameTextView.setText(mData.get(position).get("name").toString());
			numberTextView
					.setText(mData.get(position).get("number").toString());
			addthis.setIndex(position);
			String name = mData.get(position).get("name").toString();
			addthis.setname(name);
			addthis.setHandler(handler);
			addthis.setFriend(mData.get(position).get("number").toString());
			addthis.setAcount(ViewPagerActivity.account);
			addthis.setOnClickListener(MyOnClickListener.getInstance());
			String pathString = mData.get(position).get("path").toString();
			asyncloadImage(uerheadImageView, pathString);
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

	public void refreshData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData = MyService.getData(account);
				Message msg = new Message();
				msg.what = LIST_OK;
				handler.sendMessage(msg);
			}
		}).start();
	}

	public static List<HashMap<String, Object>> getData() {
		return mData;
	}

	public Handler getHandler() {
		return handler;
	}
}
