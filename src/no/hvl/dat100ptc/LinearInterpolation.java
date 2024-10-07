package no.hvl.dat100ptc;

public class LinearInterpolation{
	private LinearInterpolation() {}
	
	public static double interpolate(double pos, double a, double b){				
		return a + (b - a) * pos;
	}
	
	public static double interpolate(double pos, double[] points, int numPoints){
		double realPos = pos * numPoints;
		
		int intPos = (int) realPos;
		double fracPos = realPos - intPos;
		
		double a = points[(intPos < numPoints) ? intPos : (numPoints - 1)];
		double b = points[(intPos + 1 < numPoints) ? (intPos + 1) : (numPoints - 1)];
		
		return a + (b - a) * fracPos;
	}
}