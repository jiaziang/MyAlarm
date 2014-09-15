package com.jiaziang8.alarm.object;

public class PushObject {
	private String pushMessage;
	public PushObject(String pushMessage){
		this.pushMessage = pushMessage;
	}
	public String getPushmessage(){
		return pushMessage;
	}
	public void setPushMessage(String pushMessage){
		this.pushMessage = pushMessage;
	}
}
