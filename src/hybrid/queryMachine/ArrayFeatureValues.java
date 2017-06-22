package hybrid.queryMachine;

import hybrid.network.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * This class represents an array of feature values returned by the query machine
 * @author irma
 *
 */


public class ArrayFeatureValues  extends FeatureValue {

	private List<Value> values;
	
	public ArrayFeatureValues(List<Value> results) {
		values=results;
	}
	
	public ArrayFeatureValues(Value[] results) {
		values=Arrays.asList(results);
	}

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "ArrayFeatureValues [values=" + values + "]";
	}
  
	
	
}
