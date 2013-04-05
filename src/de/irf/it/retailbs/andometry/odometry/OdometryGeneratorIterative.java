package de.irf.it.retailbs.andometry.odometry;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import de.irf.it.retailbs.andometry.odometry.OrientationFilter.DirectionEstimation;
import de.irf.it.retailbs.andometry.odometry.OrientationFilter.OrientationComputation;

import android.util.Pair;

public class OdometryGeneratorIterative implements OdometryGenerator {

	public OdometryGeneratorIterative() {
		init(false, 1, 0.2);
	}

	private static double assumedConstantHeight = 1.2;
	private static double assumedConstantStepLenght = 0.5;
	private static int smallBufferSize = 32;
	private static int largeBufferSize = 64;
	private static double minWalkingFrequency = 0.5;
	private static double maxWalkingFrequency = 3;
	private static double minVerticalWalkingAmplitude = 0.5;
	private static double minHorizontalWalkingAmplitude = 0.01;

	private int bufferSize;
	private int frequenciesToAnalyse;
	private double dampingRate;
	private int computeOdometryEveryNthFrame;
	private int counter;

	private Vector3D odometry;
	private Rotation rotationWorld2phone;
	private IterativeFourierAnalyser[] fourierAnalyser;

	private OrientationFilter orientationFilter;

	private double stepLenght;

	// variables calculated by calculateMovementDirectionAndStepFrequency
	private Vector3D movementDirection;
	private double stepFrequency;

	private RingBufferWithSum accelerationData;
	private RingBufferWithSum accelerationDataRotated;
	private RingBufferWithSum magneticData;

	@Override
	public void init(boolean largeBuffer, int analysisFrequency,
			double dampingRate) {
		this.bufferSize = largeBuffer ? largeBufferSize : smallBufferSize;
		this.stepLenght = assumedConstantStepLenght;
		this.frequenciesToAnalyse = 17;
		this.dampingRate = dampingRate;
		this.computeOdometryEveryNthFrame = analysisFrequency;
		counter = 0;

		fourierAnalyser = new IterativeFourierAnalyser[3]; // 0,1,2 = x,y,z =
															// east,north,up
		for (int i = 0; i < 3; i++) {
			fourierAnalyser[i] = new IterativeFourierAnalyser(bufferSize,
					frequenciesToAnalyse);
		}
		odometry = new Vector3D(0, 0, assumedConstantHeight);
		rotationWorld2phone = Rotation.IDENTITY;
		accelerationData = new RingBufferWithSum(bufferSize);
		accelerationDataRotated = new RingBufferWithSum(bufferSize);
		magneticData = new RingBufferWithSum(bufferSize);

		this.orientationFilter = new OrientationFilter(accelerationData,
				magneticData, DirectionEstimation.DirectionFromLowPass,
				DirectionEstimation.DirectionFromLowPass,
				OrientationComputation.GetOwnRotation, dampingRate, dampingRate);
	}

	private void update(double timeStamp, Vector3D newAccelerationMeasurement) {
		// compute rotationWorld2phone
		// computeWorld2PhoneRotationFromAndroid();
		rotationWorld2phone = orientationFilter.computeWorld2PhoneRotation();

		Vector3D newAccelerationMeasurementRotated = rotationWorld2phone
				.applyInverseTo(newAccelerationMeasurement);
		accelerationDataRotated.put(timeStamp,
				newAccelerationMeasurementRotated);

		calculateMovementDirectionAndStepFrequency(newAccelerationMeasurementRotated);

		if (stepFrequency > 0) {
			odometry = odometry.add(stepLenght * stepFrequency
					/ accelerationData.getSamplingFrequency(), // scale
					movementDirection.normalize());
		}
	}

