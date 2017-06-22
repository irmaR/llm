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
import network.VisibilityNetwork;


public class VISIBILITYStandardSearchWithScript {
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
		VisibilityNetwork hybrid_robotics_simple=new VisibilityNetwork();
		ntw=hybrid_robotics_simple.getNetwork(1);

		//LOAD DATA
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","objvis", "txt", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "objvis", "txt",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "objvis", "txt", ntw,DataType.test);

		List<Feature> extra_features_visible_next=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("visible_next"), ntw);
		List<Feature> extra_features_visible_cur=next_arm_extra_features.getListOfFeatures(ntw.getPredicateNameToAtom().get("visible_cur"), ntw);

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

		fGen.setAdditionalFeatures(extra_features_pos_x_next, ntw.getPredicateNameToAtom().get("pos_x_next"));
		fGen.setAdditionalFeatures(extra_features_pos_y_next, ntw.getPredicateNameToAtom().get("pos_y_next"));
		fGen.setAdditionalFeatures(extra_features_pos_z_next, ntw.getPredicateNameToAtom().get("pos_z_next"));
		fGen.setAdditionalFeatures(extra_features_visible_next, ntw.getPredicateNameToAtom().get("visible_next"));
		fGen.setAdditionalFeatures(extra_features_visible_cur, ntw.getPredicateNameToAtom().get("visible_cur"));

		HashMap<Atom,List<Predicate>> exclusion_predicates=new HashMap<Atom,List<Predicate>>();


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

		fGen.setExclusionPredicates(exclusion_predicates);

		int counter=0;
		List<Feature> fts=fGen.generateFeatures(ntw.getAtom("visible_cur"),ntw.getLiterals());
		for(Feature f:fts){
			System.out.println(f);
		}
		DecisionTreeLearning dtl=new DecisionTreeLearning(new DTDependencySelectorStandardWithScript(new String[]{"/usr/bin/python"},AlgorithmParameters.output_path,null));
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,validation_machine,test_machine);	
		//int[] indices=makeSequence(0, 2,50);
		int[] indices=new int[]{19,373,61,344,322,346};
		
				str_learner.setSelectedFeatureIndices(indices);
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
						
						//Print DC clause
						VisibilityParseDecisionTreeToDC dcConverter=new VisibilityParseDecisionTreeToDC();
						dcConverter.parse(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".ser", AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dclause");
						
					}catch(IOException i)
					{
						i.printStackTrace();
					}
				}

		}

		
	}
