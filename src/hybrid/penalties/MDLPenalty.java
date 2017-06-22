package hybrid.penalties;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.interpretations.Data;
import hybrid.parameters.Parameters;

public class MDLPenalty extends Penalty {

	/**
	 * Calculate MDL penalty
	 */
	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		if(nr_data_points==0){
			return dep.getCpd().getParameters().getNumberOfFreeParameters()*dep.getNumLiterals()/AlgorithmParameters.get_penalty_coefficient();

		}
		return (hybrid.utils.Logarithm2.logarithm2(nr_data_points)*dep.getCpd().getParameters().getNumberOfFreeParameters()*dep.getNumLiterals())/AlgorithmParameters.get_penalty_coefficient();
	}


	public String toString(){
		return "Default MDL PENALTY as log2(nr_data_points)*number_of_free_parameters*number_of_literals_per_feature)/2";
	}

}
