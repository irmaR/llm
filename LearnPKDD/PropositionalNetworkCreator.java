import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.FeatureTypeException;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;

import java.util.HashMap;


public abstract class PropositionalNetworkCreator {

	public abstract HashMap<Predicate, Dependency> getDependenciesForPropositionalizations(int i) throws ConjunctionConstructionProblem, FeatureTypeException;
	public abstract NetworkInfo getNtwInfo();
}
