package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private MyPhoneReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //This is code for a broadcast receiver 
  /*  
    @Override
    protected void onPause() {
       unregisterReceiver(mReceiver);
       super.onPause();
    }

    @Override
    protected void onResume() {
       this.mReceiver = new MyPhoneReceiver();
       registerReceiver(
             this.mReceiver, 
             new IntentFilter(TelephonyManager.EXTRA_STATE));
       super.onResume();
    }
    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

    //this is my starting service method
    public void startPhanto(View view){
    	Intent serviceIntent = new Intent(this, secondService.class);
    	startService(serviceIntent);
    }
    
    //this is my stopping service method
    public void stopPhanto(View view){
    	Intent stopIntent = new Intent(this, secondService.class);
    	stopService(stopIntent);
    }
}
