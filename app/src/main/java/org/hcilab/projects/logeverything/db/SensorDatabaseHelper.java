package org.hcilab.projects.logeverything.db;

import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SensorDatabaseHelper extends SQLiteOpenHelper {

	private final String TAG = getClass().getName();
	
	private final String TABLE_SENSORLIST;
	private final String COLUMN_ID;
	private final String COLUMN_NAME;
	private final String COLUMN_ISAVAILABLE;
	private final String COLUMN_ISENABLED;
	private final String COLUMN_SETTINGSSTATE;
	private final String COLUMN_SETTINGS;
	
	
	private final Context m_context;
	
	public SensorDatabaseHelper(Context context) {
		super(context, context.getResources().getString(R.string.dbname),
				null, Integer.parseInt(context.getResources().getString(R.string.dbversion)));
		
		this.m_context=context;
		
		TABLE_SENSORLIST = m_context.getResources().getString(R.string.dbTableSenorList);
		COLUMN_ID = m_context.getResources().getString(R.string.dbColumnId);
		COLUMN_NAME = m_context.getResources().getString(R.string.dbColumnName);
		COLUMN_ISAVAILABLE = m_context.getResources().getString(R.string.dbColumnIsAvailable);
		COLUMN_ISENABLED = m_context.getResources().getString(R.string.dbColumnIsEnabled);
		COLUMN_SETTINGSSTATE = m_context.getResources().getString(R.string.dbColumnSettingsState);
		COLUMN_SETTINGS = m_context.getResources().getString(R.string.dbColumnSettings);
	}


	@Override
	public void onCreate(SQLiteDatabase pDb) {		
		String CREATE_PRODUCTS_TABLE = "create table " + TABLE_SENSORLIST+  " ( "+
				COLUMN_ID + " integer primary key autoincrement, "+
				COLUMN_NAME + " varchar(50) not null, " +
				COLUMN_ISAVAILABLE + " integer not null, " +
				COLUMN_ISENABLED + " integer not null, " +
				COLUMN_SETTINGSSTATE + " integer not null, " +
				COLUMN_SETTINGS + " varchar(500) not null)";
		pDb.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase pDb, int arg1, int arg2) {
		pDb.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORLIST);
	    onCreate(pDb);
	}
	
	/***
	 * 
	 * @param pSensor Sensor to add
	 * @return True if insert was done or was already in DB
	 */
	@SuppressWarnings("UnusedReturnValue")
	public boolean addIfNotExists (AbstractSensor pSensor)
	{
		if (contains(pSensor))
		{
			return false;
		}
		
		Log.d(TAG, pSensor.getSensorName() + " create");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(COLUMN_NAME, pSensor.getSensorName());
		values.put(COLUMN_ISAVAILABLE, pSensor.isAvailable(m_context));
		values.put(COLUMN_SETTINGS, pSensor.getSettings());
		values.put(COLUMN_SETTINGSSTATE, pSensor.getSettingsState());
		
		if (pSensor.isEnabled())
			values.put(COLUMN_ISENABLED, 1);
		else 
			values.put(COLUMN_ISENABLED, 0);
		
		db.insert(TABLE_SENSORLIST, null, values);
		
		return true;
	}
	
	/***
	 * 
	 * @param pSensor The sensor to test
	 * @return True if the sensor is selected
	 */
	public Boolean getSensorData(AbstractSensor pSensor) {
		String query = "Select * FROM " + TABLE_SENSORLIST + " WHERE " + COLUMN_NAME + " =  \"" + pSensor.getSensorName() + "\"";
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		boolean ret = false;
		String[] ColumnNames =  cursor.getColumnNames();
		if (ArrayContainsString(ColumnNames, COLUMN_ISENABLED))
		{
			if (cursor.moveToFirst())
			{
				if (cursor.getInt(cursor.getColumnIndex(COLUMN_ISENABLED)) == 1)
				{
					ret =  true;
				}
			}
		}
		else
		{
			Log.d(TAG, "Not Contraind Column " + COLUMN_ISENABLED);
		}
		cursor.close();
		db.close();
		
		return ret;
	}
	
	private boolean ArrayContainsString(String[] columnNames,
			String cOLUMN_ISENABLED2) {

		for (String columnName : columnNames) {
			if (columnName.equals(cOLUMN_ISENABLED2))
				return true;
		}
		return false;
	}


	/***
	 * 
	 * @param pSensor The sensor to test
	 * @return True if the sensor is contained in the DB
	 */
	private Boolean contains(AbstractSensor pSensor) {
		String query = "Select * FROM " + TABLE_SENSORLIST + " WHERE " + COLUMN_NAME + " =  \"" + pSensor.getSensorName() + "\"";
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		boolean ret = false;
		if (cursor.moveToFirst())
		{
			ret =  true;
		}
		cursor.close();
		db.close();
		
		return ret;
	}
	
	
	/***
	 * @param pId Id of the element that that shold be updated
	 * @param pisChecked If the element should be recorded
	 * @return Count of the updated items
	 */
	public int updateIsChecked (Integer pId, Boolean pisChecked)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    if (pisChecked)
	    	values.put(COLUMN_ISENABLED, 1);
	    else
	    	values.put(COLUMN_ISENABLED, 0);
	     
	    return db.update(TABLE_SENSORLIST, values, COLUMN_ID + " = " + pId, null);
	}

	/***
	 * 
	 * @param pId Id of the element that that shold be updated
	 * @param pSettingsSate Setting State
	 * @param pSettings Setting String
	 * @return id in the DB
	 */
	public int updateSettings (Integer pId, Integer pSettingsSate, String pSettings)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_SETTINGSSTATE, pSettingsSate);
	    values.put(COLUMN_SETTINGS, pSettings);
	    
	    return db.update(TABLE_SENSORLIST, values, COLUMN_ID + " = " + pId, null);
	}
	
	/***
	 * @return all sensors in DB
	 */
	public Cursor getCursor() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.query(TABLE_SENSORLIST, null, null, null, null, null, null);
	}
}
