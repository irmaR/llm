package hybrid.queryMachine;

import java.util.HashMap;

import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.GroundAtom;
import hybrid.network.Value;
/**
 * This class contains Markov blanket values for ground atom grAtom in a specific interpreatation.
 * @author irma
 *
 */
public class MarkovBlanketValues {

	private GroundAtom grAtom;
	private HashMap<Feature,Value> values;
	private Interpretation inter;
	
	
	
	public MarkovBlanketValues(Interpretation iter, GroundAtom grAtom, HashMap<Feature, Value> values){
		this.inter=iter;
		this.grAtom=grAtom;
		this.values=values;
	}
	public GroundAtom getGrAtom() {
		return grAtom;
	}
	public void setGrAtom(GroundAtom grAtom) {
		this.grAtom = grAtom;
	}
	public HashMap<Feature, Value> getValues() {
		return values;
	}
	public void setValues(HashMap<Feature, Value> values) {
		this.values = values;
	}
	
	
	
	
	
}
