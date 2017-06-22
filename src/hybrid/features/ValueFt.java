package hybrid.features;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.QueryDispatcher;

import java.util.List;
import java.util.Set;

import alice.tuprolog.NoSolutionException;
/**
 * This class represents a value feature. It just represents a function over exactly one element.
 * 
 * @author irma
 * @param <T>
 *
 */
public  class ValueFt extends Feature<ArrayFeatureValues> {

	private Standard_Conjunction c;

	public ValueFt(){

	}

	public ValueFt(Standard_Conjunction c){
		super(c);
		this.c=c;
	}


	@Override
	public boolean isDiscreteInput() {
		if(this.c.getNon_boolean_literal().getAtom().getPredicate().isDiscrete()){
			return true;
		}
		return false;
	}

	@Override
	public boolean isContinuousInput() {
		if(this.c.getNon_boolean_literal().getAtom().getPredicate().isDiscrete()){
			return false;
		}
		return true;
	}

	@Override
	public boolean isContinuousOutput() {
		if(this.c==null){
			return false;
		}
		if(this.c.getNon_boolean_literal().getAtom().getPredicate().isDiscrete()){
			return false;
		}
		return true;
	}

	@Override
	public boolean isDiscreteOutput() {
        if(this.c==null){
        	return false;
        }
		if(this.c.getNon_boolean_literal()==null && this.c.getBooleanAtoms().size()!=0){
			//it is a boolean feature hence discrete
			return true;
		}
		if(this.c.getNon_boolean_literal().getAtom().getPredicate().isDiscrete()){
			return true;
		}
		return false;
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
		ValueFt other = (ValueFt) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}

	public String toString(){
		if(this.c==null){
			return super.toString()+" Value";
		}
		return super.toString()+"Value "+this.c.toString();
	}



	@Override
	public Range getRange() {
		if(this.c==null){
			return new RangeDiscrete(new String[]{});
		}
		return this.c.getNon_boolean_literal().getAtom().getPredicate().getRange();
	}



	@Override
	public String getFeatureIdentifier() {
		String tmp="Val";
		for(Literal a:((List<Literal>)this.c.getLiteralList())){
			tmp+=a;
		}
		return tmp+=this.hashCode();
	}

	public String getFeatureIdentifier_weka() {
		String tmp="Val";
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
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue.getValues().size()==0){
			return new UndefinedValue();
		}
		return featureValue.getValues().get(0);
	}


	@Override
	public boolean isDeterministic() {
		return true;
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
