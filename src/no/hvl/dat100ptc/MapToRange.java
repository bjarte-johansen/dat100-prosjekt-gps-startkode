package no.hvl.dat100ptc;

public class MapToRange{
	public static double mapToRange(double value, double minInput, double maxInput, double minOutput, double maxOutput) {
		if(maxInput == minInput) {
			throw new IllegalArgumentException("input range must be non-zero");
		}
		
		double inputRange = maxInput - minInput;
		double outputRange = maxOutput - minOutput;
		
		return minOutput + ((value - minInput) / inputRange) * outputRange;
	}
}