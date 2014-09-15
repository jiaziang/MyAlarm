package com.jiaziang8.alarm.ui;

import com.jeremyfeinstein.slidingmenu.example.NotificationActivity;
import com.jiaziang8.alarm.service.MyService;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class RefuseAddFriendListener implements OnClickListener{
	private static RefuseAddFriendListener instance = null;
	Context context = null;	
	public RefuseAddFriendListener() {
		
	}

	public static RefuseAddFriendListener getInstance() {
		if (instance == null) {
			instance = new RefuseAddFriendListener();
		}
		return instance;
	}
	
	@Override 
	public void onClick(View view){
		final View buttonView = (NotificationButton) view;
		context = ((NotificationButton)view).getContext();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				MyService.refuseAddFriend(((NotificationButton) buttonView).getAccount(),
								((NotificationButton) buttonView).getselfAccount());
				Message message = new Message();
				message.what = NotificationActivity.REFRESH_LIST;
				((NotificationButton)buttonView).getHandler().sendMessage(message);
			}
		}).start();
		
	}

}
