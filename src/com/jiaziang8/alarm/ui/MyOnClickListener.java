package com.jiaziang8.alarm.ui;

import com.jiaziang8.alarm.service.MyService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class MyOnClickListener implements OnClickListener {
	private static MyOnClickListener instance = null;
	Context context = null;
	private static final int DELETE_OK = 2;
	private static final int REFRESH_DATA = 3;

	public MyOnClickListener() {

	}

	public static MyOnClickListener getInstance() {
		if (instance == null) {
			instance = new MyOnClickListener();
		}
		return instance;
	}

	@Override
	public void onClick(View view) {
		final View buttonView = (MyButton) view;
		String nameString = ((MyButton) view).getname();
		context = ((MyButton) view).getContext();
		AlertDialog.Builder builder = new Builder(context);

		builder.setMessage("确认删除" + nameString + "吗?");
		builder.setTitle("删除好友");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread(new Runnable() {				
					@Override
					public void run() {
						MyService.deleteFriend(((MyButton) buttonView).getAccount(),
								((MyButton) buttonView).getFriend());		
						Message message = new Message() ;
						message.what = DELETE_OK;
						((MyButton)buttonView).getHandle().sendMessage(message);
						Message message2  = new Message();
						message2 .what = REFRESH_DATA;
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