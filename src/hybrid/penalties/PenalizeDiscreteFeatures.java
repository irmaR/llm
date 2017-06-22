package hybrid.penalties;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;

public class PenalizeDiscreteFeatures implements SpecialPenalties<Feature> {
	private Feature f;

	@Override
	public double scalePenalty(Feature f,Double currentPenalty) {
		if(f.isDiscreteOutput()){
			return currentPenalty*AlgorithmParameters.getPenaltyOnFeatures();
		}
		return currentPenalty;
	}
}
