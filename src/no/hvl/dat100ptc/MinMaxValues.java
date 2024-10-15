package no.hvl.dat100ptc;

public class MinMaxValues {
	public static class PrimitiveDouble{
		public double min;
		public double max;
		
		public PrimitiveDouble(double min, double max){
			this.min = min;
			this.max = max;
		}
		public PrimitiveDouble(double[] vals){
			if(vals.length != 2) {
				throw new IllegalArgumentException("array must have exactly 2 elements");
			}
			this.min = vals[0];
			this.max = vals[1];
		}
		
		public double getMin() {
			return min;
		}
		
		public double getMax() {
			return max;
		}
		
		public double size() {
			return max - min;
		}
		
		public boolean empty() {
			return max - min == 0;
		}
	}
	
	public static class PrimitiveInt{
		public int min;
		public int max;
		
		public PrimitiveInt(int min, int max){
			this.min = min;
			this.max = max;
		}
		public PrimitiveInt(int[] vals){
			if(vals.length != 2) {
				throw new IllegalArgumentException("array must have exactly 2 elements");
			}
			this.min = vals[0];
			this.max = vals[1];
		}		
		
		public int getMin() {
			return min;
		}
		
		public int getMax() {
			return max;
		}
		
		public int size() {
			return max - min;
		}
		
		public boolean empty() {
			return max - min == 0;
		}
	}	
}
