package hybrid.querydata;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.Value;

import java.util.*;

public class QueryDataFilter {

	/**
	 * Given a query data associated with a dependency and a specific value assignment to it's 
	 * feature set, create new query data containing data points for those feature values.
	 * @param dep
	 * @param feature_values
	 * @param qd
	 * @return
	 */
	public QueryData filterQueryData(Dependency dep,HashMap<Feature,Value> feature_values,QueryData qd){
		QueryData newQd=new QueryData(dep);
		//System.out.println("MAPPING: "+feature_values+" is empty? "+feature_values.isEmpty());
		if(feature_values.isEmpty()){
			//System.out.println("Returning qd");
			return qd;
		}
		for(Interpretation i:qd.getQuery_results().keySet()){
			List<MarkovBlanket> markov_blankets=qd.getQuery_results().get(i);
			for(MarkovBlanket mb:markov_blankets){
				if(!AlgorithmParameters.getUseUndefinedValue()){
					if(mb.featureHasValues(feature_values) || mb.hasUndefinedValue()){
						newQd.addMarkovBlanket(i, mb);
					}
				}
				else{
					if(mb.featureHasValues(feature_values)){
						if(mb.hasUndefinedValue()){
							newQd.setHadUndefinedValue(true);
						}
						newQd.addMarkovBlanket(i, mb);
					}
				}

			}
		}
		return newQd;
	}

}
