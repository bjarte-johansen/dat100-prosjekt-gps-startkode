package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

import no.hvl.dat100ptc.TODO;

public class ShowRoute extends EasyGraphics {
	class DoublePoint2D{
		private double x_;
		private double y_;
		
		public DoublePoint2D(double x, double y) {
			this.x_ = (int) x;
			this.y_ = (int) y;
		}
		int xAsInt() { return (int) x_; }
		int yAsInt() { return (int) y_; }
	}
	
	private static int MARGIN = 50;
	private static int MAPXSIZE = 800;
	private static int MAPYSIZE = 800;

	private GPSPoint[] gpspoints;
	private GPSComputer gpscomputer;
	
	private double minlon, minlat, maxlon, maxlat;

	private double xstep, ystep;
	
	public ShowRoute() {

		String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		gpscomputer = new GPSComputer(filename);

		gpspoints = gpscomputer.getGPSPoints();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void run() {

		makeWindow("Route", MAPXSIZE + 2 * MARGIN, MAPYSIZE + 2 * MARGIN);

		minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
		minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));

		maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
		maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
		
		xstep = scale(MAPXSIZE, minlon, maxlon);
		ystep = scale(MAPYSIZE, minlat, maxlat);
		
		showRouteMap(MARGIN + MAPYSIZE);

		replayRoute(MARGIN + MAPYSIZE);
		
		showStatistics();
	}

	public double scale(int maxsize, double minval, double maxval) {

		double step = maxsize / (Math.abs(maxval - minval));

		return step;
	}

	public void showRouteMap(int ybase) {	
		setColor(0,200,0);
			
		int n = gpspoints.length;
		if(n == 0) { return; }
		
		DoublePoint2D cur = new DoublePoint2D(
			gpspoints[0].getLatitude() * xstep, 
			gpspoints[0].getLongitude() * ystep
			);
		DoublePoint2D next;
		
		for(int i=0; i<n; i++) {
			next = new DoublePoint2D(
				gpspoints[i].getLatitude() * xstep,
				gpspoints[i].getLongitude() * ystep
				);
			
			drawLine(cur.xAsInt(), ybase - cur.yAsInt(), next.xAsInt(), next.yAsInt());
			
			cur = next;	
		}
	}

	public void showStatistics() {

		int TEXTDISTANCE = 20;

		setColor(0,0,0);
		setFont("Courier new",12);
		
		String s = gpscomputer.getDisplayStatistics();
		String[] lines = s.split("\n");
		
		for(int i=0; i<lines.length; i++) {		
			drawString(lines[i], MARGIN, MARGIN + 20 * i);
		}
	}

	public void replayRoute(int ybase) {

		
	}

}