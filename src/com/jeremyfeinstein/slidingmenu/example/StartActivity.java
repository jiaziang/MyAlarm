package com.jeremyfeinstein.slidingmenu.example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.jiaziang8.alarm.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends InstrumentedActivity {

	private EditText account;
	private EditText password;
	private Button connect;
	private Button regist;
	private static final String ServletUrl = Constants.url
			+ "/AlarmServer2/LoginServlet";
	private static String ACCOUNT = "";
	private static final int MSG = 1;
	private static final int LOGIN_ERROR = 2;
	private static final int INTERNET_ERROR = 3;
	//public static Contacts contacts;
	private SharedPreferences sharedPreferences;
	private static Context startContext;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG:
/*				Toast.makeText(getApplicationContext(), "登录成功",
						Toast.LENGTH_SHORT).show();*/
				Intent contentIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", ACCOUNT);
				contentIntent.setClass(StartActivity.this,
						ViewPagerActivity.class);
				contentIntent.putExtras(bundle);
				startActivity(contentIntent);
				StartActivity.this.finish();
				break;
			case LOGIN_ERROR:
				Toast.makeText(StartActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();;
				break;				
			case INTERNET_ERROR:
				Toast.makeText(StartActivity.this, "请检查网络连接是否正常", Toast.LENGTH_SHORT).show();;
				break;
			default:
				;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startContext = this;
		sharedPreferences = this.getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
	
		//如果自动登陆
		if (sharedPreferences.getBoolean("AUTO_LOGIN", false)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String accountString = sharedPreferences.getString(
							"account", "");
					String passwordString = sharedPreferences.getString(
							"password", "");
					if (login(accountString, passwordString)) {
						if (JPushInterface
								.isPushStopped(getApplicationContext())) {
							JPushInterface.resumePush(getApplicationContext());
						}
						if (ACCOUNT.indexOf("+86") == 0) {
							ACCOUNT = ACCOUNT.substring(3);
						}
						JPushInterface.setAlias(getApplicationContext(),
								ACCOUNT, null);
						Message msg = new Message();
						msg.what = MSG;
						handler.sendMessage(msg);
					}
					else{
						Message message = new Message();
						message.what = INTERNET_ERROR;
						handler.sendMessage(message);
					}
				}
			}).start();

		} else {
			setContentView(R.layout.activity_start);

			account = (EditText) this.findViewById(R.id.account);
			password = (EditText) this.findViewById(R.id.password);
			connect = (Button) this.findViewById(R.id.connect);
			regist = (Button) this.findViewById(R.id.regist);
			account.setText(sharedPreferences.getString("account", ""));
			account.setSelection(account.getText().length());

			//点击登陆
			connect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						public void run() {
							if (login(account.getText().toString(), password
									.getText().toString())) {
								Editor editor = sharedPreferences.edit();
								editor.putString("account", account.getText()
										.toString());
								editor.putString("password", password.getText()
										.toString());
								editor.putBoolean("AUTO_LOGIN", true).commit();
								if (JPushInterface
										.isPushStopped(getApplicationContext())) {
									JPushInterface
											.resumePush(getApplicationContext());
								}
								if (ACCOUNT.indexOf("+86") == 0) {
									ACCOUNT = ACCOUNT.substring(3);
								}
								JPushInterface.setAlias(
										getApplicationContext(), ACCOUNT, null);
								Message msg = new Message();
								msg.what = MSG;
								handler.sendMessage(msg);
							}else{
								Message message = new Message();
								message.what = LOGIN_ERROR;
								handler.sendMessage(message);
							}
						}
					}).start();
				}
			});

			//点击注册	
			regist.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent registIntent = new Intent();
					registIntent.setClass(StartActivity.this,
							RegistActivity.class);
					startActivity(registIntent);
					StartActivity.this.finish();
				}
			});
		}
	}

	public static boolean login(String account, String password) {
		HttpPost post = new HttpPost(ServletUrl);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username", account));
		param.add(new BasicNameValuePair("userpwd", password));

		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			try {
				HttpResponse response = new DefaultHttpClient().execute(post);
				//登陆成功
				if (response.getStatusLine().getStatusCode() == 201) {
					ACCOUNT = account;
					return true;
				} else if(response.getStatusLine().getStatusCode() == 202){
					
					return false;
				}
					return false;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
