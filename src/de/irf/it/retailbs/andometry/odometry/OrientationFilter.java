package de.irf.it.retailbs.andometry.odometry;

import org.apache.commons.math.geometry.NotARotationMatrixException;
import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import android.hardware.SensorManager;

public class OrientationFilter {

	public static enum DirectionEstimation {
		DirectionFromLowPass, DirectionFromLatestMeasurement, DirectionFromBuffer
	}
	public static enum OrientationComputation {
		GetRotationFromAndroid, GetOwnRotation
	}

	private RingBufferWithSum accelerationData;
	private RingBufferWithSum magneticData;
	private DirectionEstimation howToEstimateUpDirection;
	private DirectionEstimation howToEstimateNorthDirection;
	private OrientationComputation howToComputeOrientation;

	private double dampingRate_acc;
	private double dampingRate_mag;
	
	private int bufferSize;
	
	// low pass estimates
	Vector3D upDirectionEstimate;
	Vector3D northDirectionEstimate;
	Vector3D upDirectionVector;
	
	public OrientationFilter(
			RingBufferWithSum accelerationData,
			RingBufferWithSum magneticData,
			DirectionEstimation howToEstimateUpDirection,
			DirectionEstimation howToEstimateNorthDirection,
			OrientationComputation howToComputeOrientation,
			double dampingRate_acc,
			double dampingRate_mag) {
		this.accelerationData = accelerationData;
		this.magneticData = magneticData;
		this.howToEstimateUpDirection = howToEstimateUpDirection;
		this.howToEstimateNorthDirection = howToEstimateNorthDirection;
		this.howToComputeOrientation = howToComputeOrientation;
		this.dampingRate_acc = dampingRate_acc;
		this.dampingRate_mag = dampingRate_mag;
		upDirectionEstimate = Vector3D.PLUS_K;
		northDirectionEstimate = new Vector3D(0, 0.001, 0);
		upDirectionVector = new Vector3D(0, 0, 0.001);
		
		bufferSize = magneticData.getSize();
	}
	

	public Rotation computeWorld2PhoneRotation() {
		switch (howToEstimateUpDirection) {
		case DirectionFromLowPass:
			computeUpDirectionFromLowPass();
			break;
		case DirectionFromBuffer:
			computeUpDirectionFromBuffer();
			break;
		case DirectionFromLatestMeasurement:
			computeUpDirectionFromLatestMeasurement();
			break;
		default:
			break;
		}
		
		switch (howToEstimateNorthDirection) {
		case DirectionFromLowPass:
			computeNorthDirectionFromLowPass();
			break;
		case DirectionFromBuffer:
			computeNorthDirectionFromBuffer();
			break;
		case DirectionFromLatestMeasurement:
			computeNorthDirectionFromLatestMeasurement();
			break;
		default:
			break;
		}
		
		Rotation world2phone;
		switch (howToComputeOrientation) {
		case GetOwnRotation:
			world2phone = computeWorld2PhoneRotationOwn();
			break;
		case GetRotationFromAndroid:
			world2phone = computeWorld2PhoneRotationFromAndroid();
			break;
		default:
			world2phone = Rotation.IDENTITY;
			break;
		}
		return world2phone;		
		
	}

	private void computeUpDirectionFromLowPass() {
		upDirectionVector = (upDirectionVector.scalarMultiply(1 - dampingRate_acc))
				.add(dampingRate_acc, accelerationData.get(0));
		if (upDirectionEstimate.getNorm() > 0) {
			upDirectionEstimate = upDirectionVector.normalize();
		} else {
			upDirectionEstimate = new Vector3D(0, 0, 1);
		}

	}

	private void computeUpDirectionFromLatestMeasurement() {
		upDirectionEstimate = accelerationData.get(0);
		if (upDirectionEstimate.getNorm() > 0) {
			upDirectionEstimate = upDirectionEstimate.normalize();
		} else {
			upDirectionEstimate = new Vector3D(0, 0, 1);
		}
	}

	private void computeUpDirectionFromBuffer() {
		upDirectionEstimate = accelerationData.getSum().scalarMultiply(
				1.0 / bufferSize);
		if (upDirectionEstimate.getNorm() > 0) {
			upDirectionEstimate = upDirectionEstimate.normalize();
		} else {
			upDirectionEstimate = new Vector3D(0, 0, 1);
		}
	}

	private void computeNorthDirectionFromLowPass() {
		northDirectionEstimate = (northDirectionEstimate
				.scalarMultiply(1 - dampingRate_mag)).add(dampingRate_mag,
				magneticData.get(0));
	}

	private void computeNorthDirectionFromLatestMeasurement() {
		northDirectionEstimate = magneticData.get(0); // use only the
														// latest
														// measurement
														// Vector3D
														// northDirectionEstimate
														// =
														// magneticData.getSum();
		if (northDirectionEstimate.getNorm() == 0) {
			northDirectionEstimate = new Vector3D(0, 1, 0);
		}

	}

	private void computeNorthDirectionFromBuffer() {
		northDirectionEstimate = magneticData.getSum().scalarMultiply(
				1.0 / bufferSize);
		if (northDirectionEstimate.getNorm() == 0) {
			northDirectionEstimate = new Vector3D(0, 1, 0);
		}
	}

	private Rotation computeWorld2PhoneRotationOwn() {
		Vector3D east = Vector3D.crossProduct(northDirectionEstimate,
				upDirectionEstimate).normalize();
		return new Rotation(Vector3D.PLUS_I, Vector3D.PLUS_K,
				east, upDirectionEstimate);
	}

	private Rotation computeWorld2PhoneRotationFromAndroid() {
		Vector3D accelerationVector = upDirectionEstimate.scalarMultiply(9.81);
		Vector3D magneticVector = northDirectionEstimate;

		float[] R = new float[9];
		float[] I = new float[9];
		float[] gravity = { (float) accelerationVector.getX(),
				(float) accelerationVector.getY(),
				(float) accelerationVector.getZ() };
		float[] geomagnetic = { (float) magneticVector.getX(),
				(float) magneticVector.getY(), (float) magneticVector.getZ() };
		SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
		double[][] m = new double[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m[j][i] = R[3 * i + j]; // strange, but this seems to be correct
										// either coloumn major instead of row,
										// or phone2word instead of world2phone
			}
		}
		Rotation rotationWorld2phone = Rotation.IDENTITY;
		try {
			rotationWorld2phone = new Rotation(m, 0.01);
		} catch (NotARotationMatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rotationWorld2phone;
	}


}
