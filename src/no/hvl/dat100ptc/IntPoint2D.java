package no.hvl.dat100ptc;

/*
 * IntPoint2D
 */

public class IntPoint2D{
	public int x;
	public int y;
	
	public IntPoint2D(){ this.x = 0; this.y = 0; }
	public IntPoint2D(int x, int y){ this.x = x; this.y = y; }
	public IntPoint2D(double x, double y){ this.x = (int) x; this.y = (int) y; }
	public IntPoint2D(DoublePoint2D other){ this.x = (int) other.x; this.y = (int) other.y; }
		
	public static IntPoint2D of(int x, int y) { return new IntPoint2D(x,y); }
	public static IntPoint2D of(double x, double y) { return new IntPoint2D(x,y); }
	
	public int getX() { return x; }
	public int getY() { return y; }
		
	public void translate(int x, int y) { this.x += x; this.y += y; }
	public IntPoint2D translated(int x, int y) { return new IntPoint2D(this.x + x, this.y + y); }
}
