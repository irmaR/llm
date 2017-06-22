package hybrid.parameters;
import hybrid.features.Feature;
import hybrid.network.Value;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents one combination of discrete parent values
 * @author irma
 *
 */

public class AssignmentKey implements Serializable{

	private LinkedList<FeatureValuePair> key;
	
	
	public AssignmentKey(Feature[] features, Value[] values){
		key=new LinkedList<FeatureValuePair>();
		for(int i=0;i<features.length;i++){
			key.add(new FeatureValuePair(features[i],values[i]));
		}
	}
	
    public AssignmentKey(List<FeatureValuePair> keys){
		this.key=new LinkedList<FeatureValuePair>();
		for(FeatureValuePair f:keys){
			key.add(f);
		}
		
	}

	public AssignmentKey() {
		this.key=new LinkedList<FeatureValuePair>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssignmentKey other = (AssignmentKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return key.toString();
	}

	public LinkedList<FeatureValuePair> getKey() {
		return key;
	}

	public Value getFeatureValue(Feature f) {
		for(FeatureValuePair fv:this.key){
			if(fv.getFt().equals(f)){
				return fv.getVal();
			}
		}
		return null;
	}
    
    


}
