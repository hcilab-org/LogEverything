package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.activity.CONST;
import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


public class AirplaneModeSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;

	public AirplaneModeSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Airplane Mode";
		FILE_NAME = "airplane_mode.csv";
	}

	@Override
	public View getSettingsView(Context context) {
		return null;
	}

	@Override
	public boolean isAvailable(Context context) {
		return true;
	}
	
	private static boolean isAirplaneModeOn(Context context) {
		return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	}

	@Override
	public void start(Context context){
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
			if(isAirplaneModeOn(context)) {
				m_FileWriter.write(getTime() + ",on");
			} else {
				m_FileWriter.write(getTime() + ",off");
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
