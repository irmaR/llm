package hybrid.network;

import java.io.Serializable;

/**
 * Range of values
 * @author irma
 *
 */
public abstract class Range implements Serializable{

	public abstract boolean isInRange(Value val) throws WrongValueType;
	public abstract void addValueToRange(Value val) throws WrongValueType;
	public abstract boolean isDiscreteRange();
	public abstract boolean isBooleanRange();
	public abstract boolean isNumericRange();
	
	
	public int getIndexOfValue(Value value) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
