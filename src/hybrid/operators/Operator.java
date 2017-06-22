package hybrid.operators;

import java.io.Serializable;

import hybrid.network.Value;
/**
 * This is an operator class that is used in operator features. These features are og the form:
 * randvar1 op randvar2 where op is one of the operators.
 * @author irma
 *
 * @param <T>
 */
public abstract class Operator<T> implements Serializable {

	
	public abstract Value calculate(T val1,T val2);
	public abstract Double calculate_numbers(Double val1, Double val2);
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
	
}
