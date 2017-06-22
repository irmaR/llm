import hybrid.dependencies.Dependency;
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


public class Create_ARFF_hybrid {
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
			
			parser.addArgument("-features")
			.dest("features")
			.help("simplicity of features: -simple -full -intrinsic")
			.type(String.class)
			.required(true);
			
			Namespace res=null;
			try {
				res = parser.parseArgs(args);
				System.out.println(" RES " +res);
			} catch (ArgumentParserException e) {
				System.out.println(" Didn't succeed to parse the parameters");
				parser.handleError(e);
				System.exit(0);
			}
			HashMap<Predicate,Dependency> propos_dependencies=new HashMap<Predicate, Dependency>();
			PropositionalNetworkCreator propositionalizedPKK=null;
			
			if(res.getString("features").equals("simple")){
			propositionalizedPKK=new Propositionalized_PKDD_Hybrid_Simpler();
			propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(1);
			}
			
			if(res.getString("features").equals("full")){
				propositionalizedPKK=new Propositionalized_PKDD_Hybrid_Full();
				propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(1);
			}
			if(res.getString("features").equals("intrinsic")){
				propositionalizedPKK=new Propositionalized_PKDD_Intrinsic();
				propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(1);
			}
			TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

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
			

		}


}
*/