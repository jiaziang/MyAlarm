package com.jeremyfeinstein.slidingmenu.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.jiaziang8.alarm.object.ContactPeople;
import com.jiaziang8.alarm.service.Contacts;
import com.jiaziang8.alarm.ui.AddFriendOnClickListener;
import com.jiaziang8.alarm.ui.MyButton;
import com.jiaziang8.alarm.util.StringHelper;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ContactsActivity extends Activity {
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	private LinearLayout layoutIndex;
	Context mContext = null;
	Contacts contacts; // 记录联系人信息的类
	ListView mListView = null;
	private List<ContactPeople> peoples = null;
	private List<ContactPeople> newPeoples = new ArrayList<ContactPeople>();
	private TextView tv_show;
	private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	MyListAdapter mAdapter = null;
	private int height;// 字体高度
	private boolean flag = false;
	public static String account = "";
	public static Handler handler;
	private  List<HashMap<String, Object>> mData ;
	private RelativeLayout backRelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list_main);
		mContext = this;

		if(ViewPagerActivity.contacts!=null){
			ViewPagerActivity.contacts.getPhoneContacts();
		}else{
			contacts = new Contacts(getApplicationContext());
			contacts.getPhoneContacts();
		}
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		account = bundle.getString("account");

		layoutIndex = (LinearLayout) this.findViewById(R.id.layout);
		layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
		mListView = (ListView) findViewById(R.id.listView);
		tv_show = (TextView) findViewById(R.id.tv);
		tv_show.setVisibility(View.GONE);
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
		mData = BaseActivity.getData();
		setData();

		String[] allNames = sortIndex(peoples);
		sortList(allNames);
		selector = new HashMap<String, Integer>();
		for (int j = 0; j < indexStr.length; j++) {
			for (int i = 0; i < newPeoples.size(); i++) {
				if (newPeoples.get(i).getName().equals(indexStr[j])) {
					selector.put(indexStr[j], i);
				}
			}
		}

		mAdapter = new MyListAdapter(this, newPeoples);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					Toast.makeText(ContactsActivity.this, "请求已发送～！",
							Toast.LENGTH_LONG).show();
					break;
				case 0:
					Toast.makeText(ContactsActivity.this, "添加好友失败~",
							Toast.LENGTH_LONG).show();
					break;
				case 2:
					Toast.makeText(ContactsActivity.this, "该好友已添加过~",
							Toast.LENGTH_LONG).show();
				default:
					break;
				}
			}
		};

	}
	
	 @Override  
	    public void onWindowFocusChanged(boolean hasFocus) {  
	        // 在oncreate里面执行下面的代码没反应，因为oncreate里面得到的getHeight=0  
	        if (!flag) {// 这里为什么要设置个flag进行标记，我这里不先告诉你们，请读者研究，因为这对你们以后的开发有好处  
	            height = layoutIndex.getMeasuredHeight() / indexStr.length;  
	            getIndexView();  
	            flag = true;  
	        }  
	    }  

	 //滑动右侧拼音索引
	 public void getIndexView(){
		 LinearLayout.LayoutParams params = new LayoutParams(  
	                LayoutParams.WRAP_CONTENT, height);  
		  for (int i = 0; i < indexStr.length; i++) {  
	            final TextView tv = new TextView(this);  
	            tv.setLayoutParams(params);  
	            tv.setText(indexStr[i]);  
	            tv.setPadding(10, 0, 10, 0);  
	            layoutIndex.addView(tv);  
	            layoutIndex.setOnTouchListener(new OnTouchListener() {  
	  
	                @Override  
	                public boolean onTouch(View v, MotionEvent event)  
	  
	                {  
	                    float y = event.getY();  
	                    int index = (int) (y / height);  
	                    if (index > -1 && index < indexStr.length) {// 防止越界  
	                        String key = indexStr[index];  
	                        if (selector.containsKey(key)) {  
	                            int pos = selector.get(key);  
	                            if (mListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。  
	                                mListView.setSelectionFromTop(  
	                                        pos + mListView.getHeaderViewsCount(), 0);  
	                            } else {  
	                                mListView.setSelectionFromTop(pos, 0);// 滑动到第一项  
	                            }  
	                            tv_show.setVisibility(View.VISIBLE);  
	                            tv_show.setText(indexStr[index]);  
	                        }  
	                    }  
	                    switch (event.getAction()) {  
	                    case MotionEvent.ACTION_DOWN:  
	                        layoutIndex.setBackgroundColor(Color  
	                                .parseColor("#606060"));  
	                        break;  
	  
	                    case MotionEvent.ACTION_MOVE:  
	  
	                        break;  
	                    case MotionEvent.ACTION_UP:  
	                        layoutIndex.setBackgroundColor(Color  
	                                .parseColor("#00ffffff"));  
	                        tv_show.setVisibility(View.GONE);  
	                        break;  
	                    }  
	                    return true;  
	                }  
	            });  
	        }  
		 
	 }

	 //把people对象按顺序加进newPeople
	private void sortList(String[] allNames) {
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < peoples.size(); j++) {
					if (allNames[i].substring(0,allNames[i].length()-1).equals(peoples.get(j).getPinYinName())) {
						ContactPeople p = new ContactPeople("@"+peoples.get(j)
								.getName(), peoples.get(j).getPinYinName(),peoples.get(j).getNumber());
						newPeoples.add(p);
					}
				}
			} else {
				newPeoples.add(new ContactPeople(allNames[i]));
			}
		}
		//Log.v("Alarm", "newpeople:"+newPeoples.size());
	}

	public String[] sortIndex(List<ContactPeople> persons) {
		TreeSet<String> set = new TreeSet<String>();
		for (ContactPeople people : persons) {
			set.add(StringHelper.getPinYinHeadChar(people.getName()).substring(
					0, 1));
		}

		String[] names = new String[persons.size() + set.size()];
		int i = 0;
		for (String string : set) {
			names[i] = string;
			i++;
		}
		String[] pinYinNames = new String[persons.size()];
		for (int j = 0; j < persons.size(); j++) {
			persons.get(j).setPinYinName(
					StringHelper
							.getPingYin(persons.get(j).getName().toString()));
			pinYinNames[j] = StringHelper.getPingYin(persons.get(j).getName()
					.toString())+"@";
		}
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		//Log.v("Alarm", "names:"+names.length);
		return names;

	}

	public void setData() {
		peoples = new ArrayList<ContactPeople>();
		int i=0;
		for (String name : Contacts.mContactsName) {
			ContactPeople people = new ContactPeople(name);
			people.setNumber(Contacts.mContactsNumber.get(i));
			peoples.add(people);
			i++;
		}
		//Log.v("Alarm", peoples.size()+"");
	}

	class MyListAdapter extends BaseAdapter {
		// Contacts contacts;
		private List<ContactPeople> list;
		private Context context;
		private ViewHolder viewHolder;  

		public MyListAdapter(Context context, List<ContactPeople> peoples) {
			this.context = context;
			this.list = peoples;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			if (list.get(position).getName().length() == 1)// 如果是字母索引
				return false;// 表示不能点击
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			boolean isAdded = false;
			String item = list.get(position).getName();
			String number = list.get(position).getNumber();
			viewHolder =  new ViewHolder();
			
			
			if (item.length() == 1) {  
	            convertView = LayoutInflater.from(context).inflate(R.layout.index,  
	                    null);  
	            viewHolder.indexTv = (TextView) convertView  
	                    .findViewById(R.id.indexTv);  
	            viewHolder.indexTv.setText(list.get(position).getName()); 
	        } else {  
	            convertView = LayoutInflater.from(context).inflate(R.layout.item,  
	                    null);  
	            viewHolder.itemTv = (TextView) convertView  
	                    .findViewById(R.id.itemTv);  
	            for(int i = 0;i<mData.size();i++){
	            	if(mData.get(i).get("number").equals(number)){
	            		isAdded = true;
	            		break;
	            	}
	            }
	            if(!isAdded){
		            viewHolder.myButton = (MyButton)convertView.findViewById(R.id.add_friend);
		            viewHolder.myButton.setHandler(handler);
		            viewHolder.myButton.setFriend(number);
		            viewHolder.myButton.setname(item.substring(1));
		            viewHolder.myButton.setOnClickListener(AddFriendOnClickListener
							.getAddFriendOnClickListenerInstance());
	            }else{
	            	viewHolder.myButton = (MyButton)convertView.findViewById(R.id.add_friend);
	            	viewHolder.myButton.setEnabled(false);
	            	viewHolder.myButton.setTextColor(getResources().getColor(R.color.black));
	            	viewHolder.myButton.setText("已添加");
	            }
	            viewHolder.itemTv.setText(list.get(position).getName().substring(1));  
	        }   	        
			return convertView;
		}

		private class ViewHolder {
			private TextView indexTv;
			private TextView itemTv;
			private MyButton myButton;
		}
	}

}
