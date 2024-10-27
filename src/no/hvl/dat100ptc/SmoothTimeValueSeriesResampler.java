package no.hvl.dat100ptc;

import no.hvl.dat100ptc.IrregularTimeValueSeriesResampler.DataPoint;

public class SmoothTimeValueSeriesResampler extends IrregularTimeValueSeriesResampler{	
	protected SmoothTimeValueSeriesResampler() {
		//super();
	}
	
	// produces a smooth but incorrect result as it may miss peak values
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

			// iterate over points while time is less than targettime
			while((currentIndex < num_data_points - 1) && (data[currentIndex + 1].time < targetTime)) {
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
		}

		return resampled;
	}
}