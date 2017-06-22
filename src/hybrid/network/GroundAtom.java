package hybrid.network;

import hybrid.utils.GenerateUIDForGroundAtom;

import java.util.HashMap;

/**
 * Class representing a ground atom. 
 * @author irma
 *
 */

public class GroundAtom {
    //this ground atom originates from Atom s
	private Atom a;
	//the substitution to logvars of a that resulted in this ground atom
	private Subst substitution;
	//ground atom represented as term in prolog
	private String term;
	//Value of this ground atom
	private Value value;
	//saved hashcode - hashcode is only based on the substitution and not the value
	private int hashCode=-1;
	//flag that a hashcode is calculated
	private boolean hashCodeCalculated=false;
	//ID of this ground atom
	private long id;
	//flag denoting the ground atom is subsampled
	private boolean subsampled=false;
	
	
	/**
	 * Creating a ground atom.
	 * @param a - originating from an Atom a
	 * @param substitution - having substitution
	 * @param term - string representation of the term
	 * @param val - value in an interpretation
	 * @throws WrongValueType 
	 */
	public GroundAtom(Atom a,Subst substitution,String term,Value val) throws WrongValueType{
		this.a=a;
		this.substitution=substitution;
		this.term=term;
		this.value=val;
		this.hashCode=this.hashCode();
		this.hashCodeCalculated=true;
		this.id=hybrid.utils.GenerateUIDForGroundAtom.getID();
	}
	/**
	 * Creating a ground atom.
	 * @param a - originating from an Atom a
	 * @param substitution - having substitution
	 * @param term - string representation of the term
	 * @param val - value in an interpretation
	 */
	public GroundAtom(Atom a,Subst substitution,Value val){
		this.a=a;
		this.substitution=substitution;
		this.value=val;
		this.term=createTerm();
		this.hashCode=this.hashCode;
		this.hashCode=this.hashCode();
		this.hashCodeCalculated=true;
		this.id=hybrid.utils.GenerateUIDForGroundAtom.getID();

	}
	/**
	 * Create value atom - as represented in interpretation
	 * @param substitution
	 * @param val
	 * @return
	 */
	public String createTerm() {
		  term=a.getPredicate().createInterpTerm(a,substitution, value);
		return term;
	}
	
	/**
	 * Create atom without value specification -
	 * @param substitution
	 * @param val
	 * @return
	 */
	public String createTermWithoutValue() {
		return a.getPredicate().createTermWithoutValue(a,substitution);
	}
	
	/**
	 * Create atom without value specification -
	 * @param substitution
	 * @param val
	 * @return
	 */
	public String createTermWithValue() {
		return a.getPredicate().createTermWithValue(a,substitution,this.value);
	}
	
	public Atom getAtom() {
		return a;
	}


	public void setA(Atom a) {
		this.a = a;
	}


	public Subst getSubst() {
		return substitution;
	}


	public String getTerm() {
		return term;
	}
	
	

	public void setTerm(String term) {
		this.term = term;
	}


	public Value getValue() {
		return value;
	}


	public void setValue(Value value) {
		this.value = value;
		this.term=createTerm();
	}
	
	public String toString(){
		return term;
		
	}

    /**
     * print ground atom in the following form: pred_name(arg1,arg2)=value
     * @return
     */
	public String printVarValuePair() {
		return this.a.getPredicate().createAtomValuePair(a, substitution, this.value);
	}


	@Override
	public int hashCode() {
		if(!this.hashCodeCalculated){
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((substitution == null) ? 0 : substitution.hashCode());
		return result;
		}
		else{
			return this.hashCode;
		}
	}


	@Override
	public boolean equals(Object obj) {
		if(this.hashCode==((GroundAtom)obj).hashCode){
			if(this.value.equals(((GroundAtom)obj).value)){
			return true;
			}
			else{
			   return false;
			}
		}
		else{
			return false;
		}
		
	}


	public boolean isSubsampled() {
		return subsampled;
	}


	public void setSubsampled(boolean subsampled) {
		this.subsampled = subsampled;
	}
	
	
	
	


	
	
	
	
}
