package no.hvl.dat100ptc;

import java.awt.Color;
import java.awt.Graphics2D;

public class GraphicsUtils{
	// create copy of Color object
	
	public static Color copyColor(Color c) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
		
	public Color createRandomGrayscaleColor() 
	{
		int v = (int)(Math.random()*255); 
		return new Color(v, v, v);
	}		

	
	// create randomized color
	
	public static Color createRandomColor() {
		return new Color(
			(int)(Math.random() * 255),
			(int)(Math.random() * 255),
			(int)(Math.random() * 255)
			);
	}
	
	
	// clamp to c-type byte [0 .. 255], note: NOT a java byte
	
	public static int clampByte(int val) {
		if(val < 0)
			val = 0;
		if(val > 255)
			val = 255;
		return val;
	}

	
	// linearly interpolate between two colors using int [0 .. 255] or float [0.0 .. 1.0]
	
	public static Color lerpColorRGBA(double alpha, Color c1, Color c2) {
		int byteAlpha = (int)(alpha * 255);
		return lerpColorRGBA(byteAlpha, c1, c2);
	}
	public static Color lerpColorRGBA(int alpha, Color c1, Color c2) {
		int r = clampByte(c1.getRed() + (((c2.getRed() - c1.getRed()) * alpha) >> 8));
		int g = clampByte(c1.getGreen() + (((c2.getGreen() - c1.getGreen()) * alpha) >> 8));
		int b = clampByte(c1.getBlue() + (((c2.getBlue() - c1.getBlue()) * alpha) >> 8));
		int a = clampByte(c1.getAlpha() + (((c2.getAlpha() - c1.getAlpha()) * alpha) >> 8));
		
		if(a < 0 || a > 255)
			System.out.println("WHAT THE FUCK " + a);
		
		return new Color(r, g, b, a);
	}
	
	
	// draw/fill circle
	public static void drawCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.drawOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	public 
	static void fillCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.fillOval(x - radius, y - radius, radius * 2, radius * 2);
	}
}