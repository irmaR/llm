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
import hybrid.queryMachine.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;

/**
 * The feature representing the maximum of values for a conjunction in an interpretation.
 * @author irma
 * @param <T>
 *
 */

public class Max extends ContinuousOutputAggregate implements NonDeterministicFeatureEvaluator{
	private AbstractConjunction c;
    
	public Max(){
		
	}
	
	public Max(Standard_Conjunction c) throws FeatureTypeException {
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
		return false;
	}

	public String toString(){
		if(this.conjunction!=null){
		    return super.toString()+"Max"+" "+this.conjunction;
		}
		else{
			return super.toString()+"Max";
		}
	}



	@Override
	public Range getRange() {
		return this.c.getNon_boolean_literal().getAtom().getPredicate().getRange();
	}



	@Override
	public String getFeatureIdentifier() {
		String tmp="Max";
		if(this.conjunction!=null){
			for(Literal a:((List<Literal>)this.c.getLiteralList())){
				tmp+=a;
			}
		}
		return tmp+=this.hashCode();
	}

	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue.getValues().size()==0){
			return new UndefinedValue();
		}
		if(featureValue==null){
			return new UndefinedValue();
		}
		NumberValue max=new NumberValue(Double.NEGATIVE_INFINITY);
		for(Value v:featureValue.getValues()){
			try {
				if(new NumberValue(v.toNumber()).biggerThan(max)){
					max=new NumberValue(v.toNumber());
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		return max; 
	}

	public String getFeatureIdentifier_weka() {
		String tmp="Max";
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
		// TODO Auto-generated method stub
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
		Max other = (Max) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}

	
	
}
