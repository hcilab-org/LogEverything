package org.hcilab.projects.logeverything.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver{
	
  	@Override
	public void onReceive(Context context, Intent intent) {
  		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
  			if(MainActivity.isLogServiceRunning(context)) {
  				MainActivity.startLogService(context);
  			} 
  		}
	}
}