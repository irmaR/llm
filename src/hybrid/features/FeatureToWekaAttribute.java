package hybrid.features;

import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import weka.core.Attribute;
import weka.core.FastVector;

public class FeatureToWekaAttribute {

	public FeatureToWekaAttribute() {
		// TODO Auto-generated constructor stub
	}


	public Attribute featureToWekaAttribute(Feature ft){
		if(ft instanceof ValueFt){
			return this.featureToWekaAttributeValue((ValueFt)ft);
		}
		if(ft instanceof Mode){
			return this.featureToWekaAttributeMode((Mode)ft);
		}
		if(ft instanceof Average){
			return this.featureToWekaAttributeAverage((Average)ft);
		}
		if(ft instanceof ValueFt){
			return this.featureToWekaAttributeMax((Max)ft);
		}
		if(ft instanceof Min){
			return this.featureToWekaAttributeMin((Min)ft);
		}
		if(ft instanceof Max){
			return this.featureToWekaAttributeMax((Max)ft);
		}
		if(ft instanceof Proportion){
			return this.featureToWekaAttributeProportion((Proportion)ft);
		}
		if(ft instanceof DiscretizedProportion){
			return this.featureToWekaAttributeDiscretizedProportion((DiscretizedProportion)ft);
		}
		if(ft instanceof OperatorFeature){
			return this.featureToWekaAttributeOperatorFeature((OperatorFeature)ft);
		}
		if(ft instanceof ComparisonFeature){
			return this.featureToWekaAttributeComparisonFeature((ComparisonFeature)ft);
		}
		if(ft instanceof Exist){
			return this.featureToWekaAttributeExist((Exist)ft);
		}
		return null;


	}

	private Attribute featureToWekaAttributeComparisonFeature(
			ComparisonFeature ft) {
		return new Attribute(ft.getFeatureIdentifier_weka());
	}


	private Attribute featureToWekaAttributeOperatorFeature(OperatorFeature ft) {
		return new Attribute(ft.getFeatureIdentifier_weka());
	}


	public Attribute featureToWekaAttributeMode(Mode ft){
		FastVector rangeFeature=new FastVector();
		for(Value range:((RangeDiscrete)ft.getRange()).getValues()){
			rangeFeature.addElement(range.toString());
		}
		return new Attribute(ft.getFeatureIdentifier_weka(),rangeFeature);

	}

	public Attribute featureToWekaAttributeValue(ValueFt ft){
		FastVector rangeFeature=new FastVector();
		if(ft.getRange() instanceof RangeDiscrete){
			for(Value range:((RangeDiscrete)ft.getRange()).getValues()){
				rangeFeature.addElement(range.toString());
			}
			return new Attribute(ft.getFeatureIdentifier_weka(),rangeFeature);
		}
		else{
			return new Attribute(ft.getFeatureIdentifier_weka());
		}
	}

	public Attribute featureToWekaAttributeAverage(Average ft){
		return new Attribute(ft.getFeatureIdentifier_weka());
	}

	public Attribute featureToWekaAttributeMax(Max ft){
		return new Attribute(ft.getFeatureIdentifier_weka());	
	}

	public Attribute featureToWekaAttributeMin(Min ft){
		return new Attribute(ft.getFeatureIdentifier_weka());
	}

	public Attribute featureToWekaAttributeProportion(Proportion ft){
		return new Attribute(ft.getFeatureIdentifier_weka());

	}
	
	public Attribute featureToWekaAttributeDiscretizedProportion(DiscretizedProportion ft){
		FastVector rangeFeature=new FastVector();
		for(Value range:((RangeDiscrete)ft.getRange()).getValues()){
			rangeFeature.addElement(range.toString());
		}
		return new Attribute(ft.getFeatureIdentifier_weka(),rangeFeature);

	}

	public Attribute featureToWekaAttributeExist(Exist ft){
		FastVector rangeFeature=new FastVector();
		for(Value range:((RangeDiscrete)ft.getRange()).getValues()){
			rangeFeature.addElement(range.toString());
		}
		return new Attribute(ft.getFeatureIdentifier_weka(),rangeFeature);
	}


}
