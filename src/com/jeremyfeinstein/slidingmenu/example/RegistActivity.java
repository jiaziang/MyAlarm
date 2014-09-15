package com.jeremyfeinstein.slidingmenu.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.jiaziang8.alarm.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends Activity {
	private EditText account;
	private EditText password;
	private Button confirm;
	private static final int EXIST_MSG = 0;
	private static final String ServletUrl = Constants.url
			+ "/AlarmServer/RegistServlet";

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXIST_MSG:
				confirm.setEnabled(true);
				Toast.makeText(RegistActivity.this, "该手机号已注册",
						Toast.LENGTH_LONG).show();
				break;
			default:
				;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		account = (EditText) this.findViewById(R.id.account);
		password = (EditText) this.findViewById(R.id.password);
		confirm = (Button) this.findViewById(R.id.confirm);

		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (account.getText().toString().equals("")
						|| password.getText().toString().equals("")) {
					Toast.makeText(RegistActivity.this, "请输入手机号或密码",
							Toast.LENGTH_LONG).show();
				} else if (account.getText().toString().length() != 11
						|| !account.getText().toString().substring(0, 1)
								.equals("1")) {
					Toast.makeText(RegistActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
				} else {
					confirm.setEnabled(false);
					new Thread(new Runnable() {
						@Override
						public void run() {							
							regist();
						}
					}).start();
				}
			}
		});
	}

	private void regist() {
		HttpPost post = new HttpPost(ServletUrl);
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		String accountString = account.getText().toString();
		param.add(new BasicNameValuePair("username", account.getText()
				.toString()));
		param.add(new BasicNameValuePair("userpwd", password.getText()
				.toString()));

		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 201) {
				// Log.v(TEST, "Login Success!");
				Intent checkIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("account", accountString);
				checkIntent.setClass(RegistActivity.this,
						SmsConfirmActivity.class);
				checkIntent.putExtras(bundle);
				startActivity(checkIntent);
				RegistActivity.this.finish();
			}
			if (response.getStatusLine().getStatusCode() == 202) {
				Message msg = new Message();
				msg.what = EXIST_MSG;
				handler.sendMessage(msg);
			}
		} catch (IOException e) {

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(RegistActivity.this, StartActivity.class);
			startActivity(intent);
			RegistActivity.this.finish();

		}
		return false;
	}

}