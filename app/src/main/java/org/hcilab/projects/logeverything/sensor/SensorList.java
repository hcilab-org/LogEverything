package org.hcilab.projects.logeverything.sensor;

import java.util.ArrayList;
import java.util.List;

import org.hcilab.projects.logeverything.db.SensorDatabaseHelper;
import org.hcilab.projects.logeverything.sensor.implementation.AccessibilitySensor;
import org.hcilab.projects.logeverything.sensor.implementation.ActivitySensor;
import org.hcilab.projects.logeverything.sensor.implementation.AppSensor;
import org.hcilab.projects.logeverything.sensor.implementation.ChargingSensor;
import org.hcilab.projects.logeverything.sensor.implementation.MyAccelerometerSensor;
import org.hcilab.projects.logeverything.sensor.implementation.MyGyroscopeSensor;
import org.hcilab.projects.logeverything.sensor.implementation.MyLightSensor;
import org.hcilab.projects.logeverything.sensor.implementation.MyProximitySensor;
import org.hcilab.projects.logeverything.sensor.implementation.OrientationSensor;
import org.hcilab.projects.logeverything.sensor.implementation.RingtoneVolumeSensor;
import org.hcilab.projects.logeverything.sensor.implementation.ScreenOnOffSensor;
import org.hcilab.projects.logeverything.sensor.implementation.ScreenOrientationSensor;
import org.hcilab.projects.logeverything.sensor.implementation.StillAliveSensor;
import org.hcilab.projects.logeverything.sensor.implementation.TouchSensor;
import org.hcilab.projects.logeverything.sensor.implementation.WifiSensor;

import android.content.Context;

public class SensorList {
		
	private SensorList() {
	}
		
	public static List<AbstractSensor> getList(Context pContext) {
		List<AbstractSensor> list  = new ArrayList<>();

		list.add(new AccessibilitySensor());
		list.add(new MyAccelerometerSensor());
		//list.add(new ActivitySensor()); // This is not longer supported by Android
		//list.add(new AirplaneModeSensor());
		list.add(new AppSensor());
		//list.add(new AudioLevelSensor());
		list.add(new ChargingSensor());
		list.add(new MyGyroscopeSensor());
		list.add(new MyLightSensor());
		list.add(new MyProximitySensor());
		list.add(new OrientationSensor());
		list.add(new RingtoneVolumeSensor());
		list.add(new ScreenOnOffSensor());
		list.add(new ScreenOrientationSensor());
		list.add(new StillAliveSensor());
		list.add(new TouchSensor());
		list.add(new WifiSensor());		
		
		SensorDatabaseHelper db = new SensorDatabaseHelper(pContext);
		
		for (AbstractSensor s : list)
			db.addIfNotExists(s);
		
		for (AbstractSensor s : list)
			s.setEnabled(db.getSensorData(s));

		db.close();
			
		return list;
	}

}
