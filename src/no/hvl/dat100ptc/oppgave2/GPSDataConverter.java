package no.hvl.dat100ptc.oppgave2;

import no.hvl.dat100ptc.TODO;
import no.hvl.dat100ptc.oppgave1.GPSPoint;

public class GPSDataConverter {

	
	private static int TIME_STARTINDEX = 11; 

	public static int toSeconds(String timeStr) {	
    	String h = timeStr.substring(11, 11 + 2);
    	String m = timeStr.substring(14, 14 + 2);
    	String s = timeStr.substring(17, 17 + 2);   	
    	
    	long time = Long.parseLong(h) * 60 * 60 
			+ Long.parseLong(m) * 60
			+ Long.parseLong(s);
    	
    	return (int) time;
	}

	public static GPSPoint convert(String timeStr, String latitudeStr, String longitudeStr, String elevationStr) {
		return GPSPoint.convert(timeStr,  latitudeStr, longitudeStr, elevationStr);
	}
	
}
