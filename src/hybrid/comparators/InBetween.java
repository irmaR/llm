package hybrid.comparators;

import hybrid.network.Value;
import hybrid.network.WrongValueType;

/**
 * This comparator checks if a value is in between two thresholds.
 * @author irma
 *
 */
public class InBetween implements Comparator {
    
	private Double threshold1;
	private Double threshold2;
	
	public InBetween(Double threshold1,Double threshold2) {
       this.threshold1=threshold1;
       this.threshold2=threshold2;
	}

	@Override
	public boolean compare(Value v1) {
		try {
			if(v1.toNumber()>this.threshold1 && v1.toNumber()<this.threshold2){
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
	
	public void setThresholds(Double t1,Double t2){
		this.threshold1=t1;
		this.threshold2=t2;
	}
	
	public String toString(){
		return this.threshold1+ " < "+"X < "+this.threshold2;
	}

	public Double getThreshold1() {
		return threshold1;
	}



	public Double getThreshold2() {
		return threshold2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((threshold1 == null) ? 0 : threshold1.hashCode());
		result = prime * result
				+ ((threshold2 == null) ? 0 : threshold2.hashCode());
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
		InBetween other = (InBetween) obj;
		if (threshold1 == null) {
			if (other.threshold1 != null)
				return false;
		} else if (!threshold1.equals(other.threshold1))
			return false;
		if (threshold2 == null) {
			if (other.threshold2 != null)
				return false;
		} else if (!threshold2.equals(other.threshold2))
			return false;
		return true;
	}


	
	

}
