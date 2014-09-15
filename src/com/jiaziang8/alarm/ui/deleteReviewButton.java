package com.jiaziang8.alarm.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

public class deleteReviewButton extends Button{
	private int id ;
	private Handler handler;
	public deleteReviewButton(Context context){
		super(context);
	}
	
	public deleteReviewButton(Context context,AttributeSet aSet){
		super(context, aSet);
	}
	
	public deleteReviewButton(Context context,AttributeSet aSet,int defStyle){
		super(context, aSet, defStyle);
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public Handler getHandler(){
		return this.handler;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id ;
	}
	
}
