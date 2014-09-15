package com.jeremyfeinstein.slidingmenu.example;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jiaziang8.alarm.service.Contacts;

public class ViewPagerActivity extends BaseActivity {
	private static ViewPager viewPager;
	private ImageView imageView;
	private TextView my_friend;
	private TextView callbed;
	private TextView log ;
	private int offset;
	private int currentIndex;
	private int bmp_width;
	public static Handler handler ;
	public static String account = "";
    public static final String PREFERENCES = "AlarmClock";
    private File cache;
    public static boolean DATKAOK = false ;
    public static Contacts contacts;
    private long exitTime = 0;

	public ViewPagerActivity() {
		super(R.string.viewpager);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content);
		InitImageView();
		InitTextView();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		account = bundle.getString("account");
    	cache = new File(Environment.getExternalStorageDirectory(), "MyRecord/cache");       
        if(!cache.exists()){
            cache.mkdirs();
        }
        contacts = new Contacts(getApplicationContext());
        contacts.getPhoneContacts();
		

		viewPager = (ViewPager)this.findViewById(R.id.viewpager);
		viewPager.setId("VP".hashCode());
		viewPager.setAdapter(new ColorPagerAdapter(getSupportFragmentManager()));
		

		//切换Fragment时
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			int one = offset*2+bmp_width;
			int two = one*2;
			@Override
			public void onPageScrollStateChanged(int position) { 

			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { 
/*				Matrix matrix = new Matrix();
				matrix.postTranslate(position*one+positionOffsetPixels/6, 0);
				imageView.setImageMatrix(matrix);*/
			}

			@Override
			public void onPageSelected(int position) {
				Animation animation = new TranslateAnimation(one*currentIndex, one*position,0,0);
				currentIndex = position;
				animation.setFillAfter(true);
				animation.setDuration(200);
				imageView.startAnimation(animation);
				switch (position) {
				case 0:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);		
					my_friend.setTextColor(getResources().getColor(R.color.red));
					callbed.setTextColor(getResources().getColor(R.color.black));
					log.setTextColor(getResources().getColor(R.color.black));
					break;
				case 1:
					my_friend.setTextColor(getResources().getColor(R.color.black));
					callbed.setTextColor(getResources().getColor(R.color.red));
					log.setTextColor(getResources().getColor(R.color.black));
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); 
					break;
				case 2:
					my_friend.setTextColor(getResources().getColor(R.color.black));
					callbed.setTextColor(getResources().getColor(R.color.black));
					log.setTextColor(getResources().getColor(R.color.red));
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); 
					break;
				default:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); 
					break;
				}
			}

		});
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				switch (msg.what) {
				case 1:
					Toast.makeText(ViewPagerActivity.this, "请求已发送", Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(ViewPagerActivity.this, "添加好友失败", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(ViewPagerActivity.this, "该好友已添加过", Toast.LENGTH_SHORT).show();
				default:
					break;
				}
			}
		};
		
		//初始界面
		my_friend.setTextColor(getResources().getColor(R.color.red));
		viewPager.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	private void InitTextView(){
		my_friend = (TextView)this.findViewById(R.id.my_friend);
		callbed=(TextView)this.findViewById(R.id.callbed);
		log = (TextView)this.findViewById(R.id.log);
		
		my_friend.setOnClickListener(new MyOnClickListener(0));
		callbed.setOnClickListener(new MyOnClickListener(1));
		log.setOnClickListener(new MyOnClickListener(2));
	}
	
	private void InitImageView(){
		imageView = (ImageView)this.findViewById(R.id.cursor);
		bmp_width = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();		
		DisplayMetrics dMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
		int screen_width = dMetrics.widthPixels;   		//屏幕宽度
		offset=(screen_width/3-bmp_width)/2;            //offset: 指向标离边缘的距离
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);               //初始位置  offset
	}
	
	private class MyOnClickListener implements OnClickListener{
		private int index ;
		public MyOnClickListener(int i){
			index = i;
		}
		public void onClick(View v){
			viewPager.setCurrentItem(index);
		}
	}

	public class ColorPagerAdapter extends FragmentPagerAdapter {
		
		private ArrayList<Fragment> mFragments;
		SetAlarmFragment setAlarmFragment;
		MyAlarmFragment myAlarmFragment;
		
		public ColorPagerAdapter(FragmentManager fm) {
			super(fm);
			FriendListFragment friendListFragment = new FriendListFragment();
			setAlarmFragment = new SetAlarmFragment();
			myAlarmFragment = new MyAlarmFragment();
			mFragments = new ArrayList<Fragment>();
			
			mFragments.add(myAlarmFragment);
			mFragments.add(setAlarmFragment);
			mFragments.add(friendListFragment);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}
	}
	
	public static Handler getHandler(){
		return handler;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		JPushInterface.onPause(this);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//清空缓存
        File[] files = cache.listFiles();
        for(File file :files){
            file.delete();
        }
        cache.delete();
	}

	public static void changeToHomepage(){
		viewPager.setCurrentItem(0);
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (KeyEvent.KEYCODE_BACK == keyCode) {  
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出  
            if (System.currentTimeMillis() - exitTime > 2000) {  
                Toast.makeText(getApplicationContext(), "再按一次退出程序",  
                        Toast.LENGTH_SHORT).show();  
                // 将系统当前的时间赋值给exitTime  
                exitTime = System.currentTimeMillis();  
            } else {  
            	return super.onKeyDown(keyCode, event); 
            }  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }  

}
