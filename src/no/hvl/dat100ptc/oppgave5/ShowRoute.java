package no.hvl.dat100ptc.oppgave5;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.*;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;




class GPSUI{
	public class Route{
		// uphill / downhill line (also used for up/down waypoint)
		static Color routeDownhillColor = new Color(0,200,0);
		static Color routeUphillColor = new Color(200,0,0);
		
		// endpoint
		static Color endpointIndicatorColor = new Color(100,100,100,200);
		static int endpointIndicatorSize = 16;
		
		// waypoint
		static int wapointIndicatorSize = 4;
		static int waypointIndicatorType = 0;
		
		// progress
		static Color progressIndicatorColor = ColorUtils.niceBlue;
		static int progressIndicatorSize = 7;		
	}
	
	public class SpeedGraph{
		static Color foregroundColor = ColorUtils.niceBlue;
		
		static int averageSpeedIndicatorSize = 2;
		static Color averageSpeedIndicatorColor = new Color(255,50,50,200);
		
		static int progressIndicatorSize = 2;
		static Color progressIndicatorColor = new Color(255,255,255,220);
	}
	
	public class ElevationGraph{
		static Color foregroundColor = GraphicsUtils.copyColor(ColorUtils.niceBlue);
		
		static int progressIndicatorSize = SpeedGraph.progressIndicatorSize;
		static Color progressIndicatorColor = GraphicsUtils.copyColor(SpeedGraph.progressIndicatorColor);		
	}	
}


/*
 * 
 */

class GPSRouteRenderer{
	class AnimatedProgressIndicator{
		private GPSPoint[] gpspoints;
		private GPSComputer gpscomputer;
				
		private double pos;
		private double delta;
		
		//private double totalTime;
		
		private IntPoint2D screenStart;
		private IntPoint2D screenEnd;
		
		private int segmentIndex;

		private double currentDistance;
		private double segmentDistance;
		
		public double animationSpeed = 70.0;
		public Color indicatorColor = GraphicsUtils.copyColor(ColorUtils.niceBlue);
				
		AnimatedProgressIndicator(GPSComputer comp){
			gpscomputer = comp;
			gpspoints = comp.getGPSPoints();
			
			segmentIndex = 0;
			
			recalc();
		}
		
		void recalc() {
			if(segmentIndex + 1 >= gpspoints.length) {
				segmentIndex = 0;
				currentDistance = 0.0;
			}
			
			GPSPoint p0 = gpspoints[segmentIndex];
			GPSPoint p1 = gpspoints[segmentIndex + 1];
			
			segmentDistance = GPSUtils.distance(p0, p1);
			double speed = GPSUtils.speed(p0, p1);
			
			screenStart = gpsPointToDrawSpace(p0);
			screenEnd = gpsPointToDrawSpace(p1);
			
			pos = 0.0;
			delta = speed / segmentDistance;			
		}
		
		void advance(double elapsedFrameTime) {			
			pos += (delta * elapsedFrameTime * animationSpeed);
			
			if(pos > 1.0) {
				currentDistance += segmentDistance;
				segmentIndex++;
				recalc();
			}
		}
		
		void render(Graphics2D ctx, int w, int h) {
			int x = (int) LinearInterpolation.interpolate(pos, screenStart.x, screenEnd.x);
			int y = (int) LinearInterpolation.interpolate(pos, screenStart.y, screenEnd.y);
		
			ctx.setColor(indicatorColor);
			GraphicsUtils.fillCircle(ctx, x, y, GPSUI.Route.progressIndicatorSize);
		}
		
		double getProgressTrackingPosition() {
			return (currentDistance + pos * segmentDistance) / gpscomputer.totalDistance();
		}
	}
	
	private static int MARGIN = 16;
	private static int MAPXSIZE = 600;
	private static int MAPYSIZE = 600;

	private GPSPoint[] gpspoints;
	private GPSComputer gpscomputer;
	
	private double minlon, minlat, maxlon, maxlat;

	private double xstep, ystep;
	
	private IntRectangle R; 
	
	public AnimatedProgressIndicator[] animatedProgressIndicator;
	
	private ElapsedTimer frameTimer = new ElapsedTimer();
	
	public GPSRouteRenderer() {

		//String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		String filename = "medium";
		gpscomputer = new GPSComputer(filename);

		gpspoints = gpscomputer.getGPSPoints();
			
		init();
		
		animatedProgressIndicator = new AnimatedProgressIndicator[3];//(gpscomputer);
		for(int i=0; i<animatedProgressIndicator.length;i++) {
			animatedProgressIndicator[i] = new AnimatedProgressIndicator(gpscomputer);
			animatedProgressIndicator[i].animationSpeed = 30.0 + Math.random() * 50.0;
		}
		
		frameTimer.restart();
	}
	
	public GPSPoint getGPSPoint(int index) {
		if(index < 0) {
			return gpspoints[gpspoints.length + index];
		}
		return gpspoints[index];
	}
	
	public double scale(int maxsize, double minval, double maxval) {
		return maxsize / (Math.abs(maxval - minval));
	}
	
