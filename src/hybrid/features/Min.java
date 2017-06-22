package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.NonDeterministicFeatureEvaluator;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.QueryDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;

/**
 * This feature represents the minumum of values for the conjunction in an interpretation
 * @author irma
 *
 */
public class Min extends ContinuousOutputAggregate implements NonDeterministicFeatureEvaluator {
	private AbstractConjunction c;

	
	public Min(){

	}
	
	public Min(Standard_Conjunction c) throws FeatureTypeException {
		super(c);
		this.c=c;
	}

	@Override
	public boolean isDiscreteInput() {
		return false;
	}

	@Override
	public boolean isContinuousInput() {
		return true;
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
		// TODO Auto-generated method stub
		return false;
	}

	public String toString(){
		if(this.conjunction!=null){
		    return super.toString()+"Min"+" "+this.conjunction;
		}
		else{
			return super.toString()+"Min";
		}
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Min other = (Min) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}

	@Override
	public Range getRange() {
		return this.c.getNon_boolean_literal().getAtom().getPredicate().getRange();
	}


	@Override
	public String getFeatureIdentifier() {
		String tmp="Min";
		if(this.conjunction!=null){
			for(Literal a:((List<Literal>)this.c.getLiteralList())){
				tmp+=a;
			}
		}
		return tmp+=this.hashCode();
	}

	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue==null){
			return new UndefinedValue();
		}
		List<Double> values=new ArrayList<Double>();
		for(Value v:featureValue.getValues()){
			try {
				values.add(v.toNumber());
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}

		if(values.size()==0){
			return new UndefinedValue();
		}
		NumberValue min=new NumberValue(Double.POSITIVE_INFINITY);
		for(Double v:values){
			if(new NumberValue(v).smallerThan(min)){
				min=new NumberValue(v);
			}
		}
		return min;

	}

	public String getFeatureIdentifier_weka() {
		String tmp="Min";
		if(this.conjunction!=null){
			for(Literal a:((List<Literal>)this.c.getLiteralList())){
				tmp+=a.getAtom().getPredicate().getPredicateName()+a.getLogvarsDashDelimited()+"_";
			}
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
	public Value dispatch(QueryDispatcher queryDisp)  {
		return queryDisp.getValue(this);
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convertFeature(this);
	}


}
