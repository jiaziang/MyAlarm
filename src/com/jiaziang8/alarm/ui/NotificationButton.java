package com.jiaziang8.alarm.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

public class NotificationButton extends Button {
	private int id ;
	private String account;
	private String name;
	private String selfAccount;
	private int mDays;
	private int mHour;
	private int mMinute;
	private String filename;
	private String time;
	private String wordsToSay;
	private Handler handler;

	public NotificationButton(Context context) {
		super(context);
	}

	public NotificationButton(Context context, AttributeSet aSet) {
		super(context, aSet);
	}

	public NotificationButton(Context context, AttributeSet aSet, int defStyle) {
		super(context, aSet, defStyle);
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setAccount(String account){
		this.account = account;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setselfAccount(String selfAccount){
		this.selfAccount =selfAccount;
	}
	
	public String getselfAccount(){
		return this.selfAccount;
	}
	
	public void setmDays(int mDays){
		this.mDays = mDays;
	}
	
	public int getmDays(){
		return this.mDays;
	}
	
	public void setmHour(int mHour){
		this.mHour = mHour;
	}

	public int getmHour(){
		return this.mHour;
	}
	
	public void setmMinute(int mMinute){
		this.mMinute = mMinute;
	}
	
	public int getMinute(){
		return this.mMinute;
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public String getFilename(){
		return this.filename;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setWordsToSay(String wordstosay){
		this.wordsToSay = wordstosay;		
	}
	
	public String getWordsToSay(){
		return this.wordsToSay;
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;		
	}
	
	public Handler getHandler(){
		return this.handler;
	}
	
	
}
