package no.hvl.dat100ptc;

import java.awt.Rectangle;

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
    public IntRectangle(Rectangle r) {
        this.x = r.x;
        this.y = r.y;
        this.width = r.width;
        this.height = r.height;
    }

    // Method to get the minimum X coordinate (left side)
    public int getMinX() {
        return x;
    }

    // Method to get the minimum Y coordinate (top side)
    public int getMinY() {
        return y;
    }

    // Method to get the maximum X coordinate (right side)
    public int getMaxX() {
        return x + width;
    }

    // Method to get the maximum Y coordinate (bottom side)
    public int getMaxY() {
        return y + height;
    }

    // Method to get the width
    public int getWidth() {
        return width;
    }

    // Method to get the height
    public int getHeight() {
        return height;
    }

    // Optionally, override toString for better readability
    @Override
    public String toString() {
        return "IntRectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }
}