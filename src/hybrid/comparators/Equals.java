package hybrid.comparators;

import hybrid.network.Value;
import hybrid.network.WrongValueType;
import java.math.BigDecimal;

/**
 * This comparator checks if a given value is equal to the threshold.
 * The question is how useful this comparator is and how likely is that
 * two real valued numbers are the same.
 * @author irma
 *
 */
public class Equals implements Comparator{
   
	private Double threshold;

	public Equals(Double threshold) {
		this.threshold=threshold;
	}

	@Override
	public boolean compare(Value v1) {
		try {
			Double value1=new Double(v1.toNumber());
			Double value2=new Double(this.threshold);
			
			value1=new BigDecimal(value1 ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			value2=new BigDecimal(value2 ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();	
			if(value1.equals(value2)){
				return true;
			}
			else{
				return false;
			}
		} catch (WrongValueType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String toString(){
		return " == ";
	}

}
