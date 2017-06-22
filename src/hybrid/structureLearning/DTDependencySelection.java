package hybrid.structureLearning;

import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Value;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class DTDependencySelection {
	public abstract LearnedDependency selectBestDependency(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score) throws IOException;
	public abstract void reset();
	
}
