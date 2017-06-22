package hybrid.network;

import hybrid.interpretations.InterpretationCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a boolean predicate. 
 * @author irma
 *
 */

public class BooleanPred extends DiscretePredicate{

	
	
/**
 * Create a boolean predicate with its name and arity. 
 * @param predicateName
 * @param arity
 */
	public BooleanPred(String predicateName, int arity) {
		super(predicateName, arity,new BoolValue[]{new BoolValue("true"),new BoolValue("false")});
	}

	/**
	 * Create string representation of the predicate
	 */
	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		String tmp=predicateName+"(";
		try{
		if(a.getArguments().size()==2){
		return tmp+=substitution.getSubstitution().get(a.getArgument(0))+","+substitution.getSubstitution().get(a.getArgument(1))+")";
		}
		else{
			return tmp+=substitution.getSubstitution().get(a.getArgument(0))+")";
	
		}
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			System.exit(0);
		}
		return tmp;
		}

	
	@Override
	public boolean conformsToType(Value val) {
		if(val instanceof BoolValue){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isBoolean() {
		return true;
	}

	@Override
	public boolean isDiscretized() {
		return false;
	}

	@Override
	public double getDiscretizationRangeSize() {
		return 1;
	}

	@Override
	public boolean isCategorical() {
		return false;
	}

	@Override
	public boolean isContinuous() {
		return false;
	}
}
