package hybrid.features;

import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.queryMachine.ArrayFeatureValues;

public abstract class ContinuousOutputAggregate extends Feature<ArrayFeatureValues> {

	public ContinuousOutputAggregate(Standard_Conjunction c) {
		super(c);
	}

	public ContinuousOutputAggregate() {
		
	}


}
