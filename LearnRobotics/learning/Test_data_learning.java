package learning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hybrid.comparators.Bigger;
import hybrid.comparators.InBetween;
import hybrid.comparators.Smaller;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.ParseArguments;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.OperatorFeature;
import hybrid.features.Feature;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.ValueFt;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.operators.Addition;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.DTDependencySelectorStandard;
import hybrid.structureLearning.DecisionTreeLearning;
import hybrid.structureLearning.GreedySearch;
import hybrid.structureLearning.LearnedModelTree;
import hybrid.structureLearning.StructureLearner;
import hybrid.structureLearning.Tree;
import network.Test_data;

public class Test_data_learning {

	private static  NetworkInfo ntw;

	public static void main(String[] args) throws Exception{
		AlgorithmParameters parameters=new AlgorithmParameters();
		ParseArguments getAlgorithmParameters=new ParseArguments(" Boolean subsampling fixed - full feature space for all - FIXED PROBLEM WITH LOADING INTERPRETATIONS - added features to blocks functionality");
		getAlgorithmParameters.parseArgumentsHRDN(args);

		System.out.println("Parameters: "+parameters);

		Test_data hybrid_robotics_simple=new Test_data();
		ntw=hybrid_robotics_simple.getNetwork(1);

		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());
		Data d_training=dataLoader_no_subsampling.loadData(parameters.input_path+"/train/","interp", "pl", ntw,DataType.training);
		Data d_validation=dataLoader_no_subsampling.loadData(parameters.input_path+"/validate/", "interp", "pl",ntw,DataType.validation);
		Data d_test=dataLoader_no_subsampling.loadData(parameters.input_path+"/test/", "interp", "pl", ntw,DataType.test);


		TuPrologQueryMachine training_data_machine=new TuPrologQueryMachine(d_training, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine training_validation_machine=new TuPrologQueryMachine(d_validation, AlgorithmParameters.getPenaltyType());
		TuPrologQueryMachine training_test_machine=new TuPrologQueryMachine(d_test, AlgorithmParameters.getPenaltyType());

		FeatureGeneratorNoRestrictions fGen=new FeatureGeneratorNoRestrictions(AlgorithmParameters.feature_length, AlgorithmParameters.nr_logvar_renamings);
		fGen.do_not_generate_features();
		Atom out=ntw.getPredicateNameToAtom().get("out");
		List<Standard_Conjunction> out_complex_conjunctions=new ArrayList<Standard_Conjunction>();
		out_complex_conjunctions.add(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))));

		double min_threshold=-20;
		double max_threshold=20;

		HashMap<String,List<Feature>> additional_features=new HashMap<String, List<Feature>>();
		double jump=4;
		List<Feature> fts=new ArrayList<Feature>();
		for(Standard_Conjunction c:out_complex_conjunctions){
			for(double i=min_threshold;i<max_threshold;i+=jump){
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new InBetween(i-jump,i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new InBetween(i-jump,i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new InBetween(i-jump,i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Bigger(i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Bigger(i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Bigger(i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Smaller(i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Smaller(i),new ValueFt()));
				fts.add(new ComparisonFeature(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in"))), new Smaller(i),new ValueFt()));
			}
		}
		fts.add(new ValueFt(new Standard_Conjunction(out,new PosLiteral(ntw.getPredicateNameToAtom().get("in")))));
		DecisionTreeLearning dtl=new DecisionTreeLearning(new DTDependencySelectorStandard());
		dtl.setOutputDirectory(AlgorithmParameters.output_path);
		StructureLearner str_learner=new StructureLearner(fGen,dtl,ntw,training_data_machine,training_validation_machine,training_test_machine);	
        fGen.setAdditionalFeatures(fts, ntw.getAtom("out"));
		
		LearnedModelTree trees=str_learner.learnModelTree(ntw.getLiterals().toArray(new Atom[ntw.getLiterals().size()]));
		for(Atom a:trees.getLearnedDependency().keySet()){
			try
			{
				Tree learned_tree=trees.getLearnedDependency().get(a);
				FileOutputStream fileOut =new FileOutputStream(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".ser");
				ObjectOutputStream out_str = new ObjectOutputStream(fileOut);
				out_str.writeObject(learned_tree);
				out_str.close();
				fileOut.close();
				System.out.printf(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName());
				FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dot"));
				fw.append(learned_tree.makeDigraph(learned_tree.getRoot(),""));
				fw.close();
				
				
			}catch(IOException i)
			{
				i.printStackTrace();
			}
		}


	}
}
