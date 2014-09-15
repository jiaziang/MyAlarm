package com.jiaziang8.alarm.service;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.example.NotificationActivity;
import com.jeremyfeinstein.slidingmenu.example.ReviewActivity;
import com.jiaziang8.alarm.object.AddFriendMessage;
import com.jiaziang8.alarm.object.PushMessage;
import com.jiaziang8.alarm.object.PushObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private Gson gson;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		gson = new Gson();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			String whatString = bundle.getString("cn.jpush.android.EXTRA");
			PushObject pushObject = gson.fromJson(whatString, PushObject.class);
			Log.v("JPush", "whatString:"+whatString);
			String pushMessageString = pushObject.getPushmessage();
			PushMessage pushMessage = gson.fromJson(pushMessageString, PushMessage.class);
			Log.v("JPush", pushMessage.toString());
			if(pushMessage.getType().equals("add_friend")){
				String message = pushMessage.message;
				AddFriendMessage addFriendMessage = gson.fromJson(message, AddFriendMessage.class);
				String user = addFriendMessage.user;
				String friend = addFriendMessage.friend;
				Intent intent2 = new Intent();
				Bundle bundle2 = new Bundle();
				bundle2.putString("account", friend);
				bundle2.putBoolean("hasMainActivity", false);
				intent2.putExtras(bundle2);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.setClass(context, NotificationActivity.class);
				context.startActivity(intent2);
				
				Log.v("JPush", "user:"+user+"  friend:"+friend);
			}
			else if(pushMessage.getType().equals("add_alarm")){
				String message = pushMessage.message;
				AddFriendMessage addFriendMessage = gson.fromJson(message, AddFriendMessage.class);
				String user = addFriendMessage.user;
				String friend = addFriendMessage.friend;
				
				Intent intent3 = new Intent();
				Bundle bundle3 = new Bundle();
				bundle3.putString("account", friend);
				bundle3.putBoolean("hasMainActivity", false);
				intent3.putExtras(bundle3);
				intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent3.setClass(context, NotificationActivity.class);
				context.startActivity(intent3);
			}else if(pushMessage.getType().equals("accept_alarm")){
				String message = pushMessage.message;
				AddFriendMessage addFriendMessage = gson.fromJson(message, AddFriendMessage.class);
				String user = addFriendMessage.user;
				String friend = addFriendMessage.friend;
				
				Intent intent4 = new Intent();
				Bundle bundle4 = new Bundle();
				bundle4.putString("account", user);
				bundle4.putBoolean("hasMainActivity", false);
				intent4.putExtras(bundle4);
				intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent4.setClass(context, ReviewActivity.class);
				context.startActivity(intent4);
			}

			//Log.v("JPush", "user:"+userString+" friend:"+friendString);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

}
