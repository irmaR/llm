package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.NonDeterministicRangeBoundEvaluator;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.HashMapFeatureValue;
import hybrid.queryMachine.QueryDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import alice.tuprolog.NoSolutionException;

public class Mode extends Feature<HashMapFeatureValue>  {

	public Mode(Standard_Conjunction c) throws FeatureTypeException {
		 super(c);
	}
	


	@Override
	public boolean isDiscreteInput() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isContinuousInput() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isContinuousOutput() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isDiscreteOutput() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isDeterministic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString(){
		return super.toString()+"Mode"+" "+this.conjunction.toString();
	}


	@Override
	public String getFeatureIdentifier() {
		String tmp="Mod";
		for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
			tmp+=a;
		}
		return tmp+=this.hashCode();
	}
	
	public String getFeatureIdentifier_weka() {
		String tmp="Mod";
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
		// TODO Auto-generated method stub
		return false;
	}

 

	@Override
	public Range getRange() {
		return this.conjunction.getNon_boolean_literal().getAtom().getPredicate().getRange();
	}


	@Override
	public Value dispatch(QueryDispatcher queryDisp) {
		return queryDisp.getValue(this);
	}


	@Override
	public Value processValue(HashMapFeatureValue featureValue) {
		HashMap<String, Integer> results=featureValue.getCounts();
		StringValue mode = new StringValue();
		int maxFreq = 0;
		for (Entry<String, Integer> entry : results.entrySet()) {
			int freq = entry.getValue();
			if (freq > maxFreq) {
				maxFreq = freq;
				mode = new StringValue(entry.getKey());
			}
		}
	    if(mode.getValue().equals("null")){
	    	return new UndefinedValue();
	    }
		if(mode.getValue().isEmpty() || results.get(mode.getValue()).equals(new Integer(0))){
			return new UndefinedValue();
		}
		return mode;
	}



	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convertFeature(this);
	}


	

}
