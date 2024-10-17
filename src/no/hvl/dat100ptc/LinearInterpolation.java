package no.hvl.dat100ptc;

public class LinearInterpolation{
	private LinearInterpolation() {}

	/**
	 * LLM generated JavaDoc
	 *  
	 * Linearly interpolates between two values based on a given fractional position.
	 *
	 * @param v0 the starting value (corresponding to position 0)
	 * @param v1 the ending value (corresponding to position 1)
	 * @param pos the fractional position between v0 and v1, where 0 represents v0 and 1 represents v1
	 * @return the interpolated value at the specified fractional position
	 *
	 * <p>This method calculates a value between v0 and v1 by linearly interpolating based
	 * on the fractional position specified by pos. A pos value of 0 returns v0, while a pos
	 * value of 1 returns v1. Values of pos between 0 and 1 return a corresponding intermediate
	 * value between v0 and v1.</p>
	 */	
	
	public static double interpolate(double v0, double v1, double pos)
	{
		return v0 + (v1 - v0) * pos;
	}

	
	
	/**
	 * LLM generated JavaDoc
	 *  
	 * Performs linear interpolation to find a y-value corresponding to a specified x-value
	 * within the range defined by two known points.
	 *
	 * @param x0 the x-coordinate of the first point
	 * @param y0 the y-coordinate of the first point
	 * @param x1 the x-coordinate of the second point
	 * @param y1 the y-coordinate of the second point
	 * @param pos the x-value for which to interpolate the corresponding y-value
	 * @return the interpolated y-value at the specified x-value (pos)
	 *
	 * <p>This method calculates the y-value by linearly interpolating between the two
	 * known points (x0, y0) and (x1, y1). A small epsilon (1e-10) is added to the denominator 
	 * to avoid division by zero if x0 and x1 are nearly equal.</p>
	 */
	
	public static double interpolate(double x0, double y0, double x1, double y1, double pos)
	{
		double delta = x1 - x0 + 1e-10; // Small epsilon to avoid exact zero division
		return (pos - x0) / delta * (y1 - y0) + y0;
	}

	
	
	/**
	 * LLM generated JavaDoc
	 * 
	 * Interpolates a value within an array of points based on a fractional position.
	 *
	 * @param points the array of points to interpolate between
	 * @param n the total number of points in the array
	 * @param pos the fractional position within the range [0, 1], where 0 represents the
	 *            start and 1 represents the end of the array
	 * @return the interpolated value at the specified position
	 *
	 * <p>This method calculates the real position within the array by scaling the given
	 * fractional position by the number of points. It then performs linear interpolation
	 * between two adjacent points in the array. If the calculated position is beyond the
	 * end of the array, the function returns the last value in the array.</p>
	 */
	
	public static double interpolate(double[] points, int n, double pos)
	{
		double realPos = pos * n;

		int intPos = (int) realPos;
		double fracPos = realPos - intPos;
		double v0;
		double v1;

		if(intPos + 1 < n) 
		{
			v0 = points[intPos];
			v1 = points[intPos + 1];
		} 
		else 
		{
			v0 = points[(intPos < n) ? intPos : (n - 1)];
			v1 = points[(intPos + 1 < n) ? (intPos + 1) : (n - 1)];
		}

		return v0 + (v1 - v0) * fracPos;
	}
}