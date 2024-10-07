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
import no.hvl.dat100ptc.TODO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


class SpeedProfileRenderer {	
	public static int MARGIN = 4;

	public GPSComputer gpscomputer;	
	private IntRectangle R;
	
	class AnimatedGraphProgressIndicator{
		private double position_;
		public Color color;
		
		AnimatedGraphProgressIndicator(){
			position_ = 0;
		}
		
		void setTrackingPosition(double val) {
			position_ = val;
		}
		double getTrackingPosition() {
			return position_;
		}
		
		void setTrackingColor(Color val) {
			color = val;
		}
		Color getTrackingColor() {
			return color;
		}			
		
		public void render(Graphics2D ctx, int w, int h) {
			ctx.setColor(color);
			ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.progressIndicatorSize));
			ctx.drawLine(R.getMinX(), R.getMinY(), R.getMaxX(), R.getMaxY());
		}
	}
	
	//public AnimatedGraphProgressIndicator[] animatedProgressIndicator = animatedProgressIndicator;
	
	public SpeedProfileRenderer() {
		//String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		String filename = "medium";
		gpscomputer = new GPSComputer(filename);
	}
	
	public void render(Graphics2D ctx, int w, int h) {
		// clear background
		ctx.setColor(ColorUtils.white);
		ctx.fillRect(0, 0, w, h);
		
		R = new IntRectangle(MARGIN, MARGIN, w - MARGIN * 2, h - MARGIN * 2);
		
		if(gpscomputer.getSpeedValues().length == 0) {
			return;
		}		
		
		renderGraph(ctx, w, h);
		
		renderAverageSpeed(ctx, w, h);
	}
	
	public double getGraphFloatValueAt(double pos) {
		double[] speedValues = gpscomputer.getSpeedValues();
		double yFactor = R.getHeight() * (1.0 / gpscomputer.getMaxSpeed()) * 0.9;
		
		return LinearInterpolation.interpolate(pos, speedValues, speedValues.length) * yFactor;		
	}
	
	public void renderGraph(Graphics2D ctx, int w, int h) {	
		double val;		
		double pos = 0.0;
		double delta = 1.0 / R.width;
		
		// draw graph
        ctx.setStroke(new BasicStroke(1));
        ctx.setColor(GPSUI.SpeedGraph.foregroundColor);
	
		for(int i=0; i<R.width; i++) {
			val = getGraphFloatValueAt(pos);
			
			int x = R.getMinX() + i;
			int y = R.getMaxY() - (int) val;
				
			ctx.drawLine(x, R.getMaxY(), x, y);
			
			pos += delta;			
		}
	}
	
	public void renderAverageSpeed(Graphics2D ctx, int w, int h) {
		double yFactor = R.getHeight() * (1.0 / gpscomputer.getMaxSpeed()) * 0.9;		

		ctx.setStroke(new BasicStroke(GPSUI.SpeedGraph.averageSpeedIndicatorSize));
		ctx.setColor(GPSUI.SpeedGraph.averageSpeedIndicatorColor);
		
		double val = gpscomputer.getAverageSpeed() * yFactor;
		int y = R.getMaxY() - (int)(val);		
		ctx.drawLine(R.getMinX(), y, R.getMaxX(), y);	
	}
}

public class ShowSpeed {	
	public ShowSpeed() {
		var speedRenderer = new SpeedProfileRenderer();
		var routeRenderer = new GPSRouteRenderer();
		
        JFrame frame = new JFrame("Double Buffering Example");		
		
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
        
        var panel2 = new CustomPanelRenderer(600, 100, speedRenderer::render);
        panel2.addComponentListener(resizeAdapter);
        panel2.setMaximumSize(new Dimension(300, 150));
        container.add(panel2);
        
        container.add(Box.createVerticalStrut(4));
        
        var panel3 = new CustomPanelRenderer(900, 900, (ctx, w, h) -> {
        	for(int i=0; i<routeRenderer.animatedProgressIndicator.length;i++) {
        		//speedRenderer.ani
        	}
        	routeRenderer.render(ctx, w, h);
        	//routeRenderer::render	
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
