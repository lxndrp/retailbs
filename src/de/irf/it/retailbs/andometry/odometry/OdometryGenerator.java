package de.irf.it.retailbs.andometry.odometry;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import android.util.Pair;

public interface OdometryGenerator {

	/**
	 * 
	 */
	static final int DATA_SERIES_RAW = 0;

	/**
	 * 
	 */
	static final int DATA_SERIES_FOURIER = 1;

	/**
	 * @param timestamp
	 * @param value
	 */
	void newAccelerometerReading(long timestamp, Vector3D value);

	/**
	 * @param timestamp
	 * @param value
	 */
	void newMagneticFieldReading(long timestamp, Vector3D value);

	/**
	 * @return
	 */
	Pair<Vector3D, Rotation> getCurrentPosition();

	/**
	 * @return
	 */
	double getCurrentStepFrequency();

	/**
	 * @param type
	 *            Which type of data: DATA_SERIES_RAW or DATA_SERIES_FOURIER
	 * @param transformed
	 *            True if the data should be transformed into world coordinates,
	 *            false if raw data is requested.
	 * @return
	 */
	Pair<double[], Vector3D[]> getCurrentDataSeries(int type,
			boolean transformed);
	
	void init(boolean largeBuffer, int analysisFrequency, double dampingRate);

	/**
	 * @param flag
	 */
	void setLargerBuffer(boolean flag);

	/**
	 * @param value
	 */
	void setAnalysisFrequency(int value);

	/**
	 * @param value
	 */
	void setDampingRate(float value);

}
