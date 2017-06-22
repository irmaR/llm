import hybrid.dependencies.Dependency;
import hybrid.evaluation.CalculatePredictionForAUC;
import hybrid.evaluation.CalculateRMSE;
import hybrid.evaluation.ExtractDependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.interpretations.Data;
import hybrid.interpretations.DiscretizedRangeExtractor;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class EvaluateDiscretizedPKDD {
}
/*
	public static void main(String[] args) throws Exception{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("prog")
				.description("Process paths.");

		parser.addArgument("-input")
		.dest("input_path")
		.type(String.class)
		.help("Path containing folder train/test/validate with interpretations used by the learning algorithm")
		.required(true)
		;

		parser.addArgument("-discr")
		.dest("discretization_level")
		.type(Integer.class)
		.help("discretization level")
		.required(true)
		;

		parser.addArgument("-path_to_results")
		.dest("path_to_results")
		.type(String.class)
		.help("Path containing folder train/test/validate with interpretations used by the learning algorithm")
		.required(true)
		;


		Namespace res=null;

		try {
			res = parser.parseArgs(args);
			System.out.println(" RES " +res);
		} catch (ArgumentParserException e) {
			System.out.println(" Didn't succeed to parse the parameters");
			parser.handleError(e);
			System.exit(0);
		}

		ExtractDependency depExtractor=new ExtractDependency(res.getInt("discretization_level"));
		CalculateRMSE calculateRMSE=new CalculateRMSE();
		CalculatePredictionForAUC calculatePredictionsForAUC=new CalculatePredictionForAUC();

		double sub_sampling_ratio=1;
		CreateDiscretizedPKDD hybrid_university=new CreateDiscretizedPKDD();
		NetworkInfo ntw=hybrid_university.getDiscretizedPKDD(res.getInt("discretization_level"),sub_sampling_ratio);



		List<Atom> auc=new ArrayList<Atom>();
		 auc.add(ntw.getAtom("hasLoan"));
	    auc.add(ntw.getAtom("loanStatus"));
	    auc.add(ntw.getAtom("freq"));
	    auc.add(ntw.getAtom("hasAccount"));
	    auc.add(ntw.getAtom("gender"));
	    auc.add(ntw.getAtom("clientDistrict"));
	    auc.add(ntw.getAtom("loanAmount"));
	    auc.add(ntw.getAtom("monthlyPayments"));
	    auc.add(ntw.getAtom("clientAge"));
	    auc.add(ntw.getAtom("avgNrWith"));
	    auc.add(ntw.getAtom("avgSumOfInc"));
	    auc.add(ntw.getAtom("stdMonthInc"));
	    auc.add(ntw.getAtom("stdMonthW"));
	    auc.add(ntw.getAtom("avgSalary"));
	    auc.add(ntw.getAtom("ratUrbInhab"));


		List<Atom> getProbsForAtoms=new ArrayList<Atom>();
		getProbsForAtoms.add(ntw.getAtom("avgSumOfW"));
		getProbsForAtoms.add(ntw.getAtom("hasLoan"));
		getProbsForAtoms.add(ntw.getAtom("hasAccount"));
		getProbsForAtoms.add(ntw.getAtom("loanStatus"));
		getProbsForAtoms.add(ntw.getAtom("hasLoan"));
		getProbsForAtoms.add(ntw.getAtom("avgSalary"));
		getProbsForAtoms.add(ntw.getAtom("clientAge"));
		getProbsForAtoms.add(ntw.getAtom("hasAccount"));
		getProbsForAtoms.add(ntw.getAtom("clientAge"));
		getProbsForAtoms.add(ntw.getAtom("gender"));
		
		getProbsForAtoms.add(ntw.getAtom("loanStatus"));
		getProbsForAtoms.add(ntw.getAtom("freq"));
		getProbsForAtoms.add(ntw.getAtom("hasAccount"));

		getProbsForAtoms.add(ntw.getAtom("clientDistrict"));
		getProbsForAtoms.add(ntw.getAtom("loanAmount"));
		getProbsForAtoms.add(ntw.getAtom("monthlyPayments"));

		getProbsForAtoms.add(ntw.getAtom("avgNrWith"));
		getProbsForAtoms.add(ntw.getAtom("avgSumOfInc"));
		getProbsForAtoms.add(ntw.getAtom("stdMonthInc"));
		getProbsForAtoms.add(ntw.getAtom("stdMonthW"));
		getProbsForAtoms.add(ntw.getAtom("ratUrbInhab"));



		int nr_runs=10;
		//GET RMSE
		File debug=new File(res.getString("path_to_results")+"/not_finalized.debug");
		debug.getParentFile().mkdirs();
		FileWriter fw=new FileWriter(debug);
		
		for(int run=1;run<=nr_runs;run++){
			String PathToData=getPathToData(res.getString("input_path"),run);
			DiscretizedRangeExtractor extractRanges=new DiscretizedRangeExtractor();		
			HashMap<Predicate,MinMaxValue> ranges=extractRanges.getMinMaxValuesForPredicatesFromDiscretizationInfo(PathToData+"/discretization_info.info",new Atom[]{ntw.getPredicateNameToAtom().get("loanAmount"),ntw.getPredicateNameToAtom().get("monthlyPayments"),ntw.getPredicateNameToAtom().get("clientAge"),ntw.getPredicateNameToAtom().get("avgNrWith"),ntw.getPredicateNameToAtom().get("avgSumOfW"),ntw.getPredicateNameToAtom().get("avgSumOfInc"),ntw.getPredicateNameToAtom().get("stdMonthInc"),ntw.getPredicateNameToAtom().get("stdMonthW"),ntw.getPredicateNameToAtom().get("avgSalary"),ntw.getPredicateNameToAtom().get("ratUrbInhab")});
			TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

			String pathToResults=getPathToData(res.getString("path_to_results"),run);
			TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
			Data d_training=dataLoader.loadData(PathToData+"/train/", "fold", "pl", ntw);
			Data d_test=dataLoader_no_subsampling.loadData(PathToData+"/test/", "fold", "pl", ntw);
            System.out.println(d_test.getNrGroundingsInData(ntw.getAtom("hasAccount")));
			TuPrologQueryMachine tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, new NoPenalty());
			TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
			//get aucs
			for(Atom at:auc){
				File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
				if(!learnedDep_file.exists()){
					continue;
				}
				else{
					File result_file=createResultingFileMatlab(pathToResults,"evaluation",at.getPredicate().getPredicateName());
					Dependency learnedDep=null;
					try{
						learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
					}
					catch(Exception e){
						fw.append("not evaluated: "+pathToResults+" atom "+at);
						continue;
					}
					learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
					calculatePredictionsForAUC.calculatePredictionsForAUCMatlab(learnedDep, tuPrologQueryMachine_test, result_file);
				}
			}
			//get only probs for each ground atom
			//not  creating matlab files!
			System.out.println("Obtaining probabilities for:");
			for(Atom at:getProbsForAtoms){
				try{
					File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
					if(!learnedDep_file.exists()){
						System.out.println(" Dependency for: "+at+" not learned");
						continue;
					}
					else{
						File result_file=new File(pathToResults+"/auc/probs_"+at.getPredicate().getPredicateName());

						result_file.getParentFile().mkdirs();
						File true_class_labels=new File(pathToResults+"/auc/probs_"+at.getPredicate().getPredicateName()+"_true_labels");
						System.out.println(" Extracting dep for: "+at+ " path: "+learnedDep_file.getPath());

						Dependency learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
						System.out.println(" LEARNED DEPENDENCY: "+learnedDep);
						
						learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
						calculatePredictionsForAUC.getProbabilitiesForGroundAtoms(learnedDep, tuPrologQueryMachine_test, result_file,true_class_labels);
					}
				}
				catch(Exception e){
					continue;
				}
			}
		}
		fw.close();
	}

	private static File createResultingFileMatlab(String pathToResults,String folder_name, String predicateName) {
		File tmp1=new File(pathToResults+"/"+folder_name+"/"+predicateName+"_mat/");
		tmp1.mkdirs();
		File tmp=new File(pathToResults+"/"+folder_name+"/"+predicateName+"_mat/"+predicateName+".mat");
		return tmp;
	}

	private static File createResultingFile(String pathToResults,String folder_name, String predicateName) {
		File tmp1=new File(pathToResults+"/"+folder_name+"/");
		tmp1.mkdirs();
		File tmp=new File(pathToResults+"/"+folder_name+"/"+predicateName+".eval");
		//tmp.
		return tmp;
	}

	private static File getFileOfLearnedDependency(String pathToResults,String predicateName) {
		return new File(pathToResults+"/"+predicateName+"_stat.res");
	}

	private static String getPathToData(String path, int run) {
		return path+"/"+"Fold"+run;
	}

}
*/