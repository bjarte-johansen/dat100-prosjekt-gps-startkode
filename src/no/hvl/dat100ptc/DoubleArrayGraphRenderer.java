package no.hvl.dat100ptc;

import java.awt.Graphics2D;
import java.util.function.DoubleConsumer;

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
		return LinearInterpolation.interpolate(values, values.length, pos);
	}
	
	// get interpolated value at pos, note that pos is [0.0 .. numValues - 1]
	public double getValueAtIndexedPos(double pos) {
		if(values.length == 0) {
			throw new RuntimeException("array must have more than 0 elements");
		}
		return LinearInterpolation.interpolate(values, values.length, pos / values.length);
	}
}

public class DoubleArrayGraphRenderer{
	// import nested class
	public static class Data extends DoubleArrayGraphData{};
	
	// hide constructor
	private DoubleArrayGraphRenderer() {}
	
	// get vertical scale
	public static double getVerticalScale(Data graphData, IntRectangle bounds) {
		return graphData.getNormalizationFactor() * bounds.getHeight() * 0.9;
	}
	
	// get vertical value
	public static double getValueAt(double pos, Data graphData) {
		return graphData.getValueAtNormalizedPos(pos);
	}
	
	// render
	public static void render(Graphics2D ctx, IntRectangle bounds, Data graphData) {
		render(ctx, bounds, graphData, null);
	}
	public static void render(Graphics2D ctx, IntRectangle bounds, Data graphData, DoubleConsumer beforeDrawLine) {
		double val;		
		double pos = 0.0;
		double delta = 1.0 / bounds.width;
		double verticalScaleFactor = getVerticalScale(graphData, bounds);
			
		for(int i=0; i<bounds.width; i++) {
			val = getValueAt(pos, graphData) * verticalScaleFactor;
			
			int x = bounds.getMinX() + i;
			int y = bounds.getMaxY() - (int) val;
			
			if(beforeDrawLine != null) {
				beforeDrawLine.accept(pos);
			}
				
			ctx.drawLine(x, bounds.getMaxY(), x, y);
			
			pos += delta;			
		}
	}
}
