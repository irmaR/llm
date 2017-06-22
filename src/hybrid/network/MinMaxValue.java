package hybrid.network;
/**
 * represents the class for keeping minimum and maximum values for atoms
 * that were discretized
 * @author irma
 *
 */
public class MinMaxValue {

	private double min;
	private double max;
	
	public MinMaxValue(double min,double max){
		this.min=min;
		this.max=max;
	}
	
	public double getLengthOfRange(){
		return Math.abs(max-min);
	}
	
	public String toString(){
		return "["+this.min+","+this.max+"]";
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public void addValueToRange(Value v) throws WrongValueType {
		if(v.toNumber()<min){
			min=v.toNumber();
		}
		if(v.toNumber()>max){
			max=v.toNumber();
		}
		
	}

	
	
	
}
