package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.IOException;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class AppSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;

	public AppSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "App";
		FILE_NAME = "app.csv";
		m_FileHeader = "TimeUnix,Package";
	}


	@Override
	public View getSettingsView(Context context) {
		return null;
	}

	@Override
	public boolean isAvailable(Context context) {
		return true;
	}

	private static String getForegroundApp(Context context) {
		try {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			return activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void start(Context context) {
		super.start(context);

        Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;

		if (m_OutputStream == null){
			Log.e(TAG, "FileWriter is null");
		}

		String info = getForegroundApp(context);
		try {
			if (info == null) {
				m_OutputStream.write((t  + ",NULL").getBytes());
			} else {
				m_OutputStream.write((t + "," + info).getBytes());
			}
			m_OutputStream.write(("\n").getBytes());
			m_OutputStream.flush();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		m_IsRunning = true;
	}

	@Override
	public void stop() {
		if (m_IsRunning) {
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
