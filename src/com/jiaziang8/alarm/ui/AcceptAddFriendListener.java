package com.jiaziang8.alarm.ui;

import com.jeremyfeinstein.slidingmenu.example.NotificationActivity;
import com.jiaziang8.alarm.service.MyService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class AcceptAddFriendListener implements OnClickListener {
	private static AcceptAddFriendListener instance = null;
	Context context = null;

	public AcceptAddFriendListener() {

	}

	public static AcceptAddFriendListener getInstance() {
		if (instance == null) {
			instance = new AcceptAddFriendListener();
		}
		return instance;
	}

	@Override
	public void onClick(View view) {
		final View buttonView = (NotificationButton) view;
		context = ((NotificationButton)view).getContext();
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认添加"+((NotificationButton) buttonView).getAccount()+"为好友?");
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean add1 = MyService.reallyAddFriend(
								((NotificationButton) buttonView).getAccount(),
								((NotificationButton) buttonView).getselfAccount());
						boolean add2 = MyService.reallyAddFriend(
								((NotificationButton) buttonView).getselfAccount(),
								((NotificationButton) buttonView).getAccount());
						Message message = new Message();
						message.what = NotificationActivity.REFRESH_LIST;
						((NotificationButton)buttonView).getHandler().sendMessage(message);
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
