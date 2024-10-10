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


class GPSElevationGraphRenderer {	
	private static int MARGIN = 4;

	private GPSComputer gpscomputer;	
	private DoubleArrayGraphRenderer.Data graphData;
	
	// declare rectangle to draw within
	private IntRectangle R;

	// progress indicators, may be null
	public App.GPSUIProgressIndicator[] animatedProgressIndicators = null;
	
	public GPSElevationGraphRenderer() {
		gpscomputer = new GPSComputer(App.gpsFilename);
		
		// create graph data for graph rendering		
		graphData = new DoubleArrayGraphRenderer.Data();
		graphData.values = gpscomputer.getElevationValues();
		graphData.numValues = graphData.values.length;
		graphData.min = gpscomputer.getMinElevation();
		graphData.max = gpscomputer.getMaxElevation();
		
		// set progress indicators to null		
		animatedProgressIndicators = null;
	}
	
	public void setAnimatedProgressIndicators(App.GPSUIProgressIndicator[] indicators) {
		animatedProgressIndicators = indicators;
	}
		
	public void render(Graphics2D ctx, int w, int h) {
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
	
	private void renderGraph(Graphics2D ctx) {
		// draw graph
        ctx.setStroke(new BasicStroke(1));
        ctx.setColor(GPSUI.SpeedGraph.foregroundColor);
        
		DoubleArrayGraphRenderer.render(ctx, R, graphData);
	}
}

public class ShowProfile {	
	public ShowProfile() {
		var elevationGraphRenderer = new GPSElevationGraphRenderer();
		
		GPSP		
		
        JFrame frame = new JFrame("GPS Elevation");		
		
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

        var panel1 = new CustomPanelRenderer(600, 100, elevationGraphRenderer::render);
        panel1.addComponentListener(resizeAdapter);        
        container.add(panel1);
        
        frame.add(container);
        
        frame.setSize(600, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {	
		new ShowProfile();
	}
}
