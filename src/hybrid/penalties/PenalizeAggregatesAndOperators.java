package hybrid.penalties;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;

public class PenalizeAggregatesAndOperators implements SpecialPenalties<Feature> {

	private Feature f;

	@Override
	public double scalePenalty(Feature f,Double currentPenalty) {
		if(!f.isDeterministic() || f.isComplex()){
			if(f.isContinuousOutput()){
				return currentPenalty;
			}
			return currentPenalty*AlgorithmParameters.getPenaltyOnFeatures();
		}
		return currentPenalty;
	}



}
