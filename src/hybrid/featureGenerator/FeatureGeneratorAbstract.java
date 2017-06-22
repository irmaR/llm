package hybrid.featureGenerator;

import hybrid.features.Feature;
import hybrid.network.Atom;

import java.util.List;
/**
 * Abstract class for generating features for target predicate head
 * and a list of predicates to be added to the features
 * @author irma
 *
 */
public abstract class FeatureGeneratorAbstract {

	public abstract List<Feature> generateFeatures(Atom head,List<Atom> predicates);
	public abstract List<Feature> generateFeaturesWithNegativeExamples(Atom head,List<Atom> predicates);
	
}
