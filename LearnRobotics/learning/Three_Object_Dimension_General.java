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
import hybrid.network.BoolValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Value;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.structureLearning.DTDependencySelectorStandard;
import hybrid.structureLearning.DecisionTreeData;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.LearnedDependency;
import hybrid.structureLearning.LearnedModelTree;
import hybrid.structureLearning.Node;
import hybrid.structureLearning.StructureLearner;
import hybrid.structureLearning.Tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import network.Three_objects_davide;
import network.Three_objects_davide_continuous_dimension;
import network.Three_objects_davide_version2;

public class Three_Object_Dimension_General {
	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);

		System.out.println("Parameters: "+parameters);

		Three_objects_davide hybrid_robotics_simple=new Three_objects_davide();
		ntw=hybrid_robotics_simple.getNetwork(1);

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_all=dataLoader_no_subsampling.loadData(parameters.input_path+"/small_test/","interp", "pl", ntw,DataType.training);
		/*Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/small/","interp", "pl", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/small/", "interp", "pl",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/small_test/", "interp", "pl", ntw,DataType.test);*/

		List<Feature> extra_features_delta_x1=next_x_ExtraFeatureCreator.getListOfFeatures_general_dimension(ntw.getPredicateNameToAtom().get("next_x"), ntw);
		//query machines
		/*TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());*/

		//set feature generator
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
		System.out.println(" number of features restriction: "+AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setNumber_of_features_restriction(AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setAdditionalFeatures(extra_features_delta_x1, ntw.getPredicateNameToAtom().get("next_x"));
		DecisionTreeLearning dtl=new DecisionTreeLearning(new DTDependencySelectorStandard());
		dtl.setOutputDirectory(AlgorithmParameters.output_path);
		//StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,null,null,null);	
        str_learner.DTLearnStructureAndEvaluateCrossValidation(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]), d_all.getInterpretations());
		//LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));

		/*for(Atom a:trees.getLearnedDependency().keySet()){
			try
			{
				Tree learned_tree=trees.getLearnedDependency().get(a);
				System.out.println("Accumulated error: "+learned_tree.getAccumulatedError(learned_tree.getRoot(), 0));
				FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dot"));
				fw.append(learned_tree.makeDigraph(learned_tree.getRoot(),""));
				fw.close();

				FileOutputStream fileOut =new FileOutputStream(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(learned_tree);
				out.close();
				fileOut.close();
				System.out.printf(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName());
			}catch(IOException i)
			{
				i.printStackTrace();
			}
		}*/


	}
}
