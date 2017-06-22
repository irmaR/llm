package hybrid.featureGenerator;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.PosLiteral;

import java.util.ArrayList;
import java.util.List;

/**
 * Feature generator with logvar restrictions
 * @author irma
 *
 */
public class FeatureGeneratorWithLogvarRestrictions extends FeatureGeneratorAbstract {

	private int length;
	private int max_num_logvars;
	private String restriction;
	private SampleFeatureSpace featureSpaceSampler;
	private int max_num_boolean_literals=2;
	
	/**
	 * Instantiate feature generator with a predefined maximum length of features
	 * @param length
	 * @param max_num_logvars TODO
	 */
	public FeatureGeneratorWithLogvarRestrictions(int length, int max_num_logvars,String restriction){
		this.length=length;
		this.max_num_logvars=max_num_logvars;
		this.restriction=restriction;
	}
	
	public FeatureGeneratorWithLogvarRestrictions(int length, int max_num_logvars, String restriction,SampleFeatureSpace sampleFeatureSpace) {
		this.length=length;
		this.max_num_logvars=max_num_logvars;
		this.restriction=restriction;
		this.featureSpaceSampler=sampleFeatureSpace;
	}

	@Override
	public List<Feature> generateFeatures(Atom head,List<Atom> predicates){
		int feature_index=0;
		
		FeatureFilter ft=new FeatureFilter();
		List<Feature> features=new ArrayList<Feature>();
		FeatureCreator featureCreatorWithLogvarRestriction=new FeatureCreator();	
		
	
		List<Literal> literals_positive=new ArrayList<Literal>();
		for(Atom a:predicates){
			literals_positive.add(new PosLiteral(a));
		}
		
		//get list of proposed conjunctions of predefined length
		AtomCombinationCreator combinationCreator=new AtomCombinationCreator();
		List<ProposedConjunction> proposedConjuctions=combinationCreator.getallPossibleCombinationUpToLength(literals_positive,length,head, max_num_logvars,max_num_boolean_literals);
		//debugging
		for(ProposedConjunction p:proposedConjuctions){
			System.out.println("Proposed conjunction: "+p);
		}
		if(AlgorithmParameters.isDebuggingFlag()){
			for(ProposedConjunction p:proposedConjuctions){
				hybrid.loggers.Debugger.println("Proposed conjunction: "+p);
			}
		}
		//rename the proposed conjunction (if internal atoms present)
		RenamedConjunctionsCreator renameator=new RenamedConjunctionsCreator();
		List<Standard_Conjunction> renamedConjunctions=renameator.createAllRenamingCombinationsWithLogvarRestrictions(proposedConjuctions, head,restriction);	
		
		//debugging
		if(AlgorithmParameters.isDebuggingFlag()){
			for(AbstractConjunction p:renamedConjunctions){
				hybrid.loggers.Debugger.println("Renamed conjunction: "+p);
			}
		}
		for(AbstractConjunction p:renamedConjunctions){
			System.out.println("Renamed conjunction: "+p);
		}
		//Create features for the conjunction
		int block=0;
		for(Standard_Conjunction c:renamedConjunctions){
			List<Feature> features_gen=featureCreatorWithLogvarRestriction.createFeature(c,block++);
			for(Feature f:features_gen){
				f.setIndexInFeatureSpace(feature_index++);
				
			}
			features.addAll(features_gen);
		}
		
		if(this.featureSpaceSampler!=null){
			return this.featureSpaceSampler.samplefeatures(features);
		}
		else{
		return features;
		}
	}
	
	
	@Override
	public List<Feature> generateFeaturesWithNegativeExamples(Atom head,
			List<Atom> predicates) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}
