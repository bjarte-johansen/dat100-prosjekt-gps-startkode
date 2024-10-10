package no.hvl.dat100ptc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomPanelRenderer extends JPanel {
	public interface CustomDrawCallback{
		void render(Graphics2D g2d, int w, int h);	
	}
	
    private BufferedImage buffer; // Off-screen buffer    
    private CustomDrawCallback customRenderCallback;
    
    private RenderingHints renderingHints_;
    
    public CustomPanelRenderer(int width, int height, CustomDrawCallback customRenderCallback) {
        this.customRenderCallback = customRenderCallback;
        
        // Create the off-screen buffer
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // create rendering hints
        renderingHints_ = new RenderingHints(null);
        
        setAntialiasing(true);
        
        double wantedFramesPerSec = 1000; 
        int millisDelay = (int)(1000.0 / wantedFramesPerSec);

        Timer timer = new Timer(millisDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call repaint to refresh the window
                repaint();
            }
        });
        timer.start();       
    }
    

    // Override the getMaximumSize() to restrict the maximum height
    @Override
    public Dimension getMaximumSize() {
        Dimension maxSize = super.getMaximumSize();
//        maxSize.height = 150; // Set maximum height to 150 pixels
        return maxSize;
    }   

    
    // set antialiasing
    public void setAntialiasing(boolean value) {
    	setAntialiasing(value, RenderingHints.VALUE_RENDER_QUALITY); 
    }
    public void setAntialiasing(boolean value, Object hint) {
    	Object state = value ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
		renderingHints_.put(RenderingHints.KEY_ANTIALIASING, state);    	
    	renderingHints_.put(RenderingHints.KEY_RENDERING, hint);
    }
    
    /*
    // get / set buffer width/height
    public int getBufferWidth() {
    	return buffer.getWidth();
    }
    public int getBufferHeight() {
    	return buffer.getHeight();
    }
    */
    
    public void setBufferSize(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    
    // paint method
    @Override
    protected void paintComponent(Graphics g) {
    	if(buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
    		setBufferSize(getWidth(), getHeight());
    	}
    	
        super.paintComponent(g);

        // Draw everything to the off-screen buffer first
        Graphics2D g2d = buffer.createGraphics();
        
        // update rendering hints
       	g2d.setRenderingHints(renderingHints_);
        
        // render
        customRenderCallback.render(g2d, buffer.getWidth(), buffer.getHeight());
        
        // Dispose of the off-screen graphics context
        g2d.dispose();
        
        // Now draw the off-screen buffer to the screen
        g.drawImage(buffer, 0, 0, this);        
    }
}