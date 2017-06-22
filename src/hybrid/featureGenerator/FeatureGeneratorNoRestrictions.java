package hybrid.featureGenerator;

import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.LiteralNotDefined;
import hybrid.network.NegLiteral;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Feature generator without restrictions 
 * @author irma
 *
 */
public class FeatureGeneratorNoRestrictions extends FeatureGeneratorAbstract {

	private int length;
	private int max_num_logvars;
	private int max_num_boolean_literals=2;
	private boolean generate_features=true;
	private int number_of_features_restriction=-1;
	private HashMap<Atom,List<Feature>> additional_features=new HashMap<Atom,List<Feature>>();
	private HashMap<Atom,List<Predicate>> exclusion_predicates;
	/**
	 * A user can set some additional features for atoms. This will be added to a's feature space
	 * @param fts
	 * @param a
	 */
	public void setAdditionalFeatures(List<Feature> fts,Atom a){
		if(this.additional_features.containsKey(a)){
			this.additional_features.get(a).addAll(fts);
		}
		else{
		this.additional_features.put(a, fts);
		}
	}
	
	public void setExclusionPredicates(HashMap<Atom,List<Predicate>> predicates){
		this.exclusion_predicates=predicates;
	}
	
	
	public int getNumber_of_features_restriction() {
		return number_of_features_restriction;
	}

	public void setNumber_of_features_restriction(int number_of_features_restriction) {
		this.number_of_features_restriction = number_of_features_restriction;
	}

	/**
	 * Instantiate feature generator with a predefined maximum length of features
	 * @param length - maximum length of each feature
	 * @param max_num_logvars - maximum number of different logvar names per feature
	 */
	public FeatureGeneratorNoRestrictions(int length, int max_num_logvars){
		this.length=length;
		this.max_num_logvars=max_num_logvars;

	}

	/**
	 * Set maximum number of boolean predicates for this feature generator.
	 * Default is 2
	 * @param max_num_boolean_literals
	 */
	public void setMaxNumberOfBooleanPredicates(int max_num_boolean_literals){
		this.max_num_boolean_literals=max_num_boolean_literals;
	}
	
	public void do_not_generate_features(){
		this.generate_features=false;
	}


	@Override
	/**
	 * Method for generating features for target predicate head and a list of 
	 * atoms to appear in the features
	 * This method uses only positive literals
	 */
	public List<Feature> generateFeatures(Atom head,List<Atom> predicates){
		int feature_index=0;
		
		if(!this.generate_features){
			if(this.additional_features.containsKey(head)){
				if(this.number_of_features_restriction!=-1 && this.number_of_features_restriction<this.additional_features.get(head).size()){
					return this.additional_features.get(head).subList(0, number_of_features_restriction);
				}
				else{
				return this.additional_features.get(head);
				}
			}
			else{
			return new ArrayList<Feature>();
			}
		}
		
		List<Feature> resulting_features=new ArrayList<Feature>();		
		FeatureCreator featureCreator=new FeatureCreator();	//specific feature creator depending on the nature of conjunction
		//create positive literals out of atomss
		List<Literal> literals_positive=new ArrayList<Literal>();
		for(Atom a:predicates){
			if(exclusion_predicates!=null && exclusion_predicates.containsKey(head) && exclusion_predicates.get(head).contains(a.getPredicate())){
				continue;
			}
			literals_positive.add(new PosLiteral(a));
		}
		//obtain proposed conjunctions of specific length and max number of allowed logvar renamings
		AtomCombinationCreator combinationCreator=new AtomCombinationCreator();
		List<ProposedConjunction> proposedConjuctions=combinationCreator.getallPossibleCombinationUpToLength(literals_positive,length, head, max_num_logvars,max_num_boolean_literals);
		//rename the proposed conjunction (if internal atoms present)
		RenamedConjunctionsCreator renameator=new RenamedConjunctionsCreator();
		List<Standard_Conjunction> renamedConjunctions=renameator.createAllRenamingCombinations(proposedConjuctions, head);	

		//Create features for the conjunction
		int block=0;
		for(Standard_Conjunction c:renamedConjunctions){
			List<Feature> features_gen=featureCreator.createFeature(c,block++);
			for(Feature f:features_gen){
				f.setIndexInFeatureSpace(feature_index++);
			}
			resulting_features.addAll(features_gen);
		}
		//Add the additional features for this atom if applicable:
		if(this.additional_features.containsKey(head)){
			for(Feature f:this.additional_features.get(head)){
				f.setIndexInFeatureSpace(feature_index++);
				resulting_features.add(f);
			}
		}
		//cutoff if applicable
		if(this.number_of_features_restriction!=-1 && this.number_of_features_restriction<resulting_features.size()){
			return resulting_features.subList(0, number_of_features_restriction);
		}
		
		return resulting_features;
	}
    
	/**
	 * Generate feature space for target atom head
	 * which includes negative examples as well
	 */
	@Override
	public List<Feature> generateFeaturesWithNegativeExamples(Atom head,List<Atom> predicates){
		List<Feature> features=new ArrayList<Feature>();
		FeatureCreator featureCreator=new FeatureCreator();	
		//create mixture of positive and negative literals
		List<Literal> literals_positive_negative=new ArrayList<Literal>();
		for(Atom a:predicates){
			literals_positive_negative.add(new PosLiteral(a));
			try{
				literals_positive_negative.add(new NegLiteral(a));
			}
			catch(LiteralNotDefined e){
				continue;
			}
		}
		//get list of proposed conjunctions of predefined length
		AtomCombinationCreator combinationCreator=new AtomCombinationCreator();
		List<ProposedConjunction> proposedConjuctions=combinationCreator.getallPossibleCombinationUpToLength(predicates.toArray(new Literal[predicates.size()]),length, head, max_num_logvars,max_num_boolean_literals);

		//rename the proposed conjunction (if internal atoms present)
		RenamedConjunctionsCreator renameator=new RenamedConjunctionsCreator();
		List<Standard_Conjunction> renamedConjunctions=renameator.createAllRenamingCombinations(proposedConjuctions, head);	
		//filter the proposed conjunctions (remove duplicates etc)
		//List<Conjunction> filteredProposedConjunctions=ft.filterConjunction(renamedConjunctions);		
		//Create features for the conjunction
		int block=0;
		for(Standard_Conjunction c:renamedConjunctions){
			features.addAll(featureCreator.createFeature(c,block++));
		}
		return features;
	}


	public int getLength() {
		return length;
	}





}
