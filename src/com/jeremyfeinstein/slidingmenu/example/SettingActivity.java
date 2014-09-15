package com.jeremyfeinstein.slidingmenu.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.Header;

import cn.jpush.android.api.JPushInterface;

import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.SelectPicPopupWindow;
import com.jiaziang8.alarm.util.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingActivity extends Activity {
    private SharedPreferences sharedPreferences;
	private ImageView userheadImageView;
	private RelativeLayout imageRelativeLayout;
	private RelativeLayout problemRelativeLayout;
	private RelativeLayout aboutRelativeLayout;
	private CheckBox dontCheckBox;
	private Button exitButton;
	private RelativeLayout backRelativeLayout;
	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private Bitmap bitmap;
    private static final int ALARM_STREAM_TYPE_BIT =
            1 << AudioManager.STREAM_ALARM;
    private File cache;
	
	/* 头像名称 */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_content);
		sharedPreferences = getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		dontCheckBox = (CheckBox)this.findViewById(R.id.dont_onoff);
		userheadImageView = (ImageView) this.findViewById(R.id.user_head);
		imageRelativeLayout = (RelativeLayout)this.findViewById(R.id.head_layout);
		problemRelativeLayout = (RelativeLayout)this.findViewById(R.id.problem_layout);
		aboutRelativeLayout = (RelativeLayout)this.findViewById(R.id.about_layout);
		exitButton = (Button)this.findViewById(R.id.exit_button);
    	cache = new File(Environment.getExternalStorageDirectory(), "MyRecord/cache");       
        if(!cache.exists()){
            cache.mkdirs();
        }
		backRelativeLayout = (RelativeLayout)this.findViewById(R.id.back_layout);
		backRelativeLayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				try {
					Runtime runtime = Runtime.getRuntime();
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				} catch (IOException e) {
					Log.e("Exception when doBack", e.toString());
				}
			}
		});
		
		problemRelativeLayout.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ProblemsActivity.class);
				startActivity(intent);
			}
		});
		
		aboutRelativeLayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AboutActivity.class);
				startActivity(intent);				
			}
		});
		
		imageRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 实例化SelectPicPopupWindow
				menuWindow = new SelectPicPopupWindow(SettingActivity.this,
						itemsOnClick);
				// 显示窗口
				menuWindow.showAtLocation(
						SettingActivity.this.findViewById(R.id.main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		});
        int ringerModeStreamTypes = Settings.System.getInt(
                getContentResolver(),
                Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);
        if((ringerModeStreamTypes&(~ALARM_STREAM_TYPE_BIT))==ringerModeStreamTypes){
        	dontCheckBox.setChecked(true);
        }else{
        	dontCheckBox.setChecked(false);
        }
		dontCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
			 int ringerwhat =Settings.System.getInt(
	                getContentResolver(),
	                Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);
			@Override

			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {

				if(ischecked){
					ringerwhat &= ~ALARM_STREAM_TYPE_BIT;
				}else{
					ringerwhat |= ALARM_STREAM_TYPE_BIT;
				}
	            Settings.System.putInt(getContentResolver(),
	                    Settings.System.MODE_RINGER_STREAMS_AFFECTED,
	                    ringerwhat);
			}
		});
		exitButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Editor editor = sharedPreferences.edit();
				editor.putBoolean("AUTO_LOGIN", false).commit();
				JPushInterface.stopPush(getApplicationContext()); //停止推送服务
				SettingActivity.this.finish();
				SetingFragment.instance.finish();
			}
		});
		asyncloadImage(userheadImageView);
	}
	
    //为弹出窗口实现监听类
    private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				camera();
				break;
			case R.id.btn_pick_photo:
				gallery();
				break;
			default:
				break;
			}				
		}   	
    };
    
	/*
	 * 从相册获取
	 */
	public void gallery() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	
	/*
	 * 从相机获取
	 */
	public void camera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(SettingActivity.this, "未找到存储卡，无法存储照片！", 0).show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			try {
				bitmap = data.getParcelableExtra("data");
				//this.userheadImageView.setImageBitmap(bitmap);   //~~~~~~~~~~~~~~~

				if(tempFile!=null){
					boolean delete = tempFile.delete();
				}			
				upload();				

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("scale", true);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void asyncloadImage(ImageView imageView){
		MyService imageMyService = new MyService();
		 AsyncImageTask task = new AsyncImageTask(imageMyService, imageView);
		 task.execute(ViewPagerActivity.account);
	}
	
	private final class AsyncImageTask extends AsyncTask<String, Integer, Uri>{
		private MyService service;
		private ImageView imageView;
		public AsyncImageTask(MyService myService,ImageView imageView){
			this.service = myService;
			this.imageView = imageView;
		}
		@Override 
		protected Uri doInBackground(String... params){
			try {
				String path = service.getHeadPathByAccount(params[0]);
				if(path.equals("")) return null;
				path = MyService.GETHEADURL+path.substring(path.lastIndexOf("/"));
				return service.getImageURI(path, cache);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override 
		protected void onPostExecute(Uri result){
			super.onPostExecute(result);
			if(imageView!=null&&result!=null){
				imageView.setImageURI(result);
			}
		}		
	}
	
	/*
	 * 上传图片
	 */
	public void upload() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();

			byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			String photo = new String(encode);
			RequestParams params = new RequestParams();
			params.put("photo", photo);
			params.put("account", ViewPagerActivity.account);
			String url = MyService.HEADURL;
			

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					try {
						if (statusCode == 200) {
							Toast.makeText(SettingActivity.this, "头像上传成功!", 0)
									.show();
							//通知主界面和本界面更新头像
							Message message = new Message() ;
							message.what = SetingFragment.REFRESHIMAGE;
							SetingFragment.getHandler().sendMessage(message);
							asyncloadImage(userheadImageView);
						} else {
							Toast.makeText(SettingActivity.this,
									"网络访问异常，错误码：" + statusCode, 0).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(SettingActivity.this,
							"网络访问异常，错误码  > " + statusCode, 0).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
