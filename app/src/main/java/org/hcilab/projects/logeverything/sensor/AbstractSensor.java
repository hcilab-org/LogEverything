package org.hcilab.projects.logeverything.sensor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import org.hcilab.projects.logeverything.activity.CONST;

import android.content.Context;
import android.util.Log;
import android.view.View;

public abstract class AbstractSensor implements Serializable  {

	protected String TAG;
	private static final long serialVersionUID = 1L;
	
	protected String SENSOR_NAME;
	private boolean m_IsEnabled = true;
	protected String FILE_NAME;
	protected String m_FileHeader;

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

        if (this.m_FileWriter == null)
        {
            try {
                m_FileWriter = new FileWriter(new File(getFilePath()), true);
                m_FileWriter.write(m_FileHeader + "\n");
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
	}
	
	abstract public void stop();


	public boolean isRunning() {
		return m_IsRunning;
	}
	
}
