package no.hvl.dat100ptc;

import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

/*
 * responsibility for mapping GPS coordinates to pixels moved out of main code
 * - important parts are mapX and mapY functions that actually does the mapping
 */

public class GPSPointMapper{
	private GPSComputer gpscomputer;
	
	private double minlon, minlat;
	private double xstep, ystep;
	
	private int mapWidth, mapHeight;
	
	public GPSPointMapper(GPSComputer computer, int width, int height){		
		gpscomputer = computer;	
		
		mapWidth = width;
		mapHeight = height;
		
		// initialize mapping variables		
		GPSPoint[] gpspoints = gpscomputer.getGPSPoints();
		double maxlon, maxlat;		
		
		double[] longitudeValues = GPSUtils.getLongitudeValues(gpspoints);
		double[] latitudeValues = GPSUtils.getLatitudeValues(gpspoints);
		
		double[] longitudeRange = DoubleArray.of(longitudeValues).minmax();
		double[] latitudeRange = DoubleArray.of(latitudeValues).minmax();
		
		minlon = longitudeRange[0];
		maxlon = longitudeRange[1];
		
		minlat = latitudeRange[0];		
		maxlat = latitudeRange[1];
		
		xstep = scale(mapWidth, minlon, maxlon);
		ystep = scale(mapHeight, minlat, maxlat);	
	}
	
	protected double scale(int maxsize, double minval, double maxval) {
		double divisor = Math.abs(maxval - minval);
		if(divisor < 1e-10) {
			throw new IllegalArgumentException("(maxval - minval) must be greater than epsilon");
		}
		return maxsize / divisor;
	}
	
	public int mapX(double p) {
		return (int)((p - minlon) * xstep);
	}
	
	public int mapY(double p) {
		return (int)((p - minlat) * ystep); 
	}
}