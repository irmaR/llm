package hybrid.dependencies;

import java.util.HashMap;

import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.GroundAtom;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;


/**
 * This class represents a markov blanket ( for head ground atom it specifies the values of its parents (features))
 * in a specific interpretation
 * @author irma
 *
 */
public class MarkovBlanket {

	private GroundAtom head;
	private HashMap<Feature,Value> featureValues;
	private Dependency dep;
	private String pathToInterpretation;

	
	
	/**
	 * Create a Markov blanket for ground atom head, and specifying the values of features in its parent set.
	 * @param head - head (target) atom for the dependency
	 * @param featureValues - values for features in the parent set
	 * @param dependency - belongs to a depenendency de
	 */
	public MarkovBlanket(GroundAtom head, Dependency dep, HashMap<Feature,Value> featureValues){
		this.head=head;
		this.featureValues=featureValues;
		this.dep=dep;
	}
	
	/**
	 * Create a Markov blanket for ground atom head. The parent set is empty.
	 * @param head
	 * @param featureValues
	 */
	public MarkovBlanket(GroundAtom head){
		this.head=head;
	}



	public GroundAtom getHead() {
		return head;
	}



	public void setHead(GroundAtom head) {
		this.head = head;
	}

    /**
     * Given a specific instantiation for features, check if this markov blanket contains that 
     * instantiation
     * @param v
     * @return
     */
    public boolean featureHasValues(HashMap<Feature,Value> v){
    	boolean res=true;
    	for(Feature f:v.keySet()){
    		if(this.featureValues.containsKey(f) && !this.featureValues.get(f).equals(v.get(f))){
    			return false;
    		}
    	}
		return res;
    }

	public HashMap<Feature, Value> getFeatureValues() {
		return featureValues;
	}


    
	public void setFeatureValues(HashMap<Feature, Value> featureValues) {
		this.featureValues = featureValues;
	}
	
	public String toString(){
		return head+"="+head.getValue()+ " | "+featureValues;
	}

	public Dependency getDep() {
		return dep;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dep == null) ? 0 : dep.hashCode());
		result = prime * result
				+ ((featureValues == null) ? 0 : featureValues.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkovBlanket other = (MarkovBlanket) obj;
		if (dep == null) {
			if (other.dep != null)
				return false;
		} else if (!dep.equals(other.dep))
			return false;
		if (featureValues == null) {
			if (other.featureValues != null)
				return false;
		} else if (!featureValues.equals(other.featureValues))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		return true;
	}
	
	public boolean hasUndefinedValue(){
		for(Feature f:featureValues.keySet()){
			if(featureValues.get(f) instanceof UndefinedValue){
				return true;
			}
		}
		return false;
	}

	public String getPathToInterpretation() {
		return pathToInterpretation;
	}

	public void setPathToInterpretation(String pathToInterpretation) {
		this.pathToInterpretation = pathToInterpretation;
	}

	
	
	
	
}
