// This is a Broadcast Receiver that should start the service once the power button has been pressed
package com.example.droidwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ScreenBroadcastReceiver extends BroadcastReceiver {

    private boolean screenOn;
    long screenTime;
    
    @Override
    public void onReceive(Context context, Intent intent) {
		screenTime = System.currentTimeMillis()/1000;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
        	screenOn = true;
        }
        
        Intent i = new Intent(context, ScreenStatusServiceListener.class);
        i.putExtra("screen_state", screenOn);
        i.putExtra("unlocked", false);
        i.putExtra("screenIntentTime", screenTime);
        context.startService(i);
    }
}
