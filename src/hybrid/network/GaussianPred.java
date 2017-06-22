package hybrid.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing gaussian predicate
 * @author irma
 *
 */
public class GaussianPred extends NumericalPredicate implements Serializable {

	
	
	public GaussianPred(String predicateName, int arity,double min,double max) {
		super(predicateName, arity,min,max);
	}
	
	public GaussianPred(String predicateName, int arity) {
		super(predicateName, arity);
	}

	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		ArrayList<Logvar> logvars=new ArrayList(substitution.getSubstitution().keySet());
		if(a.getArguments().size()==2){
			return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+substitution.getSubstitution().get(logvars.get(1))+","+val.toString()+")";
		}
		if(a.getArguments().size()==1){
			return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+val.toString()+")";
		}
		return null;

	}
	

	@Override
	public boolean conformsToType(Value val) {
		if(val instanceof NumberValue){
			return true;
		}
		return false;
	}



}
