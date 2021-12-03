package org.hcilab.projects.logeverything.sensor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.hcilab.projects.logeverything.activity.CONST;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
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

	protected OutputStream m_OutputStream = null;
	
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

		initFile(context);
	}

	public void initFile(Context context) {
		/*try {
				File file = new File(getFilePath());
				if(!file.exists()){
					m_FileWriter = new FileWriter(getFilePath(), true);
					m_FileWriter.write(m_FileHeader + "\n");
					Log.w(TAG, "File created " + getFilePath() );
				} else {
					Log.w(TAG, "File exits " + getFilePath() );
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}*/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

			ContentValues values = new ContentValues();

			values.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName());   // file name
			values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
			values.put(MediaStore.MediaColumns.RELATIVE_PATH, CONST.RELATIVE_PATH);

			Uri extVolumeUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
			String[] projection = new String[]{
					MediaStore.MediaColumns._ID,
					MediaStore.MediaColumns.DISPLAY_NAME,   // unused (for verification use only)
					MediaStore.MediaColumns.RELATIVE_PATH,  // unused (for verification use only)
					MediaStore.MediaColumns.DATE_MODIFIED   //used to set signature for Glide
			};

			String selection = MediaStore.MediaColumns.RELATIVE_PATH + "='" + CONST.RELATIVE_PATH + "' AND "
					+ MediaStore.MediaColumns.DISPLAY_NAME + "='" + getFileName() + "'";

			Uri fileUri = null;
			Cursor cursor = context.getContentResolver().query(extVolumeUri, projection, selection, null, null);
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {
					long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
					fileUri = ContentUris.withAppendedId(
							MediaStore.Downloads.EXTERNAL_CONTENT_URI,  id);
				}
			} else {
			}
			cursor.close();
			if (fileUri == null)
				fileUri = context.getContentResolver().insert(extVolumeUri, values);


			try {
				m_OutputStream = context.getContentResolver().openOutputStream(fileUri, "wa");
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Error #001: " + e.toString());
			}
		}
		else {
			String path = CONST.RELATIVE_PATH;
			File file = new File(path, getFileName());
			//Log.d(TAG, "saveFile: file path - " + file.getAbsolutePath());
			try {
				m_OutputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	abstract public void stop();

	public boolean isRunning() {
		return m_IsRunning;
	}
	
}
