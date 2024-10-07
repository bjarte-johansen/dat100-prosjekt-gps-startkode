package no.hvl.dat100ptc.oppgave5;

import no.hvl.dat100ptc.TODO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataConverter;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

class ShowProfileRenderer {
	GPSPoint[] gpspoints;
	
	public ShowProfileRenderer() {
		//String filename = JOptionPane.showInputDialog("GPS data filnavn (uten .csv): ");
		String filename = "medium";
		GPSComputer gpscomputer =  new GPSComputer(filename);
		gpspoints = gpscomputer.getGPSPoints();
	}
	public void render(Graphics2D ctx, int w, int h) {
		
	}
}

public class ShowProfile{

	private static final int MARGIN = 50;		// margin on the sides 
	
	private static final int MAXBARHEIGHT = 500; // assume no height above 500 meters

	public ShowProfile() {
		/*
		int N = gpspoints.length; // number of data points

		//makeWindow("Height profile", 2 * MARGIN + 3 * N, 2 * MARGIN + MAXBARHEIGHT);
		 */
	}
}
