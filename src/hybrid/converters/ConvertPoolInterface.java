package hybrid.converters;

import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.ComparisonFeatureContinuousOutput;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.OperatorFeature;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Value;
import hybrid.parameters.CGCoefficients;
import hybrid.parameters.GaussianCoefficients;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.LogisticCoefficients;
import hybrid.parameters.PMFCoefficients;

public interface ConvertPoolInterface {

	public abstract String convert(LinearGaussianCoeff coeff);
	
	public abstract String convert(LogisticCoefficients coeff);
	
	public abstract String convert(CGCoefficients coeff);

	public abstract String convert(GaussianCoefficients coeff);

	public abstract String convert(PMFCoefficients pmfCoefficients);

	public abstract String convertFeature(ComparisonFeature comparison_Feature);
	
	public abstract String convertFeature(ComparisonFeatureContinuousOutput comparison_Feature);

	public abstract String convertFeature(Average average);

	public abstract String convertFeature(
			DiscretizedProportion discretizedProportion);

	public abstract String convertFeature(Exist exist);

	public abstract String convertFeature(Max max);

	public abstract String convertFeature(Min min);

	public abstract String convertFeature(Mode mode);

	public abstract String convert(OperatorFeature operator_Feature);

	public abstract String convertFeature(Proportion proportion);

	public abstract String convertFeature(ValueFt valueFt);

	public abstract void setLastFeature();

	public abstract void setValueForFeature(Value value);

}