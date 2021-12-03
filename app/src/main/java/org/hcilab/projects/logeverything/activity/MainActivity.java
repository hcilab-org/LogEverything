package org.hcilab.projects.logeverything.activity;

import static android.os.Build.VERSION.SDK_INT;

import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.adapter.SensorAdapter;
import org.hcilab.projects.logeverything.db.SensorDatabaseHelper;
import org.hcilab.projects.logeverything.sensor.SensorList;
import org.hcilab.projects.logeverything.service.AccessibilityLogService;
import org.hcilab.projects.logeverything.service.LogService;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

	private final String TAG = getClass().getName();
	
	private ListView m_List;
	private Button m_ButtonAccessibility;
	private Button m_ButtonStart;
	private Button m_ButtonStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		CONST.setSavePath(this);

		m_List = findViewById(R.id.sensor_list);
		m_ButtonStart = findViewById(R.id.start_button);
		m_ButtonStop = findViewById(R.id.stop_button);
		m_ButtonAccessibility = findViewById(R.id.accessibility_button);
		setAccessibilityButtonState ();
		
		SensorDatabaseHelper db = new SensorDatabaseHelper(this);
		SensorList.getList(this);		
		
		SensorAdapter adapter = new SensorAdapter(this, db.getCursor());
		
		m_List.setAdapter(adapter);
		m_List.setItemsCanFocus(false);

		m_ButtonStart.setOnClickListener(onStartButtonClick);
		m_ButtonStop.setOnClickListener(onStopButtonClick);
		m_ButtonAccessibility.setOnClickListener(onAccessibilityButtonClick);

		isPermissionGranted(Manifest.permission.WAKE_LOCK);
		isPermissionGranted(Manifest.permission.RECORD_AUDIO);
		isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE);
		isPermissionGranted(Manifest.permission.RECEIVE_BOOT_COMPLETED);
		isPermissionGranted(Manifest.permission.READ_PHONE_STATE);
		isPermissionGranted(Manifest.permission.ACCESS_NETWORK_STATE);
		isPermissionGranted(Manifest.permission.FOREGROUND_SERVICE);

		if(checkPermission()) {
			requestPermission();
		}

		//Thread threadUdp = new Thread(new UDPReceiver(7001, view));
		//threadUdp.start();

	}

	private boolean checkPermission() {
		if (SDK_INT >= Build.VERSION_CODES.R) {
			return Environment.isExternalStorageManager();
		} else {
			int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
			int result1 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
		}
	}

	private void requestPermission() {
		if (SDK_INT >= Build.VERSION_CODES.R) {
			try {
				Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
				startActivityForResult(intent, 2296);
			} catch (Exception e) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
				startActivityForResult(intent, 2296);
			}
		} else {
			//below android 11
			//ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
	}

	public boolean isPermissionGranted(String permission) {
		Log.d(TAG, "Check Permission");
		if (SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(permission)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v(TAG,"Permission is granted");
				return true;
			} else {

				Log.v(TAG,"Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
				return false;
			}
		}
		else { //permission is automatically granted on sdk<23 upon installation
			Log.v(TAG,"Permission is granted");
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
			//resume tasks needing this permission
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		m_List.setEnabled(true);

		if (isLogServiceRunning(this)){
			m_ButtonStart.setVisibility(View.GONE);
			m_ButtonStop.setVisibility(View.VISIBLE);
			Log.d(TAG, "RESUME: service active");
		} else {
			m_ButtonStart.setVisibility(View.VISIBLE);
			m_ButtonStop.setVisibility(View.GONE);
			Log.d(TAG, "RESUME: service inactive");
		}
	}
	
	private final OnClickListener onStartButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m_ButtonStart.setVisibility(View.GONE);
			m_ButtonStop.setVisibility(View.VISIBLE);
			Log.d(TAG, "START TRACKING!");
			startLogService(MainActivity.this);
		}
	};
	
	private final OnClickListener onStopButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			m_ButtonStart.setVisibility(View.VISIBLE);
			m_ButtonStop.setVisibility(View.GONE);
			stopLogService(MainActivity.this);
		}
	};

	private final OnClickListener onAccessibilityButtonClick = new OnClickListener() {
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
		long m_AlarmInterval = 60 * 1000;
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
