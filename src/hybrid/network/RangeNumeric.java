package hybrid.network;
import hybrid.network.NumberValue;
/**
 * Numeric range of radvars
 * @author irma
 *
 */
public class RangeNumeric extends Range {

	public Double min;
	public Double max;
	
	public RangeNumeric(Double min,Double max){
		this.min=min;
		this.max=max;
	}
	
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}

	@Override
	public boolean isInRange(Value val) throws WrongValueType {
		if(!(val instanceof NumberValue)){
			throw new WrongValueType("Wrong value type. This is range of a numeric predicate");
		}
		if(((NumberValue)val).getNumber()>min && ((NumberValue)val).getNumber()<max){
			return true;
		}
		return false;
	}

	@Override
	public void addValueToRange(Value val) throws WrongValueType {
		if(!(val instanceof NumberValue)){
			throw new WrongValueType("Wrong value type. This is range of a numeric predicate");
		}
		if(((NumberValue)val).getNumber()<min){
			min=((NumberValue)val).getNumber();
		}
		if(((NumberValue)val).getNumber()>max){
			max=((NumberValue)val).getNumber();
		}	
	}
	
	public String toString(){
		return "["+min+","+max+"]";
	}

	@Override
	public boolean isDiscreteRange() {
		return false;
	}

	@Override
	public boolean isNumericRange() {
		return true;
	}

	@Override
	public boolean isBooleanRange() {
		return false;
	}
}
