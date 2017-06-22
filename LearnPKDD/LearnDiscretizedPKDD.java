import java.util.HashMap;
import java.util.List;

import hybrid.core.GenerateUIDForGroundAtom;
import hybrid.core.GenerateUIDForInterpretation;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.DiscretizedRangeExtractor;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structure_learning.GreedySearch;
import hybrid.structure_learning.StructureLearner;


public class LearnDiscretizedPKDD {

}
/*
	public static void main(String[] args) throws Exception{
		GenerateUIDForInterpretation.reset();
		GenerateUIDForGroundAtom.reset();

		NetworkInfo ntw=null;
		AlgorithmParameters parameters=new AlgorithmParameters();
        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED THE INTERPRETATION LOADER - discretization ");
        getAlgorithmParameters.parseArgumentsHRDN(args);
        
        System.out.println("Parameters: "+parameters);
        System.out.println("############################# Learning structure for PKDD - subsampled negatives with ratio "+1+" ##########################");
	    
        double sub_sampling_ratio=1;
        CreateDiscretizedPKDD hybrid_university=new CreateDiscretizedPKDD();
		ntw=hybrid_university.getDiscretizedPKDD(AlgorithmParameters.getDiscretization_level(),sub_sampling_ratio);
			
		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
	    Data d_training=dataLoader.loadData(parameters.input_path+"/train/", "fold", "pl", ntw);
	    Data d_validation=dataLoader.loadData(parameters.input_path+"/validate/", "fold", "pl",ntw);
	    
	    TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine tuPrologQueryMachine_test=null;
		
		
		DiscretizedRangeExtractor extractRanges=new DiscretizedRangeExtractor();
		
		HashMap<Predicate,MinMaxValue> ranges=extractRanges.getMinMaxValuesForPredicatesFromDiscretizationInfo(parameters.getDiscretization_info_path(),new Atom[]{ntw.getPredicateNameToAtom().get("loanAmount"),ntw.getPredicateNameToAtom().get("monthlyPayments"),ntw.getPredicateNameToAtom().get("clientAge"),ntw.getPredicateNameToAtom().get("avgNrWith"),ntw.getPredicateNameToAtom().get("avgSumOfW"),ntw.getPredicateNameToAtom().get("avgSumOfInc"),ntw.getPredicateNameToAtom().get("stdMonthInc"),ntw.getPredicateNameToAtom().get("stdMonthW"),ntw.getPredicateNameToAtom().get("avgSalary"),ntw.getPredicateNameToAtom().get("ratUrbInhab")});
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

		if(AlgorithmParameters.isEvaluation_flag()){
		  Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "fold", "pl", ntw);
		  tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, new NoPenalty());
		}
		if(!ntw.areRandVarValueTestsSetForAllPreds()){
			ntw.initializeRandvarTests();
		}

		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
	    List<Feature> features=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("avgSalary"), ntw.getAtomsAndEqualityConstraints());
	    /*for(Feature f:features){
	    	System.out.println(f+ "BLOCK: "+f.getFeatureBlock());
	    }
	    /*Dependency testDep=new Dependency(ntw.getPredicateNameToAtom().get("avgSalary"), new Feature[]{features.get(2)});
	    testDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(testDep));
	    System.out.println(testDep.getCpd().getParameters());
	    System.out.println(testDep.getCpd().getCpdEvaluator().calculatePLL(tuPrologQueryMachine_test.getQueryResults(testDep),testDep.getCpd().getParameters() , new NoPenalty()));
		
		//ADD DISCRETIZED RANDVARVALUE TESTS
		StructureLearner str_learner=new StructureLearner();		
		GreedySearch greedySearch=new GreedySearch();
		Atom[] learning_atoms=null;
		learning_atoms=ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]);
		str_learner.learnStructureAndEvaluate(learning_atoms,ntw, tuPrologQueryMachine_training, tuPrologQueryMachine_validation,tuPrologQueryMachine_test, fGen, greedySearch);

	}
}
*/