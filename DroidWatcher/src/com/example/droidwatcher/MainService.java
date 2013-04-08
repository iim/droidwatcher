package com.example.droidwatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {
	BroadcastReceiver mReceiver;
	BroadcastReceiver mReceiver2;
	private String path; // this the last time the user has turned the screen on.
	private long screenOnTime;
	private long loginTime;


	
    @Override
    public void onCreate() {
        super.onCreate();
        //register receiver that handles screen on and screen off logic
	    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF);
	    mReceiver = new ScreenBroadcastReceiver();
	    registerReceiver(mReceiver, filter);
	    
	    mReceiver2= new UserPresentBroadcastReciever();
	    IntentFilter userPresentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
	    registerReceiver(mReceiver2,userPresentFilter);
	    
	    Toast.makeText(this, "Broadcast Begun", Toast.LENGTH_SHORT).show();
    }
    
    
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
	    
    	//get the daily log file
    	String DailyPath = createDailyLog();
    	
    	//as long as we are on show a notification that we are still running
    	showNotification1();
    	
    	//According to the documentation returning START_STICKY may cause a null intent hence we should 
    	//check if a null intent is occuring so we don't get null pointer exception
    	if(intent != null){
    		boolean screenOn = intent.getBooleanExtra("screen_state", false);
    		boolean screenUnlocked = intent.getBooleanExtra("unlocked", false);
    		
    		if (screenOn && !screenUnlocked) {
    	   		screenOnTime = intent.getLongExtra("screenIntentTime", 0);
    	   		//check that the number of files is not over 50
    	   		deleteOver50();

    			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    			//create a new folder for to store the information.
    			File direct = new File(Environment.getExternalStorageDirectory()+ "//DroidWatcher//" + timeStamp);
    			path = direct.getPath();
    			if(!direct.exists()){
    			    if(direct.mkdir()) {
    			   		//directory is created;
    		     	}
    			}
    			
    			//log into the daily log
    			logIntoDailyLog(DailyPath, "Login Time: " +timeStamp);
    			
    			
    		}
    		//this records how long it took the user to log on to the phone
    		if (screenOn && screenUnlocked) {
    			//start the foregroundapplication listener
    			Intent startForegroundAppIntent = new Intent(this, CheckForegroundAppService.class);
    		    startForegroundAppIntent.putExtra("pathFromScreenStatusListener", path);
    		    startForegroundAppIntent.putExtra("screen_state", screenOn);
    		    startForegroundAppIntent.putExtra("DailyLogPath",DailyPath );
    		    startService(startForegroundAppIntent);
    			
    			//log the logintime
    			loginTime = intent.getLongExtra("loginTime", 0);
    			Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
    			PrintWriter writer = null;
				String timeItTookToLogin = String.valueOf(loginTime-screenOnTime);
    			try {
					String filePath = path+"//LoginTime.txt";
					writer = new PrintWriter(filePath);
	    			writer.println("Time: "+timeItTookToLogin);
	    			writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logIntoDailyLog(DailyPath,"Login Duration: "+timeItTookToLogin+" Applications Opened");
    		}
    		//this records how long the screen was turned on for
    		if (!screenOn){
    			//turn off the CheckForegroundApplicationChecker
    			Intent startForegroundAppIntent = new Intent(this, CheckForegroundAppService.class);
    		    startForegroundAppIntent.putExtra("pathFromScreenStatusListener", path);
    		    startForegroundAppIntent.putExtra("screen_state", false);
    		    startService(startForegroundAppIntent);
    		    
    			long logOffTime = intent.getLongExtra("screenIntentTime", -1) - screenOnTime;
    			String timeScreenWasOn = String.valueOf(logOffTime);
    			PrintWriter writer = null;
				try {
					String filePath = path+"//ScreenOnDuration.txt";
					writer = new PrintWriter(filePath);
	    			writer.println("Time: " + timeScreenWasOn);
	    			writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			logIntoDailyLog(DailyPath, "Screen On Duration " +  timeScreenWasOn + "###");
				
    		}
    	}
        return START_STICKY;
    }
	  
    @Override
	public void onDestroy() {
    	//unregister the recievers 
    	unregisterReceiver(mReceiver); 
    	unregisterReceiver(mReceiver2);
    	stopNotification();
    	Toast.makeText(this, "on Destroy has been called", Toast.LENGTH_SHORT).show();
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
	 
	  // this function will delete data that reach the folder limit
	  private void deleteOver50(){
		  File folder = new File( Environment.getExternalStorageDirectory() + "/DroidWatcher");
		  File [] listOfFiles = folder.listFiles();
		  Arrays.sort(listOfFiles, new Comparator<File>(){
			    public int compare(File f1, File f2)
			    {
			        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			    } });
		  
		  //for debugging
		  //for(int i=0; i<10; i++){
		  //Log.d("FolderContents[10]",listOfFiles[i].getName());
		  //}
		  if(listOfFiles.length>50)deleteFolder(listOfFiles[0]);
	  }
	  
	  //this is a helper function used to delete a folder and its contents 
	  public static void deleteFolder(File folder) {
		    File[] files = folder.listFiles();
		    if(files!=null) {
		        for(File f: files) {
		            if(f.isDirectory()) {
		                deleteFolder(f);
		            } else {
		                f.delete();
		            }
		        }
		    }
		    folder.delete();
		}
	  
	  public String createDailyLog(){
		  String DailyLogpath = null;
		  boolean dailyLogFound = false;
		  String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime())+ ".txt";
	      File DroidWatcherLogFolder = new File(Environment.getExternalStorageDirectory() + "/DroidWatcher"+ "/Daily Logs");
	      String logFolderPath=DroidWatcherLogFolder.getPath();
	      File [] listOfFiles = DroidWatcherLogFolder.listFiles();
	      
		  for(int i=0; i<listOfFiles.length;i++){
			 // Log.d("Daily Log",listOfFiles[i].getName()+" XXX " + timeStamp);
			  if (timeStamp.equals(listOfFiles[i].getName())){
				  dailyLogFound=true;
			//	  Log.d("Daily Log", "DAILY LOG ALREADY MADE");
			  }
		  }
		  
		  if (dailyLogFound == false){
			  //create the log file
			  PrintWriter writer = null;
				try {
					String filePath = logFolderPath+"/"+timeStamp;
					writer = new PrintWriter(filePath);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	      
		  DailyLogpath = logFolderPath+"/"+timeStamp;
		  Log.d("Daily Log",DailyLogpath);
		  return DailyLogpath;
	  }
	  
	  // Function for logging into daily Log
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
	  
	
}
