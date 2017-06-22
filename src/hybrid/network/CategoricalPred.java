package hybrid.network;

import java.util.ArrayList;

public class CategoricalPred extends DiscretePredicate {

	
	
	public CategoricalPred(String predicateName, int arity,String[] range) {
		super(predicateName, arity,range);

	}
	
	public CategoricalPred(String predicateName, int arity,Value[] range) {
		super(predicateName, arity,range);
		
	}
	
	public CategoricalPred(String predicateName, int arity) {
		super(predicateName, arity);
	}



	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		ArrayList<Logvar> logvars=new ArrayList(substitution.getSubstitution().keySet());
		if(val!=null){
		  if(a.getPredicate().getArity()==2){
			  return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+substitution.getSubstitution().get(logvars.get(1))+","+val.toString()+")";
			}
			else{
				return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+val+")";
			}
		}
		if(a.getPredicate().getArity()==2){
			return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+substitution.getSubstitution().get(logvars.get(1))+","+"null"+")";
		}
		else{
			return this.predicateName+"("+substitution.getSubstitution().get(logvars.get(0))+","+"null"+")";
		}
	}

	@Override
	public boolean conformsToType(Value val) {
		if(val instanceof StringValue){
			return true;
		}
		return false;
	}
	
	public void addValueToRange(Value val) throws WrongValueType{
		this.range.addValueToRange(val);
	}

	@Override
	public boolean isBoolean() {
		return false;
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
		return true;
	}

	@Override
	public boolean isContinuous() {
		return false;
	}



	

}
