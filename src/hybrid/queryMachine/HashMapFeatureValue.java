package hybrid.queryMachine;

import java.util.HashMap;

public class HashMapFeatureValue extends FeatureValue{

	private HashMap<String, Integer> counts;
	
	public HashMapFeatureValue(HashMap<String, Integer> results_count) {
		this.counts=results_count;
	}

	public HashMap<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(HashMap<String, Integer> counts) {
		this.counts = counts;
	}

	@Override
	public String toString() {
		return "HashMapFeatureValue [counts=" + counts + "]";
	}
	
	
	

}
