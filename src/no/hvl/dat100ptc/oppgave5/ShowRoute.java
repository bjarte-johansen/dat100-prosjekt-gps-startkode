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


class GraphicUtils{
	public static void drawCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.drawOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	public static void fillCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.fillOval(x - radius, y - radius, radius * 2, radius * 2);
	}	
}

class GPSUI{
	public class Route{
		// endpoint
		static Color endpointIndicatorColor = new Color(100,100,100,200);
		static int endpointIndicatorRadius = 16;
		
		// waypoint
		static int wapointIndicatorRadius = 4;
		
		// progress
		static Color progressIndicatorColor = ColorUtils.niceBlue;
		static int progressIndicatorRadius = 6;		
	}
}

class GPSRouteRenderer{
	private static int MARGIN = 16;
	private static int MAPXSIZE = 800;
	private static int MAPYSIZE = 800;

	private GPSPoint[] gpspoints;
	private GPSComputer gpscomputer;
	
	private double minlon, minlat, maxlon, maxlat;

	private double xstep, ystep;
	
	private IntRectangle R; 
	
	public GPSRouteRenderer() {

		//String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		String filename = "medium";
		gpscomputer = new GPSComputer(filename);

		gpspoints = gpscomputer.getGPSPoints();
		
		init();
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
	}
	
	public void render(Graphics2D ctx, int w, int h) {
		ctx.setColor(ColorUtils.white);
		ctx.fillRect(0, 0, w, h);
		
		int n = gpspoints.length - 1;
		if(n == 0) { return; }		
		
		// update R (our bounds)
		R = new IntRectangle(MARGIN, MARGIN, MAPXSIZE, MAPYSIZE);
		
		showRouteMap(ctx, w, h);
		
		showRouteEndPoints(ctx, w, h);			
		
		showStatistics(ctx, w, h);
		
		/*
		replayRoute(ctx, w, h);
		*/
	}
	
	static Color routeDownhillColor = new Color(0,200,0);
	static Color routeUphillColor = new Color(200,0,0);	
	
	public static Color getRouteSegmentColor(GPSPoint a, GPSPoint b) {
		return (a.getElevation() < b.getElevation()) ? routeUphillColor : routeDownhillColor;
	}
	
	public IntPoint2D gpsPointToDrawSpace(GPSPoint p) {
		return new IntPoint2D(
			R.getMinX() + (p.getLongitude() - minlon) * xstep,					
			R.getMaxY() - (p.getLatitude() - minlat) * ystep
			);		
	}
	
	public void showRouteEndPoints(Graphics2D ctx, int w, int h) {		
		IntPoint2D p;
		
		// render endpoints		
		ctx.setColor(GPSUI.Route.endpointIndicatorColor);
		ctx.setStroke(new BasicStroke(3));
		
		p = gpsPointToDrawSpace(gpspoints[0]);	
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius);
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius / 3 * 2);		
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius / 3 * 1);		
			
		p = gpsPointToDrawSpace(gpspoints[gpspoints.length - 1]);	
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius);
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius / 3 * 2);
		GraphicUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorRadius / 3 * 1);
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
			GraphicUtils.fillCircle(ctx, next.x,  next.y, GPSUI.Route.wapointIndicatorRadius);
		}	
		
		// render last point
		next = gpsPointToDrawSpace(getGPSPoint(-1));
		ctx.setColor(getRouteSegmentColor(getGPSPoint(-2), getGPSPoint(-1)));			
		GraphicUtils.fillCircle(ctx, next.x,  next.y, 3);			
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