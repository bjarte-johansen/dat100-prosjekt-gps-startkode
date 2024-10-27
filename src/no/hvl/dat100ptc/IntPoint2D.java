package no.hvl.dat100ptc;

/*
 * IntPoint2D
 * - constructors
 * 		- default/no-args
 * 		- int, int
 * 		- double, double
 * 		- copy-constructor
 */

public class IntPoint2D{
	public int x;
	public int y;
	
	public IntPoint2D(){ this.x = 0; this.y = 0; }
	public IntPoint2D(int x, int y){ this.x = x; this.y = y; }
	public IntPoint2D(double x, double y){
		this.x = (int) Math.round(x); 
		this.y = (int) Math.round(y);				
	}
	public IntPoint2D(DoublePoint2D other){ 
		this(other.x, other.y); 
	}
		
	public static IntPoint2D of(int x, int y) { return new IntPoint2D(x,y); }
	public static IntPoint2D of(double x, double y) { return new IntPoint2D(x,y); }
	
	public void assign(IntPoint2D other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public int getX() { return this.x; }
	public int getY() { return this.y; }

	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }

	
	void setX(double x) { this.x = (int) Math.round(x); }
	void setY(double y) { this.y = (int) Math.round(y); }	
	
	/*
	public void translate(int x, int y) { this.x += x; this.y += y; }
	public IntPoint2D translated(int x, int y) { return new IntPoint2D(this.x + x, this.y + y); }
	*/
}