import hybrid.dependencies.Dependency;
import hybrid.evaluation.CalculatePredictionForAUC;
import hybrid.evaluation.CalculateRMSE;
import hybrid.evaluation.ExtractDependency;
import hybrid.interpretations.Data;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import weka.core.Debug.Random;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import networks.PKDD_in_same_region_added_hybrid;


public class EvaluateHybridPKDD {
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

		parser.addArgument("-path_to_results")
		.dest("path_to_results")
		.type(String.class)
		.help("Path containing folder train/test/validate with interpretations used by the learning algorithm")
		.required(true)
		;
		
		parser.addArgument("-aux_folder")
		.dest("aux_folder")
		.type(String.class)
		.help("auxiliary folder")
		.required(true)
		;
		
		parser.addArgument("-folds")
		.dest("folds")
		.type(Integer.class)
		.nargs("+")
		.setDefault(new Integer[]{1,2,3,4,5,6,7,8,9,10})
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
		
		ExtractDependency depExtractor=new ExtractDependency();
		CalculateRMSE calculateRMSE=new CalculateRMSE();
		CalculatePredictionForAUC calculatePredictionsForAUC=new CalculatePredictionForAUC();
		PKDD_in_same_region_added_hybrid hybrid_university=new PKDD_in_same_region_added_hybrid();
		NetworkInfo ntw=hybrid_university.getHybridPKDD(1);
	    
	
	    List<Atom> rmse=new ArrayList<Atom>();
	    rmse.add(ntw.getAtom("clientAge"));
	    rmse.add(ntw.getAtom("avgSalary"));
	    rmse.add(ntw.getAtom("monthlyPayments"));
	    rmse.add(ntw.getAtom("clientAge"));
	    rmse.add(ntw.getAtom("avgSumOfW"));
	    rmse.add(ntw.getAtom("loanAmount"));
	    rmse.add(ntw.getAtom("monthlyPayments"));
	    rmse.add(ntw.getAtom("clientAge"));
	    rmse.add(ntw.getAtom("avgNrWith"));
	    rmse.add(ntw.getAtom("avgSumOfInc"));
	    rmse.add(ntw.getAtom("stdMonthInc"));
	    rmse.add(ntw.getAtom("stdMonthW"));
	    
	    rmse.add(ntw.getAtom("ratUrbInhab"));
	    
	    List<Atom> auc=new ArrayList<Atom>();
	    
	    List<Atom> getProbsForAtoms=new ArrayList<Atom>();	
	    getProbsForAtoms.add(ntw.getAtom("loanStatus"));
	    getProbsForAtoms.add(ntw.getAtom("freq"));
	    getProbsForAtoms.add(ntw.getAtom("hasLoan"));
	    
	    getProbsForAtoms.add(ntw.getAtom("hasAccount"));
	    getProbsForAtoms.add(ntw.getAtom("gender"));
	    getProbsForAtoms.add(ntw.getAtom("clientDistrict"));
	    
	
	    List<Integer> runs=res.getList("folds");
	    //GET RMSE
	    
	     for(int run:runs){
	    		String PathToData=getPathToData(res.getString("input_path"),run);
	    		String pathToResults=getPathToData(res.getString("path_to_results"),run);
	    		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator());
				TuPrologDataLoader dataLoader_no_subsampling=new TuPrologDataLoader(new TuPrologInterpretationCreator_NoSubsampling());

	    		Data d_training=dataLoader.loadData(PathToData+"/train/", "fold", "pl", ntw);
	    	    Data d_test=dataLoader_no_subsampling.loadData(PathToData+"/test/", "fold", "pl", ntw);
	    	    TuPrologQueryMachine tuPrologQueryMachine_test=new TuPrologQueryMachine(d_test, new NoPenalty());
	    	    TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d_training, new MDLPenalty());
	    	    //calculate rmse
	    	    Random ran=new Random();
	    	    ran.setSeed(123456789);
	    	    
	    	    for(Atom at:rmse){
	    	      File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
	    	      if(!learnedDep_file.exists()){
	    	    	  System.out.println(learnedDep_file);
	    	    	  System.out.println(" File doesn't exist");
	    	    	  continue;
	    	      }
	    	      else{
	    	    	  System.out.println(" Calculating RMSE for: "+at);
	    	    	  File result_file=createResultingFile(pathToResults+"/"+res.getString("aux_folder")+"/","rmse",at.getPredicate().getPredicateName());
	    	    	  System.out.println(" Result file: "+result_file);

	    	    	  Dependency learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
	    	    	  learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
	    	    	  calculateRMSE.calculateRMSE_no_noise(learnedDep, tuPrologQueryMachine_test, result_file);
	    	    	  //calculateRMSE.calculateRMSE(learnedDep, tuPrologQueryMachine_test, result_file,ran);

	    	      }
	    	    }
	    	    //get aucs
	    	    for(Atom at:auc){
	    	    	 File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
		    	      if(!learnedDep_file.exists()){
		    	    	  continue;
		    	      }
		    	      else{
		    	    	  File result_file=createResultingFileMatlab(pathToResults,"auc_mat_files",at.getPredicate().getPredicateName());
		    	    	  Dependency learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
		    	    	  learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
		    	    	  calculatePredictionsForAUC.calculatePredictionsForAUCMatlab(learnedDep, tuPrologQueryMachine_test, result_file);
		    	      }
	    	    }
	    	    //get only probs for each ground atom
	    	    System.out.println("Obtaining probabilities for:");
	    	    for(Atom at:getProbsForAtoms){
	    	    	File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
	    	    	if(!learnedDep_file.exists()){
	    	    		System.out.println(" Dependency for: "+at+" not learned");
		    	    	  continue;
		    	      }
		    	      else{
		    	    	  File result_file=new File(pathToResults+"/"+res.getString("aux_folder")+"/"+"/auc/probs_"+at.getPredicate().getPredicateName());
		    	    	  result_file.getParentFile().mkdirs();
		    	    	  File true_class_labels=new File(pathToResults+"/auc/probs_"+at.getPredicate().getPredicateName()+"_true_labels");
		    	    	  Dependency learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
		    	    	  learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
		    	    	  calculatePredictionsForAUC.getProbabilitiesForGroundAtoms_no_noise(learnedDep, tuPrologQueryMachine_test, result_file,true_class_labels);
		    	      }
	    	    }
	  	    	    	   
	    	} 
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