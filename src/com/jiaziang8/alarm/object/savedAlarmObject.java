package com.jiaziang8.alarm.object;

import android.R.integer;

public class savedAlarmObject {
	public int id;
	public String account;
	public String friend;
	public int mDays;
	public int mHour;
	public int mMinute;
	public String filename;
	public String wordsToSay;
	public String time;
	public String path;

	public savedAlarmObject(String accountString, String friendString,
			int mDays, int mHour, int mMinute, String filename,
			String wordsToSay, String time,int id) {
		this.id = id;
		this.account = accountString;
		this.friend  = friendString;
		this.mDays = mDays;
		this.mMinute = mMinute;
		this.mHour = mHour;
		this.filename = filename;
		this.wordsToSay = wordsToSay;
		this.time = time;
	}
	
	public savedAlarmObject(String accountString, String friendString,
			int mDays, int mHour, int mMinute, String filename,
			String wordsToSay, String time,int id,String path) {
		this.id = id;
		this.account = accountString;
		this.friend  = friendString;
		this.mDays = mDays;
		this.mMinute = mMinute;
		this.mHour = mHour;
		this.filename = filename;
		this.wordsToSay = wordsToSay;
		this.time = time;
		this.path = path;
	}
	
}
