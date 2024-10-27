package no.hvl.dat100ptc;

public class DoublePoint2D{
	public double x;
	public double y;
	
	public DoublePoint2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public DoublePoint2D(DoublePoint2D other) {
		this.x = other.x;
		this.y = other.y;
	}	
	
	double getX() { return x; }
	double getY() { return y; }
	
	void setX(double x) { this.x = x; }
	void setY(double y) { this.y = y; }
	
	public IntPoint2D toIntPoint() {
		return new IntPoint2D(x, y);
	}
}