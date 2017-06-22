package hybrid.featureGenerator;

import hybrid.features.Average;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.Proportion;

import java.util.ArrayList;
import java.util.List;

public class NonDeterministicFeatureGenerator {
	public List<? extends Feature> getAllNonDeterministicFeatures(Standard_Conjunction conj) throws FeatureTypeException {
		List<Feature> tmp=new ArrayList<Feature>();
		if(conj.getNon_boolean_literal()==null){
			//only boolean atoms
			//if there is discretization in this experiment, proportion also needs to be discretized in levels
			if(hybrid.experimenter.AlgorithmParameters.isDiscretization_flag()){
				tmp.add(new DiscretizedProportion(conj,hybrid.experimenter.AlgorithmParameters.getDiscretization_level()));
			}
			else{
				tmp.add(new Proportion(conj));

			}
			tmp.add(new Exist(conj));
			
			return tmp;
		}
		if(conj.getNon_boolean_literal().getAtom().getPredicate().isDiscrete()){
			tmp.add(new Mode(conj));
			return tmp;
		}
		else{
			tmp.add(new Max(conj));
			tmp.add(new Min(conj));
			tmp.add(new Average(conj));
			return tmp;
		}
		
	}
}
