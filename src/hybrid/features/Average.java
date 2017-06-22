package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.NonDeterministicFeatureEvaluator;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NumericalPredicate;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.QueryDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;
/**
 * Average represents a feature finding the average value of a list of values
 * @author irma
 * @param <T>
 *
 */
public class Average extends ContinuousOutputAggregate {

	
	public Average(){
		super();
	}
	
	public Average(Standard_Conjunction c){
		super(c);
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
		    return super.toString()+"Avg"+" "+this.conjunction;
		}
		else{
			return super.toString()+"Avg";
		}
	}


	@Override
	public Range getRange() {
		return this.conjunction.getNon_boolean_literal().getAtom().getPredicate().getRange();
	}


	@Override
	public String getFeatureIdentifier() {
		String tmp="Avg";
		if(this.conjunction!=null){
			for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
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
		double sum=0;
		for(Double v:values){
			sum+=v;
		}
		return new NumberValue(sum/values.size());
	}



	public String getFeatureIdentifier_weka() {
		String tmp="Avg";
		if(this.conjunction!=null){
			for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
				tmp+=a.getAtom().getPredicate().getPredicateName()+a.getLogvarsDashDelimited()+"_";
			}
		}
		return tmp+=String.valueOf(this.hashCode()).replace("-", "");
	}


	@Override
	public boolean is_with_operator() {
		return false;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public Value dispatch(QueryDispatcher queries){
		return queries.getValue(this);
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convertFeature(this);
	}	


}
