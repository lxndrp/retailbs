package de.irf.it.retailbs.andometry.odometry;


import org.apache.commons.math.geometry.Vector3D;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


public class OdometrySensorEventListener
		implements SensorEventListener {

	/**
	 * 
	 */
	private final OdometryGenerator odometryGenerator;

	public OdometrySensorEventListener(OdometryGenerator odometryGenerator) {
		this.odometryGenerator = odometryGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				this.odometryGenerator.newAccelerometerReading(event.timestamp,
						new Vector3D(event.values[0], event.values[1],
								event.values[2]));
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				this.odometryGenerator.newMagneticFieldReading(event.timestamp,
						new Vector3D(event.values[0], event.values[1],
								event.values[2]));
				break;
			default:
				break;
		} // switch
	}
}
