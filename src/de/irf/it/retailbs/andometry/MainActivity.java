package de.irf.it.retailbs.andometry;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import de.irf.it.retailbs.andometry.chart.ChartActivity;
import de.irf.it.retailbs.andometry.odometry.OdometryService;
import de.irf.it.retailbs.andometry.preferences.PreferencesActivity;
import de.irf.it.retailbs.andometry.surface.SurfaceActivity;


public class MainActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.odometry);

		/*
		 * Initialize tabbed view properly, binding each activity class to a
		 * separate tab.
		 */
		TabHost.TabSpec ts;
		Intent i;

		i = new Intent().setClass(this, SurfaceActivity.class);
		ts = this.getTabHost().newTabSpec("odometry_surface").setIndicator(
				"Surface",
				this.getResources().getDrawable(R.drawable.ic_tab_surface))
				.setContent(i);
		this.getTabHost().addTab(ts);

		i = new Intent().setClass(this, ChartActivity.class);
		ts = this.getTabHost().newTabSpec("odometry_chart").setIndicator(
				"Graph",
				this.getResources().getDrawable(R.drawable.ic_tab_chart))
				.setContent(i);
		this.getTabHost().addTab(ts);

		this.getTabHost().setCurrentTab(0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.startService(new Intent().setClass(this, OdometryService.class));
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.stopService(new Intent().setClass(this, OdometryService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.m_opt_preferences:
				this.startActivity(new Intent().setClass(this,
						PreferencesActivity.class));
				return true;
			case R.id.m_opt_quit:
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		} // switch
	}
}
