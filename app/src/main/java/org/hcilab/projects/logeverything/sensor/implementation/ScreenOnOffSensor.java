package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.activity.CONST;
import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

public class ScreenOnOffSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;
	
	private BroadcastReceiver mReceiver;
	private Context m_context;
	
	private boolean wasScreenOn = true;
	
	public ScreenOnOffSensor() {
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Screen On/Off";
		FILE_NAME = "screen_on_off.csv";
	}
	
	public View getSettingsView(Context context) {
		return null;
	}
	
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
				m_FileWriter.write("timestamp,value");
				m_FileWriter.write("\n");
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		this.m_context = context;
		try {			
			if(isScreenOn) {
				m_FileWriter.write(getTime() + ",on");
			} else {
				m_FileWriter.write(getTime() + ",off");
			}			
			m_FileWriter.write("\n");
			m_FileWriter.flush();			
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new ScreenReceiver();
		
		try{
			context.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			//Not Registered
		}
		context.registerReceiver(mReceiver, filter);
		
		m_IsRunning = true;
	}
	
	@Override
	public void stop() {
		if(m_IsRunning) {
			m_IsRunning = false;
			m_context.unregisterReceiver(mReceiver);	
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
			
			if(m_IsRunning) {
				try {
					if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
						m_FileWriter.write(getTime() + ",off");
						wasScreenOn = false;
					} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
						m_FileWriter.write(getTime() + ",on");
						wasScreenOn = true;
					}
					//Log.d(TAG, intent.getAction().toString());
					m_FileWriter.write("\n");
					m_FileWriter.flush();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
			
		}		
	}
	
    public void onPause() {
    	if(m_IsRunning) {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
	        if (wasScreenOn) {
	            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
	        	try {
					m_FileWriter.write(getTime() + ",off");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString());
				}
	        }
    	}
    }
 
    public void onResume() {
    	if(m_IsRunning) {
	        // ONLY WHEN SCREEN TURNS ON
	        if (!wasScreenOn) {
	            // THIS IS WHEN ONRESUME() IS CALLED DUE TO A SCREEN STATE CHANGE
	        	try {
					m_FileWriter.write(getTime() + ",on");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString());
				}
	        }
    	}
    }
	
}