package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.NonDeterministicRangeBoundEvaluator;
import hybrid.features_evaluators.SpecialEvaluator;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.FeatureValue;
import hybrid.queryMachine.NumberFeatureValue;
import hybrid.queryMachine.QueryDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;

/**
 * This feature checks if there exist a solution for a query. The values to the query are given
 * through an array of calues
 * @author irma
 *
 */
public class Exist extends DiscreteInputContinuousOutput{

	public Exist(Standard_Conjunction c) {
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
	public boolean isContinuousOutput() {
		return false;
	}


	@Override
	public boolean isDiscreteOutput() {
		return true;
	}

	@Override
	public boolean isDeterministic() {
		return false;
	}
	
	public String toString(){
		return super.toString()+"Exist"+" "+this.conjunction.toString();
	}


	@Override
	public Range getRange() {
		//since there is no non-boolean atom in this feature
		//we need to return the range of a boolean one {true, false}
		return new RangeDiscrete(new BoolValue[]{new BoolValue(true),new BoolValue(false)});
		//return ((List<Literal>)this.conjunction.getLiteralList()).get(0).getAtom().getPredicate().getRange();
	}


	@Override
		public String getFeatureIdentifier() {
			String tmp="Ex";
			for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
				tmp+=a;
			}
			return tmp+=this.hashCode();
		}
	
	
	
	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue==null){
			return new UndefinedValue();
		}
		try {
			if(featureValue.getValues().get(0).toNumber()==1.0){
				return new BoolValue(true);
			}
		} catch (WrongValueType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    return new BoolValue(false);
	}
	
	public String getFeatureIdentifier_weka() {
		String tmp="Ex";
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
	public Value dispatch(QueryDispatcher queryDisp)   {
		return queryDisp.getValue(this);
	}


	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convertFeature(this);
	}


	

	
}
