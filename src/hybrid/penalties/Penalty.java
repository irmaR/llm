package hybrid.penalties;

import hybrid.dependencies.Dependency;
import hybrid.interpretations.Data;
import hybrid.parameters.Parameters;

/**
 * This class represents the penalty used for penalizing the more complex structures.
 * @author irma
 *
 */
public abstract class Penalty {
	public abstract double calculatePenalty(Dependency dep,long nr_data_points);
	/**
	 * This method is used to instantiate the right penalty given a string
	 * @param penalty_name
	 * @return
	 */
	public static Penalty instantiatePenalty(String penalty_name) {
		if(penalty_name.equals("mdl")){
			return new MDLPenalty();
		}
		if(penalty_name.equals("npfc")){
			return new NoPenaltyForContinuous();
		}
		if(penalty_name.equals("rpfc")){
			return new ReducedPenaltyForContinuous();
		}
		if(penalty_name.equals("ndpp")){
			return new NoDataPointsPenalty();
		}
		if(penalty_name.equals("bic")){
			return new BIC_score();
		}
		if(penalty_name.equals("complex")){
			return new Complex_penalty();
		}
		else if(penalty_name.equals("none")){
			return new NoPenalty();
		}
		return null;
	}
	
}
