package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.IOException;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;

public class ChargingSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;
	
	public ChargingSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Charging";
		FILE_NAME = "charging.csv";
		m_FileHeader = "TimeUnix,Value";
	}

	@Override
	public View getSettingsView(Context context) {
		return null;
	}

	@Override
	public boolean isAvailable(Context context) {
		return true;
	}
	
	private static boolean isConnected(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		
		Intent intent = context.registerReceiver(null, filter);
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
	}

	@Override
	public void start(Context context) {
		super.start(context);
        Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;

		try {		
			if(isConnected(context)) {
				m_OutputStream.write((t + ",true\n").getBytes());
			} else {
				m_OutputStream.write((t + ",false\n").getBytes());
			}
			m_OutputStream.flush();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		m_IsRunning = true;
	}

	@Override
	public void stop() {
		if(m_IsRunning) {
			//m_context.unregisterReceiver(mReceiver);	
			m_IsRunning = false;
			try {
				m_OutputStream.flush();
				m_OutputStream.close();
				m_OutputStream = null;
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}		
		}	
	}

}
