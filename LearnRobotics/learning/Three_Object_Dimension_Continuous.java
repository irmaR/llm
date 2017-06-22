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
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.StructureLearner;

import java.util.List;

import network.Three_objects_davide;
import network.Three_objects_davide_continuous_dimension;

public class Three_Object_Dimension_Continuous {
	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);

		System.out.println("Parameters: "+parameters);

		Three_objects_davide_continuous_dimension hybrid_robotics_simple=new Three_objects_davide_continuous_dimension();
		//Three_objects_davide_continuous_dimension hybrid_robotics_simple=new Three_objects_davide_continuous_dimension();
		ntw=hybrid_robotics_simple.getNetwork(1);

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","interp", "pl", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "interp", "pl",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "interp", "pl", ntw,DataType.test);

		List<Feature> extra_features_delta_x1=next_x_ExtraFeatureCreator.getListOfFeatures_general_dimension_dimension_continuous(ntw.getPredicateNameToAtom().get("next_x"), ntw);
		//List<Feature> extra_features_delta_x1=next_x_ExtraFeatureCreator.getListOfFeatures_general_dimension_dimension_continuous(ntw.getPredicateNameToAtom().get("next_x"), ntw);

		//query machines
		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());

		//set feature generator
		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
		System.out.println(" number of features restriction: "+AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setNumber_of_features_restriction(AlgorithmParameters.getFeatureSpaceCutoff());
		fGen.setAdditionalFeatures(extra_features_delta_x1, ntw.getPredicateNameToAtom().get("next_x"));
		/*List<Feature> fts=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("next_x"), ntw.getLiterals());
    Feature proba=fts.get(0);
    //proba.getConjunction().setOp(new Subtraction());
    //((InBetween)proba.getComparator()).setThresholds(-11.00, 0.00);
    System.out.println(proba.getConjunction().getHead());
    Dependency test_dep=new Dependency(ntw.getPredicateNameToAtom().get("next_x"),new Feature[]{proba});
    QueryData query_res=training_data_machine.getQueryResults(test_dep);
    System.out.println(query_res);
    test_dep.getCpd().setParameters(test_dep.getCpd().getCpdEvaluator().estimateParameters(query_res));
    System.out.println(test_dep.getCpd().getParameters().toString());*/

		//fGen.do_not_generate_features();
		StructureLearner str_learner=new StructureLearner(fGen,new GreedySearch(),ntw,training_data_machine,validation_machine,test_machine);	
		//str_learner.setAdditionalFeatures(extra_features_delta_x, ntw.getPredicateNameToAtom().get("delta_x_sec"));
		//str_learner.setAdditionalFeatures(additional_features.get("delta_y"), ntw.getPredicateNameToAtom().get("delta_y"));
		str_learner.learnStandardCPTs(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));


	}
}
