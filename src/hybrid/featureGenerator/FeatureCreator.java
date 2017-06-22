package hybrid.featureGenerator;

import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for creating features. Each conjunction of atoms can represent possibly many 
 * features.
 * @author irma
 *
 */

public class FeatureCreator {

	/**
	 * Create a list of supported features
	 * @param conj
	 * @return
	 */
	public List<Feature> createFeature(Standard_Conjunction conj,int block_number){
		
		List<Feature> tmp=new ArrayList<Feature>();
		
		if(conj.isDeterministic()){
			tmp.addAll(getDeterministicFeatures(conj));
		}
		else{
			tmp.addAll(getNonDeterministicFeatures(conj));
		}
		
		//Make that all these features are in the same block number
		for(Feature ft:tmp){
			ft.setFeatureBlock(block_number);
		}
		return tmp;
	}

	private List<? extends Feature> getNonDeterministicFeatures(Standard_Conjunction conj) {
		NonDeterministicFeatureGenerator nFg=new NonDeterministicFeatureGenerator();
		try {
			return nFg.getAllNonDeterministicFeatures(conj);
		} catch (FeatureTypeException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<? extends Feature> getDeterministicFeatures(Standard_Conjunction conj) {
		DeterministicFeatureGenerator dFG=new DeterministicFeatureGenerator();
		return dFG.getAllDeterministicFeatures(conj);
	}
	
}
