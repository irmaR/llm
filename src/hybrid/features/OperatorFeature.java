package hybrid.features;

import hybrid.comparators.Comparator;
import hybrid.comparators.InBetween;
import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.RangeNumeric;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.QueryDispatcher;

import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;

/**
 * Complex feature represents a feature that operates between two continuous atoms with an operator defined on them. 
 * Complex feature can also have a comparator specified that tests the range of feature's value.
 * For example, if we set lower bound(thershold1)=0 and upper bound (threshold2)=1,operator (+) and comparator in between, and processing feature Average
 * then we will have the feature Average(threshold1 < atom1 + atom2 < threshold2), returns true or false.
 * In case you don't set thresholds, it means you want to know the raw value of the operator applied to atoms, that is:
 * Average(atom1 + atom2), returns a number representing the average value of the sum of two atoms.
 * @author irma
 *
 */
public class OperatorFeature extends Feature<ArrayFeatureValues>{
    
	private ComplexConjunction conjunction;
	private Feature non_deterministic_feature;
	private Comparator comparator;
	
	public OperatorFeature(ComplexConjunction c,Comparator comparator,Feature processing_feature) {
		super(c);
		this.conjunction=c;
		this.non_deterministic_feature=processing_feature;
		this.comparator=comparator;
	}
	
	public OperatorFeature(ComplexConjunction c,Feature processing_feature) {
		super(c);
		this.conjunction=c;
		this.non_deterministic_feature=processing_feature;
		this.comparator=null;
	}


	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		   Value value=non_deterministic_feature.processValue(featureValue);
		   if(this.comparator==null){
			   return value;
		   }
		   else{
			   return new BoolValue(this.comparator.compare(value));
		   }
	}
	
	public ComplexConjunction getConjunction(){
		return this.conjunction;
	}
	
	public String toString(){
		if(this.comparator!=null){
		return super.toString()+" "+this.non_deterministic_feature+" {"+conjunction.toString()+" "+comparator.toString()+"}";
		}
		else{
			return super.toString()+" "+this.non_deterministic_feature+" {"+conjunction.toString()+"}";
		}
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
	public boolean isContinuousOutput() {
		if(this.comparator==null){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean isDiscreteOutput() {
		if(this.comparator==null){
			return false;
		}
		else{
			return true;
		}
	}

	@Override
	public boolean isDeterministic() {
		return this.non_deterministic_feature.isDeterministic();
	}

	@Override
	public Range getRange() {
		if(this.comparator==null){
			return new RangeNumeric(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
		else{
			return new RangeDiscrete(new BoolValue[]{new BoolValue("true"),new BoolValue("false")});
		}
	}

	@Override
	public String getFeatureIdentifier() {
		String tmp= this.non_deterministic_feature.getFeatureIdentifier_weka()+"_"+this.conjunction;
		for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
			tmp+=a;
		}
		return tmp+=this.hashCode();
	}
	
	public Feature getProcessingFeature(){
		return this.non_deterministic_feature;
	}


	@Override
	public boolean isComplex() {
		return true;
	}
	
	public Comparator getComparator(){
		return this.comparator;
	}
	
	public void setComparator(Comparator comp){
		this.comparator=comp;
	}
	
	public void setAggregateFunction(Feature f){
		this.non_deterministic_feature=f;
	}

	@Override
	public boolean is_with_operator() {
		return this.conjunction.isWithOperator();
	}

	@Override
	public Value dispatch(QueryDispatcher queryDisp) {
		return queryDisp.getValue(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		//result = prime * result
		//		+ ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result
				+ ((conjunction == null) ? 0 : conjunction.hashCode());
		result = prime
				* result
				+ ((non_deterministic_feature == null) ? 0
						: non_deterministic_feature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		OperatorFeature other = (OperatorFeature) obj;
		if (comparator == null) {
			if (other.comparator != null)
				return false;
		} else if (!comparator.equals(other.comparator)){
			return false;}
		if (conjunction == null) {
			if (other.conjunction != null)
				return false;
		} else if (!conjunction.equals(other.conjunction)){
			return false;}
		if (non_deterministic_feature == null) {
			if (other.non_deterministic_feature != null)
				return false;
		} else if (!non_deterministic_feature.equals(other.non_deterministic_feature)){
			return false;
		}
			
		return true;
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convert(this);
	}
	
	
	
	
}
