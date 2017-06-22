package hybrid.queryMachine;
import java.util.HashMap;

import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.*;

/**
 * Feature cache contains all mappings from ground atom to value gor a feature F
 * @author irma
 *
 */
public class FeatureCache {

	private Feature f;
	private HashMap<GroundAtom,Value> cache;

	/**
	 * Initialize feature's f cache for an interpretation
	 * @param f
	 * @param i
	 */
	public FeatureCache(Feature f){
		this.f=f;
		cache=new HashMap<GroundAtom, Value>();
	}
	
	/**
	 * Add value for ground atom a 
	 * @param a
	 * @param val
	 */
	public void addValue(GroundAtom a,Value val){
		cache.put(a, val);
	}
	
	public String toString(){
		String tmp="";
		for(GroundAtom g:cache.keySet()){
			tmp+=g+" | "+f+" = "+cache.get(g)+"\n";
		}
		return tmp;
	}

	public Feature getF() {
		return f;
	}

	public HashMap<GroundAtom, Value> getCache() {
		return cache;
	}
	
	
}
