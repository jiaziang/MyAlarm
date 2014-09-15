package com.jiaziang8.alarm.ui;

import com.jeremyfeinstein.slidingmenu.example.NotificationActivity;
import com.jiaziang8.alarm.service.MyService;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class RefuseAlarmListener implements OnClickListener{
	private static RefuseAlarmListener instance = null;
	Context context = null;	
	public RefuseAlarmListener() {

	}

	public static RefuseAlarmListener getInstance() {
		if (instance == null) {
			instance = new RefuseAlarmListener();
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
				MyService.refuseAlarm(((NotificationButton)buttonView).getId());
				Message message = new Message();
				message.what = NotificationActivity.REFRESH_LIST;
				((NotificationButton)buttonView).getHandler().sendMessage(message);
			}
		}).start();
	}
}
