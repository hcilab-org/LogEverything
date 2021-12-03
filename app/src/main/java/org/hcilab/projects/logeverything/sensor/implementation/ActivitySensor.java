package org.hcilab.projects.logeverything.sensor.implementation;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;
import android.content.Context;
import android.view.View;

public class ActivitySensor {

}/* extends AbstractSensor {
}
}
   @Override
    public View getSettingsView(Context context) {
        return null;
    }

    @Override
    public boolean isAvailable(Context context) {
        return false;
    }

    @Override
    public void stop() {

    }
//TODO:
}

implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final long serialVersionUID = 1L;
	
	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int DETECTION_INTERVAL_SECONDS = 20;
	public static final int DETECTION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS; // 0 sets it to update as fast as possible, just use this for testing!
	
	private static ActivityRecognitionClient m_ActivityRecognitionClient;
	
	private static PendingIntent m_CallbackIntent;
							
	private Context m_Context; 
	
	public DataUpdateReceiver m_Receiver;
	
	public ActivitySensor (){
		m_IsRunning = false;
		TAG = getClass().getName();
		SENSOR_NAME = "Activity Recognition";
		FILE_NAME = "activity.csv";		
	}
	
	@Override
	public View getSettingsView(Context context) {
		return null;
	}

	@Override
	public boolean isAvailable(Context context) {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS)
			return true;
		else
		{
			Log.e(TAG, "Sensor not Avalible");
			return false;
		}
	}
	
	@Override
	public void start(Context context) {
		super.start(context);
		if (m_isSensorAvailable == false)
			return;
		
		m_Context = context;
		
		if (this.m_FileWriter == null)
		{
			Log.d(TAG, "path: " + CONST.ROOT_FOLDER.getAbsolutePath() + "/" + this.getFileName());
			try {
				m_FileWriter = new FileWriter(new File(CONST.ROOT_FOLDER.getAbsolutePath() + "/" + this.getFileName()), true);
				m_FileWriter.write("timestamp,activityType,activityName,confidence");
				m_FileWriter.write("\n");		
				m_FileWriter.flush();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}	
		}
		
		//START SERVICE
		m_ActivityRecognitionClient	= new ActivityRecognitionClient(context, this, this);
		
		m_ActivityRecognitionClient.connect();
				
		if (m_Receiver == null)
			m_Receiver = new DataUpdateReceiver();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActivityRecognitionIntentService.TAG);
		m_Context.registerReceiver(m_Receiver, intentFilter);
		
		
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
			
			try{
				m_ActivityRecognitionClient.removeActivityUpdates(m_CallbackIntent);
			} catch (IllegalStateException e){
				// probably the scan was not set up, we'll ignore
			}
			
			try {
				m_Context.unregisterReceiver(m_Receiver);
			} catch (Exception e){
				// probably the scan was not set up, we'll ignore
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(TAG, "onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "onConnected");
		Intent intent = new Intent(m_Context, ActivityRecognitionIntentService.class);
		m_CallbackIntent = PendingIntent.getService(m_Context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		m_ActivityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL_MILLISECONDS, m_CallbackIntent);
		
	}

	@Override
	public void onDisconnected() {
		
	}
	
	
	private class DataUpdateReceiver extends BroadcastReceiver {
 		
        public DataUpdateReceiver() {
        	super();
        }
 
        @Override
        public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(ActivityRecognitionIntentService.TAG)) {
	        	if(m_IsRunning) {
	        		try {
		        		if (m_FileWriter == null)
							m_FileWriter = new FileWriter(new File(CONST.ROOT_FOLDER + "/" + FILE_NAME), true);
									        		
		    			m_FileWriter.write(intent.getStringExtra(android.content.Intent.EXTRA_TEXT));
		    			//Log.d(TAG, intent.getStringExtra(android.content.Intent.EXTRA_TEXT));
	    				m_FileWriter.flush();
	        		} catch (IOException e) {
	        			Log.e(TAG, e.toString());
					}
	        	}
        	}
        }
    }
}*/
