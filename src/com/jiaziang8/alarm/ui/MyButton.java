package com.jiaziang8.alarm.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button{
	private int index = -1;
	private String name ;
	private String account;
	private String friend;
	private Handler handler;
	public MyButton(Context context){
		super(context);
	}
	
	public MyButton(Context context,AttributeSet aSet){
		super(context, aSet);
	}
	
	public MyButton(Context context,AttributeSet aSet,int defStyle){
		super(context, aSet, defStyle);
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public Handler getHandle(){
		return this.handler;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void setname(String name){
		this.name = name;		
	}
	
	public String getname(){
		return name;
	}
	
	public void setAcount(String account){
		this.account = account;
	}
	public String getAccount(){
		return this.account;
	}
	
	public String getFriend(){
		return this.friend;
	}
	
	public void setFriend(String friend){
		this.friend = friend;
		
	}

}
