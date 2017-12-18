package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.IOException;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;

public class ScreenOrientationSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;
	
	private BroadcastReceiver m_Receiver;
	private Context m_context;
	
	public boolean m_WasScreenOn = true;
	
	public ScreenOrientationSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Screen Orientation";
		FILE_NAME = "screen_orientation.csv";
		m_FileHeader = "TimeUnix,Value";
	}
	
	public View getSettingsView(Context context) {
		return null;
	}
	
	public boolean isAvailable(Context context) {
		return true;
	}
	
	@Override
	public void start(Context pContext) {
		super.start(pContext);
		Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;
		
		m_context = pContext;
		if (this.m_FileWriter == null)
		{
			try {
				if(m_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
		            m_FileWriter.write(t + ",LANDSCAPE");
		        }
		        else if(m_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
		            m_FileWriter.write(t + ",PORTRAIT");
		        }
		        else if(m_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_SQUARE){
		            m_FileWriter.write(t + ",SQUARE");
		        }
		        else {
		            m_FileWriter.write(t + ",UNDEFINED");
		        }
				m_FileWriter.write("\n");
				m_FileWriter.flush();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		m_Receiver = new ScreenReceiver();
		try{
			m_context.unregisterReceiver(m_Receiver);
		} catch (Exception e) {
			//Not Registered
		}
		m_context.registerReceiver(m_Receiver, filter);
		
		m_IsRunning = true;
	}
	
	@Override
	public void stop() {
		if(m_IsRunning) {
			m_IsRunning = false;
			m_context.unregisterReceiver(m_Receiver);	
			try {
				m_FileWriter.flush();
				m_FileWriter.close();
				m_FileWriter = null;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}		
		}	
	}
	
	public class ScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Long t = System.currentTimeMillis();
			if(m_IsRunning) {
				if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED) ) {
	                try {
		                if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
		                    m_FileWriter.write(t + ",LANDSCAPE");
		                }
		                else if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
		                    m_FileWriter.write(t + ",PORTRAIT");
		                }
						else if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_SQUARE){
							m_FileWriter.write(t + ",SQUARE");
						}
		                else {
		                    m_FileWriter.write(t + ",UNDEFINED");
		                }
						m_FileWriter.write("\n");
						m_FileWriter.flush();
	                } catch (IOException e) {
						Log.e(TAG, e.toString());
					}
	            }				
			}			
		}		
	}	
}