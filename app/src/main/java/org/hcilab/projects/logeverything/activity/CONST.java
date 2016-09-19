package org.hcilab.projects.logeverything.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

public class CONST
{
	private static final String TAG = "CONST";
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static final String SP_LOG_EVERYTHING = "sp_log_everything";
	public static final String SP_Accessibility_LOG_EVERYTHING = "sp_log_everything";
	public static final String KEY_LOG_EVERYTHING_RUNNING = "key_log_everything_running";
	public static final String KEY_Accessibility_LOG_EVERYTHING_RUNNING = "key_Accessibility_LOG_everything_running";
	
	
	
	private static final String BASE_DIR = "LogEverything";
	
	public static File ROOT_FOLDER = null;
	
	public static void setSavePath(Context pContext){
			
		String deviceId = Secure.getString(pContext.getContentResolver(), Secure.ANDROID_ID);
		if(deviceId == null) {
			deviceId = "NULL";
		}
		Log.d(TAG, "DEVICE ID: " + deviceId);
		String LOG_DIR = dateFormat.format(new Date());
		
		String SAVE_PATH = BASE_DIR + "/" + LOG_DIR + "/";
		
		ROOT_FOLDER = new File(Environment.getExternalStorageDirectory(), SAVE_PATH);

        Log.d(TAG, ROOT_FOLDER.getAbsolutePath());

		if (!ROOT_FOLDER.exists()) {
			if (!ROOT_FOLDER.mkdirs()) {
				Log.e(TAG, "error creating the folders");
			}
		}

	}
	
	
}
