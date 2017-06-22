import hybrid.converters.DC_converter;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.penalties.PenalizeAggregatesAndOperators;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structureLearning.DTDependencySelectorStandard;
import hybrid.structureLearning.DTDependencySelectorStandardWithScript;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.LearnedModelTree;
import hybrid.structureLearning.StructureLearner;
import hybrid.structureLearning.Tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.Transition_network_Version2;


public class WithoutScripDiscreteContinuous {
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
		Transition_network_Version2 hybrid_robotics_simple=new Transition_network_Version2();
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
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "data", "txt",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "data", "txt", ntw,DataType.test);
		

		//query machines
		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
		
		
		List<Feature> extra_features_pos_x_next=new ArrayList<Feature>();
		List<Feature> extra_features_pos_y_next=new ArrayList<Feature>();
		List<Feature> extra_features_pos_z_next=new ArrayList<Feature>();
		
		if(AlgorithmParameters.getPredicate_names().contains("pos_x_next")){
			System.out.println("Generating Feature space for pos_x_next");
			System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_x_next"))+" data points");
			extra_features_pos_x_next.addAll(next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,training_data_machine));
			System.out.println("Finished ...");
		}
		if(AlgorithmParameters.getPredicate_names().contains("pos_y_next")){
			System.out.println("Generating Feature space for pos_y_next");
			System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_y_next"))+" data points");
			extra_features_pos_y_next.addAll(next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,training_data_machine));
			System.out.println("Finished ...");
		}
		if(AlgorithmParameters.getPredicate_names().contains("pos_z_next")){
			System.out.println("Generating Feature space for pos_z_next");
			System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_z_next"))+" data points");
			extra_features_pos_z_next.addAll(next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,training_data_machine));
			System.out.println("Finished ...");
		}
		
		/*List<Feature> extra_features_pos_x_next=next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,training_data_machine);		
		List<Feature> extra_features_pos_y_next=next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,training_data_machine);
		List<Feature> extra_features_pos_z_next=next_arm_extra_features.getListOfFeaturesWithOrientation(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,training_data_machine);*/

		//set feature generator
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
		fGen.setAdditionalFeatures(extra_features_pos_x_next, ntw.getPredicateNameToAtom().get("pos_x_next"));
		fGen.setAdditionalFeatures(extra_features_pos_y_next, ntw.getPredicateNameToAtom().get("pos_y_next"));
     	fGen.setAdditionalFeatures(extra_features_pos_z_next, ntw.getPredicateNameToAtom().get("pos_z_next"));

		HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();
		List<Predicate> exclusion_pos_x_next=new ArrayList<Predicate>();
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		
		List<Predicate> exclusion_pos_y_next=new ArrayList<Predicate>();
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		
		List<Predicate> exclusion_pos_z_next=new ArrayList<Predicate>();
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_x_next"), exclusion_pos_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_y_next"), exclusion_pos_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_z_next"), exclusion_pos_z_next);
		fGen.setExclusionPredicates(exclusion_predicates);

		int counter=0;
		DTDependencySelectorStandard dpSelector=new DTDependencySelectorStandard(AlgorithmParameters.getScriptOutput(),new PenalizeAggregatesAndOperators());
		boolean runtimeReport=false;
		DecisionTreeLearning dtl=new DecisionTreeLearning(dpSelector);
		int[] indices=new int[]{0, 367, 62, 93, 270, 131, 334, 16, 188, 320, 120, 38, 148, 169, 30, 44, 319, 79};
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		str_learner.setSelectedFeatureIndices(indices);
		LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
	    
	}

}
