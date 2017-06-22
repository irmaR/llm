import hybrid.dependencies.Dependency;
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
import hybrid.network.BoolValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.network.Value;
import hybrid.penalties.PenalizeAggregatesAndOperators;
import hybrid.penalties.PenalizeDiscreteFeatures;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.structureLearning.DTDependencySelectorStandard;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.LearnedModelTree;
import hybrid.structureLearning.StructureLearner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import network.TransitionDisplacementNetwork;
import network.Transition_network_Version2;


public class DisplacementLearning {
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
		AlgorithmParameters.setNrFolds(5);
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
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "data", "txt",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "data", "txt", ntw,DataType.test);
		
        System.out.println("Nr training interpretations: "+d_training.getInterpretations().size());
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
			List<Feature> fts=next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,training_data_machine);
			extra_features_pos_x_next.addAll(fts);
			System.out.println("Finished ...");
		}
		
		if(AlgorithmParameters.getPredicate_names().contains("pos_y_next")){
			System.out.println("Generating Feature space for pos_y_next");
			System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_y_next"))+" data points");
			extra_features_pos_y_next.addAll(next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,training_data_machine));
			System.out.println("Finished ...");
		}
		if(AlgorithmParameters.getPredicate_names().contains("pos_z_next")){
			System.out.println("Generating Feature space for pos_z_next");
			System.out.println("Training data has: "+training_data_machine.getData().getNrGroundingsInData(ntw.getPredicateNameToAtom().get("pos_z_next"))+" data points");
			extra_features_pos_z_next.addAll(next_arm_extra_features.getListOfFeaturesWithDisplacement(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,training_data_machine));
			System.out.println("Finished ...");
		}
		
		
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

		fGen.do_not_generate_features();
		
		int counter=0;
		if(new File(AlgorithmParameters.output_path+"/trackingFeatures"+".csv").exists()){
			new File(AlgorithmParameters.output_path+"/trackingFeatures"+".csv").delete();
		}
		DTDependencySelectorStandard dpSelector=new DTDependencySelectorStandard(AlgorithmParameters.getScriptOutput(),new PenalizeDiscreteFeatures());
		boolean runtimeReport=false;
		DecisionTreeLearning dtl=new DecisionTreeLearning(dpSelector);
	    //int[] indices=new int[]{0}; //,1,1093,623,624,625
		//int[] indices=new int[]{0, 1, 1084, 664, 36, 683, 1092, 32, 47, 119, 146, 670, 200, 173, 724, 638, 1093, 673, 1083, 626, 164, 623, 165, 166, 624, 625};

		/*List<Feature> fts=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw.getLiterals());
		Dependency dep=new Dependency(ntw.getPredicateNameToAtom().get("pos_x_next"),new Feature[]{fts.get(0),fts.get(1),fts.get(1093),fts.get(623),fts.get(624),fts.get(625)});
		HashMap<Feature,Value> filter=new HashMap<Feature,Value>();
		filter.put(fts.get(1), new StringValue("g"));
		filter.put(fts.get(1093), new BoolValue(true));
		QueryDataFilter filterD=new QueryDataFilter();
		QueryData train=training_data_machine.getQueryResults(dep);
		QueryData trainFiltered=filterD.filterQueryData(dep, filter, train);
		dep.getCpd().getCpdEvaluator().estimateParameters(trainFiltered);
		QueryData validation=validation_machine.getQueryResults(dep);
		QueryData validationFiltered=filterD.filterQueryData(dep, filter, validation);
		System.out.println(dep.getCpd().getParameters());
		System.out.println(validationFiltered);
        double error_validation=dep.getCpd().getCpdEvaluator().getUnnormalizedError(validationFiltered, dep.getCpd().getParameters(),false);
        System.out.println("ERROR VALIDATION: "+error_validation);*/
		//Integer[] indicesToTrack=new Integer[]{26,27,28,29,30,31,32,33};
		//Integer[] indicesToTrack=new Integer[]{1,2};
		//dpSelector.setIndicesOfFeaturesToTrack(Arrays.asList(indicesToTrack));
		
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		//str_learner.setSelectedFeatureIndices(indices);
		LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
	}
}
