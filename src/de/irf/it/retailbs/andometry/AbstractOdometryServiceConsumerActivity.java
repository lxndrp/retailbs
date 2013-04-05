package de.irf.it.retailbs.andometry;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import de.irf.it.retailbs.andometry.odometry.OdometryGenerator;
import de.irf.it.retailbs.andometry.odometry.OdometryService;


public abstract class AbstractOdometryServiceConsumerActivity extends Activity
		implements ServiceConnection {

	/**
	 * 
	 */
	private OdometryGenerator odometryGeneratorInstance;

	/**
	 * 
	 */
	private ProgressDialog rebindDialog;

	/**
	 * @return
	 */
	public OdometryGenerator getGenerator() {
		return this.odometryGeneratorInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("anDometry",
				"AbstractOdometryServiceConsumerActivity: onCreate()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log
				.v("anDometry",
						"AbstractOdometryServiceConsumerActivity: onStart()");

		String message = "Binding Odometry Service\nPlease wait...";
		this.rebindDialog = new ProgressDialog(this);
		this.rebindDialog.setMessage(message);
		this.rebindDialog.setCancelable(false);
		this.rebindDialog.show();

		this.getApplicationContext().bindService(
				new Intent(this, OdometryService.class), this,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("anDometry", "AbstractOdometryServiceConsumerActivity: onStop()");

		this.getApplicationContext().unbindService(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("anDometry",
				"AbstractOdometryServiceConsumerActivity: onDestroy()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceConnected(android.content.
	 * ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		this.odometryGeneratorInstance = (( OdometryService.Binder )service)
				.getService();
		this.rebindDialog.dismiss();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceDisconnected(android.content
	 * .ComponentName)
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.odometryGeneratorInstance = null;

		String message = "Rebinding Odometry Service\nPlease wait...";
		this.rebindDialog = new ProgressDialog(this);
		this.rebindDialog.setMessage(message);
		this.rebindDialog.setCancelable(false);
		this.rebindDialog.show();
	}
}
