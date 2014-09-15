package com.jeremyfeinstein.slidingmenu.example;

import java.util.Calendar;

import com.cn.daming.deskclock.Alarm;
import com.cn.daming.deskclock.Alarms;
import com.cn.daming.deskclock.DigitalClock;
import com.cn.daming.deskclock.SetAlarm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyAlarmFragment extends Fragment{
	static final String PREFERENCES = "AlarmClock";
	/**
	 * This must be false for production. If true, turns on logging, test code,
	 * etc.
	 */
	static final boolean DEBUG = false;

	private SharedPreferences mPrefs;
	private LayoutInflater mFactory;
	private ListView mAlarmsList;
	private Cursor mCursor;
	
	String account = "";
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mFactory = LayoutInflater.from(getActivity());
        //取getSharedPreferences中key==“AlarmClock”的值
        mPrefs = getActivity().getSharedPreferences(PREFERENCES, 0);
        //获取闹钟的cursor
        mCursor = Alarms.getAlarmsCursor(getActivity().getContentResolver());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.alarm_clock, null);
		
		account = ViewPagerActivity.account;

		mAlarmsList = (ListView) view.findViewById(R.id.alarms_list);

		AlarmTimeAdapter adapter = new AlarmTimeAdapter(getActivity(), mCursor);
				
		mAlarmsList.setAdapter(adapter);
		mAlarmsList.setVerticalScrollBarEnabled(true);
		return view;
	}

	/**
	 * listview的适配器继承CursorAdapter
	 * 
	 * @author wangxianming 也可以使用BaseAdapter
	 */
	private class AlarmTimeAdapter extends CursorAdapter {
		public AlarmTimeAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View ret = mFactory.inflate(R.layout.alarm_time, parent, false);

			DigitalClock digitalClock = (DigitalClock) ret
					.findViewById(R.id.digitalClock);
			digitalClock.setLive(false);
			return ret;
		}

		// 把view绑定cursor的每一项
		public void bindView(View view, Context context, Cursor cursor) {
			final Alarm alarm = new Alarm(cursor);
			final int id = alarm.id;

			View indicator = view.findViewById(R.id.indicator);

			// 闹钟开关～～～～～～～～～～～～			
			final CheckBox clockOnOff = (CheckBox) indicator.findViewById(R.id.clock_onoff);
			Button deleteButton= (Button)view.findViewById(R.id.delete_alarm);
			clockOnOff.setChecked(alarm.enabled);
			// 对checkbox设置监听，使里外一致
/*			clockOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
						updateIndicatorAndAlarm(clockOnOff.isChecked(), 
								alarm);								
				}
			});*/
			indicator.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					clockOnOff.toggle();
					updateIndicatorAndAlarm(clockOnOff.isChecked(), 
							alarm);	
					
				}
			});
			deleteButton.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.delete_alarm))
                    .setMessage(getString(R.string.delete_alarm_confirm))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d,
                                        int w) {
                                    Alarms.deleteAlarm(getActivity(), id);
                                }			
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
				}
			});

			DigitalClock digitalClock = (DigitalClock) view
					.findViewById(R.id.digitalClock);

			// set the alarm text
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, alarm.hour);
			c.set(Calendar.MINUTE, alarm.minutes);
			digitalClock.updateTime(c);
			digitalClock.setTypeface(Typeface.DEFAULT);

			// Set the repeat text or leave it blank if it does not repeat.
			TextView daysOfWeekView = (TextView) digitalClock
					.findViewById(R.id.daysOfWeek);
			final String daysOfWeekStr = alarm.daysOfWeek.toString(
					getActivity(), false);
			if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
				daysOfWeekView.setText(daysOfWeekStr);
				daysOfWeekView.setVisibility(View.VISIBLE);
			} else {
				daysOfWeekView.setVisibility(View.GONE);
			}

		}
	};

	// 更新checkbox
	private void updateIndicatorAndAlarm(boolean enabled, 
			Alarm alarm) {
/*		bar.setImageResource(enabled ? R.drawable.ic_indicator_on
				: R.drawable.ic_indicator_off);*/
		Alarms.enableAlarm(getActivity(), alarm.id, enabled);
		if (enabled) {
			SetAlarm.popAlarmSetToast(getActivity(), alarm.hour, alarm.minutes,
					alarm.daysOfWeek);
		}
	}
	
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     * 创建菜单的点击事件响应
     */
    //每一个闹钟的事件相应
/*	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
		Intent intent = new Intent(getActivity(), SetAlarm.class);
        intent.putExtra(Alarms.ALARM_ID, (int) id);
        startActivity(intent);	
	}*/
	
}
