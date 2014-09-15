package com.jiaziang8.alarm.ui;

import com.jeremyfeinstein.slidingmenu.example.ViewPagerActivity;
import com.jiaziang8.alarm.service.MyService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;

public class AddFriendOnClickListener implements OnClickListener {
	private static AddFriendOnClickListener instance = null;
	public Context context ;
	private Handler handler;
	private static final int REFRESH_DATA = 3;
	public AddFriendOnClickListener(){
		handler = ViewPagerActivity.getHandler();
	}
	
	public static AddFriendOnClickListener getAddFriendOnClickListenerInstance(){
		if(instance==null){
			instance = new AddFriendOnClickListener();
		}
		return instance;
	}
	
	@Override
	public void onClick(View view){
		final View buttonView = view;
		final String number = ((MyButton)view).getFriend().replaceAll(" ", "");
		String nameString = ((MyButton)view).getname();
		//int index = ((MyButton) view).getIndex();
		//String nameString = Contacts.mContactsName.get(index);
		//final String number = Contacts.mContactsNumber.get(index).replaceAll(" ", "");		
		context = ((MyButton) view).getContext();
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认添加手机联系人"+nameString+"吗?");
		builder.setTitle("添加好友");
		
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {	
			int  isSuccess;
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						isSuccess = MyService.addfriend(ViewPagerActivity.account, number);
						Message msg = new Message();
						msg.what=isSuccess;
						handler.sendMessage(msg);
						
						Message message2 = new Message();
						message2.what = REFRESH_DATA;
						((MyButton)buttonView).getHandle().sendMessage(message2);
					}
				}).start();			
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
		});
		builder.create().show();
		
	}
	

	
}
