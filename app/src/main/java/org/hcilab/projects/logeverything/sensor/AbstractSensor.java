package org.hcilab.projects.logeverything.sensor;

import java.io.FileWriter;
import java.io.Serializable;

import org.hcilab.projects.logeverything.activity.CONST;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public abstract class AbstractSensor implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	protected String SENSOR_NAME;
	private boolean m_IsEnabled = true;
	protected String FILE_NAME;
	protected String TAG;
	private String m_Settings = "";
	
	protected boolean m_isSensorAvailable = false;
	
	protected AbstractSensor() {
	}

	protected FileWriter m_FileWriter = null;
	
	protected boolean m_IsRunning = false;
	
	public String getSensorName() {
		return SENSOR_NAME;
	}

	protected String getFileName() {
		return FILE_NAME;
	}


	public String getFilePath() {
		return CONST.ROOT_FOLDER.getAbsolutePath() + "/" + getFileName();
	}
	
	public boolean isEnabled() {
		return m_IsEnabled;
	}

	public void setEnabled(boolean selected) {
		this.m_IsEnabled = selected;
	}
	
	public int getSettingsState() {
		return 0;
	}

	public void setSettings(String pSettings) {
		m_Settings = pSettings;
	}
	
	public String getSettings() {
		return m_Settings;
	}
	
	abstract public View getSettingsView(Context context);
	
	abstract public boolean isAvailable(Context context);
	
	public void start(Context context){
		m_isSensorAvailable = isAvailable(context);
		if (!m_isSensorAvailable)
			Log.i(TAG, "Sensor not available");
	}
	
	abstract public void stop();
	
	protected String getTime()
	{
		return CONST.dateFormat.format(System.currentTimeMillis());
	}

	public boolean isRunning() {
		return m_IsRunning;
	}
	
}
