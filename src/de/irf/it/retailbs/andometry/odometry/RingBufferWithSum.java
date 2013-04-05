package de.irf.it.retailbs.andometry.odometry;

import org.apache.commons.math.geometry.Vector3D;
import android.util.Pair;

public class RingBufferWithSum {
	
	public RingBufferWithSum(int size) {
		this.size = size;
		buffer = new Vector3D[size];
		timeBuffer = new double[size];
		for (int i = 0; i<size; i++){
			buffer[i] = new Vector3D(0,0,0);
			timeBuffer[i] = 0;
		}
		sum = new Vector3D(0,0,0);
		currentIndex = 0;
	}
	
	public void put(double timeStamp, Vector3D value){
		currentIndex = (currentIndex + 1)%size;
		Vector3D temp = buffer[currentIndex];
		sum = sum.subtract(temp);
		sum = sum.add(value);
		buffer[currentIndex] = value;
		timeBuffer[currentIndex] = timeStamp;
	}
	
	public Vector3D get(int i){
		int index = (currentIndex+size-i)%size;
		return buffer[index];
	}
	
	public double getTimeAt(int i){
		int index = (currentIndex+size-i)%size;
		return timeBuffer[index];
	}
	
	public Vector3D getSum() {
		return sum;
	}
	
	public double getSamplingFrequency(){
		int indexOfOldest = (currentIndex+1)%size;
		double timeDiff = timeBuffer[currentIndex] - timeBuffer[indexOfOldest];
		if (timeDiff == 0){
			return 0.00001;
		}
		return size/timeDiff;
	}

	
	public Pair<double[], Vector3D[]> getBufferLatestLast() {
		Vector3D[] tempBuffer = new Vector3D[size];
		double[] timeStamps = new double[size];
		for (int i=0; i<size; i++){
			tempBuffer[i] = get(size-1-i);
			timeStamps[i] = getTimeAt(size-1-i);
		}
		return new Pair<double[], Vector3D[]>(timeStamps, tempBuffer);
	}
	
	public Pair<double[], Vector3D[]> getBufferLatestFirst() {
		Vector3D[] tempBuffer = new Vector3D[size];
		double[] timeStamps = new double[size];
		for (int i=0; i<size; i++){
			tempBuffer[i] = get(i);
			timeStamps[i] = getTimeAt(i);
		}
		return new Pair<double[], Vector3D[]>(timeStamps, tempBuffer);
	}
	
	public int getSize(){
		return size;
	}


	private int size;
	private Vector3D[] buffer;
	private double[] timeBuffer;
	private Vector3D sum;


	private int currentIndex;

}
