package hybrid.penalties;

import hybrid.dependencies.Dependency;

public class NoPenalty extends Penalty {

	@Override
	public double calculatePenalty(Dependency dep, long nr_data_points) {
		return 0.0;
	}

}
