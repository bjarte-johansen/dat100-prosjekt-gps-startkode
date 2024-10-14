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


/*
 * 
 */

class GPSSpeedGraphRenderer 
{
	private static int MARGIN = 4;

	//
	private GPSComputer gpscomputer;	
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	public boolean resampleData = false;
	
	public boolean showSpeedAsColor = true;	
	
	public GPSSpeedGraphRenderer() 
	{
		gpscomputer = new GPSComputer(App.gpsFilename);

		init();
	}
	
	public void init() 
	{
		var gpspoints = gpscomputer.getGPSPoints();		
		var dataValues = gpscomputer.getSpeedValues();
		
		// create time-value series
		var data = new IrregularTimeValueSeriesResampler.Data(dataValues.length);
		for(int i=0; i<data.length; i++) {
			data.times[i] = gpspoints[i].getTime();
			data.values[i] = (i < dataValues.length) ? dataValues[i] : dataValues[dataValues.length - 1];
		}
		
		// resample timeseries to regularly spaced values		
		double[] resampled = IrregularTimeValueSeriesResampler.resample(data, 600);
		
		/*
		data.items[0] = (new double[] {0, 0});
		data.items[1] = (new double[] {4, 1});
		data.items[2] = (new double[] {5, 2});
		data.items[3] = (new double[] {7, 3});
		data.items[4] = (new double[] {15, 4});
		data.items[5] = (new double[] {20, 5});
		
		var result = ResampleIrregularSamples.resample(data, 21);
		for(int i=0; i<result.length; i++) {
			System.out.printf("%2d -> %.2f\n", i, result[i]);
		}
		*/
		
		// create graph data for graph rendering
		graphData = new DoubleArrayGraphRenderer.Data();
		graphData.values = resampleData ? resampled : dataValues;
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
		DoubleArrayGraphRenderer.render(ctx, R, graphData);
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