	private void calculateMovementDirectionAndStepFrequency(
			Vector3D newAccelerationMeasurementRotated) {
		fourierAnalyser[0]
				.submitNewMeasurement(newAccelerationMeasurementRotated.getX());
		fourierAnalyser[1]
				.submitNewMeasurement(newAccelerationMeasurementRotated.getY());
		fourierAnalyser[2]
				.submitNewMeasurement(newAccelerationMeasurementRotated.getZ());

		double samplingFrequency = accelerationData.getSamplingFrequency();

		int indexOfPeakFrequency = fourierAnalyser[2]
				.getBiggestResponseIgnoringOffset();
		double[] peakZ = fourierAnalyser[2].getResponseDataAtFrequencyIndex(
				indexOfPeakFrequency, samplingFrequency);
		stepFrequency = peakZ[0];

		double[] peakX = fourierAnalyser[0].getResponseDataAtFrequencyIndex(
				indexOfPeakFrequency, samplingFrequency);
		double[] peakY = fourierAnalyser[1].getResponseDataAtFrequencyIndex(
				indexOfPeakFrequency, samplingFrequency);

		double phaseX = peakX[2] - peakZ[2];
		double phaseY = peakY[2] - peakZ[2];

		double phaseOfDominantChannel = peakX[1] > peakY[1] ? phaseX : phaseY;

		if (phaseOfDominantChannel < -Math.PI) {
			phaseOfDominantChannel += 2 * Math.PI;
		}
		if (phaseOfDominantChannel > Math.PI) {
			phaseOfDominantChannel -= 2 * Math.PI;
		}

		double factor = phaseOfDominantChannel < 0 ? -1 : 1;

		movementDirection = new Vector3D(factor * peakX[1]
				* Math.cos(phaseX - phaseOfDominantChannel), factor * peakY[1]
				* Math.cos(phaseY - phaseOfDominantChannel), 0);

		// apply some heuristic thresholds to find out if we are currently walking
		if (	movementDirection.getNorm() < minHorizontalWalkingAmplitude
				|| stepFrequency < minWalkingFrequency
				|| stepFrequency > maxWalkingFrequency
				|| peakZ[1] < minVerticalWalkingAmplitude) {
			stepFrequency = 0;
		}
	}

	@Override
	public void newAccelerometerReading(long timestamp, Vector3D value) {
		double timeStampInSeconds = timestamp / 1000000000.0; // nano-second to
																// second
		accelerationData.put(timeStampInSeconds, value);

		// counter++;
		// if (counter % computeOdometryEveryNthFrame == 0) {
		// counter = 0;
		// update(timestamp, value);
		// }

		update(timeStampInSeconds, value);
	}

	@Override
	public void newMagneticFieldReading(long timestamp, Vector3D value) {
		double timeStampInSeconds = timestamp / 1000000000.0; // nano-second to
																// second
		magneticData.put(timeStampInSeconds, value);
	}

	@Override
	public Pair<Vector3D, Rotation> getCurrentPosition() {
		return new Pair<Vector3D, Rotation>(odometry, rotationWorld2phone);
	}

	@Override
	public double getCurrentStepFrequency() {
		return stepFrequency;
	}

	@Override
	public Pair<double[], Vector3D[]> getCurrentDataSeries(int type,
			boolean transformed) {
		switch (type) {
		case DATA_SERIES_RAW:
			if (transformed) {
				return accelerationDataRotated.getBufferLatestLast();
			} else {
				return accelerationData.getBufferLatestLast();
			}
		case DATA_SERIES_FOURIER:
			double[] frequencies = fourierAnalyser[0]
					.getFrequencies(accelerationData.getSamplingFrequency());
			double[] fft_x = fourierAnalyser[0].getMagnitudes();
			double[] fft_y = fourierAnalyser[1].getMagnitudes();
			double[] fft_z = fourierAnalyser[2].getMagnitudes();

			// zero the offset-frequency
			fft_x[0] = 0;
			fft_y[0] = 0;
			fft_z[0] = 0;

			Vector3D[] fftData = new Vector3D[frequencies.length];
			for (int i = 0; i < frequencies.length; i++) {
				fftData[i] = new Vector3D(fft_x[i], fft_y[i], fft_z[i]);
			}
			return new Pair<double[], Vector3D[]>(frequencies, fftData);
		}
		return null;
	}

	@Override
	public void setLargerBuffer(boolean flag) {
		// init(flag, frequenciesToAnalyse, dampingRate);
	}

	@Override
	public void setAnalysisFrequency(int value) {
		// this.computeOdometryEveryNthFrame = value;
	}

	@Override
	public void setDampingRate(float value) {
		// this.dampingRate = value;
	}

}
