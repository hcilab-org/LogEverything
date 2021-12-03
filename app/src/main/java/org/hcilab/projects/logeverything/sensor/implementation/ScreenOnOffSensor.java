package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.IOException;

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
		m_FileHeader = "TimeUnix,Value";
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
		Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		this.m_context = context;
		try {			
			if(isScreenOn) {
				m_OutputStream.write((t + ",on\n").getBytes());
			} else {
				m_OutputStream.write((t + ",off\n").getBytes());
			}
			m_OutputStream.flush();
		} catch (Exception e) {
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
				m_OutputStream.flush();
				m_OutputStream.close();
				m_OutputStream = null;
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}	
		}	
	}
	
	public class ScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Long t = System.currentTimeMillis();
			if(m_IsRunning) {
				try {
					if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
						m_OutputStream.write((t + ",off\n").getBytes());
						wasScreenOn = false;
					} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
						m_OutputStream.write((t + ",on\n").getBytes());
						wasScreenOn = true;
					}
					m_OutputStream.flush();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			
		}		
	}
	
    public void onPause() {
		Long t = System.currentTimeMillis();
    	if(m_IsRunning) {
        // WHEN THE SCREEN IS ABOUT TO TURN OFF
	        if (wasScreenOn) {
	            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
	        	try {
					m_OutputStream.write((t + ",off\n").getBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString());
				}
	        }
    	}
    }
 
    public void onResume() {
		Long t = System.currentTimeMillis();
    	if(m_IsRunning) {
	        // ONLY WHEN SCREEN TURNS ON
	        if (!wasScreenOn) {
	            // THIS IS WHEN ONRESUME() IS CALLED DUE TO A SCREEN STATE CHANGE
	        	try {
					m_OutputStream.write((t + ",on\n").getBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString());
				}
	        }
    	}
    }
	
}