package com.jiaziang8.alarm.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.Alarm.DaysOfWeek;
import com.jeremyfeinstein.slidingmenu.example.NotificationActivity;
import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.util.Constants;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AlarmDetailListener implements OnClickListener {
	private static AlarmDetailListener instance = null;
	Context context = null;
	Context activityContext;
	private File SDPathDir;
	private File tempFile;
	private Uri alarmUri;

	private int ServerId;
	private int mHour;
	private int mMunite;
	private DaysOfWeek daysOfWeek;
	private String lable;
	private boolean enabled;
	private boolean vibrate;
	private int id;

	private Alarm alarm;

	String urlString = MyService.DOWNURL;

	public AlarmDetailListener() {
	}

	public static AlarmDetailListener getInstance() {
		if (instance == null) {
			instance = new AlarmDetailListener();
		}
		return instance;
	}

	@Override
	public void onClick(View view) {
		final View buttonView = (NotificationButton) view;
		context = view.getContext().getApplicationContext();
		activityContext = view.getContext();
		String filename = ((NotificationButton) buttonView).getFilename();

		ServerId = ((NotificationButton) buttonView).getId();
		mHour = ((NotificationButton) buttonView).getmHour();
		mMunite = ((NotificationButton) buttonView).getMinute();
		daysOfWeek = new Alarm.DaysOfWeek(
				((NotificationButton) buttonView).getmDays());
		lable = ((NotificationButton) buttonView).getWordsToSay();
		enabled = true;
		vibrate = true;
		id = -1;

		alarm = new Alarm();
		alarm.id = id;
		alarm.enabled = enabled;
		alarm.hour = mHour;
		alarm.minutes = mMunite;
		alarm.daysOfWeek = daysOfWeek;
		alarm.vibrate = vibrate;
		alarm.label = lable;
		alarm.alert = alarmUri;

		final String realfilename = filename.substring( // 获取文件名
				filename.lastIndexOf("\\") + 1);		
		Log.v("nimei", "realfilename"+realfilename);
		new Thread(new Runnable() {
			@Override
			public void run() {
				long time;
				alarmUri = loadFile(realfilename, ServerId);
				time = addNewAlarm(alarmUri, alarm);
				//Log.v("nimei", "alarmUri is:" + alarmUri);
				Message msg = new Message(); // 下载完成时
				msg.what = NotificationActivity.DOWNLOAD_COMPLET;
				msg.arg1 = mHour;
				msg.arg2 = mMunite;
				msg.obj = daysOfWeek;
				((NotificationButton) buttonView).getHandler().sendMessage(msg);
				Message refreshNotification = new Message();
				refreshNotification.what = NotificationActivity.REFRESH_LIST;
				((NotificationButton) buttonView).getHandler().sendMessage(
						refreshNotification);
				Message finishMessage = new Message();
				finishMessage.what = NotificationActivity.FINISH_ACTIVITY;
				((NotificationButton) buttonView).getHandler().sendMessage(
						finishMessage);
			}
		}).start();
	}

	private long addNewAlarm(Uri alarmUri, Alarm alarm) {
		alarm.alert = alarmUri;
		return Alarms.addAlarm(context, alarm);
	}

	private Uri loadFile(String filename, int id) {
		SDPathDir = Environment.getExternalStorageDirectory();
		String path = SDPathDir.toString() + "/MyRecord/MyDownLoad";
		SDPathDir = new File(path);
		if (!SDPathDir.exists()) {
			SDPathDir.mkdirs();
		}
		try {
			HttpPost post = new HttpPost(urlString);
			post.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("filename", filename));
			param.add(new BasicNameValuePair("id", String.valueOf(id)));
			Log.v("nimei", "filename"+filename);
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				byte[] data = readInputstream(inputStream);
				tempFile = File.createTempFile("loadFile", ".amr", SDPathDir);
				FileOutputStream fileOutputStream = new FileOutputStream(
						tempFile);
				fileOutputStream.write(data);
				fileOutputStream.close();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
				{
				        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				        File f = new File("file://"+ Environment.getExternalStorageDirectory());
				        Uri contentUri = Uri.fromFile(f);
				        mediaScanIntent.setData(contentUri);
				        context.sendBroadcast(mediaScanIntent);
				}
				else
				{
				       context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				} 

				return getImageContentUri(context, tempFile);
				// return Uri.fromFile(tempFile);
			} else
			return Uri.parse(Uri.fromFile(tempFile).toString());
		} catch (Exception e) {

			Log.v("nimei", "alarmUri is1111112222222221:");
			e.printStackTrace();
			return Uri.fromFile(tempFile);
		}
	}

	public static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		//Log.v("nimei", "filepath:" + filePath);
		Cursor cursor = context.getContentResolver().query(
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID },
				MediaStore.Audio.Media.DATA + "=? ", new String[] { filePath },
				null);
		cursor.moveToFirst();
		int id = cursor.getInt(cursor
				.getColumnIndex(MediaStore.MediaColumns._ID));
		Uri baseUri = Uri.parse("content://media/external/audio/media");
/*		Log.v("nimei",
				"alarmUri is~~~~~~~~~~~~~~~~~~~:"
						+ Uri.withAppendedPath(baseUri, "" + id));*/
		return Uri.withAppendedPath(baseUri, "" + id);
	}

	public static byte[] readInputstream(InputStream inputStream)
			throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		byteArrayOutputStream.close();
		inputStream.close();
		return byteArrayOutputStream.toByteArray();

	}

}
