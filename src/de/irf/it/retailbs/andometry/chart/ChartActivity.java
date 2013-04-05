package de.irf.it.retailbs.andometry.chart;


import org.apache.commons.math.geometry.Vector3D;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.Base.ChartTitle;
import com.artfulbits.aiCharts.Base.ChartLayoutElement.Dock;

import de.irf.it.retailbs.andometry.AbstractOdometryServiceConsumerActivity;
import de.irf.it.retailbs.andometry.R;
import de.irf.it.retailbs.andometry.odometry.OdometryGenerator;


public class ChartActivity extends AbstractOdometryServiceConsumerActivity {

	private ChartViewRefreshRunner refreshRunner;

	private ChartView rawDataChartView;

	private ChartView fourierDataChartView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.odometry_chart);

		ChartTitle ct;

		this.rawDataChartView = ( ChartView )this.findViewById(R.id.chart_raw);
		ChartTitle ct = new ChartTitle(this.getString(R.string.chart_raw_title));
		ct.setDock(Dock.Top);
		this.rawDataChartView.getChart().getTitles().add(ct);
		this.registerForContextMenu(this.rawDataChartView);

		this.fourierDataChartView = ( ChartView )this
				.findViewById(R.id.chart_fft);
		ct = new ChartTitle(this.getString(R.string.chart_fft_title));
		ct.setDock(Dock.Top);
		this.fourierDataChartView.getChart().getTitles().add(ct);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.refreshRunner = new ChartViewRefreshRunner(new Handler());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
			case R.id.chart_raw:
				menu.setHeaderTitle(R.string.chart_raw_title);
				this.getMenuInflater().inflate(R.menu.context_rawdatachart,
						menu);
				break;
			case R.id.chart_fft:
				break;
			default:
				// do nothing here.
		} // if
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.m_ctx_chart_raw_phone:
				this.refreshRunner.setRawValueWorldCoordinateSystem(false);
				return true;
			case R.id.m_ctx_chart_raw_world:
				this.refreshRunner.setRawValueWorldCoordinateSystem(true);
				return true;
			default:
				return super.onContextItemSelected(item);
		} // switch
	}

	private class ChartViewRefreshRunner
			implements Runnable {

		private static final int REFRESH_RATE = 250;

		private Handler handler;

		private boolean rawValueWorldCoordinateSystem;

		public ChartViewRefreshRunner(Handler h) {
			this.handler = h;
			this.handler.post(this);
		}

		public void setRawValueWorldCoordinateSystem(boolean flag) {
			this.rawValueWorldCoordinateSystem = flag;
		}

		private void updateValues(ChartView cv, Pair<double[], Vector3D[]> values) {
			ChartSeries csX = cv.getSeries().get("x_direction");
			ChartSeries csY = cv.getSeries().get("y_direction");
			ChartSeries csZ = cv.getSeries().get("z_direction");

			ChartPointCollection cpcX = csX.getPoints();
			ChartPointCollection cpcY = csY.getPoints();
			ChartPointCollection cpcZ = csZ.getPoints();

			cpcX.beginUpdate();
			cpcY.beginUpdate();
			cpcZ.beginUpdate();
			cpcX.clear();
			cpcY.clear();
			cpcZ.clear();

			for (int i = 0; i < values.first.length; i++) {
				cpcX.addXY(values.first[i], values.second[i].getX());
				cpcY.addXY(values.first[i], values.second[i].getY());
				cpcZ.addXY(values.first[i], values.second[i].getZ());
			} // for

			cpcX.endUpdate();
			cpcY.endUpdate();
			cpcZ.endUpdate();
		}

		@Override
		public void run() {
			OdometryGenerator og = ChartActivity.this.getGenerator();
			if (og != null){
				this.updateValues(ChartActivity.this.rawDataChartView,
						og.getCurrentDataSeries(
								OdometryGenerator.DATA_SERIES_RAW,
								this.rawValueWorldCoordinateSystem));
				this.updateValues(ChartActivity.this.fourierDataChartView,
						og.getCurrentDataSeries(
								OdometryGenerator.DATA_SERIES_FOURIER, false));
			}

			this.handler.postDelayed(this, REFRESH_RATE);
		}
	}
}
