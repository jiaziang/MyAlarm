package com.jiaziang8.alarm.ui;

import com.jeremyfeinstein.slidingmenu.example.ReviewActivity;
import com.jiaziang8.alarm.service.MyService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class DeleteReviewListener implements OnClickListener{
	public static DeleteReviewListener instance = null;
	Context context  = null;
	public DeleteReviewListener(){
		
	}
	public static DeleteReviewListener getInstance() {
		if (instance == null) {
			instance = new DeleteReviewListener();
		}
		return instance;
	}
	
	@Override 
	public void onClick(View view){
		final View deleteReView = (deleteReviewButton)view ;
		context = ((deleteReviewButton)deleteReView).getContext();
		final int id = ((deleteReviewButton)deleteReView).getId();
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确认删除本记录？");
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isDeleted = MyService.deleteReview(id);
						if(isDeleted){
							Message message = new Message();
							message.what = ReviewActivity.DELETE_OK;
							((deleteReviewButton)deleteReView).getHandler().sendMessage(message);
						}
					}
				}).start();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			
			}
		});
		builder.create().show();
		
	}
	

}
