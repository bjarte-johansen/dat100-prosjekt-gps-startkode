package no.hvl.dat100ptc;

import java.awt.Color;

public class App {
	public static String gpsFilename = "medium"; 
	
	public static class GPSUIProgressIndicator{
		private double pos_;
		private Color color_;
		
		public GPSUIProgressIndicator() {
			setTrackingPosition(0);
			setTrackingColor(Color.black);
		}
		
		public void setTrackingPosition(double val) {
			pos_ = val;
		}
		public double getTrackingPosition() {
			return pos_;
		}
		
		public void setTrackingColor(Color val) {
			color_ = val;
		}
		public Color getTrackingColor() {
			return color_;
		}				
	}
}
