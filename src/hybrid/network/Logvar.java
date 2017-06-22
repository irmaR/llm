package hybrid.network;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing a logvar (a placeholder for objects in the domain)
 * @author irma
 *
 */
public class Logvar extends Argument implements Serializable {

	private String symbol; //symbol of the logvar
	private Type type; //type of the logvar

	/**
	 * Creating a logvar with a symbol and the type of the logvar. E.g., 
	 * for a student type 
	 * @param symbol
	 * @param type
	 */
	public Logvar(String symbol,Type type){
		this.symbol=symbol.toUpperCase();
		this.type=type;
	}

	public String getSymbol() {
		return symbol;
	}

	public Type getType() {
		return type;
	}

	public String toString(){
		return symbol;
	}
	
	public void setSymbol(String symbol){
		this.symbol=symbol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Logvar other = (Logvar) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	/**
	 * Rename and copy the logvar. The sufix will be added to the symbol of the logvar.
	 * @param sufix
	 * @return
	 */
	public Logvar copyAndRename(String sufix) {
		return new Logvar(this.symbol+sufix,this.type);
	}

	
	public String createFOLTerm() {
		return type.getName()+"("+this.symbol+")";
	}





}
