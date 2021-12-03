package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;
import org.hcilab.projects.logeverything.service.AccessibilityLogService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

public class AccessibilitySensor extends AbstractSensor {

	private static final long serialVersionUID = 1L;
	
	private Context m_Context = null;
	private Intent m_Intent;
	private long m_Count;

	private DataUpdateReceiver m_Receiver;
	
	
	public AccessibilitySensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Accessibility";
		FILE_NAME = "accessibility.csv";
		m_FileHeader = "TimeUnix,Type,Class,Package,Text";
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
		
		m_Context = context;
		
		m_Intent = new Intent(m_Context, AccessibilityLogService.class);
		context.startService(m_Intent);
		
		if (m_Receiver == null)
			m_Receiver = new DataUpdateReceiver();
        
		IntentFilter intentFilter = new IntentFilter(AccessibilityLogService.TAG);
		intentFilter.addAction(AccessibilityLogService.TAG);
		m_Context.registerReceiver(m_Receiver, intentFilter);
				
		m_IsRunning = true;
	}

	@Override
	public void stop() {
		m_IsRunning = false;
		if (m_Context == null)
			return;
		m_Context.unregisterReceiver(m_Receiver);
		m_Context.stopService(m_Intent);
	}
	
	private class DataUpdateReceiver extends BroadcastReceiver {
 		
        public DataUpdateReceiver() {
        	super();
        }
 
        @Override
        public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(AccessibilityLogService.TAG)) {
	        	if(m_IsRunning) {
	        		try {
		    			m_Count++;
		    			m_OutputStream.write((intent.getStringExtra(android.content.Intent.EXTRA_TEXT)).getBytes());
						int flushLevel = 50;
						if(m_Count % flushLevel == 0) {
		    				m_OutputStream.flush();
		    				m_Count = 1;
		    			}
	        		} catch (IOException e) {
	        			Log.e(TAG, e.toString());
					}
	        	}
        	}
        }
    }
}
