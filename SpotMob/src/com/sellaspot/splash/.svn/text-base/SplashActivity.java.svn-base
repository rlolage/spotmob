package com.sellaspot.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sellaspot.MainActivity;
import com.sellaspot.R;

public class SplashActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SplashHandler mHandler = new SplashHandler();
		Message msg = new Message();
		msg.what = 0;
		
		mHandler.sendMessageDelayed(msg, 3000);
	}
	
	
	private class SplashHandler extends Handler {

    	public void handleMessage(Message msg)	  { 
    		
    		
    		// switch to identify the message by its code
    		switch (msg.what)
    	    {
    	    default:
    	    case 0:
    	      super.handleMessage(msg);
    	      
    	      //Create an intent to start the new activity.
    	      // Our intention is to start MainActivity
    	      Intent intent = new Intent();
    	      intent.setClass(SplashActivity.this,MainActivity.class);
    	      
    	      startActivity(intent);
    	      
    	      // finish the current activity
    	      SplashActivity.this.finish();
    	    }
    		
    	}
	}

}