	public void init() {
		minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
		minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));

		maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
		maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
		
		xstep = scale(MAPXSIZE, minlon, maxlon);
		ystep = scale(MAPYSIZE, minlat, maxlat);
		
		// update R (our bounds)
		R = new IntRectangle(MARGIN, MARGIN, MAPXSIZE, MAPYSIZE);		
	}

	public void render(Graphics2D ctx, int w, int h) {
		frameTimer.update();
		
		ctx.setColor(ColorUtils.white);
		ctx.fillRect(0, 0, w, h);
		
		int n = gpspoints.length - 1;
		if(n == 0) { return; }		
		
		showRouteMap(ctx, w, h);
		
		showRouteEndPoints(ctx, w, h);			
		
		showStatistics(ctx, w, h);

		for(int i=0; i<animatedProgressIndicator.length;i++) {		
			animatedProgressIndicator[i].render(ctx, 0, 0);
			animatedProgressIndicator[i].advance(frameTimer.elapsedTime());
		}
		
		System.out.println(frameTimer.elapsedTime());
	}
	
	public double[] getProgressTrackingPosition() {
		var result = new double[animatedProgressIndicator.length];
		for(int i=0; i<animatedProgressIndicator.length;i++) {	
			result[i] = animatedProgressIndicator[i].getProgressTrackingPosition();
		}
		return result;
	}

	public static Color getRouteSegmentColor(GPSPoint a, GPSPoint b) {
		return (a.getElevation() < b.getElevation()) ? GPSUI.Route.routeUphillColor : GPSUI.Route.routeDownhillColor;
	}

	public IntPoint2D gpsPointToDrawSpace(GPSPoint p) {
		return new IntPoint2D(
			R.getMinX() + (p.getLongitude() - minlon) * xstep,					
			R.getMaxY() - (p.getLatitude() - minlat) * ystep
			);		
	}

	public Color ColorFadeOpacity(Color c, int percentAsByte) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), (c.getAlpha() * percentAsByte) >> 8);
	}

	public void showRouteEndPoints(Graphics2D ctx, int w, int h) {		
		IntPoint2D p;
		
		// render endpoints
		ctx.setStroke(new BasicStroke(3));
				
		p = gpsPointToDrawSpace(gpspoints[0]);
		ctx.setColor(GPSUI.Route.endpointIndicatorColor);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize);			
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 2);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 1);		
			
		p = gpsPointToDrawSpace(gpspoints[gpspoints.length - 1]);
		ctx.setColor(GPSUI.Route.endpointIndicatorColor);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 2);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 1);
	}

	public void showRouteMap(Graphics2D ctx, int w, int h) {
		int n = gpspoints.length; 
		
		IntPoint2D cur = gpsPointToDrawSpace(gpspoints[0]);
		IntPoint2D next;
		
		// render lines
		ctx.setStroke(new BasicStroke(2));		
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];
			
			next = gpsPointToDrawSpace(p1);
			ctx.setColor(getRouteSegmentColor(p0, p1));
			ctx.drawLine(cur.x, cur.y, next.x, next.y);
			
			cur = next;	
		}
		
		// render dots
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];
			
			next = gpsPointToDrawSpace(p0);
			ctx.setColor(getRouteSegmentColor(p0, p1));			
			GraphicsUtils.fillCircle(ctx, next.x,  next.y, GPSUI.Route.wapointIndicatorSize);
		}	
		
		// render last point
		next = gpsPointToDrawSpace(getGPSPoint(-1));
		ctx.setColor(getRouteSegmentColor(getGPSPoint(-2), getGPSPoint(-1)));			
		GraphicsUtils.fillCircle(ctx, next.x,  next.y, 3);			
	}
	
	public void replayRoute(Graphics2D ctx, int w, int h) {
	}

	public void showStatistics(Graphics2D ctx, int w, int h) {
		int TEXTDISTANCE = 20;
		
		ctx.setColor(new Color(255,255,255,200));
		ctx.fillRect(R.getMinX(), R.getMinY(), 240, 112);

		ctx.setColor(new Color(0,0,0));
		ctx.setFont(new Font("Courier new", 0, 12));
		
		String s = gpscomputer.getDisplayStatistics();
		String[] lines = s.split("\n");
		
		for(int i=0; i<lines.length; i++) {		
			ctx.drawString(lines[i], MARGIN, MARGIN + TEXTDISTANCE * i);
		}
	}
}

public class ShowRoute{	
	private static int MARGIN = 16;

	public static void main(String[] args) {
		var renderer = new GPSRouteRenderer();
		
		//var container = new JPanel();
        //container.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        
		// create custom panel with render callback
		// it will resize to parent container width
        var panel = new CustomPanelRenderer(600, 600, renderer::render);
        panel.setAntialiasing(true);
        panel.setDoubleBuffered(true);
        //container.add(panel);
        
        JFrame frame = new JFrame("GPS Route renderer");
        frame.setSize(940, 940);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        frame.add(panel);     
        frame.setVisible(true);        
	}
}