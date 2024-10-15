package no.hvl.dat100ptc.oppgave5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import no.hvl.dat100ptc.App;
import no.hvl.dat100ptc.CustomPanelRenderer;
import no.hvl.dat100ptc.oppgave4.GPSComputer;


/*
 * Classes are mostly stored in no.hvl.dat100ptc
 * - The project uses a total of 17 added classes in no.hvl.dat100ptc package
 * - Additional classes are GPSApplication and GPSUI which are in no.hvl.dat100ptc.oppgave5
 * - Oppgave 5 and 6 are intertwined
 */

public class GPSApplication 
{
	// util
	public void setPanelAllSizes(JPanel component, Dimension dim)
	{
		component.setPreferredSize(dim); 
        component.setMaximumSize(dim); 
        component.setMinimumSize(dim); 
	}
	
	public JPanel createLabelBox(JLabel label) {
        // Outer box
        JPanel outerBox = new JPanel();
        outerBox.setLayout(new BorderLayout());
        
        // Inner box
        JPanel innerBox = new JPanel();
        innerBox.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Label
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
	
	public JPanel createLabel(String message) {
		return createLabel(message, Color.BLACK);
	}
	public JPanel createLabel(String message, Color c) 
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
        label.setForeground(c);
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
		
		// share GPSComputer amongst components
		GPSComputer sharedGpsComputer = new GPSComputer(App.gpsFilename);
		
		var speedRenderer = new GPSSpeedGraphRenderer(sharedGpsComputer);
		var elevationRenderer = new GPSElevationGraphRenderer(sharedGpsComputer);
		var routeRenderer = new GPSRouteRenderer(sharedGpsComputer);
		
		System.out.println(Arrays.toString(sharedGpsComputer.getGPSPoints()));
		System.out.println(Arrays.toString(sharedGpsComputer.getSpeedValues()));		
		
        JFrame frame = new JFrame("GPS Fitness Tracker");
        
        var componentBorder = BorderFactory.createLineBorder(GPSUI.Default.componentBorderColor);
  
        // Add KeyAdapter to the JFrame
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key Pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                if(e.getKeyChar() == 't') {
                	speedRenderer.resampleData = !speedRenderer.resampleData;
                	speedRenderer.init();
                	
                	elevationRenderer.resampleData = !elevationRenderer.resampleData;
                	elevationRenderer.init();
                }
                
                if(e.getKeyChar() == 'c') {
                	GPSUI.Default.advancedColors = !GPSUI.Default.advancedColors; 
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
		
		container.add(createLabel("Viktig melding:", Color.RED));
		container.add(createLabel("[t] toggle regular interval, [c] toggle colorization", Color.RED));		
        container.add(Box.createVerticalStrut(10));		
		
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
