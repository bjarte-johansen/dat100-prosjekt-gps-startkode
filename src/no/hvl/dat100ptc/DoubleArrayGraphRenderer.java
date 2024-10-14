package no.hvl.dat100ptc;

import java.awt.Graphics2D;

import no.hvl.dat100ptc.DoubleArrayGraphRenderer.Data;

class DoubleArrayGraphData{
	public double[] values;
	public int numValues;
	public double min;
	public double max;
	
	public double getNormalizationFactor() {
		if(Math.abs(max) < 1e-10) {
			throw new RuntimeException("max must be greater than 1e-10");
		}
		
		return 1.0 / max;
	}
	
	// get interpolated value at pos, note that pos is [0.0 .. 1.0]
	public double getValueAtNormalizedPos(double pos) {		
		return LinearInterpolation.interpolate(pos, values, values.length);
	}
	public double getValueAtIndexedPos(double pos) {
		if(values.length == 0) {
			throw new RuntimeException("array must have more than 0 elements");
		}
		return LinearInterpolation.interpolate(pos / values.length, values, values.length);
	}
}

public class DoubleArrayGraphRenderer{
	// import nested class
	public static class Data extends DoubleArrayGraphData{};
	
	// hide constructor
	private DoubleArrayGraphRenderer() {}
	
	// get vertical scale
	public static double getGraphVerticalScale(Data graphData, IntRectangle bounds) {
		return graphData.getNormalizationFactor() * bounds.getHeight() * 0.9;
	}
	
	// get vertical value
	public static double getGraphFloatValueAt(double pos, Data graphData) {
		return graphData.getValueAtNormalizedPos(pos);
	}
	
	// render
	public static void render(Graphics2D ctx, IntRectangle bounds, Data graphData) {
		double val;		
		double pos = 0.0;
		double delta = 1.0 / bounds.width;
		double verticalScaleFactor = getGraphVerticalScale(graphData, bounds);
			
		for(int i=0; i<bounds.width; i++) {
			val = getGraphFloatValueAt(pos, graphData) * verticalScaleFactor;
			
			int x = bounds.getMinX() + i;
			int y = bounds.getMaxY() - (int) val;
				
			ctx.drawLine(x, bounds.getMaxY(), x, y);
			
			pos += delta;			
		}
	}
}
