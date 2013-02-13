package com.example.myfirstapp;

import android.R.bool;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.Toast;


public class secondService extends Service {
	 
	  @Override
	  public void onCreate() {
	  //do nothing just create it
	  } 

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	      showNotification1();
	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
	      return null;
	  }
	  
	  @Override
	  public void onDestroy() {
	    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	  }
	  
	  public void showNotification1(){
	      // Prepare intent which is triggered if the
		  // notification is selected
		  Intent serviceintent = new Intent(this, HelloService.class);
		  PendingIntent pIntent = PendingIntent.getActivity(this, 0, serviceintent, 0);
	      // Build notification
		  // Actions are just fake
		  NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
		      .setContentTitle("You cannot kill phanto")
		      .setContentText("Subject")
		      .setSmallIcon(R.drawable.phanto1)
		      .setContentIntent(pIntent);
		  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		  // Hide the notification after its selected; only available at API level 17
		  //noti. = Notification.FLAG_AUTO_CANCEL;
		  notificationManager.notify(0, noti.build());
	  }
	  public void showNotification2(){
	      // Prepare intent which is triggered if the
		  // notification is selected
		  Intent serviceintent = new Intent(this, HelloService.class);
		  PendingIntent pIntent = PendingIntent.getActivity(this, 0, serviceintent, 0);
	      // Build notification
		  // Actions are just fake
		  NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
		      .setContentTitle("You cannot kill phanto")
		      .setContentText("Subject")
		      .setSmallIcon(R.drawable.phanto2)
		      .setContentIntent(pIntent);
		  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		  // Hide the notification after its selected; only available at API level 17
		  //noti.flags |= Notification.FLAG_AUTO_CANCEL
		  notificationManager.notify(0, noti.build());
	  }
	  
	}