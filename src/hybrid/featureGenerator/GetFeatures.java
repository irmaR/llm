package hybrid.featureGenerator;

import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Value;
import hybrid.queryMachine.QueryMachine;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

import java.util.HashMap;
import java.util.List;

public abstract class GetFeatures {

	public abstract List<Feature> getListOfFeatures(Atom at, NetworkInfo ntw,QueryMachine queryData,HashMap<Feature,Value> filter) throws Exception;


}
