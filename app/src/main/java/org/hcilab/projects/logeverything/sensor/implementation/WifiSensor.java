package org.hcilab.projects.logeverything.sensor.implementation;


import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

public class WifiSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;
	
	public WifiSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Wi-Fi SSID";
		FILE_NAME = "wifi_ssid.csv";
		m_FileHeader = "TimeUnix,Ssid";
	}

	@Override
	public View getSettingsView(Context context) {
		return null;
	}

	@Override
	public boolean isAvailable(Context context) {
		return true;
	}

	@Override
	public void start(Context context) {
		super.start(context);
        Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;		

		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();		
		String ssid = (wifiInfo.getSSID() == null) ? "NONE" : wifiInfo.getSSID();
		try {
			m_OutputStream.write((t + "," + ssid +"\n").getBytes());
			m_OutputStream.flush();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		m_IsRunning = true;
	}

	@Override
	public void stop() {
		if(m_IsRunning) {
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
