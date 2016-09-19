package org.hcilab.projects.logeverything.service;

import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.activity.MainActivity;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public abstract class  ForegroundService extends Service {

	String TAG = getClass().getName();
	
	private PowerManager.WakeLock m_wakeLock;

	@Override
	public void onCreate() {
		super.onCreate();		
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		NotificationCompat.Builder notification = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getText(R.string.notif_ticker))
				.setContentTitle(getText(R.string.notif_title))
				.setContentText(getText(R.string.notif_text))
				.setContentIntent(pendingIntent).setAutoCancel(true)
				.setOngoing(true).setContentInfo("");

		startForeground(42, notification.build());
	
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		m_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

		boolean m_InProgress = false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  Log.d(TAG, newConfig.toString());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		if(!m_wakeLock.isHeld()) {
			m_wakeLock.acquire();
		}
				
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Stopped");
		
		if(m_wakeLock.isHeld()) {
			m_wakeLock.release();			
		}
		
		stopForeground(true);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
