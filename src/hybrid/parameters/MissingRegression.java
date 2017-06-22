package hybrid.parameters;

import hybrid.dependencies.Dependency;
import hybrid.features.Feature;

import java.util.HashMap;
import java.util.List;

/**
 * This class is used to denote a missing regression coefficients. Namely, this coefficient
 * is left out on purpose, because, for logistic regression of a variable with three values
 * e.g., {low,mid,high} we only need to specify n-1 parameters
 * @author irma
 *
 */
public class MissingRegression extends Regression{

	private String dummy="";
	
	public MissingRegression(List<Feature> features) {
		super(features);
	}


	public boolean isNthRegressor() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Nth parameter";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dummy == null) ? 0 : dummy.hashCode());
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
		MissingRegression other = (MissingRegression) obj;
		if (dummy == null) {
			if (other.dummy != null)
				return false;
		} else if (!dummy.equals(other.dummy))
			return false;
		return true;
	}
	
	
}