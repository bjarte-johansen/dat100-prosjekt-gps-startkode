package no.hvl.dat100ptc.oppgave5;

import java.awt.Color;
import java.awt.Font;

import no.hvl.dat100ptc.ColorUtils;
import no.hvl.dat100ptc.GraphicsUtils;

class GPSUI{
	public class Default{
		
		static Color componentBorderColor = Color.decode("#CCCCCC");
		
		static Color bgColor = Color.decode("#FFFFFF");
		static Color fgColor = Color.decode("#333333");
		
		static boolean advancedColors = true;
	}
	
	public class Route{
		// 
		static int lineSize = 2;
		
		// uphill / downhill line (also used for up/down waypoint)
		static Color routeDownhillColor = new Color(0,200,0);
		static Color routeSecondDownhillColor = new Color(0,100,0);
		static Color routeUphillColor = new Color(200,0,0);
		static Color routeSecondUphillColor = new Color(100,0,0);
		
		// endpoint
		static Color endpointIndicatorColor = new Color(100,100,100,200);
		static int endpointIndicatorSize = 16;
		static int endpointIndicatorStrokeSize = 3;
		
		// waypoint
		static int wapointIndicatorSize = 4;
		static int waypointIndicatorType = 0;
		
		// progress
		static Color progressIndicatorColor = ColorUtils.niceBlue;
		static int progressIndicatorSize = 7;	
		
		// text
		static Color textBgColor = new Color(255,255,255,200);
		static Color textFgColor = new Color(50,50,50);
		static Font font = new Font("Courier new", Font.ITALIC, 12);
		
	}
	
	public class SpeedGraph{
		static Color foregroundColor = GraphicsUtils.copyColor(ColorUtils.niceBlue);
		
		static int averageSpeedIndicatorSize = 2;
		static Color averageSpeedIndicatorColor = new Color(255,50,50,200);
		
		static int progressIndicatorSize = 2;
		static Color progressIndicatorColor = new Color(255,255,255,220);
		
		static Color acceleratingColor1 = Color.green;
		static Color acceleratingColor2 = new Color(0, 100, 0);
		
		static Color deceleratingColor1 = Color.red;
		static Color deceleratingColor2 = new Color(100, 0, 0);		
		
		static boolean SHOW_SPEED_AS_COLOR = true;
	}
	
	public class ElevationGraph{
		static Color foregroundColor = GraphicsUtils.copyColor(ColorUtils.niceBlue);
		
		static int progressIndicatorSize = SpeedGraph.progressIndicatorSize;
		static Color progressIndicatorColor = GraphicsUtils.copyColor(SpeedGraph.progressIndicatorColor);		
	}	
}