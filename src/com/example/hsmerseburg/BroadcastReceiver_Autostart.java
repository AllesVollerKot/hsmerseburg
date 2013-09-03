package com.example.hsmerseburg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiver_Autostart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {	
		Intent i = new Intent(context, Service_StarterService.class);
		context.startService(i);		
	}	
}
