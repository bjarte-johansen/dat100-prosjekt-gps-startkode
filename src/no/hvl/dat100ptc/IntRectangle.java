package no.hvl.dat100ptc;

import java.awt.Rectangle;


/*
 * simple class for int-type rectangle
 * - based on java.awt.Rectangle but with int-type for functions
 * 	getMinX/getMaxX/getMinY/getMaxY to avoid excessive casting 
 */

public class IntRectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    // Constructor that takes a Rectangle and initializes IntRectangle fields
    public IntRectangle(int x, int y, int width, int height) {
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    }
    
    // copy-constructor
    public IntRectangle(Rectangle r) {
        this.x = r.x;
        this.y = r.y;
        this.width = r.width;
        this.height = r.height;
    }

    public int getMinX() { return x; }
    public int getMinY() { return y; }
    public int getMaxX() { return x + width; }
    public int getMaxY() { return y + height; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // override toString for better readability
    @Override
    public String toString() {
        return "IntRectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }
}