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


/*
 * 
 * App.GPSUIProgressIndicator has the shared methods ?etTrackingColor and ?etTrackingPos
 * which is why we can share them amongst the elevation/speed/route renderers
 */

class AnimatedProgressIndicator extends App.GPSUIProgressIndicator{
	private GPSPoint[] gpspoints;
	private GPSComputer gpscomputer;
	private GPSRouteRenderer gpsrenderer;
			
	private double pos;
	private double delta;

	private IntPoint2D screenStart;
	private IntPoint2D screenEnd;
	
	private int segmentIndex;

	private double currentDistance;
	private double segmentDistance;
	
	public double animationSpeed = 70.0;
					
	AnimatedProgressIndicator(GPSComputer comp, GPSRouteRenderer renderer){
		gpscomputer = comp;
		gpspoints = comp.getGPSPoints();
		
		gpsrenderer = renderer;
		
		segmentIndex = 0;
		
		setTrackingColor(GraphicsUtils.copyColor(ColorUtils.niceBlue));
		setTrackingPosition(0.0);
		
		recalc();
	}
	
	void recalc() {
		if(segmentIndex + 1 >= gpspoints.length) {
			segmentIndex = 0;
			currentDistance = 0.0;
		}

		double segmentSpeed;

		GPSPoint p0 = gpspoints[segmentIndex];
		GPSPoint p1 = gpspoints[segmentIndex + 1];
		
		segmentDistance = GPSUtils.distance(p0, p1);
		segmentSpeed = GPSUtils.speed(p0, p1);
		
		screenStart = gpsrenderer.gpsPointToDrawSpace(p0);
		screenEnd = gpsrenderer.gpsPointToDrawSpace(p1);
		
		pos = 0.0;
		delta = segmentSpeed / segmentDistance;			
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
		
		ctx.setStroke(new BasicStroke(1));
		
		//GPSPoint p0 = gpspoints[segmentIndex];
		//GPSPoint p1 = gpspoints[segmentIndex + 1];		
	
		ctx.setColor(getTrackingColor());
		//ctx.setColor(GraphicsUtils.lerpColorRGBA(1.0-GPSUtils.speed(p0, p1)/gpscomputer.getMaxSpeed(), Color.red, Color.black));
		//ctx.setColor(GraphicsUtils.lerpColorRGBA(1.0-GPSUtils.speed(p0, p1)/gpscomputer.getMaxSpeed(), Color.red, Color.black));
		GraphicsUtils.fillCircle(ctx, x, y, GPSUI.Route.progressIndicatorSize);
		
		ctx.setColor(Color.black);
		GraphicsUtils.drawCircle(ctx, x, y, GPSUI.Route.progressIndicatorSize);
	}
	
	@Override
	public double getTrackingPosition() {
		return (currentDistance + pos * segmentDistance) / gpscomputer.totalDistance();
	}
}



/*
 * 
 */

class GPSRouteRenderer{	
	private static int MARGIN = 16;
	private static int MAPXSIZE = 600;
	private static int MAPYSIZE = 600;

	private GPSComputer gpscomputer;
	private GPSPoint[] gpspoints;	
	
	// rectangle to render in
	private IntRectangle R; 
	
	// declare shared animated progress indicator
	public AnimatedProgressIndicator[] animatedProgressIndicator;
	
	// frametimer for animation
	private ElapsedTimer frameTimer = new ElapsedTimer();
	
	// move gpspointmapping responsibility to GPSPointMapper
	private GPSPointMapper gpsPointMapper; 
	
	public GPSRouteRenderer(GPSComputer computer) {
		gpscomputer = computer;
		gpspoints = gpscomputer.getGPSPoints();
			
		init();
	}
	
	public void init() {
		// init gps point mapper
		gpsPointMapper = new GPSPointMapper(gpscomputer, MAPYSIZE, MAPXSIZE);
		
		// update R (our bounds)
		R = new IntRectangle(MARGIN, MARGIN, MAPXSIZE, MAPYSIZE);
		
		// Color[] colors = (new Color[] {Color.blue, Color.magenta, Color.orange});
		
		double currentAnimationSpeed = 20.0;
		double animationSpeedOffset = 1.0;
		double animationSpeedRandomAmount = 1.0;
		
		int numIndicators = 10;
		
		// allocate create progress indicator objects
		animatedProgressIndicator = new AnimatedProgressIndicator[numIndicators];
		for(int i=0; i<animatedProgressIndicator.length;i++) {			
			animatedProgressIndicator[i] = new AnimatedProgressIndicator(gpscomputer, this);
			animatedProgressIndicator[i].animationSpeed = currentAnimationSpeed;
			//
			animatedProgressIndicator[i].setTrackingColor(GraphicsUtils.createRandomColor());
			
			currentAnimationSpeed += animationSpeedOffset + Math.random() * animationSpeedRandomAmount;			
		}
		
		// restart frametimer
		frameTimer.restart();	
	}

