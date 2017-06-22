package hybrid.comparators;

import java.util.ArrayList;
import java.util.List;

import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.utils.Bin;

/**
 * This comparator is comparing if a value given is bigger than some threshold.
 * E.g., new Bigger(20) gives true for compare(15.0) 
 * @author irma
 *
 */
public class Bigger implements Comparator {
    
	private Double threshold;
	
	public Bigger(Double threshold1) {
		this.threshold=threshold1;
	}

	@Override
	public boolean compare(Value v1) {
		
		try {
			if(v1.toNumber()>this.threshold){
				return true;
			}
			else{
				return false;
			}
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString(){
		return "> "+this.threshold;
	}


	public void setupperbound(Double t1) {
		this.threshold=t1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((threshold == null) ? 0 : threshold.hashCode());
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
		Bigger other = (Bigger) obj;
		if (threshold == null) {
			if (other.threshold != null)
				return false;
		} else if (!threshold.equals(other.threshold))
			return false;
		return true;
	}







}
