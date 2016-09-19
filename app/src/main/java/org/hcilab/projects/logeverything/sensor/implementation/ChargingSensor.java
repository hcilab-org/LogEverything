package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.activity.CONST;
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
		if (!m_isSensorAvailable)
			return;
		
		if (this.m_FileWriter == null)
		{
			try {
				m_FileWriter = new FileWriter(new File(getFilePath()), true);
				m_FileWriter.write("timestamp,value");
				m_FileWriter.write("\n");
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		try {		
			if(isConnected(context)) {
				m_FileWriter.write(getTime() + ",true");
			} else {
				m_FileWriter.write(getTime() + ",false");
			}			
			m_FileWriter.write("\n");
			m_FileWriter.flush();			
		} catch (IOException e) {
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
				m_FileWriter.flush();
				m_FileWriter.close();
				m_FileWriter = null;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}		
		}	
	}

}
