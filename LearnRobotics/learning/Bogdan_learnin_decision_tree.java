package learning;

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
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
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

public class Bogdan_learnin_decision_tree {
	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);
        AlgorithmParameters.detailed_logging_flag=false;
		System.out.println("Parameters: "+parameters);
		System.out.println("Running box learning - decision trees");
		Bogdan_network hybrid_robotics_simple=new Bogdan_network();
		//Three_objects_davide_continuous_dimension hybrid_robotics_simple=new Three_objects_davide_continuous_dimension();
		ntw=hybrid_robotics_simple.getNetwork(1);
		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","interp", "pl", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "interp", "pl",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "interp", "pl", ntw,DataType.test);

		List<Feature> extra_features_x_pos_b=bogdan_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_b"), ntw);
		List<Feature> extra_features_delta_x_sec_pos_b=bogdan_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("delta_x_sec"), ntw);
		List<Feature> extra_features_y_pos_b=bogdan_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_b"), ntw);
		List<Feature> extra_features_x_pos_a=bogdan_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_a"), ntw);
		List<Feature> extra_features_y_pos_a=bogdan_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("y_pos_a"), ntw);

		//query machines
		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(2, 1);
		fGen.setNumber_of_features_restriction(AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setAdditionalFeatures(extra_features_x_pos_b, ntw.getPredicateNameToAtom().get("x_pos_b"));
		fGen.setAdditionalFeatures(extra_features_y_pos_b, ntw.getPredicateNameToAtom().get("y_pos_b"));
		fGen.setAdditionalFeatures(extra_features_x_pos_a, ntw.getPredicateNameToAtom().get("x_pos_a"));
		fGen.setAdditionalFeatures(extra_features_y_pos_a, ntw.getPredicateNameToAtom().get("y_pos_a"));
		fGen.setAdditionalFeatures(extra_features_delta_x_sec_pos_b, ntw.getPredicateNameToAtom().get("delta_x_sec"));
		HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();
		List<Predicate> exclusion_x_pos_b=new ArrayList<Predicate>();
		exclusion_x_pos_b.add(ntw.getPredicateNameToAtom().get("y_pos_b").getPredicate());
		List<Predicate> exclusion_y_pos_b=new ArrayList<Predicate>();
		exclusion_y_pos_b.add(ntw.getPredicateNameToAtom().get("x_pos_b").getPredicate());
		List<Predicate> exclusion_x_pos_a=new ArrayList<Predicate>();
		exclusion_y_pos_b.add(ntw.getPredicateNameToAtom().get("y_pos_a").getPredicate());
		List<Predicate> exclusion_y_pos_a=new ArrayList<Predicate>();
		exclusion_y_pos_b.add(ntw.getPredicateNameToAtom().get("x_pos_a").getPredicate());

		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("x_pos_b"), exclusion_x_pos_b);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_y_next"), exclusion_y_pos_b);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_z_next"), exclusion_x_pos_a);
		exclusion_predicates.put(ntw.getPredicateNameToAtom().get("arm_roll_next"), exclusion_y_pos_a);

		fGen.setExclusionPredicates(exclusion_predicates);
		DecisionTreeLearning dtl=new DecisionTreeLearning(new DTDependencySelectorStandard());
		dtl.setOutputDirectory(AlgorithmParameters.output_path);
		dtl.setOutput_query_data(true);
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		str_learner.setSelectedFeatureIndices(new int[]{1,2,3,4,5,6});
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
			}catch(IOException i)
			{
				i.printStackTrace();
			}
		}

	}
}
