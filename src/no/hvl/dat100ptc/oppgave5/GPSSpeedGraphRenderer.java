/*
 * 
 * this application is fullfilled thru GPSApplication.java in oppgave 5
 * therefore we skip it alltogether
 *
 */

package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.*;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleConsumer;


/*
 * 
 */

class GPSPointDataValueResampler{
	public static double[] getResampledValues(GPSPoint[] gpspoints, double[] dataValues) {
		// create time-value series
		var data = SmoothTimeValueSeriesResampler.DataPoint.createArray(dataValues.length);
		
		for(int i=0; i<data.length; i++) {
			data[i].time = gpspoints[i].getTime();
			data[i].value = dataValues[i];
		}
		
		// resample timeseries to regularly spaced values		
		return SmoothTimeValueSeriesResampler.resample(data, 600);
	}	
}

class GPSSpeedGraphRenderer 
{
	private static int MARGIN = 4;

	//
	private GPSComputer gpscomputer;	
	
	// use our own graphData class
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	//
	public boolean resampleData = false;
	
	public GPSSpeedGraphRenderer(GPSComputer computer) 
	{
		gpscomputer = computer;

		init();
	}
	
	public void init()
	{
		var gpspoints = gpscomputer.getGPSPoints();		
		var dataValues = gpscomputer.getSpeedValues();
		
		if(GPSUI.RESAMPLE_TIME_SERIES) {
			dataValues = GPSPointDataValueResampler.getResampledValues(gpspoints, dataValues);
		}
		
		// create graph data for graph rendering
		graphData = new DoubleArrayGraphRenderer.Data();
		graphData.values = dataValues;
		graphData.numValues = graphData.values.length;
		graphData.min = gpscomputer.getMinSpeed();
		graphData.max = gpscomputer.getMaxSpeed();
		
		// set progress indicators to null
		animatedProgressIndicators = null;
	}
	
	public void setAnimatedProgressIndicators(App.GPSUIProgressIndicator[] indicators) 
	{
		animatedProgressIndicators = indicators;
	}
	
	private double getGraphVerticalScale() 
	{
		return (1.0 / graphData.max) * R.getHeight() * 0.9;
	}	
	
	public void render(Graphics2D ctx, int w, int h) 
	{
		// clear background
		ctx.setColor(GPSUI.Default.bgColor);
		ctx.fillRect(0, 0, w, h);
		
		// make bounding rectangle for graph
		R = new IntRectangle(MARGIN, MARGIN, w - MARGIN * 2, h - MARGIN * 2);

		// early exit if no points
		if(graphData.numValues == 0) return;

		// render graph
		renderGraph(ctx);
		
		// render average speed
		renderAverageSpeed(ctx);
		
		// render progress indicators
		renderAnimatedProgressIndicators(ctx);	
	}
	
	// render progress indicator	
	private void renderAnimatedProgressIndicators(Graphics2D ctx) 
	{
		if(animatedProgressIndicators == null) {
			return;
		}

		for(int i=0; i<animatedProgressIndicators.length; i++) 
		{
			var indicator = animatedProgressIndicators[i];
		
			ctx.setColor(indicator.getTrackingColor());
			ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.progressIndicatorSize));
			
			int x = (int)(indicator.getTrackingPosition() * R.getWidth());
			ctx.drawLine(x, R.getMinY(), x, R.getMaxY());
		}
	}	
	
	// render graph
	private void renderGraph(Graphics2D ctx) 
	{		
		// draw graph
        ctx.setStroke(new BasicStroke(1));
        ctx.setColor(GPSUI.SpeedGraph.foregroundColor);
        
        // [0]: 1 = up, 0 = down		
		int[] capturedDirection = {-1};

		DoubleConsumer beforeRenderColumn = null;
		if(GPSUI.Default.advancedColors) { 
			beforeRenderColumn = (pos) -> {
				// colorizing is buggy and leftover from debugging, its kinda nice though
				int intPos = (int)(pos * graphData.numValues);
				double v0 = graphData.safeGetValueAtOffset(intPos);
				double v1 = graphData.safeGetValueAtOffset(intPos + 1);
				
				double valueDelta = (v1 - v0);			
	
				// only change direction if we valueDelta <> 0.0
				if(Math.abs(valueDelta) > 1e-6 || capturedDirection[0] == -1) {
					capturedDirection[0] = (valueDelta >= 0.0) ? 1 : 0;
	
					// colorize acceleration/deceleration
					double normaValueDelta = Math.abs(valueDelta) / graphData.max;				
					if(capturedDirection[0] == 0) {
						ctx.setColor(GraphicsUtils.lerpColorRGBA(normaValueDelta * 20, GPSUI.Route.routeSecondDownhillColor, GPSUI.Route.routeDownhillColor));
					}else {
						ctx.setColor(GraphicsUtils.lerpColorRGBA(normaValueDelta * 20, GPSUI.Route.routeSecondUphillColor, GPSUI.Route.routeUphillColor));
					}
				}
			};
		}      
               
		DoubleArrayGraphRenderer.render(ctx, R, graphData, beforeRenderColumn);
	}
	
	// render average speed indicator
	private void renderAverageSpeed(Graphics2D ctx) 
	{		
		ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.averageSpeedIndicatorSize));
		ctx.setColor(GPSUI.SpeedGraph.averageSpeedIndicatorColor);
		
		double val = gpscomputer.getAverageSpeed() * getGraphVerticalScale();
		int y = R.getMaxY() - (int)(val);		
		ctx.drawLine(R.getMinX(), y, R.getMaxX(), y);	
	}
}