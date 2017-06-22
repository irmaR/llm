package hybrid.features;

import java.util.ArrayList;
import java.util.List;

import alice.tuprolog.NoSolutionException;

import hybrid.converters.ConvertPoolInterface;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features_evaluators.SpecialEvaluator;
import hybrid.network.BoolValue;
import hybrid.network.Literal;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.ArrayFeatureValues;
import hybrid.queryMachine.NumberFeatureValue;
import hybrid.queryMachine.QueryDispatcher;

/**
 * This class represents a discretized proportion. That is, this feature is used instead of Proportion in case
 * when domain is discretized. 
 * @author irma
 *
 */

public class DiscretizedProportion extends DiscreteInputContinuousOutput implements SpecialEvaluator{
	private int discretization_level;
	private ArrayList<Level> discretization_levels;
	private Range range;
	
	public DiscretizedProportion(Standard_Conjunction c,int discretization_level) {
		super(c);
		this.discretization_level=discretization_level;
		this.discretization_levels=extractDiscretizationLevels();
		this.range=makeRange();

	}

	private Range makeRange() {
		RangeDiscrete range=new RangeDiscrete();
		for(Level l:this.discretization_levels){
			range.addValueToRange(new StringValue(l.getBin_name()));
		}
		return range;
	}

	@Override
	public Value calculateValues(Integer count, int numberOfPossibleGroundings) {
        return getDiscretizedCountProportion(this.discretization_levels,count,numberOfPossibleGroundings,discretization_level);
	}
	
	@Override
	public Value processValue(ArrayFeatureValues featureValue) {
		if(featureValue==null){
			return new UndefinedValue();
		}
        Value val=null;
		try {
			val = getDiscretizedCountProportion(this.discretization_levels,featureValue.getValues().get(0).toNumber().intValue(),featureValue.getValues().get(1).toNumber().intValue(),discretization_level);
		} catch (WrongValueType e) {
			e.printStackTrace();
		}
	    return val;
	}
	
	

	protected Value getDiscretizedCountProportion(ArrayList<Level> levels,Integer count,int numberOfPossibleGroundings, int discretization_level) {
        double percentage=(new Double(count)/numberOfPossibleGroundings)*100;
        for(Level l:this.discretization_levels){
        	if(percentage<=l.getMax() && percentage>=l.getMin()){
        		return new StringValue(l.getBin_name());
        	}
        }
		return null;
	}
	
	protected ArrayList<Level> extractDiscretizationLevels() {
		double nr_bins_length=100/this.discretization_level;
		double start=-1;
		ArrayList<Level> levels=new ArrayList<DiscretizedProportion.Level>();
		for(int i=0;i<this.discretization_level;i++){
			start++;
			double min=start;
			double max=start+nr_bins_length;
			if(max>100){
				max=100;
			}
			levels.add(new Level("lev"+(i+1),min,max));
			start=max;
		}
		return levels;
	}
	
	public void setDiscretization_level(int discretization_level) {
		this.discretization_level = discretization_level;
	}

	public void setDiscretization_levels(ArrayList<Level> discretization_levels) {
		this.discretization_levels = discretization_levels;
	}


	@Override
	public boolean isContinuousOutput() {
		return false;
	}

	@Override
	public boolean isDiscreteOutput() {
		return true;
	}
	
	public String toString(){
		return super.toString()+"DiscretePROP "+this.conjunction.toString();

	}

	@Override
	public Range getRange() {
		return this.range;
	}

	@Override
	public String getFeatureIdentifier() {
		String tmp="Prop";
		for(Literal a:((List<Literal>)this.conjunction.getLiteralList())){
			tmp+=a;
		}
		return tmp+=this.hashCode();
	}
	
	class Level{
		String bin_name;
		double min;
		double max;
		
		public Level(String bin_name,double min, double max){
			this.bin_name=bin_name;
			this.min=min;
			this.max=max;
		}

		public String getBin_name() {
			return bin_name;
		}

		public void setBin_name(String bin_name) {
			this.bin_name = bin_name;
		}

		public double getMin() {
			return min;
		}

		public void setMin(double min) {
			this.min = min;
		}

		public double getMax() {
			return max;
		}

		public void setMax(double max) {
			this.max = max;
		}
		
		public String toString(){
			return bin_name+"{"+min+","+max+"}";
		}
	}
	
	public String getFeatureIdentifier_weka() {
		String tmp="DisProp";
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
	public boolean isDeterministic() {
		// TODO Auto-generated method stub
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
