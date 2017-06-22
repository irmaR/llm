package learning;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.Interpretation;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.StructureLearner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.xml.internal.bind.v2.util.FatalAdapter;

import network.Box_network;
import network.Transition_network;

public class Davide_transition_learning_features {
	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);

		System.out.println("Parameters: "+parameters);
        System.out.println("Running box learning - decision trees");
		Box_network hybrid_robotics_simple=new Box_network();
		ntw=hybrid_robotics_simple.getNetwork(1);

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","box", "txt", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "box", "txt",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "box", "txt", ntw,DataType.test);

		List<Feature> extra_features_arm_x_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_b"), ntw);
		List<Feature> extra_features_arm_y_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_b"), ntw);
		List<Feature> extra_features_arm_z_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_a"), ntw);
		List<Feature> extra_features_arm_roll_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_a"), ntw);
		List<Feature> extra_features_arm_pitch_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_pitch_next"), ntw);
		List<Feature> extra_features_arm_yaw_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_yaw_next"), ntw);
		//List<Feature> extra_features_arm_finger_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("arm_finger_next"), ntw);
		
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
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_goal_reached.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		
		List<Predicate> exclusion_pos_x_next=new ArrayList<Predicate>();
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_x_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_pos_y_next=new ArrayList<Predicate>();
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_x_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_y_next").getPredicate());
		exclusion_pos_y_next.add(ntw.getPredicateNameToAtom().get("pos_z_next").getPredicate());
		
		List<Predicate> exclusion_pos_z_next=new ArrayList<Predicate>();
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw_next").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_pos_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());
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
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_finger_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());
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
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_x_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());
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
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_y_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());		
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
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_z_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
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
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_roll_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
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
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_pitch_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
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
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_yaw").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_roll").getPredicate());
		exclusion_arm_yaw_next.add(ntw.getPredicateNameToAtom().get("arm_pitch").getPredicate());	
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
		List<Feature> fts=fGen.generateFeatures(ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)), ntw.getLiterals());
		HashMap<Interpretation,Integer> map=d_training.getNrGroundingsOfAtomPerInterpetation(ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)));
		for(Interpretation i:map.keySet()){
			System.out.println(i.getPath_to_interpretation()+" "+map.get(i));
		}
		/*
		BufferedWriter fw=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)).getPredicate().getPredicateName()+".csv")));
		BufferedWriter fw_fts_type=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+AlgorithmParameters.predicates.get(0)+"_feature_types"+".info")));
		BufferedWriter fw_features=new BufferedWriter(new FileWriter(new File(AlgorithmParameters.getOutput_path()+"/"+AlgorithmParameters.predicates.get(0)+"_features"+".info")));
		for(Feature f:fts){
        	fw_features.append(f.toString()+"\n");
        	String type=null;
        	if(f.isContinuousOutput()){
        		type="cont";
        	}
        	else if(f.isDiscreteOutput() && f.getRange().isBooleanRange()){
        		type="boolean";
        	}
        	else{
        		type="categ";
        	}
        }
        fw_fts_type.close();
        fw_features.close();
		HashMap<Feature,QueryData> map=new HashMap<Feature,QueryData>();
		Integer data_points=null;
		int counter=0;
		for(Feature f:fts){
			counter++;
			Dependency dep=new Dependency(ntw.getPredicateNameToAtom().get(AlgorithmParameters.predicates.get(0)),new Feature[]{f});
			QueryData qd=training_data_machine.getQueryResults(dep);
			System.out.println("Queried "+counter+"th feature out of: "+fts.size());
			if(data_points==null){
				data_points=qd.getNr_groundings_for_head();
			}
			map.put(f,qd);
		}
		System.out.println("Nr data points: "+data_points);
		for(int i=0;i<data_points;i++){
		   System.out.println("Writing "+i+"th row out of: "+data_points);
		   String headValue=null;
		   String fts_string="";
		   String row_string="";
		   for(Feature f:fts){
			    if(headValue==null){
			    	headValue=map.get(f).getFlatData().get(i).getHead().getValue().toString();
			    }
				fts_string+=map.get(f).getFlatData().get(i).getFeatureValues().get(f).toString()+",";	
			}
		   // System.out.println(" Row "+i+" "+headValue);
		    row_string+=headValue+","+fts_string+"\n";
			fw.append(row_string);
		}
		fw.close();
			*/
		
	}
}
