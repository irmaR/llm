package hybrid.network;

/**
 * Class that represents a domain constant
 * @author irma
 *
 */
public class Constant extends Argument{

     private String constant;
     
     public Constant(String constant){
    	 this.constant=constant;
     }

	public String getConstant() {
		return constant;
	}
     
    public String toString(){
    	return constant;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constant == null) ? 0 : constant.hashCode());
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
		Constant other = (Constant) obj;
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}
	
    
	
}
