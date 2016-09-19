package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.activity.CONST;
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
		if (!m_isSensorAvailable)
			return;		
		
		if (this.m_FileWriter == null)
		{
			try {
				m_FileWriter = new FileWriter(new File(getFilePath()), true);
				m_FileWriter.write("timestamp,ssid");
				m_FileWriter.write("\n");		
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();		
		String ssid = (wifiInfo.getSSID() == null) ? "NONE" : wifiInfo.getSSID();
		try {
			m_FileWriter.write(getTime() + "," + ssid);			
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
