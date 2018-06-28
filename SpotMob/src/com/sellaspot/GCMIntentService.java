package com.sellaspot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String SENDER_ID = "sellaspot";

  public GCMIntentService() {
        super(SENDER_ID);
    }
	  
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {

		String message = arg1.getStringExtra("message");
 
	        NotificationManager notificationManager = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification note = new Notification(R.drawable.logo, "SellASpot Notification", System.currentTimeMillis());
	        Intent notificationIntent = new Intent(arg0, MainActivity.class);
	        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, notificationIntent, 0);
	        note.setLatestEventInfo(arg0, "SellASpot Notification", message, pendingIntent);
	        //note.number = count++;
	        note.defaults |= Notification.DEFAULT_SOUND;
	        note.defaults |= Notification.DEFAULT_VIBRATE;
	        note.defaults |= Notification.DEFAULT_LIGHTS;
	        note.flags |= Notification.FLAG_AUTO_CANCEL;
	        notificationManager.notify(0, note);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
		System.out.println("Registered");
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
