import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.FeatureTypeException;
import hybrid.interpretations.Data;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Predicate;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.ArffFile;
import hybrid.querydata.QueryData;

import java.io.File;
import java.util.HashMap;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class Create_ARFF_discretized {
	
}
/*

	//Given a specific DN, this file will create
	//arff files for the markov blankets given in dependencies. 
	//This class will create for each target predicate in DN
	//an ARFF file with the predicate's name and attributes 
	//as values of features in the predicate's parent set
	
	//Arguments
	//1) Path to training data used to extract values of features in ARFF(
	//(training data)
	//2) Path output folder where the results will be stored 
	
	public static void main(String[] args) throws Exception{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("prog")
				.description("Process paths.");

		parser.addArgument("-input")
		.dest("input_path")
		.type(String.class)
		.help("Path containing folder train/test/validate with interpretations used by the learning algorithm")
		.required(true)
		;

		
		parser.addArgument("-output")
		.dest("outputPath")
		.help("Path where the logging and results will be written")
		.type(String.class)
		.required(true);
		
		parser.addArgument("-predicate")
		.dest("predicate")
		.help("predicate for which we obtain arff format")
		.type(String.class)
		.required(true);
		
		parser.addArgument("-type_of_data")
		.dest("type_of_data")
		.help("predicate for which we obtain arff format")
		.type(String.class)
		.required(true);
		
	
		
		parser.addArgument("-discretization")
		.dest("discretization")
		.type(Integer.class)
		.setDefault(new Integer(-1))
		.help("can be 2 4 6 8 ")
		.required(false);
		

		

		Namespace res=null;

		try {
			res = parser.parseArgs(args);
			System.out.println(" RES " +res);
		} catch (ArgumentParserException e) {
			System.out.println(" Didn't succeed to parse the parameters");
			parser.handleError(e);
			System.exit(0);
		}
		
		TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

		Create_Propositionalized_PKDD_simpler propositionalizedPKK=new Create_Propositionalized_PKDD_simpler();
		HashMap<Predicate,Dependency> propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(Integer.valueOf(res.getInt("discretization")),1);
		//PropositionalizedPKDD propositionalizedPKK=new PropositionalizedPKDD();
		//HashMap<Predicate,Dependency> propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(Integer.valueOf(res.getInt("discretization")),1);
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
		Data data=null;
		File output_file_test=null;
		if(res.getString("type_of_data").equals("train")){
   		  data=dataLoader.loadData(res.getString("input_path"), "fold", "pl",propositionalizedPKK.getNtwInfo());
		}
		else{
		  data=dataLoader_no_subsampling.loadData(res.getString("input_path"), "fold", "pl",propositionalizedPKK.getNtwInfo());
		  output_file_test=new File(res.getString("outputPath")+"/"+res.getString("predicate")+"_test.arff");
		}
		
		File output_file=new File(res.getString("outputPath")+"/"+res.getString("predicate")+".arff");
		output_file.getParentFile().mkdirs();
		File predicate_arff=new File(output_file.getPath()+"/"+res.getString("predicate")+".arff");
		
		TuPrologQueryMachine  tuPrologQueryMachine_test=new TuPrologQueryMachine(data, new NoPenalty());
		Dependency gender_dep=propos_dependencies.get(propositionalizedPKK.getNtwInfo().getPredicateNameToAtom().get(res.getString("predicate")).getPredicate());
		QueryData qData=tuPrologQueryMachine_test.getQueryResults(gender_dep);
        ArffFile arffFile_gender=new ArffFile(qData);
        arffFile_gender.getQueryDataAsARFF_file(output_file);
        if(output_file_test!=null){
        qData.outputGroundAtomsForAllvalues(output_file_test);
        }
       // System.out.println(qData);
		
		

	}

}
*/