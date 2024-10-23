/*
 * 
 * this application is fullfilled thru GPSApplication.java in oppgave 5
 * therefore we skip it alltogether
 *
 */

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

	private IntPoint2D screenStart = new IntPoint2D();
	private IntPoint2D screenEnd = new IntPoint2D();
	
	private int segmentIndex;

	public double currentDistance;
	private double segmentDistance;
	private double segmentTime;
	private double segmentDistanceAccumulator;
	private double segmentTimeAccumulator;
	
	public double currentTime;
	
	public double segmentSpeed;
	
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
			// reset to start
			segmentIndex = 0;
			
			currentTime = 0.0;
			currentDistance = 0.0;
			
			segmentTimeAccumulator = 0.0;			
			segmentDistanceAccumulator = 0.0;
		}

		// store refs to gpspoints
		GPSPoint segmentPointA = gpspoints[segmentIndex];
		GPSPoint segmentPointB = gpspoints[segmentIndex + 1];
						
		// compute variables we need
		segmentDistance = GPSUtils.distance(segmentPointA, segmentPointB);
		segmentSpeed = GPSUtils.speed(segmentPointA, segmentPointB);
		segmentTime = GPSUtils.timeBetweenPoints(segmentPointA, segmentPointB);
		
		// map segment points to screenStart and screenEnd
		gpsrenderer.gpsPointToDrawSpace(segmentPointA, screenStart);
		gpsrenderer.gpsPointToDrawSpace(segmentPointB, screenEnd);
		
		pos = 0.0;
		delta = segmentSpeed / segmentDistance;			
	}
	
	void advance(double elapsedFrameTime) {
		// advance position
		pos += delta * elapsedFrameTime * animationSpeed;
			
		// compute current time & distance
		currentTime = segmentTimeAccumulator + (segmentTime * pos);
		currentDistance = segmentDistanceAccumulator + (segmentDistance * pos);
		
		
		// switch segment if needed
		if(pos >= 1.0) {	
			segmentTimeAccumulator += segmentTime;			
			segmentDistanceAccumulator += segmentDistance;
			
			segmentIndex++;
			
			recalc();
		}
	}
	
	void render(Graphics2D ctx, int w, int h) {
		int x = (int) LinearInterpolation.interpolate(screenStart.x, screenEnd.x, pos);
		int y = (int) LinearInterpolation.interpolate(screenStart.y, screenEnd.y, pos);
		
		ctx.setStroke(new BasicStroke(1));	
		
		ctx.setColor(getTrackingColor());
		GraphicsUtils.fillCircle(ctx, x, y, GPSUI.Route.progressIndicatorSize);
		
		ctx.setColor(GPSUI.Route.progressIndicatorStrokeColor);
		GraphicsUtils.drawCircle(ctx, x, y, GPSUI.Route.progressIndicatorSize);
	}
	
	@Override
	public double getTrackingPosition() {
		return (segmentDistanceAccumulator + pos * segmentDistance) / gpscomputer.totalDistance();
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
		// comments ..
		gpscomputer = computer;
		gpspoints = gpscomputer.getGPSPoints();
		
		// init gps point mapper		
		gpsPointMapper = new GPSPointMapper(gpscomputer, MAPYSIZE, MAPXSIZE);		
			
		init();
	}
	
	public void init() {
		System.out.println("Initializing GPSRouterenderer, " + GPSUI.Default.numberOfRiders + " riders");
		
		// update R (our bounds)
		R = new IntRectangle(MARGIN, MARGIN, MAPXSIZE, MAPYSIZE);
		
		double currentAnimationSpeed = 50.0;
		double animationSpeedOffset = 5.0;
		double animationSpeedRandomAmount = 5.0;
		
		int numIndicators = GPSUI.Default.numberOfRiders;
		
		// allocate create progress indicator objects
		animatedProgressIndicator = new AnimatedProgressIndicator[numIndicators];
		for(int i=0; i<animatedProgressIndicator.length;i++) {			
			animatedProgressIndicator[i] = new AnimatedProgressIndicator(gpscomputer, this);
			animatedProgressIndicator[i].animationSpeed = currentAnimationSpeed;
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

	@Deprecated
	public IntPoint2D gpsPointToDrawSpace(GPSPoint p) {
		throw new RuntimeException("deprecated");
		/*
		return new IntPoint2D(
			R.getMinX() + gpsPointMapper.mapX(p.getLongitude()),					
			R.getMaxY() - gpsPointMapper.mapY(p.getLatitude())
			);
		*/		
	}
	public IntPoint2D gpsPointToDrawSpace(GPSPoint p, IntPoint2D dest) {
		dest.x = R.getMinX() + gpsPointMapper.mapX(p.getLongitude());					
		dest.y = R.getMaxY() - gpsPointMapper.mapY(p.getLatitude());
		return dest;
	}		

	public void render(Graphics2D ctx, int w, int h) {
		// reinitialize if wanted number of riders have changed, could probably
		// reinit on keypresses to change number but not useful.
		if(GPSUI.Default.numberOfRiders != animatedProgressIndicator.length) {
			init();
		}
		
		// update frametimer
		frameTimer.update();

		// clear background
		ctx.setColor(GPSUI.Default.bgColor);
		ctx.fillRect(0, 0, w, h);

		// leave if less than 2 points
		if(gpspoints.length < 2) { return; }

		// render route lines
		renderRouteLines(ctx);
		
		// render route waypoints
		renderRouteWaypoints(ctx);		

		// render endpoints		
		renderRouteEndPoints(ctx);			

		// render stats		
		renderStatistics(ctx);

		// render replay
		renderRouteReplay(ctx);
		
		// hint to GC
		System.gc();
	}

	// 
	public void renderRouteEndPoints(Graphics2D ctx) {
		IntPoint2D p = new IntPoint2D();
		
		// render endpoints
		ctx.setStroke(new BasicStroke(GPSUI.Route.endpointIndicatorStrokeSize));
		
		// map gpspoint to drawspace
		gpsPointToDrawSpace(gpspoints[0], p);
		ctx.setColor(GPSUI.Route.endpointIndicatorColor);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize);			
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 2);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 1);		
			
		// map gpspoint to drawspace
		gpsPointToDrawSpace(gpspoints[gpspoints.length - 1], p);
		ctx.setColor(GPSUI.Route.endpointIndicatorColor);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 2);		
		GraphicsUtils.drawCircle(ctx, p.x,  p.y, GPSUI.Route.endpointIndicatorSize / 3 * 1);
	}

	public void renderRouteLines(Graphics2D ctx) {
		int n = gpspoints.length;
		
		IntPoint2D cur = new IntPoint2D();
		IntPoint2D next = new IntPoint2D();
		
		cur = gpsPointToDrawSpace(gpspoints[0], cur);		
		
		// render lines
		ctx.setStroke(new BasicStroke(GPSUI.Route.lineSize));		
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];

			next = gpsPointToDrawSpace(p1, next);
			ctx.setColor(getRouteSegmentColor(p0, p1));
			ctx.drawLine(cur.x, cur.y, next.x, next.y);
			
			cur.assign(next);	
		}
	
	}
	
	public void renderRouteWaypoints(Graphics2D ctx) {
		int n = gpspoints.length;
		
		IntPoint2D cur = new IntPoint2D();
		
		// render waypoints, separated into two bulks because we want correct
		// segmentcolor the last waypoint as well, didnt try to optimize
		for(int i=1; i<n; i++) {
			var p0 = gpspoints[i - 1];
			var p1 = gpspoints[i];
			
			cur = gpsPointToDrawSpace(p0, cur);
			ctx.setColor(getRouteSegmentColor(p0, p1));			
			GraphicsUtils.fillCircle(ctx, cur.x,  cur.y, GPSUI.Route.wapointIndicatorSize);
		}	
		
		// render last waypoint
		cur = gpsPointToDrawSpace(getGPSPoint(-1), cur);
		ctx.setColor(getRouteSegmentColor(getGPSPoint(-2), getGPSPoint(-1)));			
		GraphicsUtils.fillCircle(ctx, cur.x,  cur.y, 3);		
	}
	
	public void renderRouteReplay(Graphics2D ctx) {
		double elapsedTime = frameTimer.elapsedTime();
		
		// render and advance animated indicators
		for(int i=0; i<animatedProgressIndicator.length;i++) {		
			animatedProgressIndicator[i].render(ctx, 0, 0);
			animatedProgressIndicator[i].advance(elapsedTime);
		}		
	}
	
	public void renderStatisticsLine(Graphics2D ctx, int lineNumber, String msg) {
		int TEXTDISTANCE = 20;
		
		ctx.drawString(msg, R.getMinX(), R.getMinY() + TEXTDISTANCE * lineNumber);		
	}
	
	public void renderStatistics(Graphics2D ctx) {
		int numExtraLines = 5;
		
		String s = gpscomputer.getDisplayStatistics();
		String[] lines = s.split("\n");

		ctx.setColor(GPSUI.Route.textBgColor);
		ctx.fillRect(R.getMinX() - 8, R.getMinY() - 16, 240, (lines.length + numExtraLines) * 20);

		ctx.setColor(GPSUI.Route.textFgColor);
		ctx.setFont(GPSUI.Route.font);

		
		for(int i=0; i<lines.length; i++) {
			renderStatisticsLine(ctx, i, lines[i]);
		}

		String msg;
		int selectedRiderIndex = animatedProgressIndicator.length - 1;
		
		msg = GPSComputer.formatStatsString("Speed", animatedProgressIndicator[selectedRiderIndex].segmentSpeed * 3.6, "km/t");
		renderStatisticsLine(ctx, lines.length, msg);

		msg = GPSComputer.formatStatsString("Distance", animatedProgressIndicator[selectedRiderIndex].currentDistance / 1000, "km");
		renderStatisticsLine(ctx, lines.length + 1, msg);		
		
		msg = GPSComputer.formatStatsString("Time", GPSUtils.formatTime((int) animatedProgressIndicator[selectedRiderIndex].currentTime), "");
		renderStatisticsLine(ctx, lines.length + 2, msg);		
		
		msg = GPSComputer.formatStatsString("Riders", String.format("%s", animatedProgressIndicator.length), "");
		renderStatisticsLine(ctx, lines.length + 3, msg);		
		
		msg = GPSComputer.formatStatsString("FPS", String.format("%5.2f", frameTimer.unfilteredFramesPerSecond()), "");
		renderStatisticsLine(ctx, lines.length + 4, msg);			
	}
}