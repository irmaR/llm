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
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.penalties.PenalizeAggregatesAndOperators;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.DTDependencySelectorSplitDiscreteAllFeatures;
import hybrid.structureLearning.DTDependencySelectorStandard;
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

import network.Bogdan_network;
import network.Box_network;
import network.Transition_network_Version2;

public class StandardSplitProcedure {
	private static  NetworkInfo ntw;

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
		System.out.println(d_training.getNrGroundingsInData(ntw.getAtom("pos_x_next")));
		List<Feature> extra_features_arm_x_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_b"), ntw);
		List<Feature> extra_features_arm_y_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_b"), ntw);
		List<Feature> extra_features_arm_z_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_a"), ntw);
		List<Feature> extra_features_arm_roll_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_a"), ntw);
		List<Feature> extra_features_arm_pitch_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_pitch_next"), ntw);
		List<Feature> extra_features_arm_yaw_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_yaw_next"), ntw);		
		List<Feature> extra_features_pos_x_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("pos_x_next"), ntw);
		List<Feature> extra_features_pos_y_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("pos_y_next"), ntw);
		List<Feature> extra_features_pos_z_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("pos_z_next"), ntw);

		//query machines
		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());

		//set feature generator
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
		fGen.setNumber_of_features_restriction(AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setAdditionalFeatures(extra_features_arm_x_next, ntw.getPredicateNameToAtom().get("arm_x_next"));
		fGen.setAdditionalFeatures(extra_features_arm_y_next, ntw.getPredicateNameToAtom().get("arm_y_next"));
		fGen.setAdditionalFeatures(extra_features_arm_z_next, ntw.getPredicateNameToAtom().get("arm_z_next"));
		fGen.setAdditionalFeatures(extra_features_arm_roll_next, ntw.getPredicateNameToAtom().get("arm_roll_next"));
		fGen.setAdditionalFeatures(extra_features_arm_pitch_next, ntw.getPredicateNameToAtom().get("arm_pitch_next"));
		fGen.setAdditionalFeatures(extra_features_arm_yaw_next, ntw.getPredicateNameToAtom().get("arm_yaw_next"));
		//fGen.setAdditionalFeatures(extra_features_arm_finger_next, ntw.getPredicateNameToAtom().get("arm_finger_next"));
	
		fGen.setAdditionalFeatures(extra_features_pos_x_next, ntw.getPredicateNameToAtom().get("pos_x_next"));
		fGen.setAdditionalFeatures(extra_features_pos_y_next, ntw.getPredicateNameToAtom().get("pos_y_next"));
     	fGen.setAdditionalFeatures(extra_features_pos_z_next, ntw.getPredicateNameToAtom().get("pos_z_next"));

		HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();
		List<Predicate> exclusion_goal_reached=new ArrayList<Predicate>();
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_x_cur").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_y_cur").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_z_cur").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		
		List<Predicate> exclusion_pos_x_next=new ArrayList<Predicate>();
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_pos_y_next=new ArrayList<Predicate>();
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_pos_z_next=new ArrayList<Predicate>();
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_arm_finger_next=new ArrayList<Predicate>();
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_arm_x_next=new ArrayList<Predicate>();
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		

		List<Predicate> exclusion_arm_y_next=new ArrayList<Predicate>();
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_arm_z_next=new ArrayList<Predicate>();
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());

		List<Predicate> exclusion_arm_roll_next=new ArrayList<Predicate>();
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());

		List<Predicate> exclusion_arm_pitch_next=new ArrayList<Predicate>();
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());


		List<Predicate> exclusion_arm_yaw_next=new ArrayList<Predicate>();
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());

		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_x_next"), exclusion_arm_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_y_next"), exclusion_arm_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_z_next"), exclusion_arm_z_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_roll_next"), exclusion_arm_roll_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_pitch_next"), exclusion_arm_pitch_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_yaw_next"), exclusion_arm_yaw_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_finger_next"), exclusion_arm_finger_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_x_next"), exclusion_pos_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_y_next"), exclusion_pos_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("pos_z_next"), exclusion_pos_z_next);

		fGen.setExclusionPredicates(exclusion_predicates);
		
		int counter=0;
		List<Feature> fts=fGen.generateFeatures(ntw.getAtom("pos_x_next"),ntw.getLiterals());
		DecisionTreeLearning dtl=new DecisionTreeLearning(new DTDependencySelectorStandard(AlgorithmParameters.output_path,new PenalizeAggregatesAndOperators()));
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		//str_learner.setSelectedFeatureIndices(indices);
		LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
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
				FileWriter fw2=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"_indices"+".info"));
				fw2.append(learned_tree.getIndicesOfFeatures(learned_tree.getRoot(), ""));
				fw2.close();
			}catch(IOException i)
			{
				i.printStackTrace();
			}
		}

	}
}
