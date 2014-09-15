package com.jeremyfeinstein.slidingmenu.example;

import java.util.HashMap;
import java.util.List;

import com.jiaziang8.alarm.service.MyService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectFriendActivity extends Activity{
	private List<HashMap<String, Object>> mData ;
	private ListView friendList;
	private static final int LIST_OK = 1;
	private static Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selectfriend);
		friendList = (ListView)this.findViewById(R.id.friends_listview);
		handler =new Handler(){
			@Override 
			public void handleMessage(Message msg){
				switch (msg.what) {
				case LIST_OK:
					MyAdapter adapter = new MyAdapter(SelectFriendActivity.this);
					friendList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		};
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mData = MyService.getData(ViewPagerActivity.account);
				Message msg = new Message();
				//Log.v(Constants.LOG_TAG,"mData is"+mData);
				msg.what = LIST_OK;
				handler.sendMessage(msg);
			}
		}).start();

	}
	
	
	
	public class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount(){
			return mData.size();
		}
		@Override 
		public Object getItem(int position){
			return null;
		}
		@Override
		public long getItemId(int position){
			return 0;
		}
		@Override 
		public View getView(int position,View convertView,ViewGroup parent){
			if(convertView==null)
				convertView = mInflater.inflate(R.layout.select_friend,null);
			TextView nameTextView = (TextView)convertView.findViewById(R.id.select_name);
			RelativeLayout selected_friendLayout = (RelativeLayout)convertView.findViewById(R.id.selected_friend); 
			nameTextView.setText(mData.get(position).get("name").toString());

			final String name = mData.get(position).get("name").toString();
			final String number = mData.get(position).get("number").toString();
			selected_friendLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent mIntent = new Intent();
					mIntent.putExtra("number", number);
					mIntent.putExtra("name", name);
					SelectFriendActivity.this.setResult(1, mIntent);
					SelectFriendActivity.this.finish();
					
				}
			});
			
			return convertView;
			
		}
	} 
}
