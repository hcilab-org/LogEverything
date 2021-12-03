package org.hcilab.projects.logeverything.activity;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Build;
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
	public static String RELATIVE_PATH;

	public static File ROOT_FOLDER = null;

	public static File commonDocumentDirPath(String FolderName)
	{
		File dir;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
		{
			RELATIVE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator  + FolderName;
			RELATIVE_PATH = Environment.DIRECTORY_DOWNLOADS + File.separator  + FolderName;
		}
		else
		{
			RELATIVE_PATH = Environment.getExternalStorageDirectory() + File.separator + FolderName;
		}
		dir = new File(RELATIVE_PATH);
		// Make sure the path directory exists.
		if (!dir.exists())
		{
			// Make it, if it doesn't exit
			boolean success = dir.mkdirs();
			if (!success)
			{
				//dir = null;
				Log.e(TAG, "commonDocumentDirPath failed");
			}
		}
		return dir;
	}

	public static void setSavePath(Context pContext){
			
		/*String deviceId = Secure.getString(pContext.getContentResolver(), Secure.ANDROID_ID);
		if(deviceId == null) {
			deviceId = "NULL";
		}
		Log.d(TAG, "DEVICE ID: " + deviceId);*/
		String LOG_DIR = dateFormat.format(new Date());

		ROOT_FOLDER = commonDocumentDirPath(BASE_DIR + File.separator + LOG_DIR + File.separator);


        Log.d(TAG, ROOT_FOLDER.getAbsolutePath());

		if (!ROOT_FOLDER.exists()) {
			if (!ROOT_FOLDER.mkdirs()) {
				Log.e(TAG, "error creating the folders");
			}
		}

	}

	public static boolean sdk29AndUp() {
	 	return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
	}

	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	static {
		numberFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
		numberFormat.setGroupingUsed(false);
	}
}
	

