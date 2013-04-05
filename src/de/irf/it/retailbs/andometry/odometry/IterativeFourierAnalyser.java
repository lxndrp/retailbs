package de.irf.it.retailbs.andometry.odometry;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.util.MathUtils;

public class IterativeFourierAnalyser {
	
	private int bufferSize;
	private int frequenciesToAnalyse;
	
	private int positionInBuffer;
	
	private Complex buffers[][];
	private Complex bufferSums[];
	private double debug_rawValueBuffer[];
	
	public IterativeFourierAnalyser(int bufferSize, int frequenciesToAnalyse) {
		init(bufferSize, frequenciesToAnalyse);
	}
	
	public void init(int bufferSize_, int frequenciesToAnalyse){
		this.bufferSize = MathUtils.pow(2, (int)MathUtils.round( MathUtils.log(2, bufferSize_),0));
		this.frequenciesToAnalyse = frequenciesToAnalyse;
		positionInBuffer = 0;
		buffers = new Complex[frequenciesToAnalyse][bufferSize];
		//Arrays.fill(buffers, Complex.ZERO); 
		bufferSums = new Complex[frequenciesToAnalyse];
		//Arrays.fill(bufferSums, Complex.ZERO); 
		for (int i=0; i<frequenciesToAnalyse; i++){
			bufferSums[i] = Complex.ZERO;
			for (int j=0; j<bufferSize; j++){
				buffers[i][j] = Complex.ZERO;
			}
		}
		
		debug_rawValueBuffer = new double[bufferSize];
	}
	
	public Complex[] getFrequencyResponses(){
		return bufferSums;
	}
	
	public double[] getFrequencies(double samplingFrequency){
		double [] frequencies = new double[frequenciesToAnalyse];
		for (int i=0; i<frequenciesToAnalyse; i++){
			frequencies[i] = getFrequencyAt(i, samplingFrequency);
		}
		return frequencies;
	}
	
	public double getFrequencyAt(int index, double samplingFrequency){
		return index * samplingFrequency / bufferSize;
	}
	
	public double[] getMagnitudes(){
		double[] magnitudes = new double[bufferSums.length];
		for (int i=0; i<bufferSums.length; i++){
			magnitudes[i] = bufferSums[i].abs()*2/bufferSize;
		}
		return magnitudes;
	}
	
	public void submitNewMeasurement(double x){
		debug_rawValueBuffer[positionInBuffer] = x;
		for (int frequencyIndex=0; frequencyIndex<frequenciesToAnalyse; frequencyIndex++){
			bufferSums[frequencyIndex] = bufferSums[frequencyIndex].subtract(buffers[frequencyIndex][positionInBuffer]);
			// one term in the Fourier sum: x * e^(-i*2*pi*k/N * n ) 
			// where N is the bufferSize
			// 		 n is the positionInBuffer
			// 		 k is the frequencyIndex
			buffers[frequencyIndex][positionInBuffer] = Complex.I.multiply(-2*Math.PI*frequencyIndex*positionInBuffer/bufferSize).exp().multiply(x);			
			bufferSums[frequencyIndex] = bufferSums[frequencyIndex].add(buffers[frequencyIndex][positionInBuffer]);			
		}
		positionInBuffer++;
		positionInBuffer = positionInBuffer % bufferSize;
	}
	
	public int getBiggestResponse(){
		// find biggest response
		double biggestAmplitude = -1;
		int index = 0;
		for (int i=0; i<frequenciesToAnalyse; i++){
			if (bufferSums[i].abs() > biggestAmplitude){
				biggestAmplitude = bufferSums[i].abs();
				index = i;
			}
		}
		return index;
	}
	
	public double[] getResponseDataAtFrequencyIndex(int index, double samplingFrequency){
		double[] result = new double[3]; // frequency, amplitude, phase
		result[0] = getFrequencyAt(index, samplingFrequency);
		result[1] = bufferSums[index].abs()*2/bufferSize;
		result[2] = bufferSums[index].getArgument();
		return result;
	}
	
	public int getBiggestResponseIgnoringOffset(){
		// find biggest response
		// (ignoring the zero-frequency corresponding to a static offset)
		double biggestAmplitude = -1;
		int index = 0;
		for (int i=1; i<frequenciesToAnalyse; i++){
			if (bufferSums[i].abs() > biggestAmplitude){
				biggestAmplitude = bufferSums[i].abs();
				index = i;
			}
		}
		return index;
	}
	
	public double[] debug_getRawData(){
		double[] result = new double[debug_rawValueBuffer.length];
		for (int i = 0; i < debug_rawValueBuffer.length; i++){
			int index = (positionInBuffer + debug_rawValueBuffer.length - 1 - i)%debug_rawValueBuffer.length; // ensure positive values
			result[i] = debug_rawValueBuffer[index];
		}
		return result;
	}
	

}
