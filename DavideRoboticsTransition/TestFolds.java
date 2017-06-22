import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.penalties.PenalizeAggregatesAndOperators;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.DTDependencySelectorStandard;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.LearnedModelTree;
import hybrid.structureLearning.StructureLearner;
import hybrid.utils.CVFoldPair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.TransitionDisplacementNetwork;


public class TestFolds {
	private static  NetworkInfo ntw;

	public static int[] makeSequence(int begin, int end) {
		int[] ret = new int[end - begin + 1];
		for(int i = begin; i <= end; i++){
			ret[i]=i;
		}
		return ret;  
	}

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);
		AlgorithmParameters.setDetailed_logging_flag(false);
		AlgorithmParameters.setNrFolds(10);
		System.out.println("Parameters: "+parameters);
		System.out.println("Running box learning - decision trees");
		TransitionDisplacementNetwork hybrid_robotics_simple=new TransitionDisplacementNetwork();
		ntw=hybrid_robotics_simple.getNetwork(1);
		
		if(AlgorithmParameters.getScriptOutput()!=null){
			File theDir = new File(AlgorithmParameters.getScriptOutput());
			// if the directory does not exist, create it
			System.out.println("Output script: "+AlgorithmParameters.getScriptOutput());
			System.out.println("Already exists? "+theDir.exists());
			if (AlgorithmParameters.getScriptOutput()!=null && !theDir.exists()) {
			    System.out.println("creating directory: " + AlgorithmParameters.getScriptOutput());
			    boolean result = false;
			    try{
			        theDir.mkdirs();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("DIR created");  
			    }
			}
		}

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","data", "txt", ntw,DataType.training);
		//query machines
		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		ValueFt test=new ValueFt(new Standard_Conjunction<>(ntw.getPredicateNameToAtom().get("pos_x_next"),true,new PosLiteral(ntw.getPredicateNameToAtom().get("pos_x_cur"))));
		Dependency testDep=new Dependency(ntw.getPredicateNameToAtom().get("pos_x_next"),new Feature[]{test});
		QueryData qd=training_data_machine.getQueryResults(testDep);
		List<CVFoldPair> qds=qd.splitIntoFolds(5);
		for(CVFoldPair q:qds){
			System.out.println("Query: "+q);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	    
	}
}
