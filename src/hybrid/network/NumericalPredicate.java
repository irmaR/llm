package hybrid.network;

import java.util.List;

public class NumericalPredicate extends Predicate {
	 
	protected RangeNumeric range;
  
	public NumericalPredicate(String predicateName, int arity,double min,double max) {
		super(predicateName, arity);
		this.range=new RangeNumeric(min,max);
	}
	
	public NumericalPredicate(String predicateName, int arity) {
		super(predicateName, arity);
		this.range=new RangeNumeric(Double.POSITIVE_INFINITY,0.0);
	}

	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		return null;
	}

	@Override
	public boolean conformsToType(Value val) {
		try {
			if(range.isInRange(val)){
				return true;
			}
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}



	@Override
	public boolean isDiscretized() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public double getDiscretizationRangeSize() {
		return 1;
	}

	@Override
	public Range getRange() {
		return this.range;
	}

	@Override
	public void addToRange(Value v) {
		try {
			this.range.addValueToRange(v);
		} catch (WrongValueType e) {
			System.out.println("Error for: "+this);
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean isCategorical() {
		return false;
	}

	@Override
	public boolean isContinuous() {
		return true;
	}


	


}
