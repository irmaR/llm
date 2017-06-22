package learning;

import hybrid.comparators.*;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.Renaming;
import hybrid.features.*;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.operators.Addition;
import hybrid.operators.Division;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.StructureLearner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.Bogdan_network;
import network.Hybrid_robot_network;

public class Robotics_predicate_invention {

	private static  NetworkInfo ntw;
	
	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
        ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
        getAlgorithmParameters.parseArgumentsHRDN(args);
        
        System.out.println("Parameters: "+parameters);
	    	
        Bogdan_network hybrid_robotics_simple=new Bogdan_network();
		ntw=hybrid_robotics_simple.getNetwork(1);
			
		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
	    Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","interp", "pl", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "interp", "pl",ntw,DataType.validation);
	    Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "interp", "pl", ntw,DataType.test);

		/*Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/",new String[]{"interp_1.pl","interp_2.pl"}, ntw);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/",new String[]{"interp_1.pl"},ntw);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/",new String[]{"interp_1.pl"}, ntw);
	*/
	    
	    //query machines
	    TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
	    TuPrologQueryMachine training_validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
	    TuPrologQueryMachine training_test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());
	    
	    //set feature generator
	    FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
	    //fGen.do_not_generate_features();
	    //RENAMING
	    Atom x_pos_b=ntw.getPredicateNameToAtom().get("x_pos_b");
	    Renaming ren_object=new Renaming();
	    ren_object.addRenaming(x_pos_b.getArgument(0).getType(), new Logvar("Obj_1",x_pos_b.getArgument(0).getType()));
	    Atom x_pos_b1=x_pos_b.applyRenaming(ren_object).get(0);
	    
	    Atom y_pos_b=ntw.getPredicateNameToAtom().get("x_pos_b");
	    ren_object=new Renaming();
	    ren_object.addRenaming(y_pos_b.getArgument(0).getType(), new Logvar("Obj_1",y_pos_b.getArgument(0).getType()));
	    Atom y_pos_b1=y_pos_b.applyRenaming(ren_object).get(0);
	    
	    
	    Atom x_pos_a=ntw.getPredicateNameToAtom().get("x_pos_a");
	    ren_object=new Renaming();
	    ren_object.addRenaming(x_pos_a.getArgument(0).getType(), new Logvar("Obj_1",x_pos_a.getArgument(0).getType()));
	    Atom x_pos_a1=x_pos_a.applyRenaming(ren_object).get(0);
	    
	    Atom y_pos_a=ntw.getPredicateNameToAtom().get("y_pos_a");
	    ren_object=new Renaming();
	    ren_object.addRenaming(y_pos_a.getArgument(0).getType(), new Logvar("Obj_1",y_pos_a.getArgument(0).getType()));
	    Atom y_pos_a_1=y_pos_a.applyRenaming(ren_object).get(0);
	    
	    Atom main_object=ntw.getPredicateNameToAtom().get("main_object");
	    ren_object=new Renaming();
	    ren_object.addRenaming(main_object.getArgument(0).getType(), new Logvar("Obj_1",main_object.getArgument(0).getType()));
	    Atom main_object_1=main_object.applyRenaming(ren_object).get(0);
	    
	    Atom secondary_object=ntw.getPredicateNameToAtom().get("secondary_object");
	    ren_object=new Renaming();
	    ren_object.addRenaming(secondary_object.getArgument(0).getType(), new Logvar("Obj_1",secondary_object.getArgument(0).getType()));
	    Atom secondary_object_1=secondary_object.applyRenaming(ren_object).get(0);
	    
	    //Literal[] context_1=new Literal[]{new PosLiteral(main_object),new PosLiteral(main_object)};
	    Literal[] context_2=new Literal[]{new PosLiteral(main_object),new PosLiteral(secondary_object_1)};
	    Literal[] context_3=new Literal[]{new PosLiteral(main_object_1),new PosLiteral(secondary_object)};
	    //Literal[] context_4=new Literal[]{new PosLiteral(secondary_object),new PosLiteral(main_object)};
	    HashMap<String,List<ComplexConjunction>> tmp=new HashMap<String,List<ComplexConjunction>>();

	    
	    List<Feature> extra_features_delta_x=delta_x_ExtraFeatureCreator.getListOfFeatures(ntw.getPredicateNameToAtom().get("delta_x_sec"), ntw);
	    List<Feature> extra_features_x_pos_a=delta_x_ExtraFeatureCreator.getListOfFeatures(ntw.getPredicateNameToAtom().get("x_pos_a"), ntw);
	    //List<Feature> extra_features_x_pos_a=x_pos_a_ExtraFeatureCreator.getListOfFeatures(ntw.getPredicateNameToAtom().get("delta_x"), ntw);
	    int counter=1;
	    fGen.setAdditionalFeatures(extra_features_delta_x, ntw.getPredicateNameToAtom().get("delta_x_sec"));
	    fGen.setAdditionalFeatures(extra_features_x_pos_a, ntw.getPredicateNameToAtom().get("x_pos_a"));
	    List<Feature> ft_space=fGen.generateFeatures(ntw.getPredicateNameToAtom().get("x_pos_a"), ntw.getAtomsAndEqualityConstraints());
	    for(Feature f:ft_space){
	    	System.out.println(counter+" "+f);

	    	counter++;
	    }
	    //Complex_Feature proba=(Complex_Feature)extra_features_delta_x.get(25);
	    //proba.getConjunction().setOp(new Subtraction());
	    //((InBetween)proba.getComparator()).setThresholds(-11.00, 0.00);
	    //System.out.println(proba.getConjunction().getHead());
	   /* Dependency test_dep=new Dependency(ntw.getPredicateNameToAtom().get("delta_x_sec"),new Feature[]{ft_space.get(0)});
	    QueryData query_res=training_data_machine.getQueryResults(test_dep);
	    System.out.println(query_res);
	    test_dep.getCpd().setParameters(test_dep.getCpd().getCpdEvaluator().estimateParameters(query_res));
	    System.out.println(test_dep.getCpd().getParameters().toString());
	   */
	   StructureLearner str_learner=new StructureLearner(fGen,new GreedySearch(),ntw,training_data_machine,training_validation_machine,training_test_machine);	
       str_learner.learnStandardCPTs(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
	  
	
	}

}
