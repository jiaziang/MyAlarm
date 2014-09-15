package com.jeremyfeinstein.slidingmenu.example;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class ProblemsActivity extends Activity{
	private RelativeLayout backRelativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.problems);		
		
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
		
	}

}
