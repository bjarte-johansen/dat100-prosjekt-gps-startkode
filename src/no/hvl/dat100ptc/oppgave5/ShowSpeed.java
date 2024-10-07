package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;
import java.awt.Color;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.*;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;
import no.hvl.dat100ptc.TODO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


class SpeedProfileRenderer {	
	public static int MARGIN = 50;
	public static int BARHEIGHT = 100; 

	public GPSComputer gpscomputer;
	
	private final static Color red = new Color(255,0,0);
	private final static Color green = new Color(0,255,0);
	private final static Color blue = new Color(0,0,255);
	
	public SpeedProfileRenderer() {
		//String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		String filename = "medium";
		gpscomputer = new GPSComputer(filename);
	}
		
	public Color createRandomColor() {
		return new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	}
	
	public void render(Graphics2D g2d, int w, int h) {	
		int x = MARGIN, y;
		double scaleY = 6;			
		
		Rectangle rect = new Rectangle(MARGIN, MARGIN, w - MARGIN * 2, h - MARGIN * 2);
		
		var speeds = gpscomputer.speeds();	
		if(speeds.length == 0) {
			return;
		}
		
		double averageSpeed = Math.min(BARHEIGHT, gpscomputer.averageSpeed());		
		int prevX = MARGIN;
		int prevY = (int)(rect.getMaxY() - Math.min(BARHEIGHT, speeds[0] * scaleY));
		int curX;
		int curY;
		
	
		g2d.setColor(ColorUtils.niceBlue);		
	
		int n = speeds.length;
		for(int i=0; i<n; i++) {
			curX = x + i * 2;			
			curY = (int)(rect.getMaxY() - Math.min(BARHEIGHT, speeds[i] * scaleY));
			
	        // Set the stroke (line thickness)
	        g2d.setStroke(new BasicStroke(1));			
			g2d.drawLine(curX, (int) rect.getMaxY(), curX, curY);
			
			if(prevX > -1) {
				g2d.drawLine(prevX, prevY, curX, curY);
			}
			
			prevX = curX;
			prevY = curY;					
		}
				
		g2d.setColor(red);
		y = (int)(rect.getMaxY() - averageSpeed * scaleY);
		g2d.drawLine(MARGIN, y, MARGIN + 2 * speeds.length, y);		
	}	
	
	public void setColor(EasyGraphics eg, Color c) {		
		eg.setColor(c.getRed(), c.getGreen(), c.getBlue());
	}	
}

public class ShowSpeed {
			
	private static int MARGIN = 50;
	private static int BARHEIGHT = 100; 
	
	public ShowSpeed() {
		var renderer = new SpeedProfileRenderer();
		
        JFrame frame = new JFrame("Double Buffering Example");
        var panel = new CustomPanelRenderer(600, 600, renderer::render);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        panel.setDoubleBuffered(true);
	}
	
	public static void main(String[] args) {	
		new ShowSpeed();
	}
}
