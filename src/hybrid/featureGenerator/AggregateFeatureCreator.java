package hybrid.featureGenerator;

import hybrid.features.Average;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.network.DiscretePredicate;
import hybrid.network.NumericalPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Create all aggregate features for a conjunction
 * @author irma
 *
 */
public class AggregateFeatureCreator {

	public List<? extends Feature> getallAggregateFeatures(Standard_Conjunction conj) throws FeatureTypeException {
		List<Feature> tmp=new ArrayList<Feature>();

		if(conj.getNon_boolean_literal()!=null){
			if(conj.getNon_boolean_literal().getAtom().getPredicate() instanceof DiscretePredicate){
				tmp.add(new Mode(conj));
			}
			else if(conj.getNon_boolean_literal().getAtom().getPredicate() instanceof NumericalPredicate){
				tmp.add(new Average(conj));
				tmp.add(new Max(conj));
				tmp.add(new Min(conj));
			}
		}
		else{
			tmp.add(new Exist(conj));
		}
		return tmp;
	}

}
