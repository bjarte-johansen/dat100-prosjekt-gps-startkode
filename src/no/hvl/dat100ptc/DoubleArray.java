package no.hvl.dat100ptc;

import java.util.Arrays;

public class DoubleArray{
	private double[] arr;
	
	//
	public DoubleArray() {
		arr = new double[0];
	}
	
	public DoubleArray(int n) {
		arr = new double[n];
	}
	
	public DoubleArray(double[] data) {
		arr = data;
	}
	
	public DoubleArray(double first, double last, double step) {
		int n = (int) Math.ceil((last - first) / step) + 1;
		arr = new double[n];
		
		double value = first;
		for(int i=0; i<n; i++) {
			arr[i] = value;
			value += step;
		}
	}

	// of constructors
	public static DoubleArray of() {
		return new DoubleArray();
	}		
	public static DoubleArray of(int n) {
		return new DoubleArray(n);
	}	
	public static DoubleArray of(double[] data) {
		return new DoubleArray(data);
	}
	public static DoubleArray of(double first, double last, double step) {
		return new DoubleArray(first, last, step);
	}
	
	// check tests, throw if fails
	protected void checkNonNull() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
	}	
	protected void checkNonNullNonEmpty() {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
	}
	protected void checkMinSize(int minSize) {
		if (arr.length < minSize) {
			throw new IllegalArgumentException("array must have at least " + minSize + " elements");
		}
	}
	
	// set/get value
	public double getValue(int index) {
		return arr[index];
	}
	public void setValue(int index, double val) {
		arr[index] = val;
	}
	
	// return size
	public int size() {
		return (arr == null) ? 0 : arr.length;
	}

	// get reference to storage
	public double[] getArrayReference() {
		return arr;
	}
	
	// get copy of array
	public double[] toArray() {
		checkNonNull();
		
		return Arrays.copyOf(arr, arr.length);
	}
	
	
	// get first / last value
	public double first() {
		checkNonNullNonEmpty();
		
		return arr[0];
	}
	
	public double last() {
		checkNonNullNonEmpty();
		
		return arr[arr.length - 1];
	}
	
	
	// return true if array is sorted
	// note that this does not check near-equal numbers
	public boolean isSorted(boolean ascending) {
		checkNonNull();
		
		int n = arr.length;
		if(ascending) {
			for(int i=1; i<n; i++) {
				if(arr[i - 1] > arr[i]) {
					return false;
				}
			}
		}else {
			for(int i=1; i<n; i++) {
				if(arr[i - 1] < arr[i]) {
					return false;
				}
			}
		}
		
		return true;
	}

	//
	public double sum() {
		checkNonNull();
		
		double sum = 0.0;
		int n = arr.length;
		
		for(int i = 0; i < n; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	public double[] minmax() {
		checkNonNullNonEmpty();
		
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
		checkNonNullNonEmpty();
		
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
		checkNonNullNonEmpty();
		
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
		checkNonNullNonEmpty();
		
		return sum() / size();
	}	
}

/*
// we extend doublearray just to get the check* and size methods
class DoubleArrayDeltaUtils extends DoubleArray{
	public DoubleArrayDeltaUtils(double[] arr) {
		super(arr);
	}
	
	// find max deltavalue
	public double maxdelta() {
		checkNonNull();
		checkMinSize(2);
	
		int n = size();
		double found = arr[1] - arr[0];   		
		double delta;		  
		double cur;
		double prev = arr[0];
		
        for(int i=1; i<n; i++) {
        	cur = arr[i];
        	delta = cur - prev;
        	
        	if(delta > found) {
        		found = delta; 
        	}
        	
        	prev = cur;	        	
        }
        
        return found;
	}
	
	public double mindelta(){
		checkNonNull();
		checkMinSize(2);		
	
		int n = size();
		double found = arr[1] - arr[0];   		
		double delta;		  
		double cur;
		double prev = arr[0];
		
        for(int i=1; i<n; i++) {
        	cur = arr[i];
        	delta = cur - prev;
        	
        	if(delta < found) {
        		found = delta; 
        	}
        	
        	prev = cur;	        	
        }
        
        return found;
	}
	
	public double[] minmaxdelta() {
	    checkNonNull();
		checkMinSize(2);	    
	
		int n = size();
		double delta = arr[1] - arr[0]
		double foundMin = delta;
		double foundMax = delta;  
		double cur;
		double prev = arr[0];
		
        for(int i=1; i<n; i++) {
        	cur = arr[i];
        	delta = cur - prev;
        	
        	if(delta > foundMax) {
        		foundMax = delta; 
        	}
        	
        	if(delta < foundMin) {
        		foundMin = delta; 
        	}
        	
        	prev = cur;	        	
        }
        
        return (new double[] {foundMin, foundMax});
	}
}
*/