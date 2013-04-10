package com.example.droidwatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ScreenStatusServiceListener extends Service {
	BroadcastReceiver mReceiver;
	private String path; // this the last time the user has turned the screen on.
	private long screenOnTime;
	private long loginTime;
	
    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
	     IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
	     filter.addAction(Intent.ACTION_SCREEN_OFF);
	     mReceiver = new ScreenBroadcastReceiver();
	     registerReceiver(mReceiver, filter);
	     Toast.makeText(this, "Broadcast Begun", Toast.LENGTH_SHORT).show();
    }
    
    
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
	    
    	//as long as we are on show a notification that we are still running
    	showNotification1();
    	
    	//According to the documentation returning START_STICKY may cause a null intent hence we should 
    	//check if a null intent is occuring so we don't get null pointer exception
    	if(intent != null){
    		boolean screenOn = intent.getBooleanExtra("screen_state", true);
    		boolean screenUnlocked = intent.getBooleanExtra("unlocked", true);
    		
    		if (screenOn && !screenUnlocked) {
    	   		screenOnTime = intent.getLongExtra("screenIntentTime", 0);
    			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    			// Get instance of Vibrator from current Context and vibrate for 300 miliseconds
    			Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
    			mVibrator.vibrate(300);
    			//create a new folder for to store the information.
    			File direct = new File(Environment.getExternalStorageDirectory()+ "//DroidWatcher//" + timeStamp);
    			path = direct.getPath();
    			if(!direct.exists()){
    			    if(direct.mkdir()) {
    			   		//directory is created;
    		     	}
    			}
    		}
    		//this records how long it took the user to log on to the phone
    		if (screenOn && screenUnlocked) {
    			loginTime = intent.getLongExtra("loginTime", 0);
    			Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
    			PrintWriter writer = null;
				try {
					String filePath = path+"//LoginTime.txt";
					String timeItTookToLogin = String.valueOf(loginTime-screenOnTime);
					writer = new PrintWriter(filePath);
	    			writer.println("Time: "+timeItTookToLogin);
	    			writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		//this records how long the screen was turned on for
    		if (!screenOn){
    			long logOffTime = intent.getLongExtra("screenIntentTime", -1) - screenOnTime;
    			PrintWriter writer = null;
				try {
					String filePath = path+"//ScreenOnDuration.txt";
					String timeScreenWasOn = String.valueOf(logOffTime);
					writer = new PrintWriter(filePath);
	    			writer.println("Time: " + timeScreenWasOn);
	    			writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
        return START_STICKY;
    }
	  
    @Override
	public void onDestroy() {
    	unregisterReceiver(mReceiver);
    	Toast.makeText(this, "onDestroy has been called", Toast.LENGTH_SHORT).show();
    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	  public void showNotification1(){
	      // Prepare intent which is triggered if the
		  // notification is selected
		  Intent serviceintent = new Intent(this, MainActivity.class);
		  PendingIntent pIntent = PendingIntent.getActivity(this, 0, serviceintent, 0);
	      // Build notification
		  // Actions are just fake
		  NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
		      .setContentTitle("DroidWatcher Active")
		      .setContentText("DroidWatcher is currently Running")
		      .setSmallIcon(R.drawable.ic_launcher)
		      .setOngoing(true)
		      .setContentIntent(pIntent);
		  	  
		  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		  notificationManager.notify(0, noti.build()); // send the notification.
	  }
	  
	  //cancels the notification.
	  public void stopNotification(){
		  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		  notificationManager.cancelAll();
	  }
	 
	
}
