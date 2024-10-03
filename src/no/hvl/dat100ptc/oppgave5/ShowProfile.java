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

class DrawingExample extends JPanel {
	BufferedImage canvas;
	
	private static final int MARGIN = 50;		// margin on the sides 	
	private static final int MAXBARHEIGHT = 500; // assume no height above 500 meters
	
	public DrawingExample(GPSPoint[] gpspoints, int ybase){
		canvas = new BufferedImage(2 * MARGIN + 3 * gpspoints.length, 2 * MARGIN + MAXBARHEIGHT, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = canvas.createGraphics();
		drawOnCanvas(g2d, gpspoints, ybase);
		g2d.dispose();
	}
	public void drawOnCanvas(Graphics2D g2d, GPSPoint[] gpspoints, int ybase) {
		int x = MARGIN; // første høyde skal tegnes ved MARGIN
		int y;
		
		g2d.setColor(new Color(127, 200, 255, 255));
		
		int n = gpspoints.length;
		for(int i=0; i<n; i++) 
		{
			double e = Math.min(MAXBARHEIGHT, gpspoints[i].getElevation());
			
			y = (int)(ybase - e);			
			g2d.drawLine(x + i * 2, ybase, x + i * 2, y);
		}
		
		g2d.dispose();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the BufferedImage onto the panel
        g.drawImage(canvas, 0, 0, this);
    }	
}

public class ShowProfile extends EasyGraphics {

	private static final int MARGIN = 50;		// margin on the sides 
	
	private static final int MAXBARHEIGHT = 500; // assume no height above 500 meters
	
	private GPSPoint[] gpspoints;

	public ShowProfile() {

		String filename = JOptionPane.showInputDialog("GPS data filnavn (uten .csv): ");
		GPSComputer gpscomputer =  new GPSComputer(filename);

		gpspoints = gpscomputer.getGPSPoints();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void run() {

		int N = gpspoints.length; // number of data points

		makeWindow("Height profile", 2 * MARGIN + 3 * N, 2 * MARGIN + MAXBARHEIGHT);
		
		rootPane.setDoubleBuffered(true);

		// top margin + height of drawing area
		showHeightProfile(MARGIN + MAXBARHEIGHT);
		/*
        SwingUtilities.invokeLater(() -> {
            // Create a window (JFrame) to display the panel
            JFrame frame = new JFrame("Drawing Example");
            DrawingExample panel = new DrawingExample(gpspoints, MARGIN + MAXBARHEIGHT);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.setSize(400, 400);
            frame.setVisible(true);
        });
        */		
	}

	public void showHeightProfile(int ybase) {
		//BufferedImage canvas = new BufferedImage(2 * MARGIN + 3 * gpspoints.length, 2 * MARGIN + MAXBARHEIGHT, BufferedImage.TYPE_INT_ARGB);
		//Graphics2D g2d = canvas.createGraphics();
		
		int x = MARGIN; // første høyde skal tegnes ved MARGIN
		int y;
				
		setColor(0, 0, 255);

		{
			int n = gpspoints.length;
			for(int i=0; i<n; i++) 
			{
				double e = Math.min(MAXBARHEIGHT, gpspoints[i].getElevation());
				
				y = (int)(ybase - e);			
				drawLine(x + i * 2, ybase, x + i * 2, y);
			}
		}
		
		{
			int n = gpspoints.length;
			for(int i=0; i<n; i++) 
			{
				double e = Math.min(MAXBARHEIGHT, gpspoints[i].getElevation());
				
				y = (int)(ybase - e);			
				drawLine(x + i * 2, ybase, x + i * 2, y);
			}
		}
		
		//g2d.dispose();
	}

}
