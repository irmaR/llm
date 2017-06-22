package hybrid.network;

import java.io.Serializable;


/**
 * Representing a value a ground atom can have
 * @author irma
 *
 */
public abstract class Value implements Serializable{

public abstract boolean isnumeric();
public abstract boolean isBoolean();
public abstract boolean isDiscrete();
public abstract Double toNumber() throws WrongValueType;
	
	
	
}
