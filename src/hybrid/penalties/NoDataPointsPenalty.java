package hybrid.penalties;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;

public class NoDataPointsPenalty extends Penalty {

	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		return (dep.getCpd().getParameters().getNumberOfFreeParameters()*dep.getNumLiterals())/AlgorithmParameters.get_penalty_coefficient();
	}

}
