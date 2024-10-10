package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;
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

class ResampleIrregularSamples{
	public static class Data{
		double[][] items;
		int length;
		
		public Data(int n) {
			resize(n);
		}
		
		public void resize(int n) {
			items = new double[n][2];
			length = n;
		}
	}
	
	static double interpolate(double x0, double y0, double x1, double y1, double pos) {
		double time_range = x1 - x0;
		double value_range = y1 - y0;
		
		if(Math.abs(time_range) < 1e-10) {
			throw new IllegalArgumentException("x1 - x0 must be greater than 0");
		}
		
		return y0 + ((pos - x0) / time_range) * value_range; 
	}
	
	static double[] resample(Data data, int new_length) {
		if(new_length == 0) {
			throw new IllegalArgumentException("output length must be greater than 0");
		}
		if(data.length == 0) {
			throw new IllegalArgumentException("input length must be greater than 0");
		}
		
		int currentIndex = 0;
		double startTime = data.items[0][0];
		double endTime = data.items[data.length - 1][0];
		double interval = (endTime - startTime) / (new_length - 1);		
		double nextTime = startTime;
		double maxValue;
		
		//System.out.printf("startTime: %.2f, endTime: %.2f, interval: %.2f\n", startTime, endTime, interval);
		
		if(Math.abs(endTime - startTime) < 1e-9) {
			throw new IllegalArgumentException("time-range must be greater than 0");
		}		
		
		// allocate memory
		double[] resampled = new double[new_length];
	
		// find remaining resampled values
		for(int i=0; i<new_length; i++) {					
			// find last segment
			maxValue = Double.MIN_VALUE;
			
			// find max value within now .. next
			while(currentIndex + 1 < data.length && data.items[currentIndex + 1][0] < nextTime) {
				if(data.items[currentIndex + 1][1] > maxValue) {
					maxValue = data.items[currentIndex + 1][1];
				}
				currentIndex++;
			}
				
			// resample value
			if(currentIndex + 1 >= data.length) {
				resampled[i] = data.items[currentIndex][1];
			}else {
				double[] p1 = data.items[currentIndex];
				double[] p2 = data.items[currentIndex + 1];
				
				resampled[i] = interpolate(p1[0], p1[1], p2[0], p2[1], nextTime);
			}
			
			// take max of resampled and found max value
			resampled[i] = Math.max(resampled[i], maxValue);
			
			// advance
			nextTime += interval;
		}
		
		return resampled;
	}
}

class GPSSpeedGraphRenderer {
	private static int MARGIN = 4;

	//
	private GPSComputer gpscomputer;	
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	public boolean resampleData = false;
	
	public GPSSpeedGraphRenderer() {
		gpscomputer = new GPSComputer(App.gpsFilename);

		init();
	}
	
	public void init() {
		var gpspoints = gpscomputer.getGPSPoints();		
		var speedValues = gpscomputer.getSpeedValues();
		var data = new ResampleIrregularSamples.Data(gpscomputer.getGPSPoints().length);

		for(int i=0; i<data.length; i++) {
			data.items[i][0] = gpspoints[i].getTime();
			data.items[i][1] = (i < speedValues.length) ? speedValues[i] : speedValues[speedValues.length - 1];
		}
		
		double[] resampled = ResampleIrregularSamples.resample(data, speedValues.length);
		
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
		graphData.values = resampleData ? resampled : speedValues;
		graphData.numValues = graphData.values.length;
		graphData.min = gpscomputer.getMinSpeed();
		graphData.max = gpscomputer.getMaxSpeed();
		
		// set progress indicators to null
		animatedProgressIndicators = null;
	}
	
	
	public void setAnimatedProgressIndicators(App.GPSUIProgressIndicator[] indicators) {
		animatedProgressIndicators = indicators;
	}
	
	private double getGraphVerticalScale() {
		return (1.0 / graphData.max) * R.getHeight() * 0.9;
	}	
	
	public void render(Graphics2D ctx, int w, int h) {
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
	private void renderAnimatedProgressIndicators(Graphics2D ctx) {
		if(animatedProgressIndicators == null) {
			return;
		}
			
		for(int i=0; i<animatedProgressIndicators.length; i++) {
			var indicator = animatedProgressIndicators[i];
		
			ctx.setColor(indicator.getTrackingColor());
			ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.progressIndicatorSize));
			
			int x = (int)(indicator.getTrackingPosition() * R.getWidth());
			ctx.drawLine(x, R.getMinY(), x, R.getMaxY());
		}
	}	
	
	// render graph
	private void renderGraph(Graphics2D ctx) {
		// draw graph
        ctx.setStroke(new BasicStroke(1));
        ctx.setColor(GPSUI.SpeedGraph.foregroundColor);
        
		DoubleArrayGraphRenderer.render(ctx, R, graphData);
	}
	
	// render average speed indicator
	private void renderAverageSpeed(Graphics2D ctx) {		
		ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.averageSpeedIndicatorSize));
		ctx.setColor(GPSUI.SpeedGraph.averageSpeedIndicatorColor);
		
		double val = gpscomputer.getAverageSpeed() * getGraphVerticalScale();
		int y = R.getMaxY() - (int)(val);		
		ctx.drawLine(R.getMinX(), y, R.getMaxX(), y);	
	}
}



/*
 * 
 */

public class ShowSpeed {	
	public ShowSpeed() {
		var speedRenderer = new GPSSpeedGraphRenderer();
		var elevationRenderer = new GPSElevationGraphRenderer();
		var routeRenderer = new GPSRouteRenderer();
		
		
		
		
        JFrame frame = new JFrame("Double Buffering Example");
  
        // Add KeyAdapter to the JFrame
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key Pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                	speedRenderer.resampleData = !speedRenderer.resampleData;
                	speedRenderer.init();
                	
                	elevationRenderer.resampleData = !elevationRenderer.resampleData;
                	elevationRenderer.init();                	
                	//speedRenderer.invalidate();
                	//speedRenderer.repaint();
    
                }
            }
        });		
		var resizeAdapter = new ComponentAdapter() {		
			@Override
			public void componentResized(ComponentEvent e) {
				var o = (JPanel) e.getComponent();
				o.setMaximumSize(new Dimension(frame.getWidth(), 150));
				o.setMinimumSize(new Dimension(frame.getWidth(), 150));
				o.setPreferredSize(new Dimension(frame.getWidth(), 150));
				
				o.invalidate();
				o.repaint();
			}
		};
		
		var container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        var panel1 = new CustomPanelRenderer(600, 100, speedRenderer::render);
        panel1.addComponentListener(resizeAdapter);        
        container.add(panel1);
        
        container.add(Box.createVerticalStrut(4));
        
        var panel2 = new CustomPanelRenderer(600, 100, elevationRenderer::render);
        panel2.addComponentListener(resizeAdapter);
        panel2.setMaximumSize(new Dimension(300, 150));
        container.add(panel2);
        
        container.add(Box.createVerticalStrut(4));
        
        var panel3 = new CustomPanelRenderer(900, 900, (ctx, w, h) -> {
        	speedRenderer.setAnimatedProgressIndicators(routeRenderer.animatedProgressIndicator);
        	elevationRenderer.setAnimatedProgressIndicators(routeRenderer.animatedProgressIndicator);
        	routeRenderer.render(ctx, w, h);
        });
        container.add(panel3);     
        
        frame.add(container);
        
        frame.setSize(940, 1100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {	
		new ShowSpeed();
	}
}
