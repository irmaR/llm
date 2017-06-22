package hybrid.queryMachine;

import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Value;

import java.util.HashMap;
import java.util.List;
/**
 * This class contains calculated feature for atom a
 * @author irma
 *
 */
public class Cache {

	private Atom a;
	private HashMap<Feature, FeatureCache> featureCaches;
	
	public Cache(Atom a,List<Feature> featureSpace){
		featureCaches=new HashMap<Feature, FeatureCache>();
		this.a=a;
		for(Feature f:featureSpace){
			featureCaches.put(f, new FeatureCache(f));
		}
	}
	
	public void addFeatureCache(Feature ft,GroundAtom g,Value val){
		this.featureCaches.get(ft).addValue(g,val);
	}
	
	public void addFeatureCache(Feature ft,FeatureCache ftCache){
		this.featureCaches.put(ft, ftCache);
	}

	public Atom getA() {
		return a;
	}

	public HashMap<Feature, FeatureCache> getFeatureCaches() {
		return featureCaches;
	}
	
	public String toString(){
		String tmp=" ";
		for(Feature f:featureCaches.keySet()){
			tmp+= "---------------------------- Ft cache: "+f+" -------------------------------- \n:";
			tmp+=featureCaches.get(f);
		}
		return tmp;
	}
	
	
}
