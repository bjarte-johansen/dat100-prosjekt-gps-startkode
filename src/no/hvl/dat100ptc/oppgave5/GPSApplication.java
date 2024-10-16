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
	
	GPSSpeedGraphRenderer speedRenderer;
	GPSElevationGraphRenderer elevationRenderer;
	GPSRouteRenderer routeRenderer;
	
	
	// utility method
	public void setPanelAllSizes(JPanel component, Dimension dim)
	{
		component.setPreferredSize(dim); 
        component.setMaximumSize(dim); 
        component.setMinimumSize(dim); 
	}

	// create colorized labels that fit within BoxLayout, we dont know
	// much about box layouts but this seems to work
	
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
        
        setPanelAllSizes(outerBox, labelSize);
                
        return outerBox;
	}

	public GPSApplication() 
	{
		/*
		 * lager applikasjonen med 3 paneler, speedgraph, elevation graph og route-renderer-.
		 * PS: dere trenger ikke lese denne koden, den er dårlig og bare hevet sammen
		 * 
		 * - vindu kan resizes
		 * - [t] toggle regular/irregular sampling interval (intervall mellom gps punkter
		 * er i utgangspunktet irregulært, t toggler resampling av verdier slik at doublearrays
		 * av dem blir tids-korrekt, dvs at x-akse på speed/elevation-graph samstemmer med tid
		 * - [c] toggle colorization
		 * - [+] add 5 riders, 
		 * - [-] remove 5 riders
		 * 
		 * rytter som vises på display er den seineste, som ligger i animatedProgressIndicators[0]
		 * 
		 * vi har kokt alt sammen til et enkelt vindu og viser alt samtidig siden dette oppfyller
		 * målene med de forskjellige oppgavene. det er ingen poeng i å vise speedgraph i eget
		 * vindu feks når man kan vise alle samtidig på paneler, det er bare et spørsmål om å skrive
		 * mer kode
		 * 
		 * vi bruker paneler som har assignet hver sin callback til en renderer som renderer
		 * til paneloverflaten via en bufferedimage
		 * 
		 * vi har bare fått til å vise ting riktig, vi hevder IKKE å at koden i GPSApplication
		 * har effektiv kode, den kan i stor grad oversees, og bare kjøres
		 * 
		 * vi har måtte lære nye ting om JFrame, JPanel, JLabel og BoxLayout etc så det blir litt
		 * så-som-så på førsteforsøk :)
		 */
		
		int W_PAD = 8;
		
		// create GPSComputer to share amongst component-renderers
		GPSComputer sharedGpsComputer = new GPSComputer(App.gpsFilename);
		
		// init component renderers
		speedRenderer = new GPSSpeedGraphRenderer(sharedGpsComputer);
		elevationRenderer = new GPSElevationGraphRenderer(sharedGpsComputer);
		routeRenderer = new GPSRouteRenderer(sharedGpsComputer);
		
		System.out.println(Arrays.toString(sharedGpsComputer.getGPSPoints()));
		System.out.println(Arrays.toString(sharedGpsComputer.getSpeedValues()));		
		
		// create window
        JFrame frame = new JFrame("GPS Fitness Tracker");
        
        // configure default component border
        var componentBorder = BorderFactory.createLineBorder(GPSUI.Default.componentBorderColor);
  
        // Add KeyAdapter to the JFrame to listen for keyboard events
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key Pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                
                char ch = e.getKeyChar();
                
                if(ch == 't') {
                	// resample data for regular/irregular time intervals
                	speedRenderer.resampleData = !speedRenderer.resampleData;
                	speedRenderer.init();
                	
                	elevationRenderer.resampleData = !elevationRenderer.resampleData;
                	elevationRenderer.init();
                }
                
                if(ch == 'c') {
                	GPSUI.Default.advancedColors = !GPSUI.Default.advancedColors; 
                }
                
                if(ch == '+' || ch == '-') {
                	int offset = 5;
                	if(ch == '-') {
                		offset = -offset;
                	}
                	
                	int newNumberOfRiders = GPSUI.Default.numberOfRiders + offset;
                	
                	newNumberOfRiders = Math.clamp(newNumberOfRiders, 1, 100);
                	
                	GPSUI.Default.numberOfRiders = newNumberOfRiders;
                	
                	// reinitialize route that instanciates progress indicators
                	//routeRenderer.init();
                }
            }
        });

        // vi måtte prøve oss fram for å finne en måte å resize paneler på som
        // faktisk fungerte. denne metoden er høyst sannsynligvis ikke den beste
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
		container.add(createLabel("[t] toggle regular interval, [c] toggle colorization, [+] add 5 riders, [-] remove 5 riders", Color.RED));		
        container.add(Box.createVerticalStrut(10));		
		
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// vi bruker scopes til følgende selv om det ikke er nødvendig eller
		// strengt talt ønskeligt, det er for å tydeligjøre kode bare
		
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
