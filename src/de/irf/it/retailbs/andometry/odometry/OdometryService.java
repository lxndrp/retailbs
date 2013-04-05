package de.irf.it.retailbs.andometry.odometry;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import de.irf.it.retailbs.andometry.Preferences;


public class OdometryService extends Service {

	/**
	 * 
	 */
	private final IBinder binder = new Binder();

	/**
	 * 
	 */
	private OdometrySensorEventListener sensorEventListener;

	/**
	 * 
	 */
	private OdometryGenerator generator;

	/**
	 * 
	 */
	public OdometryService() {
		//this.generator = new FakeOdometryGenerator();
		this.generator = new OdometryGeneratorIterative();
		this.sensorEventListener = new OdometrySensorEventListener(
				this.generator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());

		/*
		 * Set configuration for generator from preferences.
		 */
		boolean fftBufferSize = sp.getBoolean(Preferences.FFT_BUFFER_SIZE,
				false);
		this.generator.setLargerBuffer(fftBufferSize);

		int fftAnalysisFrequency = sp.getInt(
				Preferences.FFT_ANALYSIS_FREQUENCY, 10);
		this.generator.setAnalysisFrequency(fftAnalysisFrequency);

		float fftDampingValue = sp
				.getFloat(Preferences.FFT_DAMPING_VALUE, 0.1f);
		this.generator.setDampingRate(fftDampingValue);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());

		/*
		 * Register for change notifications.
		 */
		sp
				.registerOnSharedPreferenceChangeListener(new SensorDelayPreferenceChangeListener());

		/*
		 * Attach sensor readings to listener according to preferences.
		 */
		int sensorDelay = sp.getInt(Preferences.SENSOR_DELAY, 2);
		SensorManager sm = ( SensorManager )this
				.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorDelay);
		sm.registerListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE), sensorDelay);
		sm.registerListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensorDelay);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(this.getPackageName(), "onBind()");
		return this.binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(this.getPackageName(), "onUnbind()");
		return super.onUnbind(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(this.getPackageName(), "onDestroy()");
		SensorManager sm = ( SensorManager )this
				.getSystemService(Context.SENSOR_SERVICE);
		sm.unregisterListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sm.unregisterListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
		sm.unregisterListener(this.sensorEventListener, sm
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
	}

	public final class Binder extends android.os.Binder {

		/**
		 * @return
		 */
		public OdometryGenerator getService() {
			return OdometryService.this.generator;
		}
	}

	public final class SensorDelayPreferenceChangeListener
			implements OnSharedPreferenceChangeListener {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals(Preferences.SENSOR_DELAY)) {
				/*
				 * Re-register sensor listener with updated delay.
				 */
				SensorManager sm = ( SensorManager )OdometryService.this
						.getSystemService(Context.SENSOR_SERVICE);

				sm.unregisterListener(OdometryService.this.sensorEventListener);

				int sensorDelay = PreferenceManager
						.getDefaultSharedPreferences(
								OdometryService.this.getApplicationContext())
						.getInt(Preferences.SENSOR_DELAY, 2);

				sm.registerListener(OdometryService.this.sensorEventListener,
						sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						sensorDelay);
				sm
						.registerListener(
								OdometryService.this.sensorEventListener,
								sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
								sensorDelay);
				sm.registerListener(OdometryService.this.sensorEventListener,
						sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
						sensorDelay);
			} // if
		}
	}
}
