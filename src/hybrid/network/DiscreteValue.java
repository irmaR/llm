package hybrid.network;

/**
 * Abstract class representing discrete values 
 * @author irma
 *
 */
public abstract class DiscreteValue extends Value {


	public DiscreteValue(){

	}

	public DiscreteValue(String obj){

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
	public Double toNumber() throws WrongValueType {
		throw new WrongValueType("Discrete Value cannot be turned into a number!");
	}

	

}
