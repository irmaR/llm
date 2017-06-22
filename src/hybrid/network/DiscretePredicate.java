package hybrid.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing discrete predicates (predicates with discrete range)
 * @author irma
 *
 */
public abstract class DiscretePredicate extends Predicate {
	
	protected RangeDiscrete range;
	
	public DiscretePredicate(String predicateName, int arity,String[] range) {
		super(predicateName, arity);
		this.range=new RangeDiscrete(range);

	}
	
	public DiscretePredicate(String predicateName, int arity,Value[] range) {
		super(predicateName, arity);
		this.range=new RangeDiscrete(range);
	}
	
	public DiscretePredicate(String predicateName, int arity) {
		super(predicateName, arity);
		this.range=new RangeDiscrete();
	}
	
	
	/**
	 * Given a specific ground atom, create all possible ground atoms from
	 * the range of the predicate of that ground atom
	 * @param a - ground atom for which we want all possible assignments
	 * @return - list of ground atoms assigned a value from the range of a's predicate
	 */
	@Override
	public List<GroundAtom> getGroundAtomsForAllPossibleValues(GroundAtom a){
		List<GroundAtom> groundAtoms=new ArrayList<GroundAtom>();
		for(Value v:((RangeDiscrete)this.range).getValues()){
			GroundAtom newAtom=new GroundAtom(a.getAtom(),a.getSubst(),v);
			groundAtoms.add(newAtom);
		}
		return groundAtoms;
	}

	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean conformsToType(Value val) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isDiscrete(){
		return true;
	}
	
	@Override
	public Range getRange() {
		return this.range;
	}
	
	@Override
	public void addToRange(Value v) {
		this.range.addValueToRange(v);
	}




}
