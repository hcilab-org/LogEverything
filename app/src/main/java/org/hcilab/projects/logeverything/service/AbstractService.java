package org.hcilab.projects.logeverything.service;

import org.hcilab.projects.logeverything.activity.CONST;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

@SuppressLint("Registered")
public class AbstractService extends ForegroundService {
	
	private static final int MILLISECONDS_PER_SECOND = 1000;
	private static final int DETECTION_INTERVAL_SECONDS = 20;
	public static final int DETECTION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private PendingIntent mActivityRecognitionPendingIntent;

	private boolean mInProgress;

	public enum REQUEST_TYPE {
		START, STOP
	}
	
	private REQUEST_TYPE mRequestType;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(CONST.ROOT_FOLDER == null)
			CONST.setSavePath(this);
				
		return super.onStartCommand(intent, flags, startId);
	}


	private boolean servicesConnected() {
		Log.d(TAG, "activity servicesConnected");
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d(TAG, "Google Play services is available.");
			return true;
		} else {
			return false;
		}

	}

	public void onConnectionFailed(ConnectionResult connectionResult) {
		mInProgress = false;
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(null, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				Log.e(TAG, e.toString());
			}
		} else {
			int errorCode = connectionResult.getErrorCode();
			Log.d(TAG, "ERROR: " + errorCode);
		}
	}

	public void onConnected() {
		Log.d(TAG, "activity onConnected");
		mInProgress = false;
	}

	public void onDisconnected() {
		mInProgress = false;
	}
	
	public void startUpdates() {
		Log.d(TAG, "start activity updates");
		mRequestType = REQUEST_TYPE.START;
		if (!servicesConnected()) {
			return;
		}
		if (!mInProgress) {
			mInProgress = true;
		}
	}

	public void stopUpdates() {
		Log.d(TAG, "stop activity updates");
		mRequestType = REQUEST_TYPE.STOP;
		if (!servicesConnected()) {
			return;
		}
		if (!mInProgress) {
			mInProgress = true;
		}
	}
}
