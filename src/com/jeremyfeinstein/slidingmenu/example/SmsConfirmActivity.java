package com.jeremyfeinstein.slidingmenu.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.util.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmsConfirmActivity extends Activity {
	//private static final String ServletUrl = Constants.url+"/AlarmServer/CheckServlet";
	private static final int REGIST_SUCCESS = 1;
	private static final int REGIST_FAILER = 0;
	String accountString ;
	TextView info;
	EditText checknumber;
	Button regetButton;
	Button registButton;
	
	private  Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			int time = msg.what;
			if (time>0){
				regetButton.setText("重新获取验证码("+time+"s)");
			}
			else {
				regetButton.setEnabled(true);
				regetButton.setText("重新获取验证码");
			}		
		}
	};
	
	private Handler uIHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case REGIST_SUCCESS:
					Toast.makeText(SmsConfirmActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("account", accountString);
					intent.putExtras(bundle);
					intent.setClass(SmsConfirmActivity.this,ViewPagerActivity.class);
					startActivity(intent);
					SmsConfirmActivity.this.finish();
				break;
			case REGIST_FAILER:
					Toast.makeText(SmsConfirmActivity.this,"验证码错误~" ,Toast.LENGTH_SHORT).show();
					
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_confirm);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			accountString = bundle.getString("account");
		}
		info = (TextView)this.findViewById(R.id.info);
		checknumber = (EditText)this.findViewById(R.id.checknumber);
		regetButton = (Button)this.findViewById(R.id.reget);
		registButton = (Button)this.findViewById(R.id.regist);
		info.setText("已经向"+accountString+"发送短信");
		
		regetButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				//reget_checknumber();
				regetButton.setEnabled(false);
				new timeThread().start();
			}
		});
		
		registButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						submit();
					}
				}).start();
			}
		});	
		new timeThread().start();
	}
	
	private void submit(){
		HttpPost post = new HttpPost(MyService.CHECKURL);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username", accountString));
		param.add(new BasicNameValuePair("checknumber", checknumber.getText().toString()));
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if(response.getStatusLine().getStatusCode()==201){
				Message msg= new Message();
				msg.what=REGIST_SUCCESS;
				uIHandler.sendMessage(msg);				
			}
			if(response.getStatusLine().getStatusCode()==202){
				Message msg = new Message();
				msg.what = REGIST_FAILER;
				uIHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			
		}
		
	}
	
	private class timeThread extends Thread{
		private int time = 60;
		
		@Override
		public void run(){
			while(time>-1){
				Message msg = new Message();
				msg.what = time;
				handler.sendMessage(msg);
				time --;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode ==KeyEvent.KEYCODE_BACK){
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			isExit.setTitle("提示？");
			isExit.setMessage("放弃本次注册?");	
			isExit.setButton(AlertDialog.BUTTON_POSITIVE,"确认", listener);
			isExit.setButton(AlertDialog.BUTTON_NEGATIVE,"取消", listener);
			isExit.show();
		}		
		return false;
	}
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:
				SmsConfirmActivity.this.finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				break;
			default:
				break;
			}
		}
	};
	



}
