package hybrid.network;

import hybrid.interpretations.InterpretationCreator;

import java.io.Serializable;
import java.util.List;

/**
 * class representing a predicate with 
 * predicate name, arity n
 * @author irma
 *
 */

public abstract class Predicate implements Serializable {
	//protected Range range;
	protected String predicateName;
	protected int arity;
	protected boolean range_set=false;
	protected InterpretationCreator subsampleingProcedure;

	/**
	 * 
	 * @param predicateName - predicate name
	 * @param arity -predicate arity 
	 * @param t - predicate type
	 */
	public Predicate(String predicateName,int arity){
		this.predicateName=predicateName;
		this.arity=arity;		
		this.range_set=true;
	}
	

	/**
	 * Create a term for interpretation of the form PredicateName(Const1,Const2,...,Value)
	 * @param a
	 * @param substitution
	 * @param val
	 * @return
	 */
	public abstract String createInterpTerm(Atom a, Subst substitution, Value val);
	public abstract boolean isDiscrete();
	public abstract boolean isCategorical();
	public abstract boolean isContinuous();
	public abstract boolean isDiscretized();
	public abstract boolean isBoolean();
	public abstract double getDiscretizationRangeSize();
	public abstract Range getRange();
	public abstract void addToRange(Value v);
	
	public MinMaxValue get_minimum_maximum_value(){
		return null;
	}

	/**
	 * Method which checks if the value conforms to the types of predicates
	 * @param val
	 */
	public abstract boolean conformsToType(Value val);

	/**
	 * Create a string representation of atom a, for substitution and value. 
	 * Example: intelligence(s1)=120.53
	 * @param a
	 * @param substitution
	 * @param val
	 * @return
	 */
	public String createAtomValuePair(Atom a, Subst substitution, Value val) {
		String tmp=predicateName+"(";
		if(a.getArguments().size()>1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+","+substitution.getSubstitution().get(a.getArgument(1))+")="+val;	
		}
		else if(a.getArguments().size()==1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+")="+val;	
		}
		return tmp;
	}


	public String getPredicateName() {
		return predicateName;
	}

	public int getArity() {
		return arity;
	}

	public void setPredicateName(String predicateName) {
		this.predicateName = predicateName;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}


	
	public String toString(){
		return this.predicateName+"/"+this.arity;
	}

	/**
	 * Create a string representation of atom with a substitution. 
	 * For example, the result might be: intelligence(s1). and not
	 * intelligence(s1)=value.
	 * @param a
	 * @param substitution
	 * @return
	 */
	public String createTermWithoutValue(Atom a, Subst substitution) {
		String tmp=predicateName+"(";
		if(a.getArguments().size()>1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+","+substitution.getSubstitution().get(a.getArgument(1))+")";	
		}
		else if(a.getArguments().size()==1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+")";	
		}
		return tmp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((predicateName == null) ? 0 : predicateName.hashCode());
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
		Predicate other = (Predicate) obj;
		if (predicateName == null) {
			if (other.predicateName != null)
				return false;
		} else if (!predicateName.equals(other.predicateName))
			return false;
		return true;
	}

	public String createTermWithValue(Atom a, Subst substitution,Value val) {
		String tmp=predicateName+"(";
		if(a.getArguments().size()>1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+","+substitution.getSubstitution().get(a.getArgument(1))+","+val+")";	
		}
		else if(a.getArguments().size()==1){
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+","+val+")";	
		}
		return tmp;
	}

	public boolean isRangeDetermined(){
		return this.range_set;
	}


	/**
	 * Get subsampling procedure of this predicate
	 * @return
	 */
	/*public InterpretationCreator getSubsampleingProcedure() {
		return subsampleingProcedure;
	}*/

	public List<GroundAtom> getGroundAtomsForAllPossibleValues(GroundAtom a){
		return null;
	}



}


