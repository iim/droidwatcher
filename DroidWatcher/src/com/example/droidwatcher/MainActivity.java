package com.example.droidwatcher;

import java.io.File;

import com.example.droidwatcher.R;
import com.example.droidwatcher.MainService;


import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File DroidWatcherDirectory = new File(Environment.getExternalStorageDirectory() + "/DroidWatcher");
        File DroidWatcherLogFolder = new File(Environment.getExternalStorageDirectory() + "/DroidWatcher"+ "/Daily Logs");  
        if(!DroidWatcherDirectory.exists())
         {if(DroidWatcherDirectory.mkdir()) {
                //directory is created;
               }
         }
        if(!DroidWatcherLogFolder.exists())
        {if(DroidWatcherLogFolder.mkdir()) {
               //directory is created;
              }
        }
        
        
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    
    //this is my starting for the broadcast Listener service method
    public void StartBroadcastListener(View view){
   
	    Toast.makeText(this, "Button works", Toast.LENGTH_SHORT).show();
    	Intent startBroadcastIntent = new Intent(this, MainService.class);
    	startService(startBroadcastIntent);
    	
    }
    
    public void StopBroadcastListener(View view){
    	//stop the Main Service
    	Intent stopBroadcastIntent = new Intent(this, MainService.class);
    	stopService(stopBroadcastIntent);
    	
    	// Stop the CheckForegroudApplication Service
		Intent startForegroundAppIntent = new Intent(this, CheckForegroundAppService.class);
	    startForegroundAppIntent.putExtra("pathFromScreenStatusListener", "turnedOffFromMainActivity");
	    startForegroundAppIntent.putExtra("screen_state", false);
	    startService(startForegroundAppIntent);
    	
    }
	
}
