package no.hvl.dat100ptc;

import java.awt.Graphics2D;

public class GraphicsUtils{
	public static void drawCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.drawOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	public static void fillCircle(Graphics2D ctx, int x, int y, int radius) {
		ctx.fillOval(x - radius, y - radius, radius * 2, radius * 2);
	}
}