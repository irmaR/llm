package hybrid.parameters;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import hybrid.features.Feature;
import hybrid.network.Value;

/**
 * This one represents a particular exception to continuous distribution such as Conditional Gaussian and Conditional Linear Gaussian.
 * This key is used to represent continuous head values when there are non-existing combinations of discrete parents in the data.
 * @author irma
 *
 */
public class UndefinedAssignmentKey extends AssignmentKey implements Serializable{

	
	public UndefinedAssignmentKey(){
		super();
	}
	
	public UndefinedAssignmentKey(List<FeatureValuePair> keys){
		super(keys);

	}

	@Override
	public String toString() {
		return "UndefinedAssignmentKey";
	}



}
