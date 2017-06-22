
import hybrid.core.GenerateUIDForGroundAtom;
import hybrid.core.GenerateUIDForInterpretation;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.FeatureGeneratorWithLogvarRestrictions;
import hybrid.features.Feature;
import hybrid.*;
import hybrid.interpretations.Data;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structure_learning.GreedySearch;
import hybrid.structure_learning.StructureLearner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LearnHybridPKDD {
	
}
/*	
	
	public static void main(String[] args) throws Exception{
		GenerateUIDForInterpretation.reset();
		GenerateUIDForGroundAtom.reset();

		NetworkInfo ntw=null;
		AlgorithmParameters parameters=new AlgorithmParameters();
        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED THE INTERPRETATION LOADER");
        getAlgorithmParameters.parseArgumentsHRDN(args);
        
        System.out.println("Parameters: "+parameters);
        System.out.println("############################# Learning structure for PKDD - subsampled negatives with ratio "+1+" ##########################");
        PKDDHybridNetwork hybrid_university=new PKDDHybridNetwork();
		ntw=hybrid_university.getHybridPKDD(1);
		
		//FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(3, AlgorithmParameters.nr_logvar_renamings);
		FeatureGeneratorWithLogvarRestrictions fGen=new FeatureGeneratorWithLogvarRestrictions(3, AlgorithmParameters.nr_logvar_renamings,"\\==");

		//fGen.setMaxNumberOfBooleanPredicates(2);
		List<Atom> atoms=new ArrayList<Atom>();
		atoms.add(ntw.getPredicateNameToAtom().get("clientDistrict"));
		atoms.add(ntw.getPredicateNameToAtom().get("hasAccount"));
		atoms.add(ntw.getPredicateNameToAtom().get("ratUrbInhab"));
		
		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
	    Data d_training=dataLoader.loadData(parameters.input_path+"/train/", "fold", "pl", ntw);
	    Data d_validation=dataLoader.loadData(parameters.input_path+"/validate/", "fold", "pl",ntw);
	    
	    TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine tuPrologQueryMachine_test=null;
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

		List<Feature> features=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("avgNrWith"), ntw.getAtomsAndEqualityConstraints());
	        for(Feature f:features){
	        	System.out.println(f);
	        }
		Dependency test_dep=new Dependency(ntw.getPredicateNameToAtom().get("avgNrWith"),new Feature[]{features.get(16)});
		QueryData qd=tuPrologQueryMachine_training.getQueryResults(test_dep);
		System.out.println(qd);
		if(AlgorithmParameters.isEvaluation_flag()){
		  Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "fold", "pl", ntw);
		  tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, new NoPenalty());
		}
	
		
	    //ADD DISCRETIZED RANDVARVALUE TESTS
		if(!ntw.areRandVarValueTestsSetForAllPreds()){
			ntw.initializeRandvarTests();
		}
		StructureLearner str_learner=new StructureLearner();		
		GreedySearch greedySearch=new GreedySearch();
		Atom[] learning_atoms=null;
		learning_atoms=ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]);
		str_learner.learnStructureAndEvaluate(learning_atoms,ntw, tuPrologQueryMachine_training, tuPrologQueryMachine_validation,tuPrologQueryMachine_test, fGen, greedySearch);

	}
}
*/