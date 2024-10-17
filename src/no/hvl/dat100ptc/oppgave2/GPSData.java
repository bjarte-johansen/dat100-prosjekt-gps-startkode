package no.hvl.dat100ptc.oppgave2;

import no.hvl.dat100ptc.TODO;
import no.hvl.dat100ptc.oppgave1.GPSPoint;

public class GPSData {

	private GPSPoint[] gpspoints;
	protected int antall = 0;

	public GPSData(int antall) {
		this.antall = 0;
		this.gpspoints = new GPSPoint[antall];
	}

	public GPSPoint[] getGPSPoints() {
		return this.gpspoints;
	}
	
	protected boolean insertGPS(GPSPoint gpspoint) {	
		if(antall < gpspoints.length) {
			gpspoints[antall++] = gpspoint;
			
			return true;
		}
		
		return false;
	}

	public boolean insert(String time, String latitude, String longitude, String elevation) {
		GPSPoint gpspoint;
		
		try {
			// convert values to GPSPoint
			gpspoint = GPSPoint.convert(time, latitude, longitude, elevation);			
			
			// return status of insertGPS
			return insertGPS(gpspoint);			
		}catch(Exception e){
			System.out.println("Exception");
			System.out.println(e.getMessage());
			
			throw e;
		}		
	}

	public void print() {
		System.out.println("====== GPS Data - START ======");

		for(int i=0; i<antall; i++) {
			System.out.println(gpspoints[i]);
		}
		
		System.out.println("====== GPS Data - SLUTT ======");
	}
}
