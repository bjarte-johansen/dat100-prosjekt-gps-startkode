package no.hvl.dat100ptc.oppgave3;

import static java.lang.Math.*;

import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.DoubleArray;
import no.hvl.dat100ptc.MapGpsPointPairToDouble;
import no.hvl.dat100ptc.MapGpsPointToDouble;
import no.hvl.dat100ptc.TODO;

public class GPSUtils {
	private static final int R = 6371000; // jordens radius
	
	public static double findMax(double[] da) {
		return DoubleArray.of(da).max();
	}

	public static double findMin(double[] da) {
		return DoubleArray.of(da).min();
	}

	
	// we didnt like the naming for these so these just call the
	// actual implementation below, use ctrl-click to "go to" it 
	public static double[] getLongitudes(GPSPoint[] gpspoints) {	
		return getLongitudeValues(gpspoints);
	}		
	public static double[] getLatitudes(GPSPoint[] gpspoints) {
		return getLatitudeValues(gpspoints);
	}	
	
	// actual implementation for getL***tudes
	// - use methods that map gpspoints to double value with 
	// GPSPoint::methodReference to reduce errors and code
	// - may cause slight performance-reduction if code is not inlined
	public static double[] getLatitudeValues(GPSPoint[] gpspoints) {
		return mapGpsPointsToDouble(gpspoints, GPSPoint::getLatitude);
	}
	public static double[] getLongitudeValues(GPSPoint[] gpspoints) {
		return mapGpsPointsToDouble(gpspoints, GPSPoint::getLongitude);
	}
	

	// map using callback, double[i] = (GPSPoint[i])	
	public static double[] mapGpsPointsToDouble(GPSPoint[] gpspoints, MapGpsPointToDouble callback) {
		int n = gpspoints.length;
		if(n == 0) {
			throw new IllegalArgumentException("array must have at least 1 element(s)");
		}

		var result = new double[n];				
		for(int i=0; i<n; i++) {
			result[i] = callback.apply(gpspoints[i]);
		}

		return result;
	}	
	
	// map using callback, double[i] = (GPSPoint[i], GPSPoint[i + 1])	
	public static double[] mapGpsPointPairsToDouble(GPSPoint[] gpspoints, MapGpsPointPairToDouble callback) {
		int n = gpspoints.length - 1;
		if(n + 1 < 2) {
			throw new IllegalArgumentException("array must have at least 2 element(s)");
		}

		var result = new double[n];
		for(int i=0; i<n; i++) {		
			result[i] = callback.apply(gpspoints[i], gpspoints[i + 1]);
		}
		
		return result;
	}

	public static double distance(GPSPoint gpspoint1, GPSPoint gpspoint2) {
		double d;
		double latitude1, longitude1, latitude2, longitude2;
		
		latitude1 = Math.toRadians(gpspoint1.getLatitude());
		latitude2 = Math.toRadians(gpspoint2.getLatitude());
		longitude1 = Math.toRadians(gpspoint1.getLongitude());
		longitude2 = Math.toRadians(gpspoint2.getLongitude());
		
		double deltaPhi = (latitude2 - latitude1);
		double deltaDelta = (longitude2 - longitude1);
		
		double a = compute_a(latitude1,	latitude2, deltaPhi, deltaDelta);
		double c = compute_c(a);		
		d = R * c;
		
		return d;
	}
	
	private static double compute_a(double phi1, double phi2, double deltaphi, double deltadelta) {
		double d1 = Math.sin(deltaphi / 2);
		double d2 = Math.sin(deltadelta / 2);
		
		return (d1 * d1) + Math.cos(phi1) * Math.cos(phi2) * (d2 * d2);
	}

	private static double compute_c(double a) {
		return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
	
	public static double computeElevation(GPSPoint p1, GPSPoint p2) {
		return p2.getElevation() - p1.getElevation();
	}

	public static long timeBetweenPoints(GPSPoint p1, GPSPoint p2) {
		return Math.abs(p2.getTime() - p1.getTime());
	}
	
	public static double speed(GPSPoint p1, GPSPoint p2) {
		double dist = distance(p1, p2);	
		if(dist < 0) {
			throw new RuntimeException("blarh");
		}
		
		long secs = timeBetweenPoints(p1, p2);		
		
		if(secs == 0) {
			throw new IllegalArgumentException("time between gpspoints must be non-zero");
		}
		
		return dist / secs;
	}

	public static String formatIntTimeDigits(int digits) {
		String s = String.valueOf(digits);
		if(digits < 10)
			s = "0" + s;
		return s;
	}
	
	public static String formatTime(int secs) {
		String TIMESEP = ":";
		
		int h = secs / 3600;
		int m = (secs % 3600) / 60;
		int s = (secs % 60);

		return "  " + formatIntTimeDigits(h) + TIMESEP + formatIntTimeDigits(m) + TIMESEP + formatIntTimeDigits(s);		
	}
	
	private static int TEXTWIDTH = 10;

	public static String formatDouble(double d) {
		//return String.format("%10.2f", d);
		
		int a = (int) Math.round(d * 100);
		int intPart = a / 100;
		int fracPart = a % 100;
		
		String result = null;
		if(fracPart < 10) {
			result = intPart + ".0" + fracPart;
		}else {
			result = intPart + "." + fracPart;
		}
		result = " ".repeat(TEXTWIDTH - result.length()) + result;
		return result;
	}
}
