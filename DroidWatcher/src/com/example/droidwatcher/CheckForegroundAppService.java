// The purpose of this application is to poll for the change in activity
package com.example.droidwatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CheckForegroundAppService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private boolean breaker;
    private List<String> forgroundApplications;
    private List<String> previousForegroundApps;
    private String path;
    private String dailyLogPath;
	private PrintWriter writer = null;
	private String previousApplication = "nothing";
    
    HandlerThread thread = null;
    
    public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
    	thread = new HandlerThread("ServiceStartArguments",
	            Process.THREAD_PRIORITY_DEFAULT);
	    thread.start();
	    // Get the HandlerThread's Looper and use it for our Handler 
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	    breaker = false;
	    forgroundApplications = null;
	  }
    
    public int onStartCommand(Intent intent,int flags, int startId) {
    	// a null intent was sent.
    	if (intent != null){
    		path = intent.getStringExtra("pathFromScreenStatusListener");
    		dailyLogPath = intent.getStringExtra("DailyLogPath");
    		
    		// if the screen is off we want this thread to wait
    		if (!intent.getBooleanExtra("screen_state",false)){
    			//kill the thread.
    			breaker = true;
        	}
  	  	}
    
    	
    	// For each start request, send a message to start a job and deliver the
    	// start ID so we know which request we're stopping when we finish the job
  	  	
  	  	Message msg = mServiceHandler.obtainMessage();
  	  	msg.arg1 = startId;
  	  	mServiceHandler.sendMessage(msg);
	    
  	  	// If we get killed, after returning from here, restart
  	  	return START_STICKY;
    }
    
    private final class ServiceHandler extends Handler {
	      public ServiceHandler(Looper looper) {
	          super(looper);
	      }
	      public void handleMessage(Message msg) {
	    	  
	    	  while (breaker!=true){
	    		//do poll here
	    		forgroundApplications = getRecentExecutedTasks();
    			Log.d("Foreground Application",forgroundApplications.get(0));
    			
	    		if (!previousApplication.equals(forgroundApplications.get(0))){
	    			previousApplication=forgroundApplications.get(0);
	    			
	    			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    			//write this into the file
	    			
	    			try {
	    				String filePath = path+"//applicationsOpened.txt";
	    				writer = new PrintWriter(new FileWriter(filePath,true));
	    				writer.println("Application oppened: " + forgroundApplications.get(0) +" at: "+timeStamp);
	    				writer.close();
	    			} catch (FileNotFoundException e) {
	    				//File Not Found
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				//File not able to open
	    				e.printStackTrace();
	    			}
	    			logIntoDailyLog(dailyLogPath,forgroundApplications.get(0) + " @ "+timeStamp + " ");
	    		}
	    		synchronized (this){
	    		try {wait(2500);} 
	    		catch (InterruptedException e) {e.printStackTrace();}
	    		}
	    		
	    	  }
	    	  stopSelf(msg.arg1);
	      }
	  }
    
    public void onDestory(){
    	Toast.makeText(this, "CheckForeGroundStopped", Toast.LENGTH_SHORT).show();
    }
    
    private List<String> getRecentExecutedTasks(){
    	List <String> recentTasks = new ArrayList<String>();
    	int numberOfTasks = 1;
    	ActivityManager m = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
    	//Get some number of running tasks and grab the first one.  getRunningTasks returns newest to oldest
    	RunningTaskInfo task = m.getRunningTasks(numberOfTasks).get(0);
    	
    	final PackageManager pm = getApplicationContext().getPackageManager();
    	ApplicationInfo ai;
    	try {
    	    ai = pm.getApplicationInfo( task.baseActivity.getPackageName(), 0);
    	} catch (final NameNotFoundException e) {
    	    ai = null;
    	}
    	final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    
    	//Build output
    	String taskInfo  = "Task ID '"+task.id+"'PID: '"+task.baseActivity.getPackageName()+"' AppName:'"+applicationName+"'";
    	recentTasks.add(taskInfo);
    	
    	return recentTasks;
    }
    
	  public void showNotification(String Message){
	      // Prepare intent which is triggered if the
		  // notification is selected
		  Intent serviceintent = new Intent(this, MainActivity.class);
		  PendingIntent pIntent = PendingIntent.getActivity(this, 0, serviceintent, 0);
	      // Build notification
		  // Actions are just fake
		  NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
		      .setContentTitle("AFW")
		      .setContentText("FrontApp"+Message)
		      .setSmallIcon(R.drawable.ic_launcher)
		      .setOngoing(true)
		      .setContentIntent(pIntent);
		  	  
		  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		  notificationManager.notify(0, noti.build()); // send the notification.
	  }
	  
	  private void takeScreenShot(String Path){
	  // image naming and path  to include sd card  appending name you choose for file
		  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		  String mPath = path + "//" + timeStamp;

	  // create bitmap screen capture
	  Bitmap bitmap;
	  View mCurrentUrlMask = null;
	  View v1 = mCurrentUrlMask.getRootView();
	  v1.setDrawingCacheEnabled(true);
	  bitmap = Bitmap.createBitmap(v1.getDrawingCache());
	  v1.setDrawingCacheEnabled(false);

	  OutputStream fout = null;
	  File imageFile = new File(mPath);

	  try {
	      fout = new FileOutputStream(imageFile);
	      bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
	      fout.flush();
	      fout.close();

	  } catch (FileNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	  } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	  }
	  }
    
	  private void logIntoDailyLog(String file, String data){
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(file,true));
				writer.println(data);
				writer.close();
			} catch (FileNotFoundException e) {
				//File Not Found
				e.printStackTrace();
			} catch (IOException e) {
				//File not able to open
				e.printStackTrace();
			}
	  }
	  
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
