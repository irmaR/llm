package hybrid.network;

/**
 * numeric value
 * @author irma
 *
 */
public class NumberValue extends Value {
	
	private Double d;
	
	public NumberValue(Double obj) {
		this.d=obj;
	}

	public NumberValue(int obj) {
		this.d=new Double(obj);
	}

	@Override
	public String toString() {
		return  d + "";
	}
	
	public Double getNumber(){
		return d;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d == null) ? 0 : d.hashCode());
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
		NumberValue other = (NumberValue) obj;
		if (d == null) {
			if (other.d != null)
				return false;
		} 
		//defined epsilon for a number
		else if (!((Math.abs(d-other.d))<0.001))
			return false;
		return true;
	}
	
	public boolean biggerThan(NumberValue v){
		if(this.d>v.getNumber()){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean smallerThan(NumberValue v) {
		if(this.d<v.getNumber()){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean isnumeric() {
		return true;
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

	@Override
	public Double toNumber() throws WrongValueType {
		return d;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	
	
}
