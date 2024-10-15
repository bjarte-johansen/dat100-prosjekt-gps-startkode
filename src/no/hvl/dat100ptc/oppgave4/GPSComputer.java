package no.hvl.dat100ptc.oppgave4;

import no.hvl.dat100ptc.*;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataConverter;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;

import java.util.Arrays;

import no.hvl.dat100ptc.TODO;

/*
public class ObjectArray<T>{
	T[] arr;
	
	public int size() { return arr.length; }
}
*/

public class GPSComputer {
	private GPSPoint[] gpspoints;
	
	private double[] speedValues_;
	private double[] distanceValues_;
	private double[] elevationValues_;
	
	private double[] speedRange_;
	private double[] elevationRange_;
	private double[] distanceRange_;
	
	private double totalDistance_;
	private double averageSpeed_;
	
	private double totalElevation_;
	private int totalTime_;
	
	public GPSComputer(String filename) {
		GPSData gpsdata = GPSDataFileReader.readGPSFile(filename);
		gpspoints = gpsdata.getGPSPoints();

		init();		
	}

	public GPSComputer(GPSPoint[] gpspoints) {
		this.gpspoints = gpspoints;
		
		init();		
	}
	
	private void init() {
		totalTime_ = computeTotalTime();
		
		speedValues_ = computeSpeedValues();
		distanceValues_ = computeDistanceValues();
		elevationValues_ = computeElevationValues();
		
		speedRange_ = DoubleArray.of(speedValues_).minmax();
		distanceRange_ = DoubleArray.of(distanceValues_).minmax();
		elevationRange_ = DoubleArray.of(elevationValues_).minmax();	
		
		totalDistance_ = computeTotalDistance();
		averageSpeed_ = computeAverageSpeed();
		
		totalElevation_ = computeTotalElevation();
	}
	
	public GPSPoint[] getGPSPoints() {
		return this.gpspoints;
	}
	
	private double computeTotalDistance() {
		double distance = 0;
		
		int n = gpspoints.length - 1;
		for(int i=0; i<n; i++) {			
			distance += GPSUtils.distance(gpspoints[i], gpspoints[i + 1]); 
		}
		
		return distance;
	}
		
	private double computeTotalElevation() {
		double elevation = 0;
		int n = gpspoints.length - 1;
		for(int i=0; i<n; i++) {
			// get elevation points
			var e0 = gpspoints[i].getElevation();
			var e1 = gpspoints[i + 1].getElevation();
			
			// add positive elevation only
			elevation += Math.max(0, e1 - e0);
		}		
		return elevation;
	}
	
	private int computeTotalTime() {
		if(gpspoints.length < 2) {
			throw new RuntimeException("minimum 2 gpspoints required");
		}
		
		var t1 = gpspoints[gpspoints.length - 1].getTime();
		var t0 = gpspoints[0].getTime();
		return (int)(t1 - t0);
	}	

	private double[] computeSpeedValues() {
		return GPSUtils.mapGpsPointPairsToDouble(gpspoints, GPSUtils::speed);
	}
	
	private double[] computeDistanceValues() {
		return GPSUtils.mapGpsPointPairsToDouble(gpspoints, GPSUtils::distance);
	}		
	
	private double[] computeElevationValues() {
		return GPSUtils.mapGpsPointsToDouble(gpspoints, GPSPoint::getElevation);
	}
	
	private double computeAverageSpeed() {
		long t = totalTime();
		
		if(t == 0) {
			throw new RuntimeException("time must be greater than 0");
		}
		
		return totalDistance() / totalTime();
	}

	public double totalDistance() {
		return totalDistance_;
	}	
	public double getTotalDistance() {
		return totalDistance_;
	}
	
	public double totalElevation() {
		return totalElevation_;
	}
	public double getTtalElevation() {
		return totalElevation_;
	}	
	
	public int totalTime() {
		return totalTime_;
	}
		
	
	public double[] speeds() {
		return speedValues_;
	}	
	public double[] getSpeedValues() {
		return speedValues_;
	}
	
