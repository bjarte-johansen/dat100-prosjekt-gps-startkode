package no.hvl.dat100ptc.oppgave1;

public class Main {

	public static void main(String[] args) {
		GPSPoint p = new GPSPoint(1,2.0,3.0,5.0);
		System.out.println(p.getTime());
		p.setTime(2);
		System.out.println(p);
	}
}
