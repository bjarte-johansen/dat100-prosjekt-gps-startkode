package no.hvl.dat100ptc;

import no.hvl.dat100ptc.IrregularTimeValueSeriesResampler.DataPoint;

import java.lang.reflect.Array;
//import java.lang.SuppressWarnings;

class ArrayUtils {
    public static <T> T[] createArray(Class<T> clazz, int size) {
        //@SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(clazz, size);
        
        try {
            for (int i = 0; i < size; i++) {
                array[i] = clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate elements", e);
        }
        
        return array;
    }
}

public class IrregularTimeValueSeriesResampler{
	interface DataCallback{
		boolean accept(DataPoint p, double target_time);
	}
	
	public static class DataPoint{
		public double time;
		public double value;
		
		public static DataPoint[] createArray(int n) {
			return ArrayUtils.createArray(DataPoint.class, n);
		}
	}
	
	protected IrregularTimeValueSeriesResampler() {}

	static double interpolate(double x0, double y0, double x1, double y1, double pos) 
	{
		double time_range = x1 - x0;
		double value_range = y1 - y0;
		
		if(Math.abs(time_range) < 1e-10) {
			throw new IllegalArgumentException("x1 - x0 must be greater than 0");
		}
		
		return y0 + ((pos - x0) / time_range) * value_range; 
	}
}

// this class should have been called SmoothTimeValueSeriesResampler but we didnt bother

/*
class MaxTimeValueSeriesResampler extends IrregularTimeValueSeriesResampler{
	
	private MaxTimeValueSeriesResampler() {}

	// produces a potentioal non-smooth but correct result as it takes peak values into
	// should be broken out to class, but we got bored
	
	public static double[] resample(DataPoint[] data, int new_length) {
		if(new_length == 0) {
			throw new IllegalArgumentException("output length must be greater than 0");
		}
		
		if(data.length == 0) {
			throw new IllegalArgumentException("input length must be greater than 0");
		}
		
		int num_data_points = data.length;			
		double startTime = data[0].time;
		double endTime = data[num_data_points - 1].time;
		double interval = (endTime - startTime) / (new_length - 1);
		int currentIndex = 0;

		if(Math.abs(endTime - startTime) < 1e-9)  {
			throw new IllegalArgumentException("time-range must be greater than 0");
		}
		
		// allocate memory
		double[] resampled = new double[new_length];	
		
		// find remaining resampled values
		for(int i=0; i<new_length; i++)	{
			// compute target time
			double targetTime = startTime + interval * i;
			
			// get segment value
			double maxValue = data[currentIndex].value;
			
			// iterate over points while time is less than targettime, collect max value
			while((currentIndex < num_data_points - 1) && (data[currentIndex + 1].time < targetTime)) {
				maxValue = Math.max(maxValue, data[currentIndex + 1].value);
				currentIndex++;
			}

			// resample value
			if(currentIndex + 1 >= num_data_points) {
				resampled[i] = data[num_data_points - 1].value;
			} else {
				resampled[i] = interpolate(
					data[currentIndex].time, 
					data[currentIndex].value, 
					data[currentIndex + 1].time, 
					data[currentIndex + 1].value, 
					targetTime
					);
			}

			// take max of resampled and found max value
			resampled[i] = Math.max(maxValue, resampled[i]);
		}
		
		return resampled;
	}	
}
*/