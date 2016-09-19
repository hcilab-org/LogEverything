package org.hcilab.projects.logeverything.activity;

import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.adapter.SensorAdapter;
import org.hcilab.projects.logeverything.db.SensorDatabaseHelper;
import org.hcilab.projects.logeverything.sensor.SensorList;
import org.hcilab.projects.logeverything.service.AccessibilityLogService;
import org.hcilab.projects.logeverything.service.LogService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private final String TAG = getClass().getName();
	
	private ListView m_List;
	private Button m_ButtonAccessibility;
	private LinearLayout m_StartLayout;
	private RelativeLayout m_StopLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CONST.setSavePath(this);

		m_List = (ListView) findViewById(R.id.sensor_list);
		Button startButton = (Button) findViewById(R.id.start_button);
		Button stopButton = (Button) findViewById(R.id.stop_button);
		m_StartLayout = (LinearLayout) findViewById(R.id.start_layout);
		m_StopLayout = (RelativeLayout) findViewById(R.id.stop_layout);
		m_ButtonAccessibility = (Button) findViewById(R.id.accessibility_button);
		setAccessibilityButtonState ();
		
		SensorDatabaseHelper db = new SensorDatabaseHelper(this);
		SensorList.getList(this);		
		
		SensorAdapter adapter = new SensorAdapter(this, db.getCursor());
		
		m_List.setAdapter(adapter);
		m_List.setItemsCanFocus(false);

		startButton.setOnClickListener(onStartButtonClick);
		m_ButtonAccessibility.setOnClickListener(onAccessililityButtonClick);

		stopButton.setOnClickListener(onStopButtonClick);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		m_List.setEnabled(true);

		if (isLogServiceRunning(this)){
			m_StartLayout.setVisibility(View.GONE);
			m_StopLayout.setVisibility(View.VISIBLE);
			Log.d(TAG, "RESUME: service active");
		} else {
			m_StartLayout.setVisibility(View.VISIBLE);
			m_StopLayout.setVisibility(View.GONE);
			Log.d(TAG, "RESUME: service inactive");
		}
	}
	
	private final OnClickListener onStartButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m_StartLayout.setVisibility(View.GONE);
			m_StopLayout.setVisibility(View.VISIBLE);
			startLogService(MainActivity.this);
		}
	};
	
	private final OnClickListener onStopButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m_StartLayout.setVisibility(View.VISIBLE);
			m_StopLayout.setVisibility(View.GONE);
			stopLogService(MainActivity.this);
		}
	};

	private final OnClickListener onAccessililityButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isAccessibilityServiceEnabled(MainActivity.this))
			{
				Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivityForResult(intent, 0);
			}
			setAccessibilityButtonState ();				
		}
	};
	
	private void setAccessibilityButtonState ()
	{
		if (!isAccessibilityServiceEnabled(this))
		{
			m_ButtonAccessibility.setTextColor(Color.RED);
			m_ButtonAccessibility.setText(R.string.accessibility_button_Off);
		}
		else
		{
			m_ButtonAccessibility.setTextColor(Color.GREEN);
			m_ButtonAccessibility.setText(R.string.accessibility_button_On);
		}
	}
	

	private static PendingIntent getPendingIntent(Context context) {
		Intent alarmIntent = new Intent(context.getApplicationContext(), LogService.class);
		return PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	

	public static void startLogService(Context context) {
		Intent intent = new Intent(context, LogService.class);
		context.startService(intent);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = getPendingIntent(context);
		long m_AlarmInterval = 50 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + m_AlarmInterval, m_AlarmInterval, pendingIntent);
		SharedPreferences sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, Activity.MODE_PRIVATE);
		sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, true).apply();
	}

	private void stopLogService(Context context) {
		Intent intent = new Intent(context, LogService.class);
		context.stopService(intent);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = getPendingIntent(context);
		alarmManager.cancel(pendingIntent);
		SharedPreferences sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, Activity.MODE_PRIVATE);
		sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false).apply();
	}
	
	public static boolean isLogServiceRunning(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, Activity.MODE_PRIVATE);
		return sp.getBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false);
	}

	private boolean isAccessibilityServiceEnabled(Context context) {
		int accessibilityEnabled = 0;
		
		try {
			accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
			android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
		} catch (SettingNotFoundException e) {
			Log.d(TAG, e.toString());
		}
		
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
		
		if (accessibilityEnabled == 1) {
			String settingValue =
			Settings.Secure.getString(context.getContentResolver(),
			Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				mStringColonSplitter.setString(settingValue);
				while (mStringColonSplitter.hasNext()) {
					String accessibilityService = mStringColonSplitter.next();
					if (accessibilityService.equalsIgnoreCase(AccessibilityLogService.SERVICE)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
