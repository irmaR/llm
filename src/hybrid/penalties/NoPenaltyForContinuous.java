package hybrid.penalties;

import hybrid.dependencies.Dependency;
/**
 * For this penalty we return 0 penalty if the last added feature is continuous.
 * In case the last added feature is discrete we return MDL penalty.
 * This for use in decision tree learning where we want to penalize more
 * adding the discrete features than the continuous features.
 * @author irma
 *
 */
public class NoPenaltyForContinuous extends Penalty {

	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		if (dep.getFeatures().size()!=0 && dep.getFeatures().get(dep.getFeatures().size()-1).isContinuousOutput()){
			return 0.0;
		}
		else{
			return new MDLPenalty().calculatePenalty(dep, nr_data_points);
		}
	}

}
