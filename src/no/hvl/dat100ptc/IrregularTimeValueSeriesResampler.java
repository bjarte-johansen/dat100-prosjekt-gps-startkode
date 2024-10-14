package no.hvl.dat100ptc;

import no.hvl.dat100ptc.IrregularTimeValueSeriesResampler.Data;

public class IrregularTimeValueSeriesResampler {
	public interface DataCallback{
		boolean accept(double time, double value, double target_time);
	}
	
	public static class Data
	{
		public double[] times;
		public double[] values;
		public int length;
		
		public Data(int n) 
		{
			resize(n);
		}
		
		public void resize(int n) 
		{
			times = new double[n];
			values = new double[n];
			length = n;
		}
	}
	
	private IrregularTimeValueSeriesResampler() {}

	static double interpolate(double x0, double y0, double x1, double y1, double pos) 
	{
		double time_range = x1 - x0;
		double value_range = y1 - y0;
		
		if(Math.abs(time_range) < 1e-10) {
			throw new IllegalArgumentException("x1 - x0 must be greater than 0");
		}
		
		return y0 + ((pos - x0) / time_range) * value_range; 
	}
	
	/*
	// return index to element before first that satifies criteria, or index - 1 to item that does not satisfy
	// - if first element satisfies 
	// - if not found, return length - 1
	public static int upper_bound(Data data, int startIndex, double target_time, DataCallback fnCompare) {
		for(int i=startIndex; i<data.length; i++) {
			if(!fnCompare.accept(data.times[i], data.values[i], target_time)) {
				return i - 1;
			}
		}
		
		return data.length - 1;
	}
	*/
	
	public static double[] resample(Data data, int new_length) 
	{
		/*
		if(new_length == 0) 
		{
			throw new IllegalArgumentException("output length must be greater than 0");
		}
		if(data.length == 0) 
		{
			throw new IllegalArgumentException("input length must be greater than 0");
		}
		*/
		
		double startTime = data.times[0];
		double endTime = data.times[data.length - 1];
		double interval = (endTime - startTime) / (new_length);	// debug, why -1?	
		double maxValue;
		double targetTime;
		int currentIndex = 0;		
		
		//System.out.printf("startTime: %.2f, endTime: %.2f, interval: %.2f\n", startTime, endTime, interval);
		
		if(Math.abs(endTime - startTime) < 1e-9) 
		{
			throw new IllegalArgumentException("time-range must be greater than 0");
		}		
		
		// allocate memory
		double[] resampled = new double[new_length];
	
		// find remaining resampled values
		for(int i=0; i<new_length; i++) 
		{
			// compute target time
			targetTime = startTime + interval * i;
			
			// find last segment
			maxValue = Double.MIN_VALUE;
			
			// find max value within now .. next
			while((currentIndex < data.length - 1) && (data.times[currentIndex + 1] < targetTime)) 
			{
				maxValue = Math.max(maxValue,  data.values[currentIndex + 1]);
				currentIndex++;
			}
				
			// resample value
			if(currentIndex == 0 || currentIndex + 1 >= data.length) 
			{
				resampled[i] = data.values[data.length - 1];			
			}			
			else 
			{
				double t0 = data.times[currentIndex];
				double t1 = data.times[currentIndex + 1];
				
				double v0 = data.values[currentIndex];
				double v1 = data.values[currentIndex + 1];
				
				resampled[i] = interpolate(t0, v0, t1, v1, targetTime);
			}
			
			// take max of resampled and found max value
			resampled[i] = Math.max(maxValue, resampled[i]);
		}
		
		return resampled;
	}
}