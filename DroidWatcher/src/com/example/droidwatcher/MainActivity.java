package com.example.droidwatcher;

import java.io.File;

import com.example.droidwatcher.R;
import com.example.droidwatcher.ScreenStatusServiceListener;


import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File direct = new File(Environment.getExternalStorageDirectory() + "/DroidWatcher");

        if(!direct.exists())
         {
             if(direct.mkdir()) 
               {
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
    	Intent startBroadcastIntent = new Intent(this, ScreenStatusServiceListener.class);
    	startService(startBroadcastIntent);
    }
    
    public void StopBroadcastListener(View view){
    	Intent stopBroadcastIntent = new Intent(this, ScreenStatusServiceListener.class);
    	stopService(stopBroadcastIntent);
    }
    
	
}
