package com.example.myfirstapp;

import android.R.bool;
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


public class HelloService extends Service {
	 
	  @Override
	  public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",
	            Process.THREAD_PRIORITY_DEFAULT);
	    thread.start();
	    
	    // Get the HandlerThread's Looper and use it for our Handler 
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	  }

	  private Looper mServiceLooper;
	  private ServiceHandler mServiceHandler;

	  // Handler that receives messages from the thread
	  private final class ServiceHandler extends Handler {
	      public ServiceHandler(Looper looper) {
	          super(looper);
	      }
	      public void handleMessage(Message msg) {
	    	  // We will do the waiting here. normally we will do the job
	          // This is going to send a notification every 2 seconds.
	    	  boolean show1=true;
	    	  while(true){
	    		  if (show1==true){showNotification1();}else {showNotification2();}
	    		  long endTime = System.currentTimeMillis() + 2*1000;
	    		  while (System.currentTimeMillis() < endTime) {
	    			  synchronized (this) {
	    				  try {
	    					  wait(endTime - System.currentTimeMillis());
	    				  } catch (Exception e) {
	    				  }
	    			  }
	    		  }
	    		  if (show1==true){show1=false;}else{show1=true;}
	    	  }
	    	  
	    	  //stopSelf(msg.arg1);
	      }
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	      
	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
	      Message msg = mServiceHandler.obtainMessage();
	      msg.arg1 = startId;
	      mServiceHandler.sendMessage(msg);
	      
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
		  //noti.flags |= Notification.FLAG_AUTO_CANCEL
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