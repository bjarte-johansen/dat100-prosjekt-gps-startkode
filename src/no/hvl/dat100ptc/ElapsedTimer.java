package no.hvl.dat100ptc;

public class ElapsedTimer{
	private long prevMillis_;
	private long curMillis_;
	private long elapsedMillis_;	
	
	public ElapsedTimer(){
		restart();
	}
	
	public void restart() {
		prevMillis_ = System.currentTimeMillis();
		curMillis_ = prevMillis_;
		elapsedMillis_ = 0;
	}
	
	public long update() {
		long curMillis = System.currentTimeMillis();
		return update(curMillis);		
	}
	
	public long update(long newMillis) {
		curMillis_ = newMillis;
		elapsedMillis_ = curMillis_ - prevMillis_;
		prevMillis_ = curMillis_;
		return elapsedMillis_;
	}
	
	public long elapsedMillis() {
		return elapsedMillis_;
	}
	public double elapsedTime() {
		return elapsedMillis_ / 1000.0;
	}	
	
	public double unfilteredFramesPerSecond() {
		if(elapsedMillis_ == 0) {
			return 0;
		}
		return 1000.0 / elapsedMillis_;
	}
}