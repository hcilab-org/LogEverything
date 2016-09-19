package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.activity.CONST;
import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MyAccelerometerSensor extends AbstractSensor implements SensorEventListener {

	private static final long serialVersionUID = 1L;
	
	private SensorManager sensorManager;
	private long count;

	public MyAccelerometerSensor() {
		m_IsRunning = false;
		SENSOR_NAME = "Accelerometer";
		FILE_NAME = "accelerometer.csv";
	}

	public View getSettingsView(Context context) {
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 10, 20, 10);
		TextView textView = new TextView(context);
		textView.setText("Sensor delay");
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		RadioGroup radioGroup = new RadioGroup(context);
		RadioButton r1 = new RadioButton(context);
		r1.setText("Normal");
		radioGroup.addView(r1);
		RadioButton r2 = new RadioButton(context);
		r2.setText("Game");
		radioGroup.addView(r2);
		RadioButton r3 = new RadioButton(context);
		r3.setText("Fastest");
		radioGroup.addView(r3);
		r3.setChecked(true);
		linearLayout.addView(textView);
		linearLayout.addView(radioGroup);
		return linearLayout;
	}

	public boolean isAvailable(Context context) {
		SensorManager sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		return !(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null);
	}

	@Override
	public void start(Context context) {
		super.start(context);
		if (!m_isSensorAvailable)
			return;
		
		if (this.m_FileWriter == null) {
			try {
				m_FileWriter = new FileWriter(new File(getFilePath()), true);
				m_FileWriter.write("timestamp,x,y,z,reliable");
				m_FileWriter.write("\n");
				m_FileWriter.flush();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		m_IsRunning = true;
		count = 0;
	}

	@Override
	public void stop() {
		if (m_IsRunning) {
			sensorManager.unregisterListener(this);
			try {
				m_FileWriter.flush();
				m_FileWriter.close();
				m_FileWriter = null;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			m_IsRunning = false;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (m_IsRunning) {
			try {
				count++;
				if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
					m_FileWriter.write(getTime() + ","
							+ event.values[0] + "," + event.values[1] + ","
							+ event.values[2] + ",false");
				} else {
					m_FileWriter.write(getTime() + ","
							+ event.values[0] + "," + event.values[1] + ","
							+ event.values[2] + ",true");
				}
				m_FileWriter.write("\n");
				int flushLevel = 100;
				if (count % flushLevel == 0) {
					m_FileWriter.flush();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

}