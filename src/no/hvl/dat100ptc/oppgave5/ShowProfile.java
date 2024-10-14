package no.hvl.dat100ptc.oppgave5;

import no.hvl.dat100ptc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataConverter;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

 
class GPSElevationGraphRenderer
{
	private static int MARGIN = 4;

	private GPSComputer gpscomputer;	
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	public boolean resampleData;
	
	public GPSElevationGraphRenderer()
	{
		gpscomputer = new GPSComputer(App.gpsFilename);
		
		init();
	}
	
	public void init() 
	{
		var gpspoints = gpscomputer.getGPSPoints();		
		var dataValues = gpscomputer.getElevationValues();

		// create time-value series
		var data = new IrregularTimeValueSeriesResampler.Data(dataValues.length);
		for(int i=0; i<data.length; i++) {
			data.times[i] = gpspoints[i].getTime();
			data.values[i] = (i < dataValues.length) ? dataValues[i] : dataValues[dataValues.length - 1];
		}

		// resample timeseries to regularly spaced values
		double[] resampled = IrregularTimeValueSeriesResampler.resample(data, 600);

		// create graph data for graph rendering		
		graphData = new DoubleArrayGraphRenderer.Data();
		graphData.values = resampleData ? resampled : gpscomputer.getElevationValues();
		graphData.numValues = graphData.values.length;
		graphData.min = gpscomputer.getMinElevation();
		graphData.max = gpscomputer.getMaxElevation();

		// set progress indicators to null
		animatedProgressIndicators = null;		
	}
	
	public void setAnimatedProgressIndicators(App.GPSUIProgressIndicator[] indicators) 
	{
		animatedProgressIndicators = indicators;
	}
		
	public void render(Graphics2D ctx, int w, int h) 
	{
		// clear background
		ctx.setColor(GPSUI.Default.bgColor);
		ctx.fillRect(0, 0, w, h);
		
		// make graph rectangle
		R = new IntRectangle(MARGIN, MARGIN, w - MARGIN * 2, h - MARGIN * 2);

		// leave if less than 2 points
		if(graphData.numValues == 0) return;

		// render graph
		renderGraph(ctx);
		
		// render progress indicators
		renderAnimatedProgressIndicators(ctx);	
	}
	
	private void renderAnimatedProgressIndicators(Graphics2D ctx) 
	{
		if(animatedProgressIndicators == null) 
		{
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
	
	private void renderGraph(Graphics2D ctx) 
	{
		// draw graph
        ctx.setStroke(new BasicStroke(1));
        ctx.setColor(GPSUI.SpeedGraph.foregroundColor);
        
		//DoubleArrayGraphRenderer.render(ctx, R, graphData);
        
        IntRectangle bounds = R;
        
		double val;		
		double pos = 0.0;
		double delta = 1.0 / bounds.width;
		double verticalScaleFactor = DoubleArrayGraphRenderer.getGraphVerticalScale(graphData, bounds);
			
		Color currentColor;		
		double previousValue = DoubleArrayGraphRenderer.getGraphFloatValueAt(0.0, graphData) * verticalScaleFactor;
		
		for(int i=0; i<bounds.width; i++) {
			val = DoubleArrayGraphRenderer.getGraphFloatValueAt(pos, graphData) * verticalScaleFactor;
			
			{
				double diffValue = (val - previousValue);
				boolean uphill = (diffValue >= 0.0);
				double normalizedDiffValue = Math.abs(diffValue) / graphData.max;
				
				if(uphill) {
					currentColor = GraphicsUtils.lerpColorRGBA(normalizedDiffValue * 100, GPSUI.Route.routeSecondUphillColor, GPSUI.Route.routeUphillColor);
				}else {
					currentColor = GraphicsUtils.lerpColorRGBA(normalizedDiffValue * 100, GPSUI.Route.routeSecondDownhillColor, GPSUI.Route.routeDownhillColor);					
				}
				ctx.setColor(currentColor);
				
				previousValue = val;
			}		
			
			int x = bounds.getMinX() + i;
			int y = bounds.getMaxY() - (int) val;
				
			ctx.drawLine(x, bounds.getMaxY(), x, y);
			
			pos += delta;			
		}   
		
		System.out.println();
	}
}