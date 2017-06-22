package hybrid.parameters;

import java.io.Serializable;

import hybrid.features.Feature;
import hybrid.network.Value;
/**
 * This class represents feature value pair - Feature mapping to its value in an interpretation
 * @author irma
 *
 */
public class FeatureValuePair  implements Serializable{

	private Feature ft;
	private Value val;
	
	public FeatureValuePair(Feature ft,Value val){
		this.ft=ft;
		this.val=val;
	}

	public Feature getFt() {
		return ft;
	}

	public Value getVal() {
		return val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ft == null) ? 0 : ft.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		FeatureValuePair other = (FeatureValuePair) obj;
		if (ft == null) {
			if (other.ft != null)
				return false;
		} else if (!ft.equals(other.ft))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ft.toString().trim() + "->" + val;
	}
	
	
	
	
	
	
}
