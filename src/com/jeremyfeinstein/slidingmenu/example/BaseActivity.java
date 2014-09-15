package com.jeremyfeinstein.slidingmenu.example;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jiaziang8.alarm.service.MyService;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;
	private SetingFragment mSetting;
	private static List<HashMap<String, Object>> mData ;
	private static final int LIST_OK = 1;
	private Handler handler =new Handler(){
		@Override 
		public void handleMessage(Message msg){
			switch (msg.what) {
			case LIST_OK:
				Intent add_friendIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ViewPagerActivity.account);				
				add_friendIntent.setClass(BaseActivity.this, ContactsActivity.class);
				add_friendIntent.putExtras(bundle);
				startActivity(add_friendIntent);
				break;
			default:
				break;
			}
		}
	};
	

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);
		

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			//mFrag = new SampleListFragment();
			mSetting = new SetingFragment();
			t.replace(R.id.menu_frame, mSetting);
			t.commit();
		} else {
			mSetting = (SetingFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
			//mFrag = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		//sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.menu));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.github:
			//Util.goToGitHub(this);
			refreshData();	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void refreshData(){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				mData = MyService.getData(ViewPagerActivity.account);
				Message msg = new Message();
				msg.what = LIST_OK;
				handler.sendMessage(msg);
			}
		}).start();
	}
	public static List<HashMap<String, Object>> getData() {
		return mData;
	}
}
