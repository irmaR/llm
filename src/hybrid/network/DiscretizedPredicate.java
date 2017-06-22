package hybrid.network;

import java.util.ArrayList;
import java.util.List;

public class DiscretizedPredicate extends DiscretePredicate{

	private int discretization_level;
	private MinMaxValue range;

	public DiscretizedPredicate(String predicateName, int arity, int discretization_level) {
		super(predicateName, arity);
		this.discretization_level=discretization_level;
	}

	@Override
	public String createInterpTerm(Atom a, Subst substitution, Value val) {
		List<Logvar> logvars=a.getArguments();
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
	public List<GroundAtom> getGroundAtomsForAllPossibleValues(GroundAtom a){
		List<GroundAtom> groundAtoms=new ArrayList<GroundAtom>();

		for(Value v:((RangeDiscrete)super.range).getValues()){
			GroundAtom newAtom=new GroundAtom(a.getAtom(),a.getSubst(),v);
			groundAtoms.add(newAtom);
		}
		return groundAtoms;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	@Override
	public boolean conformsToType(Value val) {
		if(val instanceof StringValue){
			return true;
		}
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	public int getDiscretization_level() {
		return discretization_level;
	}

	public MinMaxValue get_minimum_maximum_value(){
		return range;
	}

	public void setMinMaxValueRange(MinMaxValue range) {
		this.range = range;
	}

	public double getDiscretizationRangeSize() {
		return Math.abs(range.getMax()-range.getMin())/this.discretization_level;
	}

	@Override
	public boolean isDiscretized() {
		return true;
	}

	@Override
	public void addToRange(Value v) {
		try {
			this.range.addValueToRange(v);
		} catch (WrongValueType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