	public GPSPoint getGPSPoint(int index) {
		if(index < 0) {
			return gpspoints[gpspoints.length + index];
		}
		return gpspoints[index];
	}

	public static Color getRouteSegmentColor(GPSPoint a, GPSPoint b) {
		return (a.getElevation() < b.getElevation()) ? 
			GPSUI.Route.routeUphillColor : 
			GPSUI.Route.routeDownhillColor;
	}

	public IntPoint2D gpsPointToDrawSpace(GPSPoint p) {
		return new IntPoint2D(
			R.getMinX() + gpsPointMapper.mapX(p.getLongitude()),					
			R.getMaxY() - gpsPointMapper.mapY(p.getLatitude())
			);		
	}	

	public void render(Graphics2D ctx, int w, int h) {
		// update frametimer
		frameTimer.update();

		// clear background
		ctx.setColor(GPSUI.Default.bgColor);
		ctx.fillRect(0, 0, w, h);

		// leave if less than 2 points
		if(gpspoints.length < 2) { return; }

		// render route
		renderRouteMap(ctx);

		// render endpoints		
		showRouteEndPoints(ctx);			

		// render stats		
		renderStatistics(ctx);

		// render replay
		renderRouteReplay(ctx);
	}

	public void showRouteEndPoints(Graphics2D ctx) {		
		IntPoint2D p;
		
		// render endpoints
		ctx.setStroke(new BasicStroke(GPSUI.Route.endpointIndicatorStrokeSize));
				
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

	public void renderRouteMap(Graphics2D ctx) {
		int n = gpspoints.length;
		
		IntPoint2D cur = gpsPointToDrawSpace(gpspoints[0]);
		IntPoint2D next;
		
		// render lines
		ctx.setStroke(new BasicStroke(GPSUI.Route.lineSize));		
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];

			next = gpsPointToDrawSpace(p1);
			ctx.setColor(getRouteSegmentColor(p0, p1));
			ctx.drawLine(cur.x, cur.y, next.x, next.y);
			
			cur = next;	
		}
		
		// render waypoints, separated into two bulks because we want correct
		// segmentcolor the last waypoint as well, didnt try to optimize
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];
			
			next = gpsPointToDrawSpace(p0);			
			ctx.setColor(getRouteSegmentColor(p0, p1));			
			GraphicsUtils.fillCircle(ctx, next.x,  next.y, GPSUI.Route.wapointIndicatorSize);
		}	
		
		// render last waypoint
		next = gpsPointToDrawSpace(getGPSPoint(-1));
		ctx.setColor(getRouteSegmentColor(getGPSPoint(-2), getGPSPoint(-1)));			
		GraphicsUtils.fillCircle(ctx, next.x,  next.y, 3);			
	}
	
	public void renderRouteReplay(Graphics2D ctx) {
		// render and advance animated indicators
		for(int i=0; i<animatedProgressIndicator.length;i++) {		
			animatedProgressIndicator[i].render(ctx, 0, 0);
			animatedProgressIndicator[i].advance(frameTimer.elapsedTime());
		}		
	}

	public void renderStatistics(Graphics2D ctx) {
		int TEXTDISTANCE = 20;

		ctx.setColor(GPSUI.Route.textBgColor);
		ctx.fillRect(R.getMinX() - 8, R.getMinY() - 16, 240, 126);

		ctx.setColor(GPSUI.Route.textFgColor);
		ctx.setFont(GPSUI.Route.font);

		String s = gpscomputer.getDisplayStatistics();
		String[] lines = s.split("\n");
		
		for(int i=0; i<lines.length; i++) {
			ctx.drawString(lines[i], R.getMinX(), R.getMinY() + TEXTDISTANCE * i);
		}
	}
}