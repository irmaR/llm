package hybrid.network;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.FeatureTypeException;

import java.util.HashMap;

public interface ApplicationNetwork {

	public NetworkInfo defineApplicationNetwork(int subsampling_ratio);
	public HashMap<Atom,Dependency> getTrueDependencies(NetworkInfo ntw) throws FeatureTypeException, ConjunctionConstructionProblem;
	
}
