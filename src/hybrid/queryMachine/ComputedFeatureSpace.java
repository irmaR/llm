package hybrid.queryMachine;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Value;

import java.util.HashMap;
import java.util.List;

public class ComputedFeatureSpace {

	
	private Data data;
	private HashMap<Interpretation,Cache> featureValues;
	
	public ComputedFeatureSpace(Atom a, List<Feature> featureSpace,Data d){
		featureValues=new HashMap<Interpretation, Cache>();
		this.data=d;
		for(Interpretation i:d.getInterpretations()){
			featureValues.put(i, new Cache(a,featureSpace));
		}
	}
	/**
	 * Add value val of feature ft in interpretation i for ground head atom g.
	 * @param i
	 * @param g
	 * @param ft
	 * @param val
	 */
	public void addValueForFeatureAndInterpretation(Interpretation i, GroundAtom g,Feature ft,Value val){
		this.featureValues.get(i).addFeatureCache(ft, g, val);
	}
	
	
	public void addValueForFeatureAndInterpretation(Interpretation i,FeatureCache ftCache){
		this.featureValues.get(i).addFeatureCache(ftCache.getF(), ftCache);
	}
	
	public String toString(){
		String tmp="";
		for(Interpretation i:featureValues.keySet()){
			tmp+= " Interpretation : "+i.getId()+ " Cache: \n";
			tmp+=featureValues.get(i);
		}
		return tmp;
	}
	public Data getData() {
		return data;
	}
	
	public HashMap<Interpretation, Cache> getFeatureValues() {
		return featureValues;
	}
	
	/**
	 * Get Markov blanket for interpretation i, ground atom head and dependency dep.
	 * @param i
	 * @param head
	 * @param dep
	 * @return
	 */
	public MarkovBlanket getMarkovBlanket(Interpretation i, GroundAtom head,Dependency dep){
		HashMap<Feature,Value> ftValues=new HashMap<Feature, Value>();
		
		for(Feature ft:dep.getFeatures()){
			ftValues.put(ft, this.featureValues.get(i).getFeatureCaches().get(ft).getCache().get(head));
		}
		return new MarkovBlanket(head,dep,ftValues);
		
	}
	
}
