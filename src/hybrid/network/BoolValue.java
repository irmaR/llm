package hybrid.network;

/**
 * Boolean value of a ground atom.
 * @author irma
 *
 */
public class BoolValue extends Value {


	private boolean value;
	private String stringValue;
	
	public BoolValue(String obj) {
		if(obj.equals("true")){
			value=true;
		}
		else if(obj.equals("false")){
			value=false;
		}
		this.stringValue=String.valueOf(value);
	}
	
	public BoolValue(boolean b) {
		this.value=b;
		this.stringValue=String.valueOf(value);

	}
	
	@Override
	public String toString() {
		return ""+value;
	}

	

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StringValue && ((StringValue)obj).getValue().equals(this.getStringValue())){
			return true;
		}
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoolValue other = (BoolValue) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stringValue == null) ? 0 : stringValue.hashCode());
		return result;
	}

	

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
	public String getStringValue(){
		if(this.value==true){
			return "true";
		}
		else{
			return "false";
		}
	}

	@Override
	public boolean isnumeric() {
	// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	@Override
	public Double toNumber() {
		if(value){
			return 1.0;
		}
		else{
			return 0.0;
		}
	}

	@Override
	public boolean isBoolean() {
		return true;
	}
	
	
	
	

}
