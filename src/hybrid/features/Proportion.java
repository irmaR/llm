package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.SpecialEvaluator;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.RangeNumeric;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.NumberFeatureValue;
import hybrid.queryMachine.QueryDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;

/**
 * This class represents the proportion of true groundings and the number of possible groundings.
 * @author irma
 *
 */
public class Proportion extends DiscreteInputContinuousOutput{
  
	public Proportion(Standard_Conjunction c) {
		super(c);
	}

	@Override
	public boolean isDiscreteInput() {
		return true;
	}

	@Override
	public boolean isContinuousInput() {
		return false;
	}
	
	@Override
	public boolean isDeterministic() {
		return false;
	}

	@Override
	public boolean isContinuousOutput() {
		return true;
	}

	@Override
	public boolean isDiscreteOutput() {
		return false;
	}

	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue==null){
			return new UndefinedValue();
		}
		NumberValue val=null;
		try {
			val = new NumberValue((new Double(featureValue.getValues().get(0).toNumber())/featureValue.getValues().get(1).toNumber())*100);
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
	    return val;
	}

	public String toString(){
		return super.toString()+"PROP"+" "+this.conjunction.toString();
	}

	@Override
	public Range getRange() {
		return new RangeNumeric(0.0,1.0);
	}

	@Override
	public String getFeatureIdentifier() {
		String tmp="Prop";
		for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
			tmp+=a;
		}
		return tmp+=this.hashCode();
	}

	public String getFeatureIdentifier_weka() {
		String tmp="Prop";
		for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
			tmp+=a.getAtom().getPredicate().getPredicateName()+a.getLogvarsDashDelimited()+"_";
		}
		return tmp+=String.valueOf(this.hashCode()).replace("-", "");
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public boolean is_with_operator() {
		return false;
	}

	@Override
	public Value dispatch(QueryDispatcher queryDisp) {
		return queryDisp.getValue(this);
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convertFeature(this);
	}

	
	

}
