package no.hvl.dat100ptc;

import java.awt.Color;
import java.awt.Graphics2D;

public class GraphicsUtils{
	public static Color copyColor(Color c) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	/*
	public static Color lerpColorRGBA(int alpha, Color c1, Color c2) {
		return new Color(
			c1.getRed() + (((c2.getRed() - c1.getRed()) * alpha) >> 8),
			c1.getGreen() + (((c2.getGreen() - c1.getGreen()) * alpha) >> 8),
			c1.getBlue() + (((c2.getBlue() - c1.getBlue()) * alpha) >> 8),
			c1.getAlpha() + (((c2.getAlpha() - c1.getAlpha()) * alpha) >> 8)
			);
	}
	*/
	
	public static void drawCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.drawOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	public static void fillCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.fillOval(x - radius, y - radius, radius * 2, radius * 2);
	}
}