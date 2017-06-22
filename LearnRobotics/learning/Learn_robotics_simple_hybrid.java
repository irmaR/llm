package learning;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.DistributeFeaturesInBlocks;
import hybrid.featureGenerator.FeatureBlock;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.DiscretizedRangeExtractor;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;
import hybrid.penalties.MDLPenalty;
import hybrid.penalties.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.StructureLearner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import network.Hybrid_robot_network;
public class Learn_robotics_simple_hybrid {
	

	private static  NetworkInfo ntw;
		
		public static void main(String[] args) throws Exception{
			AlgorithmParameters parameters=new AlgorithmParameters();
	        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
	        getAlgorithmParameters.parseArgumentsHRDN(args);
	        
	        System.out.println("Parameters: "+parameters);
		    	
	        Hybrid_robot_network hybrid_robotics_simple=new Hybrid_robot_network();
			ntw=hybrid_robotics_simple.getUniversityHybrid(1);
				
			//LOAD DATA
			TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
			TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		    Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/", "robot", "pl", ntw,DataType.training);
		    Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "robot", "pl",ntw,DataType.validation);
		    Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "robot", "pl", ntw,DataType.test);
		    
		    System.out.println("TEST DATA: \n"+d_test.getInterpretations().get(0).getPrologFormat());
		    //query machines
		    TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		    TuPrologQueryMachine training_validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		    TuPrologQueryMachine training_test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
		    //System.out.println(" subsampling performed? "+d_test.getInterpretations().get(0).getDatabaseFormat());
		    //Dependency dep_test=new Dependency(ntw.getPredicateNameToAtom().get("affected"), new Feature[]{});
		    //dep_test.getCpd().getCpdEvaluator().estimateParameters(training_data_machine.getQueryResults(dep_test));
		    //System.out.println(dep_test.getCpd().getParameters());
		    //System.out.println(dep_test.getCpd().getCpdEvaluator().calculatePLL(training_test_machine.getQueryResults(dep_test),dep_test.getCpd().getParameters(), new MDLPenalty()));
		    FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
			StructureLearner str_learner=new StructureLearner(fGen,new GreedySearch(),ntw,training_data_machine,training_validation_machine,training_test_machine);	
            
			//remove action from predicates - has only one value (push)
			List literals_for_learning=ntw.getLiterals();
			literals_for_learning.remove(ntw.getPredicateNameToAtom().get("action"));
	        str_learner.learnStandardCPTs((Atom[]) literals_for_learning.toArray(new Atom[literals_for_learning.size()]));
		}
	}


