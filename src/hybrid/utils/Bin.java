package hybrid.utils;

public class Bin {

	private double from;
	private double to;
	
	public Bin(double from,double to){
		this.from=from;
		this.to=to;
	}

	public double getFrom() {
		return from;
	}

	public void setFrom(double from) {
		this.from = from;
	}

	public double getTo() {
		return to;
	}

	public void setTo(double to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "Bin [" + from + "," + to + "]";
	}
	
	
	
}
