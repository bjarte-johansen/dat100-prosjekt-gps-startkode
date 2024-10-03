package no.hvl.dat100ptc.oppgave4;

import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataConverter;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;

import java.util.Arrays;

import no.hvl.dat100ptc.TODO;

public class GPSComputer {
	
	private GPSPoint[] gpspoints;
	
	public GPSComputer(String filename) {

		GPSData gpsdata = GPSDataFileReader.readGPSFile(filename);
		gpspoints = gpsdata.getGPSPoints();

	}

	public GPSComputer(GPSPoint[] gpspoints) {
		this.gpspoints = gpspoints;
	}
	
	public GPSPoint[] getGPSPoints() {
		return this.gpspoints;
	}
	
	public double totalDistance() {
		double distance = 0;
		
		int n = gpspoints.length - 1;
		for(int i=0; i<n; i++) {			
			distance += GPSUtils.distance(gpspoints[i], gpspoints[i + 1]); 
		}
		
		return distance;
	}
	
	public double totalElevation() {
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

	public int totalTime() {
		if(gpspoints.length < 2) {
			throw new RuntimeException("minimum 2 gpspoints required");
		}
		
		var t1 = gpspoints[gpspoints.length - 1].getTime();
		var t0 = gpspoints[0].getTime();
		return (int)(t1 - t0);
	}
		

	public double[] speeds() {
		int n = gpspoints.length - 1;
		double[] speeds = new double[n];
	
		for(int i=0; i<n; i++) {
			speeds[i] = GPSUtils.speed(gpspoints[i], gpspoints[i + 1]);
		}
		
		//System.out.printf("Speeds: %s, count: %d\n", Arrays.toString(speeds), n);
		
		return speeds;
	}
	
	public double findMax(double[] arr) {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		double found = arr[0];
		int n = arr.length;
		
		for(int i = 1; i < n; i++) {
			if(arr[i] > found) {
				found = arr[i];
			}
		}
		return found;
	}
	public double findAverage(double[] arr) {
		if(arr == null || arr.length == 0) {
			throw new IllegalArgumentException("array must be non-null and non-empty");
		}
		
		int n = arr.length;
		double sum = 0.0;
		
		for(int i = 0; i < n; i++) {
			sum += arr[i];
			System.out.println(arr[i]);
		}
		System.out.printf("%.2f / %d\n", sum, n);
		return sum / n;
	}	
	
	public double maxSpeed() {
		var speedData = speeds();
		return findMax(speedData);
		/*
		double maxSpeed = 0;
		double speed;
		
		int n = gpspoints.length - 1;
		for(int i=0; i<n; i++) {
			speed = GPSUtils.speed(gpspoints[i], gpspoints[i + 1]);
			maxSpeed = Math.max(speed, maxSpeed);
		}	
	
		return maxSpeed;
		*/
	}

	public double averageSpeed() {
		long t = totalTime();
		if(t == 0) {
			throw new RuntimeException("time must be greater than 0");
		}
		
		return totalDistance() / totalTime();
		//var speedData = speeds();		
		//return findAverage(speedData);
		/*
		double average = 0.0;
		var speedData = speeds();
		
		int n = speedData.length;
		for(int i=0; i<n; i++) {
			average += speedData[i];
		}
		return average / n;
		*/
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

		kcal = met * weight * ((double) secs / 3600.0);
		
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
	
	public String getDisplayStatistics() {
		double weight = 80;
		
		String s = "";		
		s += String.format("%-15s: %10s %-5s\n", "Total Time", GPSUtils.formatTime(totalTime()), "");
		s += String.format("%-15s: %10.2f %-5s\n", "Total distance", totalDistance() / 1000, "km");
		s += String.format("%-15s: %10.2f %-5s\n", "Total elevation", totalElevation(), "m");
		s += String.format("%-15s: %10.2f %-5s\n", "Max speed", maxSpeed(), "km/t");
		s += String.format("%-15s: %10.2f %-5s\n", "Average speed", averageSpeed(), "km/t");
		s += String.format("%-15s: %10.2f %-5s\n", "Energy", totalKcal(weight) / 1000, "kcal");
		
		return s;
	}

}
