package hybrid.network;


/**
 * String value of a ground atom, e.g., for categorical randvars
 * @author irma
 *
 */
public class StringValue extends DiscreteValue {


	private String value;
	
	public StringValue(String val) {
		this.value=val;
	}

	public StringValue() {
		value="null";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BoolValue && value.equals(((BoolValue)obj).getStringValue())){
			return true;
		}
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringValue other = (StringValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	public String toString(){
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isnumeric() {
		return false;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}
	
	
	
}
