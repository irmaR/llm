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


public class STDiscrete {
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

		System.out.println("Parameters: "+parameters);
		System.out.println("Running box learning - decision trees");
		Transition_network_Version2 hybrid_robotics_simple=new Transition_network_Version2();
		ntw=hybrid_robotics_simple.getNetwork(1);

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
		
		List<Feature> extra_features_pos_x_next=next_arm_extra_features.getListOfFeaturesWithOrientationWithoutContinuous(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw,training_data_machine);
		List<Feature> extra_features_pos_y_next=next_arm_extra_features.getListOfFeaturesWithOrientationWithoutContinuous(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw,training_data_machine);
		List<Feature> extra_features_pos_z_next=next_arm_extra_features.getListOfFeaturesWithOrientationWithoutContinuous(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw,training_data_machine);

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
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_cur").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());

		
		List<Predicate> exclusion_pos_y_next=new ArrayList<Predicate>();
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_cur").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
		
		List<Predicate> exclusion_pos_z_next=new ArrayList<Predicate>();
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_cur").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("yaw_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pitch_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("roll_next").getPredicate());
		
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_x_next"), exclusion_pos_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_y_next"), exclusion_pos_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_z_next"), exclusion_pos_z_next);
		fGen.setExclusionPredicates(exclusion_predicates);

		int counter=0;
		DTDependencySelectorStandardWithScript dpSelector=new DTDependencySelectorStandardWithScript(new String[]{"/usr/bin/python"},AlgorithmParameters.output_path,new PenalizeAggregatesAndOperators());
		FileWriter timeOverallFtRun=new FileWriter(AlgorithmParameters.output_path+"/overall_Ft_run.csv");
		FileWriter writingPythonScriptsRun=new FileWriter(AlgorithmParameters.output_path+"/writingPythonScripts.csv");
		FileWriter runningPythonScript=new FileWriter(AlgorithmParameters.output_path+"/runningPythonScript.csv");
		FileWriter filteringRunTime=new FileWriter(AlgorithmParameters.output_path+"/filteringRunTime.csv");

		boolean runtimeReport=false;
		dpSelector.setTimeOverallFtRun(timeOverallFtRun);
		dpSelector.setWritingPythonScriptsRun(writingPythonScriptsRun);
		dpSelector.setRunningPythonScript(runningPythonScript);
		dpSelector.setFilteringRunTime(filteringRunTime);
		dpSelector.setRuntimeReport(runtimeReport);
		DecisionTreeLearning dtl=new DecisionTreeLearning(dpSelector);
		//int[] indices=new int[]{0,34};
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		//str_learner.setSelectedFeatureIndices(indices);
		LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
		timeOverallFtRun.close();
		writingPythonScriptsRun.close();
		runningPythonScript.close();
		filteringRunTime.close();

		for(Atom a:trees.getLearnedDependency().keySet()){
			try
			{
				Tree learned_tree=trees.getLearnedDependency().get(a);
				FileOutputStream fileOut =new FileOutputStream(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(learned_tree);
				out.close();
				fileOut.close();
				System.out.printf(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName());
				FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dot"));
				fw.append(learned_tree.makeDigraph(learned_tree.getRoot(),""));
				fw.close();
				double acc_error=learned_tree.getAccumulatedError(learned_tree.getRoot());
				FileWriter fw1=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".nrmse"));
				fw1.append(String.valueOf(Math.sqrt(acc_error/test_machine.getData().getNrGroundingsInData(a))));
				fw1.close();
				
				double accNormError=learned_tree.getAccumulatedNormalizedError(learned_tree.getRoot());
				FileWriter fw4=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".avgnrmse"));
				fw4.append(String.valueOf(Math.sqrt(accNormError/learned_tree.getNrLeaves(learned_tree.getRoot()))));
				System.out.println("Number of leaves: "+learned_tree.getNrLeaves(learned_tree.getRoot()));
				fw4.close();
				
				FileWriter fw3=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".testLL"));
				fw3.append(String.valueOf(learned_tree.getAccumulatedTestLL(learned_tree.getRoot(),0)));
				fw3.close();
				FileWriter fw2=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"_indices"+".info"));
				fw2.append(learned_tree.getIndicesOfFeatures(learned_tree.getRoot(), ""));
				fw2.close();
				
				FileWriter fw5=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dclause"));
				fw5.append(learned_tree.printTree_DC(learned_tree.getRoot(), new DC_converter()));
                fw5.close();				
			}catch(IOException i)
			{
				i.printStackTrace();
			}
		}

	}

}
