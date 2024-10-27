/*
 * 
 * this application is fullfilled thru GPSApplication.java in oppgave 5
 * therefore we skip it alltogether
 *
 */

package no.hvl.dat100ptc.oppgave5;

import no.hvl.dat100ptc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.function.DoubleConsumer;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataConverter;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

 
public class GPSElevationGraphRenderer
{
	private static int MARGIN = 4;

	private GPSComputer gpscomputer;	
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	public boolean resampleData;
	
	public GPSElevationGraphRenderer(GPSComputer computer)
	{
		gpscomputer = computer;
		
		init();
	}
	
	public void init() 
	{
		var gpspoints = gpscomputer.getGPSPoints();		
		var dataValues = gpscomputer.getElevationValues();

		if(GPSUI.RESAMPLE_TIME_SERIES) {
			dataValues = GPSPointDataValueResampler.getResampledValues(gpspoints, dataValues);
		}

		// create graph data for graph rendering		
		graphData = new DoubleArrayGraphRenderer.Data();
		graphData.values = dataValues;
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
		if(graphData.numValues == 0) { return; }

		// render graph
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
						ctx.setColor(GraphicsUtils.lerpColorRGBA(normaValueDelta * 30, GPSUI.Route.routeSecondDownhillColor, GPSUI.Route.routeDownhillColor));
					}else {
						ctx.setColor(GraphicsUtils.lerpColorRGBA(normaValueDelta * 30, GPSUI.Route.routeSecondUphillColor, GPSUI.Route.routeUphillColor));
					}
				}
			};
		}
		
		DoubleArrayGraphRenderer.render(ctx, R, graphData, beforeRenderColumn);
		
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
}