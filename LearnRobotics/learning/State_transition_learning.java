package learning;

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
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.LearnedDependency;
import hybrid.structureLearning.LearnedStructure;
import hybrid.structureLearning.StructureLearner;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.Box_network;
import network.Transition_network;

public class State_transition_learning {
	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);

		System.out.println("Parameters: "+parameters);

		Transition_network hybrid_robotics_simple=new Transition_network();
		//Three_objects_davide_continuous_dimension hybrid_robotics_simple=new Three_objects_davide_continuous_dimension();
		ntw=hybrid_robotics_simple.getNetwork(1);

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		
		//Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/",new String[]{"box8_7.txt"}, ntw);

        Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","box", "txt", ntw,DataType.training);	
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "box", "txt",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "box", "txt", ntw,DataType.test);



		List<Feature> extra_features_arm_x_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_x_next"), ntw);
		List<Feature> extra_features_arm_y_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_y_next"), ntw);
		List<Feature> extra_features_arm_z_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_z_next"), ntw);
		List<Feature> extra_features_arm_roll_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_roll_next"), ntw);
		List<Feature> extra_features_arm_pitch_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_pitch_next"), ntw);
		List<Feature> extra_features_arm_yaw_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_yaw_next"), ntw);
		List<Feature> extra_features_arm_finger_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_finger_next"), ntw);
		List<Feature> extra_features_x_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_next"), ntw);
		List<Feature> extra_features_y_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_next"), ntw);
		List<Feature> extra_features_z_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("z_next"), ntw);


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
		fGen.setAdditionalFeatures(extra_features_arm_finger_next, ntw.getPredicateNameToAtom().get("arm_finger_next"));

		fGen.setAdditionalFeatures(extra_features_x_next, ntw.getPredicateNameToAtom().get("x_next"));
		fGen.setAdditionalFeatures(extra_features_y_next, ntw.getPredicateNameToAtom().get("y_next"));
		fGen.setAdditionalFeatures(extra_features_z_next, ntw.getPredicateNameToAtom().get("z_next"));


		
		HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();
		List<Predicate> exclusion_goal_reached=new ArrayList<Predicate>();
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_x").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_y").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_z").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_finger").getPredicate());

		List<Predicate> exclusion_arm_finger_next=new ArrayList<Predicate>();
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		


		List<Predicate> exclusion_arm_x_next=new ArrayList<Predicate>();
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		


		List<Predicate> exclusion_arm_y_next=new ArrayList<Predicate>();
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		


		List<Predicate> exclusion_arm_z_next=new ArrayList<Predicate>();
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		

		List<Predicate> exclusion_arm_roll_next=new ArrayList<Predicate>();
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		

		List<Predicate> exclusion_arm_pitch_next=new ArrayList<Predicate>();
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		


		List<Predicate> exclusion_arm_yaw_next=new ArrayList<Predicate>();
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_x_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_y_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_z_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_finger_next").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
		
		
		List<Predicate> exclusion_x_next=new ArrayList<Predicate>();
		exclusion_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
		
		List<Predicate> exclusion_y_next=new ArrayList<Predicate>();
		exclusion_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
		
		
		List<Predicate> exclusion_z_next=new ArrayList<Predicate>();
		exclusion_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
		

		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_x_next"), exclusion_arm_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_y_next"), exclusion_arm_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_z_next"), exclusion_arm_z_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_roll_next"), exclusion_arm_roll_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_pitch_next"), exclusion_arm_pitch_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_yaw_next"), exclusion_arm_yaw_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_finger_next"), exclusion_arm_finger_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("x_next"), exclusion_x_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("y_next"), exclusion_y_next);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("z_next"), exclusion_z_next);

		fGen.setExclusionPredicates(exclusion_predicates);

		GreedySearch gr=new GreedySearch();
		StructureLearner str_learner=new StructureLearner(fGen,gr,ntw,training_data_machine,validation_machine,test_machine);	

		List<Feature> fts=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("arm_finger_next"),ntw.getLiterals());
		int counter=0;	
		for(Feature f:fts){
				System.out.println((counter++)+" "+f);
			}
		
		/*Dependency test=new Dependency(ntw.getPredicateNameToAtom().get("arm_finger_next"),new Feature[]{fts.get(10),fts.get(12)});
		QueryData tr=training_data_machine.getQueryResults(test);
		System.out.println(tr);
*/

		LearnedStructure ls=str_learner.learnStandardCPTs(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
		for(Atom a:ls.getLearnedDependency().keySet()){
				LearnedDependency ld=ls.getLearnedDependency().get(a);
				FileOutputStream fileOut =new FileOutputStream(AlgorithmParameters.getOutput_path()+"/"+a.getPredicate().getPredicateName()+".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(ld);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in"+AlgorithmParameters.getOutput_path()+"/"+a.getPredicate().getPredicateName()+".ser");
		}

	}
}
