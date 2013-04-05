package de.irf.it.retailbs.andometry.surface;


import android.content.ComponentName;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import de.irf.it.retailbs.andometry.AbstractOdometryServiceConsumerActivity;
import de.irf.it.retailbs.andometry.R;


public class SurfaceActivity extends AbstractOdometryServiceConsumerActivity {

	/**
	 * 
	 */
	private GLSurfaceView surfaceView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.odometry_surface);

		this.surfaceView = ( GLSurfaceView )this.findViewById(R.id.surfaceView);

		SurfaceRenderer sr = new SurfaceRenderer(this);

		this.surfaceView.setRenderer(sr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.surfaceView.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.surfaceView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		this.surfaceView.onResume();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		super.onServiceDisconnected(name);
		this.surfaceView.onPause();
	}

}
