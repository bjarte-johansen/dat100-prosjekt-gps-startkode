package no.hvl.dat100ptc.oppgave5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import no.hvl.dat100ptc.CustomPanelRenderer;

public class GPSApplication 
{
	// util
	public void setPanelAllSizes(JPanel component, Dimension dim)
	{
		component.setPreferredSize(dim); 
        component.setMaximumSize(dim); 
        component.setMinimumSize(dim); 
	}
	
	public JPanel createLabel(String message) 
	{
        // Outer box
        JPanel outerBox = new JPanel();
        outerBox.setLayout(new BorderLayout());
        
        // Inner box
        JPanel innerBox = new JPanel();
        innerBox.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Label
        JLabel label = new JLabel(message);
        label.setFont(new Font("Verdana", Font.PLAIN, 14));  // Set font size as needed
        Dimension labelSize = label.getPreferredSize();
        labelSize.width = 1920;
        labelSize.height = 20;

        // Add components
        innerBox.add(label);
        outerBox.add(innerBox, BorderLayout.CENTER);
        
        outerBox.setPreferredSize(labelSize); // Match height to text size
        outerBox.setMaximumSize(labelSize); // Match height to text size
        outerBox.setMinimumSize(labelSize); // Match height to text size
        
        return outerBox;
	}

	public GPSApplication() 
	{
		int W_PAD = 8;
		
		var speedRenderer = new GPSSpeedGraphRenderer();
		var elevationRenderer = new GPSElevationGraphRenderer();
		var routeRenderer = new GPSRouteRenderer();
		
        JFrame frame = new JFrame("GPS Fitness Tracker");
        
        var componentBorder = BorderFactory.createLineBorder(GPSUI.Default.componentBorderColor);
  
        // Add KeyAdapter to the JFrame
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key Pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                	speedRenderer.resampleData = !speedRenderer.resampleData;
                	speedRenderer.init();
                	
                	elevationRenderer.resampleData = !elevationRenderer.resampleData;
                	elevationRenderer.init();
                }
            }
        });

		var resizeAdapter = new ComponentAdapter() 
		{
			@Override
			public void componentResized(ComponentEvent e) {
				var o = (JPanel) e.getComponent();
				o.setMaximumSize(new Dimension(frame.getWidth(), 150));
				o.setMinimumSize(new Dimension(frame.getWidth(), 150));
				o.setPreferredSize(new Dimension(frame.getWidth(), 150));
				
				o.invalidate();
				o.repaint();
			}
		};
		
		var container = new JPanel();
		container.setBorder(new EmptyBorder(W_PAD, W_PAD, W_PAD, W_PAD));
		
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// speed
		{
			container.add(createLabel("Speed"));
	
	        var panel1 = new CustomPanelRenderer(600, 100, speedRenderer::render);
	        panel1.addComponentListener(resizeAdapter);
	        panel1.setBorder(componentBorder);
	        container.add(panel1);
	        
	        container.add(Box.createVerticalStrut(4));
		}
        
        // elevation
		{
	        container.add(createLabel("Elevation"));	// label
	        
	        var panel2 = new CustomPanelRenderer(600, 100, elevationRenderer::render);
	        panel2.addComponentListener(resizeAdapter);
	        panel2.setMaximumSize(new Dimension(300, 150));
	        panel2.setBorder(componentBorder);
	        container.add(panel2);
        }
        
        container.add(Box.createVerticalStrut(4));
        
        // route
        {
	        container.add(createLabel("Route"));	// label
	        
	        var panel3 = new CustomPanelRenderer(900, 900, (ctx, w, h) -> {
	        	speedRenderer.setAnimatedProgressIndicators(routeRenderer.animatedProgressIndicator);
	        	elevationRenderer.setAnimatedProgressIndicators(routeRenderer.animatedProgressIndicator);
	        	routeRenderer.render(ctx, w, h);
	        });
	        panel3.setBorder(componentBorder);
	        container.add(panel3);
        }
        
        frame.add(container);
        
        frame.setSize(940, 1100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        frame.setVisible(true);
	}
	
	public static void main(String[] args) 
	{	
		new GPSApplication();
	}
}