	public double[] getDistanceValues() {
		return distanceValues_;		
	}
	
	public double[] getElevationValues() {
		return elevationValues_;		
	}	
	/*
	//@Deprecated
	public double findMax(double[] arr) {
		return DoubleArray.of(arr).max();
	}
	
	//@Deprecated
	public double findAverage(double[] arr) {
		return DoubleArray.of(arr).average();		
	}
	*/	

	public double minSpeed() {
		return speedRange_[0];
	}	
	public double maxSpeed() {
		return speedRange_[1];
	}
	
	public double getMinSpeed() {
		return speedRange_[0];
	}	
	public double getMaxSpeed() {
		return speedRange_[1];
	}	
	
	public double getMinElevation() {
		return elevationRange_[0];
	}
	public double getMaxElevation() {
		return elevationRange_[1];
	}
	
	public double getMinDistance() {
		return distanceRange_[0];
	}
	public double getMaxDistance() {
		return distanceRange_[1];
	}	


	
	public double averageSpeed() {
		return averageSpeed_;
	}
	
	public double getAverageSpeed() {
		return averageSpeed_;
	}


	// conversion factor m/s to miles per hour (mps)
	public static final double MS = 2.23;

	/*
	 * get metabolic rate from speed
	 Hastighet	MET
		<10 mph	4.0
		10-12 mph	6.0
		12-14 mph	8.0
		14-16 mph	10.0
		16-20 mph	12.0
		>20 mph	16.0		
	 */
	
	public double speedToMetabolicRate(double speed) {
		if(speed < 10) return 4.0;
		if(speed < 12) return 6.0;
		if(speed < 14) return 8.0;
		if(speed < 16) return 10.0;
		if(speed < 20) return 12.0;
		return 16.0;
	}
	
	
	public double kcal(double weight, int secs, double speed) {
		return kcal(weight, secs, speed, true);
	}
	public double kcal(double weight, int secs, double speed, boolean debug) {
		double kcal; 
		
		if(debug) {
			System.out.println("speed: " + speed);
		}
	
		double speedmph = speed * MS;
		double met = speedToMetabolicRate(speedmph);		

		double t = (double) secs / 3600.0;
		kcal = met * weight * t;
		
		return kcal;
	}

	public double totalKcal(double weight) {
		double totalkcal = 0;
		//var speedData = speeds();
		
		int n = gpspoints.length - 1;
		for(int i=0; i<n; i++) {
			var p0 = gpspoints[i];
			var p1 = gpspoints[i + 1];
			
			long t = GPSUtils.timeBetweenPoints(p0, p1);
			double speed = GPSUtils.speed(p0, p1);
			totalkcal += kcal(weight, (int) t, speed, false);
		}

		return totalkcal;
	}
	
	private static double WEIGHT = 80.0;
	
	public void displayStatistics() {
		final String SEP = "==============================================";
		
		System.out.println(SEP);
		System.out.print(getDisplayStatistics());
		System.out.println(SEP);
	}

	public static String formatStatsFloat(double val) {
		return String.format("%10.2f", val);
	}
	public static String formatStatsString(String title, String value, String unit) {
		return String.format("%-15s: %10s %-5s\n", title, value, unit);
	}
	public static String formatStatsString(String title, double value, String unit) {
		return formatStatsString(title, formatStatsFloat(value), unit);
	}	
	public String getDisplayStatistics() {
		double weight = 80;
		
		String s = "";		
		s += formatStatsString("Total Time", GPSUtils.formatTime(totalTime()), "");
		s += formatStatsString("Total distance", totalDistance() / 1000, "km");
		s += formatStatsString("Total elevation", totalElevation(), "m");
		s += formatStatsString("Max speed", maxSpeed() * 3.6, "km/t");		
		s += formatStatsString("Average speed", averageSpeed() * 3.6, "km/t");			
		s += formatStatsString("Energy", totalKcal(weight), "kcal");		

		return s;
	}

}
