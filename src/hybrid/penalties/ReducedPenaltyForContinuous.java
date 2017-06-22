package hybrid.penalties;

import hybrid.dependencies.Dependency;

public class ReducedPenaltyForContinuous extends Penalty {

	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		//TODO REDUCED HOW?
		if (dep.getFeatures().get(dep.getFeatures().size()-1).isContinuousOutput()){
			return 0.0;
		}
		else{
			return new MDLPenalty().calculatePenalty(dep, nr_data_points);
		}
	}

}
