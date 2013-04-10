package com.example.droidwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentBroadcastReciever extends BroadcastReceiver {

    private boolean userPresent;
    long loginTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
			loginTime = System.currentTimeMillis()/1000;
            userPresent = true;
        }
        
        Intent i = new Intent(context, ScreenStatusServiceListener.class);
        i.putExtra("unlocked", userPresent);
        i.putExtra("screen_state", true);
        i.putExtra("loginTime",loginTime);
        context.startService(i);
    }
}

