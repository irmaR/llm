package hybrid.network;

public class ValueFactory {

	public Value getValue(Object obj){
		if(obj instanceof String){
			return new StringValue((String)obj); 
			}
		else if(obj instanceof Double){
			return new NumberValue((Double) obj);
		}
		return null;	
	}

}
