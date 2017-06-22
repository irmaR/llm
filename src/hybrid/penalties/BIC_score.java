package hybrid.penalties;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;

public class BIC_score extends Penalty{

	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		return (hybrid.utils.Logarithm2.logarithm2(nr_data_points)*hybrid.utils.Logarithm2.logarithm2(dep.getCpd().getParameters().getNumberOfFreeParameters()))/AlgorithmParameters.get_penalty_coefficient();
	}

	public String toString(){
		return "BIC PENALTY as log2(nr_data_points)*number_of_free_parameters)/2";
	}

}
