package de.irf.it.retailbs.andometry.odometry;


import java.util.Random;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import android.util.Pair;


public class FakeOdometryGenerator
		implements OdometryGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.irf.it.retailbs.andometry.odometry.OdometryGenerator#
	 * newAccelerometerReading(long, org.apache.commons.math.geometry.Vector3D)
	 */
	@Override
	public void newAccelerometerReading(long timestamp, Vector3D value) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.irf.it.retailbs.andometry.odometry.OdometryGenerator#
	 * newMagneticFieldReading(long, org.apache.commons.math.geometry.Vector3D)
	 */
	@Override
	public void newMagneticFieldReading(long timestamp, Vector3D value) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.irf.it.retailbs.andometry.odometry.OdometryGenerator#getCurrentPosition
	 * ()
	 */
	@Override
	public Pair<Vector3D, Rotation> getCurrentPosition() {
		return new Pair<Vector3D, Rotation>(new Vector3D(0, 0, 0),
				Rotation.IDENTITY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.irf.it.retailbs.andometry.odometry.OdometryGenerator#
	 * getCurrentStepFrequency()
	 */
	@Override
	public double getCurrentStepFrequency() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.irf.it.retailbs.andometry.odometry.OdometryGenerator#getCurrentDataSeries
	 * (int, boolean)
	 */
	@Override
	public Pair<double[], Vector3D[]> getCurrentDataSeries(int type, boolean transformed) {
		double[] xaxis = new double[50];
		for (int i = 0; i < xaxis.length; i++) {
			xaxis[i] = i;
		}

		Random r = new Random();

		Vector3D[] yaxis = new Vector3D[50];
		for (int i = 0; i < yaxis.length; i++) {
			yaxis[i] = new Vector3D(r.nextDouble() * 9, r.nextDouble() * 6, r
					.nextDouble() * 3);
		}
		return new Pair<double[], Vector3D[]>(xaxis, yaxis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.irf.it.retailbs.andometry.odometry.OdometryGenerator#setAnalysisFrequency
	 * (int)
	 */
	@Override
	public void setAnalysisFrequency(int value) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.irf.it.retailbs.andometry.odometry.OdometryGenerator#setDampingRate
	 * (float)
	 */
	@Override
	public void setDampingRate(float value) {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.irf.it.retailbs.andometry.odometry.OdometryGenerator#setLargerBuffer
	 * (boolean)
	 */
	@Override
	public void setLargerBuffer(boolean flag) {
		// do nothing here
	}

	@Override
	public void init(boolean largeBuffer, int frequencyWindow,
			double dampingRate) {
		// TODO Auto-generated method stub
		
	}

}
