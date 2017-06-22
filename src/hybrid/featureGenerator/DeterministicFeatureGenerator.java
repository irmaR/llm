package hybrid.featureGenerator;

import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.ValueFt;

import java.util.ArrayList;
import java.util.List;

/**
 * Create all deterministic features for this conjunction
 * @author irma
 *
 */

public class DeterministicFeatureGenerator {

	public List<? extends Feature> getAllDeterministicFeatures(Standard_Conjunction conj) {
		List<Feature> tmp=new ArrayList<Feature>();
		if(!(conj.getNon_boolean_literal()==null)){
			tmp.add(new ValueFt(conj));
		}
		else{
			tmp.add(new Exist(conj));

		}
		return tmp;

	}
	
	
	
	
	
	
}
