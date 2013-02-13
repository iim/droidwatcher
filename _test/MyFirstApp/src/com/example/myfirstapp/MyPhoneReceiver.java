// This is a Broadcast Receiver that should start the service once the powere button has been pressed
package com.example.myfirstapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyPhoneReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
	  	Toast.makeText(context, "power button clicked",Toast.LENGTH_LONG).show();
    	context.startService(new Intent(context,HelloService.class));
  }
} 