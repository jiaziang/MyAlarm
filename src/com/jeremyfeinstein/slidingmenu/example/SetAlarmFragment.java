package com.jeremyfeinstein.slidingmenu.example;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import com.jiaziang8.alarm.service.MyService;
import com.jiaziang8.alarm.ui.CustomExpandableListView;
import com.jiaziang8.alarm.util.Constants;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAlarmFragment extends Fragment implements
		TimePickerDialog.OnTimeSetListener {
	private int mHour;
	private int mMinutes;
	private static int mDays;
	private String friend_name;
	private String friend_number;
	private TextView timeTextView;
	private TextView selected_friendNameTextView;
	private TextView record_timeTextView;
	private TextView words_tosay;
	private Button start_record;
	private Button clear;
	private Button play;
	private Button submit;
	private ProgressBar uploadBar;
	private RelativeLayout timepickLayout;
	private RelativeLayout selectLayout;
	private boolean isSDCardExit; // 判断SDCard是否存在
	private boolean isRecording; // 是否已开始录音
	private boolean isPlaying; // 是否正在播放录音
	private List<String> groupDatas;
	private List<List<HashMap<String, String>>> childDatas;
	private CustomExpandableListView expandableListView;
	private MyExpandableListViewAdapter expandableListViewAdapter;
	private MediaRecorder recorder;
	private MediaPlayer mediaPlayer;
	private File SDPathDir;
	private File tempFile;
	private String urlStr = MyService.UPLOADALARMURL;
	private int record_time;
	private static Handler handler;
	private Runnable runnable;
	private static final int UPLOAD_START = 2;
	private static final int UPLOAD_END = 3;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Inflater inflater = new Inflater();
		record_time = 0;
		mDays = 0;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					String timeString;
					if (msg.arg1 < 10) {
						timeString = "00:0" + msg.arg1;
					} else if (msg.arg1 >= 10 && msg.arg1 < 60) {
						timeString = "00:" + msg.arg1;
					} else {
						timeString = "01:00";
					}
					record_timeTextView.setText(timeString);
					break;
				case UPLOAD_START :
					uploadBar.setVisibility(View.VISIBLE);
					break;
				case UPLOAD_END:
					uploadBar.setVisibility(View.INVISIBLE);
					Toast.makeText(getActivity(), "上传成功～", Toast.LENGTH_SHORT).show();
					mHour = 0;
					mMinutes = 0;
					timeTextView.setText("00:00");
					words_tosay .setText("");
					if (tempFile.exists()) {
						tempFile.delete();
						tempFile = null;
					}
					start_record.setEnabled(true);
					start_record.setBackgroundResource(R.drawable.start_record);
					clear.setEnabled(false);					
					clear.setTextColor(getResources().getColor(R.color.gray));
					play.setEnabled(false);
					mDays = 0;
					InitDatas();
					expandableListViewAdapter = new MyExpandableListViewAdapter(
							getActivity(), groupDatas, childDatas);
					expandableListView.setAdapter(expandableListViewAdapter);
					selected_friendNameTextView.setText("未选择");
					friend_name = null;
					friend_number = null;
					
					
				default:
					break;
				}
			}
		};
		isSDCardExit = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		SDPathDir = Environment.getExternalStorageDirectory();
		String path = SDPathDir.toString() + "/MyRecord";
		SDPathDir = new File(path);
		if (!SDPathDir.exists()) {
			SDPathDir.mkdirs();
		}

		isRecording = false;
		isPlaying = false;
		
		InitDatas();

		runnable = new Runnable() {
			@Override
			public void run() {
				while (isRecording) {
					try {
						if (record_time < 60) {
							Message message = new Message();
							message.what = 1;
							message.arg1 = record_time;
							handler.sendMessage(message);
							Thread.sleep(1000);
							record_time++;
						} else {
							isRecording = false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mediaPlayer = new MediaPlayer();
		if(view ==null){
			view = inflater.inflate(R.layout.setalarm, null);
		}		
		timeTextView = (TextView) view.findViewById(R.id.time_value);
		selectLayout = (RelativeLayout) view
				.findViewById(R.id.select_friend_layout);
		selected_friendNameTextView = (TextView)view.findViewById(R.id.friendname_text);
		record_timeTextView = (TextView) view.findViewById(R.id.record_time);
		words_tosay = (TextView) view.findViewById(R.id.words_tosay);
		start_record = (Button) view.findViewById(R.id.start_record);
		timepickLayout = (RelativeLayout) view.findViewById(R.id.timepick);
		clear = (Button) view.findViewById(R.id.clear);
		clear.setEnabled(false);
		clear.setTextColor(getResources().getColor(R.color.gray));
		play = (Button) view.findViewById(R.id.play);
		submit = (Button) view.findViewById(R.id.submit);
		uploadBar = (ProgressBar)view.findViewById(R.id.uploading_progressbar);
		timepickLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showTimePicker();
			}
		});

		selectLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SelectFriendActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		buttonListener();

		
		InitExpandableView(view);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {

		} else {
			switch (requestCode) {
			case 1:
				friend_name = data.getStringExtra("name");
				friend_number = data.getStringExtra("number");
				selected_friendNameTextView.setText(friend_name);
				break;

			default:
				break;
			}
		}

	}

	private void buttonListener() {
		// 开始录音
		start_record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRecording) {
					initRecorder();
					startRecorder();
					isRecording = true;
					start_record.setBackgroundResource(R.drawable.stop);
					new Thread(runnable).start();
				} else {
					stopRecorder();
					start_record.setEnabled(false);
					start_record.setBackgroundResource(R.drawable.stop_record);
					clear.setEnabled(true);
					clear.setTextColor(getResources().getColor(R.color.red));
					play.setEnabled(true);
					isRecording = false;
					record_time = 0;
				}

			}
		});
		// 清除之前的录音
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tempFile.exists()) {
					tempFile.delete();
					tempFile = null;
				}
				start_record.setEnabled(true);
				start_record.setBackgroundResource(R.drawable.start_record);
				clear.setEnabled(false);
				clear.setTextColor(getResources().getColor(R.color.gray));
				play.setEnabled(false);
				record_timeTextView.setText("00:00");
			}
		});
		// 播放录音
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					if (!isPlaying) {
						mediaPlayer.setDataSource(tempFile.toString());
						mediaPlayer.setLooping(false);
						mediaPlayer.prepare();
						mediaPlayer.start();
						isPlaying = true;
						play.setBackgroundResource(R.drawable.stop);
						clear.setEnabled(false);
						clear.setTextColor(getResources().getColor(R.color.gray));
					} else {
						mediaPlayer.stop();
						mediaPlayer.reset();
						isPlaying = false;
						play.setBackgroundResource(R.drawable.play);
						clear.setEnabled(true);
						clear.setTextColor(getResources().getColor(R.color.red));
					}

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				try {
					mediaPlayer.stop();
					mediaPlayer.reset();
					isPlaying = false;
					play.setBackgroundResource(R.drawable.play);
					clear.setEnabled(true);
					clear.setTextColor(getResources().getColor(R.color.red));
				} catch (Exception e) {
					e.printStackTrace();
				}

				
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (friend_name != null && friend_number != null
						&& tempFile != null
						&& !words_tosay.getText().equals("")) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							Message message = new Message();
							message.what = UPLOAD_START;
							handler.sendMessage(message);
							upload(tempFile);
							Message message2 = new Message();
							message2.what = UPLOAD_END;
							handler.sendMessage(message2);
						}
					}).start();
					
				} else {
					Toast.makeText(getActivity(), "请完成所有信息填写",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	// Timepicker回调事件
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinutes = minute;
		updateTime();
	}

	//更新界面上显示的时间
	private void updateTime() {
		String mhourString = "" + mHour;
		String mminuteString = "" + mMinutes;
		if (mHour < 10) {
			mhourString = "0" + mhourString;
		}
		if (mMinutes < 10) {
			mminuteString = "0" + mminuteString;
		}
		timeTextView.setText(mhourString + ":" + mminuteString);
		Toast.makeText(getActivity(),
				"time:" + mhourString + ":" + mminuteString, Toast.LENGTH_SHORT)
				.show();
	}

	// 显示选择时间对话框
	private void showTimePicker() {
		new TimePickerDialog(getActivity(), this, mHour, mMinutes,
				DateFormat.is24HourFormat(getActivity())).show();
	}

	// repeatTime数据初始化
	private void InitDatas() {
		groupDatas = new ArrayList<String>();
		childDatas = new ArrayList<List<HashMap<String, String>>>();
		addInfo("重复",
				new String[] { "周一", "周二", "周三", "周四", "周五", "周六", "周日" },
				new String[] { "1", "2", "4", "8", "16", "32", "64" });
	}

	private void addInfo(String group, String[] childs, String[] price) {
		groupDatas.add(group);
		List<HashMap<String, String>> childItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < childs.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", childs[i]);
			map.put("price", price[i]);
			childItem.add(map);
		}
		childDatas.add(childItem);
	}

	private void InitExpandableView(View view) {
		expandableListView = (CustomExpandableListView) view
				.findViewById(R.id.expandablelistview);
		expandableListViewAdapter = new MyExpandableListViewAdapter(
				getActivity(), groupDatas, childDatas);
		expandableListView.setAdapter(expandableListViewAdapter);
		expandableListView.setGroupIndicator(null);

	}

	/**
	 * 准备录音
	 */
	private void initRecorder() {
		recorder = new MediaRecorder();
		/* 设置音频源 */
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		/* 设置输出格式 */
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		/* 设置音频编码器 */
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			/* 创建一个临时文件，用来存放录音 */
			tempFile = File.createTempFile("tempFile", ".amr", SDPathDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* 设置录音文件 */
		recorder.setOutputFile(tempFile.getAbsolutePath());
	}

	/**
	 * 开始录音
	 */
	private void startRecorder() {
		try {
			if (!isSDCardExit) {
				Toast.makeText(getActivity(), "请插入SD卡", Toast.LENGTH_LONG)
						.show();
				return;
			}
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 停止录音
	 */
	private void stopRecorder() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();// 释放资源
			recorder = null;
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            需要上传的文件
	 * @return 上传是否成功
	 */
	private boolean upload(File file) {
		try {
			String end = "\r\n";
			String hyphens = "--";
			String boundary = "*****";
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			/* 允许使用输入流，输出流，不允许使用缓存 */
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			/* 请求方式 */
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			/* 当文件不为空，把文件包装并且上传 */

			if (file != null) {
				DataOutputStream ds = new DataOutputStream(
						conn.getOutputStream());
				/*
				 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
				 * 比如:abc.png
				 */
				ds.writeBytes(hyphens + boundary + end);
				byte[] aaa = new byte[1024];
				String bbbString =  "Content-Disposition: form-data; "
						+ "name=\"file1\";filename=\"" + file.getName() +makeMessageString()+"\""
						+ end;
				aaa = bbbString.getBytes("utf-8");
				ds.write(aaa);
				ds.writeBytes(end);

				InputStream input = new FileInputStream(file);
				int size = 1024;
				byte[] buffer = new byte[size];
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = input.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				input.close();
				ds.writeBytes(end);
				ds.writeBytes(hyphens + boundary + hyphens + end);
				ds.flush();
				/* 获取响应码 */
				if (conn.getResponseCode() == 200) {
					return true;
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String makeMessageString() {
		//DaysOfWeek daysOfWeek = new DaysOfWeek(mDays);
		long alarm_time = calculateAlarm(mHour, mMinutes, mDays);
		String messageString = "_" + ViewPagerActivity.account + "_"
				+ friend_number + "_" + mDays + "_" + mHour + "_" + mMinutes
				+ "_" + words_tosay.getText().toString()+"_"+alarm_time;
		try {
			messageString = new String(messageString.getBytes(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return messageString;
	}
	
    private long calculateAlarm(int hour, int minute,
            int mDays) {

        // start with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarm is behind current time, advance one day
        if (hour < nowHour  ||
            hour == nowHour && minute <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int addDays = getNextAlarm(c,mDays);
        if (addDays > 0) c.add(Calendar.DAY_OF_WEEK, addDays);
        return c.getTimeInMillis();
    }
    
    public int getNextAlarm(Calendar c,int mDays) {
        if (mDays == 0) {
            return -1;
        }

        int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;

        int day = 0;
        int dayCount = 0;
        for (; dayCount < 7; dayCount++) {
            day = (today + dayCount) % 7;
            if (isSet(day,mDays)) {
                break;
            }
        }
        return dayCount;
    }
    
    private boolean isSet(int day,int mDays) {
        return ((mDays & (1 << day)) > 0);
    }

	public static int getmDays() {
		return mDays;
	}

	public static void setmDays(int mDays) {
		SetAlarmFragment.mDays = mDays;
	}

	@Override
	public void onStop() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();// 释放资源
			recorder = null;
		}
		super.onStop();
	}

}