import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.FeatureGeneratorWithLogvarRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structure_learning.GreedySearch;
import hybrid.structure_learning.StructureLearner;

import java.util.List;


public class LearnIMDB {
}
/*
	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS");
        getAlgorithmParameters.parseArgumentsHRDN(args);
        
        System.out.println("Parameters: "+parameters);
	    	
        IMDBHybrid hybrid_imdb=new IMDBHybrid();
		NetworkInfo ntw=hybrid_imdb.getIMDBHybrid(1);
			
		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

		Data d_training=dataLoader.loadData(parameters.input_path+"/train/", "fold", "pl", ntw);
	    Data d_validation=dataLoader.loadData(parameters.input_path+"/validate/", "fold", "pl",ntw);
	    Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "fold", "pl", ntw);
	    
	    TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, new NoPenalty());
	
		String predicate_name="budget";
		FeatureGeneratorWithLogvarRestrictions fGen=new FeatureGeneratorWithLogvarRestrictions(5, AlgorithmParameters.nr_logvar_renamings,"\\==");
	    List<Feature> features=fGen.generateFeatures(ntw.getPredicateNameToAtom().get(predicate_name), ntw.getAtomsAndEqualityConstraints());
		for(Feature f:features){
			System.out.println(ntw.getPredicateNameToAtom().get(predicate_name)+" | "+f);
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