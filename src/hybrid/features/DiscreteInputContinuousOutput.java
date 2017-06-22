package hybrid.features;

import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.queryMachine.ArrayFeatureValues;

public abstract class DiscreteInputContinuousOutput extends Feature<ArrayFeatureValues>{

	public DiscreteInputContinuousOutput(Standard_Conjunction c) {
		super(c);
	}

}
