package no.hvl.dat100ptc;

import no.hvl.dat100ptc.oppgave1.GPSPoint;

public interface MapGpsPointPairToDouble {
	double apply(GPSPoint p1, GPSPoint p2);
}
