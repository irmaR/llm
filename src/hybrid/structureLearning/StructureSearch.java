package hybrid.structureLearning;

import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.queryMachine.QueryMachine;

import java.util.List;

public abstract class StructureSearch {

	public abstract DNdependency performSearchForAtom(Atom a,List<Feature> features,QueryMachine query_machine_training, QueryMachine query_machine_validation,QueryMachine test);

}
