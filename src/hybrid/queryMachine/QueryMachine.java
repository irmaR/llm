package hybrid.queryMachine;

import java.util.HashMap;
import java.util.List;

import hybrid.cpds.CPD;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.network.Atom;
import hybrid.penalties.Penalty;
import hybrid.querydata.QueryData;

public abstract class QueryMachine {
	
	protected HashMap<Atom,ComputedFeatureSpace> cache;
	protected Data data;
	protected Penalty penalty;
	protected boolean useCache=true;
	/**
	 * Create a query machine which will operate on data with a specific penalty on 
	 * complex structures.
	 * @param data
	 * @param penalty
	 */
	public QueryMachine(Data data,Penalty penalty){
		this.data=data;
		this.penalty=penalty;
	}
	
	public ComputedFeatureSpace getCache(Atom a) {
		return cache.get(a);
	}


	public void setCache(Atom a,ComputedFeatureSpace cache) {
		if(this.cache==null){
			this.cache=new HashMap<Atom, ComputedFeatureSpace>();
		}
		this.cache.put(a, cache);
	}
	
	
	public ComputedFeatureSpace calculateCache(Atom atom,List<Feature> featureSpace,String procedure){
		return null;
	}
	
	public boolean isUseCache() {
		return useCache;
	}


	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	
	
	/**
	 * Return data in a format of QueryData for queries on CPD cpd.
	 * @param cpd
	 * @return
	 */
	public abstract QueryData getQueryResults(Dependency depend);

	public HashMap<Atom, ComputedFeatureSpace> getCache() {
		return cache;
	}

	public Data getData() {
		return data;
	}

	public Penalty getPenalty() {
		return penalty;
	}

	
	
}
