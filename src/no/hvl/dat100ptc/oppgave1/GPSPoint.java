package no.hvl.dat100ptc.oppgave1;

import no.hvl.dat100ptc.TODO;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GPSPoint {
	private long time;
	private double latitude;
	private double longitude;
	private double elevation;
	
	public GPSPoint(long time, double latitude, double longitude, double elevation) {
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}
	
	public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getElevation() { return elevation; }
    public void setElevation(double elevation) { this.elevation = elevation; }
    
    public String toString() {
        // Set a custom NumberFormat to use US locale
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(1);  // Ensure ".0" is shown
        
        // Use MessageFormat with this custom format
        MessageFormat mf = new MessageFormat("{0,number,integer} ({1,number,#.0},{2,number,#.0}) {3,number,#.0}\n", Locale.US);
        mf.setFormatByArgumentIndex(1, numberFormat);
        mf.setFormatByArgumentIndex(2, numberFormat);
        mf.setFormatByArgumentIndex(3, numberFormat);
        
    	Object[] args = {time, latitude, longitude, elevation};
    	return mf.format(args);
    }
    
    public static GPSPoint convert(String timeStr, String latitudeStr, String longitudeStr, String elevationStr) {
    	String h = timeStr.substring(11, 11 + 2);
    	String m = timeStr.substring(14, 14 + 2);
    	String s = timeStr.substring(17, 17 + 2);   	
    	
    	long time = Integer.parseInt(h) * 60 * 60 
    			+ Integer.parseInt(m) * 60
    			+ Integer.parseInt(s);
    	double latitude = Double.parseDouble(latitudeStr);
    	double longitude = Double.parseDouble(longitudeStr);
    	double elevation = Double.parseDouble(elevationStr);
    	
    	return new GPSPoint(time, latitude, longitude, elevation);
    }
}
