package hybrid.queryMachine;

public class NumberFeatureValue  extends FeatureValue {

	private int count;
	private int nr_possible_groundings;
	
	
	public NumberFeatureValue(int count, int nr_possible_groundings) {
		this.count=count;
		this.nr_possible_groundings=nr_possible_groundings;
	}

	public String toString(){
		return String.valueOf(this.count);
	}

	public int getValue() {
		return count;
	}

	public int getNrPossibleGroundings() {
		return nr_possible_groundings;
	}
	
}
