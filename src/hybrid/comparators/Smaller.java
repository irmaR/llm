package hybrid.comparators;

import hybrid.network.Value;
import hybrid.network.WrongValueType;

/**
 * This comparator checks if a given value is smaller than a pre-defined threshold.
 * @author irma
 *
 */
public class Smaller implements Comparator{

	private Double threshold;
	
	public Smaller(Double threshold1) {
		this.threshold=threshold1;
	}

	@Override
	public boolean compare(Value v1) {
		try {
			return (v1.toNumber()<this.threshold);
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString(){
		return "< "+this.threshold;
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
		Smaller other = (Smaller) obj;
		if (threshold == null) {
			if (other.threshold != null)
				return false;
		} else if (!threshold.equals(other.threshold))
			return false;
		return true;
	}
	
	

}
