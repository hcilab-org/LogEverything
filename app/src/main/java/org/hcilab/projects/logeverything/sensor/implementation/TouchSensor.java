package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.hcilab.projects.logeverything.activity.CONST;
import org.hcilab.projects.logeverything.handler.HandlerListener;
import org.hcilab.projects.logeverything.handler.TouchHandler;
import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class TouchSensor extends AbstractSensor implements HandlerListener {

	private static final long serialVersionUID = 1L;
	
	private TouchHandler m_ServiceHandler;
	
	private long count;

	public TouchSensor() {
        super();
		m_IsRunning = false;
		TAG = getClass().getSimpleName();
		SENSOR_NAME = "Touch Log";
		FILE_NAME = "touch.csv";
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
				m_FileWriter.write("timestamp,finger,event,x,y,prs,q");
				m_FileWriter.write("\n");		
				m_FileWriter.flush();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
		android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		m_ServiceHandler = new TouchHandler(thread.getLooper());
		m_ServiceHandler.addListener(this);
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = m_ServiceHandler.obtainMessage();
		msg.arg1 = (new Random()).nextInt(Integer.MAX_VALUE);
		m_ServiceHandler.sendMessage(msg);
		
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
			m_ServiceHandler.stop();
			m_ServiceHandler = null;
		}
	}

	@Override
	public void sendMessage(String msg) {
		if (m_FileWriter == null)
		{
			try {
				m_FileWriter = new FileWriter(new File(getFilePath()), true);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		
		if(m_IsRunning) {
			try {
				Log.d(TAG, "#"+msg);
				m_FileWriter.write(msg);
				m_FileWriter.write("\n");		
				count++;
				int flushLevel = 100;
				if (count % flushLevel == 0) {
					m_FileWriter.flush();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		else
		{
			Log.d(TAG, "not running");
		}
	}

}