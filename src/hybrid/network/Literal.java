package hybrid.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a general value literal
 * It contains the atom associated with it
 * @author irma
 *
 */
public class Literal extends TermRDN implements Serializable {

	protected Atom a;
	protected Integer hash_value=null;
	
	public Literal(Atom a){
		this.a=a;
	}
	
	public Literal(){

	}


	/**
	 * Adding only general purpose literals
	 * @param list
	 * @return
	 */
	public  List<Literal> getLiterals(List<Atom> list){
		List<Literal> literals=new ArrayList<Literal>();
		for(Atom a:list){
			literals.add(new Literal(a));
		}
		return literals;
	}

	public Atom getAtom() {
		return a;
	}

	@Override
	public int hashCode() {
		if(hash_value==null){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		hash_value=result;
		return result;
		}
		else{
			return hash_value;
		}
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Literal other = (Literal) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		return true;
	}
	
	public String toString(){
		return this.a.toString();
	}

	public String getLogvarsDashDelimited() {
		String tmp="_";
		for(Logvar l:this.getAtom().getArguments()){
			tmp+=l.getSymbol()+"_";
		}
		return tmp;
	}
	
	@Override
	public String createFOLTerm() {
		return this.a.createFOLTerm();
	}
	
	

}
