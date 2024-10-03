package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;
import java.awt.Color;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;
import no.hvl.dat100ptc.TODO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class DoubleBufferingExample extends JPanel {

    private BufferedImage buffer; // Off-screen buffer
    public SpeedProfileRenderer speedProfileRenderer = new SpeedProfileRenderer();
    
    public boolean antialised_ = false;

    public DoubleBufferingExample() {
        // Create the off-screen buffer
        buffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        
        speedProfileRenderer = new SpeedProfileRenderer();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw everything to the off-screen buffer first
        Graphics2D g2d = buffer.createGraphics();
        
        // antialias
        Object aaHint = (antialised_) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_DEFAULT;
       	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaHint);
        
        // render
        doPaintComponent(g2d);
        
        // Dispose of the off-screen graphics context
        g2d.dispose();
        
        // Now draw the off-screen buffer to the screen
        g.drawImage(buffer, 0, 0, this);        
    }
    
    protected void doPaintComponent(Graphics2D g2d) {
        speedProfileRenderer.render(g2d,  SpeedProfileRenderer.MARGIN + SpeedProfileRenderer.BARHEIGHT);
        
    }
}

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
	
	public void render(Graphics2D g2d, int ybase) {	
		int x = MARGIN, y;
		double scaleY = 6;			
		
		var speeds = gpscomputer.speeds();	
		if(speeds.length == 0) {
			return;
		}
		
		double averageSpeed = Math.min(BARHEIGHT, gpscomputer.averageSpeed());		
		
		int prevX = MARGIN;
		int prevY = (int)(ybase - Math.min(BARHEIGHT, speeds[0] * scaleY));
		int curX;
		int curY;
		
	
		g2d.setColor(createRandomColor());		
	
		int n = speeds.length;
		for(int i=0; i<n; i++) {
			curX = x + i * 2;			
			curY = (int)(ybase - Math.min(BARHEIGHT, speeds[i] * scaleY));
				
	        // Set the stroke (line thickness)
	        g2d.setStroke(new BasicStroke(2));			
			g2d.drawLine(curX, prevY, curX, curY);
			
	        // Set the stroke (line thickness)
	        g2d.setStroke(new BasicStroke(1));			
			g2d.drawLine(curX, ybase, curX, curY);
			//drawLine(x + i * 2 + 1, ybase, x + i * 2 + 1, ybase - y);
			
			if(prevX > -1) {
				g2d.drawLine(prevX, prevY, curX, curY);
			}
			
			prevX = curX;
			prevY = curY;					
		}
				
		g2d.setColor(red);
		y = (int)(ybase - averageSpeed * 4);
		g2d.drawLine(MARGIN, y, MARGIN + 2 * speeds.length, y);		
	}	
	
	public void setColor(EasyGraphics eg, Color c) {		
		eg.setColor(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public void renderEG(EasyGraphics eg, int ybase) {	
		int x = MARGIN, y;
		
		var speeds = gpscomputer.speeds();	
		if(speeds.length == 0) {
			return;
		}		
		
		double averageSpeed = Math.min(BARHEIGHT, gpscomputer.averageSpeed());
		
		double scaleY = 6;
		
		setColor(eg, blue);
		
		int prevX = MARGIN;
		int prevY = (int)(ybase - Math.min(BARHEIGHT, speeds[0] * scaleY));
		int curX;
		int curY;
	
		int n = speeds.length;
		for(int i=0; i<n; i++) {
			curX = x + i * 2;			
			curY = (int)(ybase - Math.min(BARHEIGHT, speeds[i] * scaleY));
				
			eg.drawLine(curX, prevY, curX, curY);
			eg.drawLine(curX, ybase, curX, curY);
			//drawLine(x + i * 2 + 1, ybase, x + i * 2 + 1, ybase - y);
			
			if(prevX > -1) {
				eg.drawLine(prevX, prevY, curX, curY);
			}
			
			prevX = curX;
			prevY = curY;					
		}		
				
		setColor(eg, red);
		y = (int)(ybase - averageSpeed * 4);
		eg.drawLine(MARGIN, y, MARGIN + 2 * speeds.length, y);
	}	
}

public class ShowSpeed extends EasyGraphics {
			
	private static int MARGIN = 50;
	private static int BARHEIGHT = 100; 
	
	DoubleBufferingExample panel;
	
	public ShowSpeed() {
        JFrame frame = new JFrame("Double Buffering Example");
        panel = new DoubleBufferingExample();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        panel.setDoubleBuffered(true);
	}
	
	public static void main(String[] args) {	
		launch(args);
	}

	public void run() {
		makeWindow("Speed profile",	10, 10);
		
		
	}		
}
