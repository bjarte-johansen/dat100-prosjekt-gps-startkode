package no.hvl.dat100ptc;

import java.util.Arrays;

public class DoubleArray{
	private double[] arr;
	
	public DoubleArray() {
		arr = new double[0];
	}
	
	public DoubleArray(int n) {
		arr = new double[n];
	}
	
	public DoubleArray(double[] data) {
		arr = data;
	}

	public static DoubleArray of() {
		return new DoubleArray();
	}		
	public static DoubleArray of(int n) {
		return new DoubleArray(n);
	}	
	public static DoubleArray of(double[] data) {
		return new DoubleArray(data);
	}
	
	public double getValue(int index) {
		return arr[index];
	}
	public void setValue(int index, double val) {
		arr[index] = val;
	}
	
	public int size() {
		return arr.length;
	}
	/*
	public double[] getArrayReference() {
		return arr;
	}
	*/
	public double[] toArray() {
		return Arrays.copyOf(arr, arr.length);
	}

	public double sum() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		double sum = 0.0;
		int n = arr.length;
		
		for(int i = 0; i < n; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	public double[] minMax() {
		if(arr == null || arr.length == 0) {
			throw new RuntimeException("array must be non-null and non-empty");
		}
		
		double e = arr[0];
		double minVal = e;
		double maxVal = e;
		
		int n = arr.length;
		for(int i=0; i<n; i++) {
			e = arr[i];
			
			if(e < minVal)
				minVal = e;
			if(e > maxVal)
				maxVal = e;
		}		
		
		return (new double[] {minVal, maxVal});		
	}
	
	public double min() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		double found = arr[0];
		int n = arr.length;
		
		for(int i = 1; i < n; i++) {
			if(arr[i] < found) {
				found = arr[i];
			}
		}
		return found;
	}	
	
	public double max() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		double found = arr[0];
		int n = arr.length;
		
		for(int i = 1; i < n; i++) {
			if(arr[i] > found) {
				found = arr[i];
			}
		}
		return found;
	}
	
	public double average() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		return sum() / size();
	}	
}