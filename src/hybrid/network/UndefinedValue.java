package hybrid.network;
/**
 * Class representing undefined value for a feature / ground Atom.
 * @author irma
 *
 */
public class UndefinedValue extends Value {

	private String val="NDVal";

	@Override
	public String toString() {
		return val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		UndefinedValue other = (UndefinedValue) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	@Override
	public boolean isnumeric() {
		return false;
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

	@Override
	public Double toNumber() throws WrongValueType {
		return Double.NaN;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}
	
	
	
	
	
}
