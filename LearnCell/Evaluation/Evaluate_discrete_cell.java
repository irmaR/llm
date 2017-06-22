package Evaluation;

import hybrid.dependencies.Dependency;
import hybrid.evaluation.CalculatePredictionForAUC;
import hybrid.evaluation.CalculateRMSE;
import hybrid.evaluation.ExtractDependency;
import hybrid.experimenter.ExtractValueFromFile;
import hybrid.interpretations.Data;
import hybrid.interpretations.NoCycles;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_NoSubsampling;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.RelationType;
import hybrid.network.Type;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.ArffFile;
import hybrid.querydata.QueryData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.Debug.Random;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class Evaluate_discrete_cell {
}/*
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
				ExtractValueFromFile extractWPLL=new ExtractValueFromFile();
				ExtractDependency depExtractor=new ExtractDependency();
				CalculateRMSE calculateRMSE=new CalculateRMSE();
				CalculatePredictionForAUC calculatePredictionsForAUC=new CalculatePredictionForAUC();
				
				//the network
				Type cells=new Type("cell_id");
				Logvar cell=new Logvar("cell",cells);
				Logvar cell1=new Logvar("cell1",cells);
				
				BooleanPred parent_pred=new BooleanPred("parent",2);
				parent_pred.setSubsampleingProcedure(new TuPrologSubSample(new NoCycles(),0.7));
				
				
				CategoricalPred cell_oldpole_pred=new CategoricalPred("cell_oldpole", 1);
				CategoricalPred cell_age_pred=new CategoricalPred("cell_age", 1);
				DiscretizedPredicate cell_doublingtime_pred=new DiscretizedPredicate("cell_doublingtime", 1,5);
				DiscretizedPredicate cell_lengthatbirth_pred=new DiscretizedPredicate("cell_lengthatbirth",1,5);
				DiscretizedPredicate cell_avglength_pred=new DiscretizedPredicate("cell_avglength",1,5);
				DiscretizedPredicate cell_lengthgrowthrate_pred=new DiscretizedPredicate("cell_lengthgrowthrate",1,5);

				

				Atom parent=new Atom(parent_pred, new Logvar[]{cell,cell1});
				parent.setRelationType(RelationType.INTERNAL);
				Atom cell_age=new Atom(cell_age_pred, new Logvar[]{cell});
				Atom cell_pole=new Atom(cell_oldpole_pred, new Logvar[]{cell});
				Atom cell_doublingtime=new Atom(cell_doublingtime_pred, new Logvar[]{cell});
				Atom cell_lengthatbirth=new Atom(cell_lengthatbirth_pred, new Logvar[]{cell});
				Atom cell_avglength=new Atom(cell_avglength_pred, new Logvar[]{cell});
				Atom cell_lengthgrowthrate=new Atom(cell_lengthgrowthrate_pred, new Logvar[]{cell});
				
				NetworkInfo ntw=new NetworkInfo(new Atom[]{parent,cell_age,cell_pole,cell_doublingtime,cell_lengthatbirth,cell_avglength,cell_lengthatbirth,cell_lengthgrowthrate},new Type[]{cells});
			    List<Atom> rmse=new ArrayList<Atom>(); 
			    List<Atom> auc=new ArrayList<Atom>();
			    
			    List<Atom> getProbsForAtoms=new ArrayList<Atom>();	
			    getProbsForAtoms.add(ntw.getAtom("cell_doublingtime"));
			    getProbsForAtoms.add(ntw.getAtom("cell_lengthatbirth"));
			    getProbsForAtoms.add(ntw.getAtom("cell_avglength"));
			    getProbsForAtoms.add(ntw.getAtom("cell_lengthgrowthrate"));
			    getProbsForAtoms.add(ntw.getAtom("cell_age"));
			    getProbsForAtoms.add(ntw.getAtom("cell_oldpole"));
			    getProbsForAtoms.add(ntw.getAtom("parent"));
			 
			
			    List<Integer> runs=res.getList("folds");
			    //GET RMSE
			    HashMap<String,Double> average_results=new HashMap<String, Double>();
			    HashMap<String,List<Double>> results_folds=new HashMap<String, List<Double>>();
			    HashMap<String,List<Double>> per_fold_WPLL=new HashMap<String, List<Double>>();
			    
			    for(Atom at:ntw.getLiterals()){
			    	results_folds.put(at.getPredicate().getPredicateName(), new ArrayList<Double>());
			    	average_results.put(at.getPredicate().getPredicateName(),0.0);
			    	per_fold_WPLL.put(at.getPredicate().getPredicateName(), new ArrayList<Double>());
			    }
			    
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
			    	      per_fold_WPLL.get(at.getPredicate().getPredicateName()).add(Double.valueOf(extractWPLL.getValueOfLineContainingString(learnedDep_file,"WPLL_test","=", 1)));
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
			    	    	  double rmse_res= calculateRMSE.calculateRMSE_no_noise(learnedDep, tuPrologQueryMachine_test, result_file);
			    	    	  results_folds.get(at.getPredicate().getPredicateName()).add(rmse_res);
			    	    	  //calculateRMSE.calculateRMSE(learnedDep, tuPrologQueryMachine_test, result_file,ran);

			    	      }
			    	    }
			    	    //get aucs
			    	    for(Atom at:auc){
			    	    	 File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
				    	      per_fold_WPLL.get(at.getPredicate().getPredicateName()).add(Double.valueOf(extractWPLL.getValueOfLineContainingString(learnedDep_file,"WPLL_test","=", 1)));
	 
			    	    	 if(!learnedDep_file.exists()){
				    	    	  continue;
				    	      }
				    	      else{
				    	    	  File result_file=createResultingFileMatlab(pathToResults,"auc_mat_files",at.getPredicate().getPredicateName());
				    	    	  Dependency learnedDep=depExtractor.extractDependency(learnedDep_file, at, ntw);
				    	    	  learnedDep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine_training.getQueryResults(learnedDep));
				    	    	  calculatePredictionsForAUC.calculatePredictionsForAUCMatlab(learnedDep, tuPrologQueryMachine_test, result_file);
				    	    	  QueryData qd_test=tuPrologQueryMachine_test.getQueryResults(learnedDep);
				    	    	  QueryData qd_train=tuPrologQueryMachine_training.getQueryResults(learnedDep);
				    	    	  ArffFile arffFile_test=new ArffFile(qd_test);
				    	    	  ArffFile arffFile_train=new ArffFile(qd_train);
				    	    	  
				    	    	  Instances training_instances=arffFile_train.getQueryDataAsARFF_instances();
				    	    	  Instances test_instances=arffFile_test.getQueryDataAsARFF_instances();

				    	    	  Classifier cls = new Logistic();
				    			  cls.setOptions(new String[]{"-A"});
				    			  System.out.println(training_instances);
				    			  cls.buildClassifier(training_instances);
				    			  
				    			  Evaluation eval = new Evaluation(training_instances);
				    			  eval.evaluateModel(cls, test_instances);
				    			  results_folds.get(at.getPredicate().getPredicateName()).add(eval.weightedAreaUnderROC());
				    			  System.out.println(cls);
				    	      }
			    	    }
			    	    //get only probs for each ground atom
			    	    System.out.println("Obtaining probabilities for:");
			    	    
			    	    for(Atom at:getProbsForAtoms){
			    	    	System.out.println("Atom "+at+" \n"+pathToResults);
			    	    	File learnedDep_file=getFileOfLearnedDependency(pathToResults,at.getPredicate().getPredicateName());
				    	    per_fold_WPLL.get(at.getPredicate().getPredicateName()).add(Double.valueOf(extractWPLL.getValueOfLineContainingString(learnedDep_file,"WPLL_test","=", 1)));

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
				    	    	  QueryData qd_test=tuPrologQueryMachine_test.getQueryResults(learnedDep);
				    	    	 // calculatePredictionsForAUC.getProbabilitiesForGroundAtoms_no_noise(learnedDep, tuPrologQueryMachine_test, result_file,true_class_labels);
				    	    	  //System.out.println("Query data test: "+qd_test);

				    	    	  QueryData qd_train=tuPrologQueryMachine_training.getQueryResults(learnedDep);  	  
				    	    	  ArffFile arffFile_test=new ArffFile(qd_test);
				    	    	  ArffFile arffFile_train=new ArffFile(qd_train);
				    	    	  
				    	    	  Instances training_instances_orig=arffFile_train.getQueryDataAsARFF_instances();
				    	    	  Instances test_instances_orig=arffFile_test.getQueryDataAsARFF_instances();
				    	    	  
				    	    	  Instances training_instances=null;
				    	    	  Instances test_instances=null;
				    	    	  
				    	    	  
	                              if(at.getPredicate().getPredicateName().equals("cell_oldpole") || at.getPredicate().getPredicateName().equals("cell_age")){
	                            	  System.out.println(" ------------ CONVERTING TO NOMINAL ----------------------");
	                            	  String[] options= new String[2];
	                                  options[0]="-R";
	                                  options[1]="last";  //range of variables to make numeric

	                                 
	                            	  NumericToNominal convert_train= new NumericToNominal();
	                            	  convert_train.setOptions(options);
	                            	  NumericToNominal convert_test= new NumericToNominal();
	                            	  convert_test.setOptions(options);
	                            	  convert_train.setInputFormat(training_instances_orig);
	                            	  convert_test.setInputFormat(test_instances_orig);                                  
	                            	  training_instances=Filter.useFilter(training_instances_orig, convert_train);
	                                  test_instances=Filter.useFilter(test_instances_orig, convert_test);
	                                  
				    	    	  }
	                              else{
	                            	  training_instances=training_instances_orig;
	                            	  test_instances=test_instances_orig;
	                              }
	                              training_instances.setClassIndex(training_instances.numAttributes() - 1);
				    	    	  test_instances.setClassIndex(test_instances.numAttributes() - 1);
	                              System.out.println("Nr test instances: "+test_instances.numInstances());
				    	    	  //System.out.println("Test data: "+test_instances);
				    	    	  
								  Classifier cls = new Logistic();
				    			  cls.setOptions(new String[]{"-A"});
				    			  cls.buildClassifier(training_instances);
				    			  Evaluation eval = new Evaluation(training_instances);
				    			  eval.evaluateModel(cls, test_instances);
				    			  results_folds.get(at.getPredicate().getPredicateName()).add(eval.weightedAreaUnderROC());
				    			 System.out.println("ATOM" +at+ " \n"+cls+ " \n AUC "+eval.weightedAreaUnderROC());
				    	         System.out.println(eval.toClassDetailsString());
				    	      }
			    	    }
			  	    	    	   
			    	} 
			     File output_average=new File(res.getString("path_to_results")+"average_results.stat");
			     BufferedWriter bw=new BufferedWriter(new FileWriter(output_average));
			     
			     
			     for(String s:results_folds.keySet()){
			    	 System.out.println(per_fold_WPLL.get(s));
				     File output_per_fold=new File(res.getString("path_to_results")+s+"_average_results_per_fold.stat");
				     File output_per_fold_wpll=new File(res.getString("path_to_results")+s+"_wpll_per_fold.stat");

				     BufferedWriter bw1=new BufferedWriter(new FileWriter(output_per_fold));
				     BufferedWriter bw2=new BufferedWriter(new FileWriter(output_per_fold_wpll));
			    	 Double sum=0.0;
			    	 for(Double d:results_folds.get(s)){
			    		 bw1.append(d+"\t");
			    		 sum+=d;
			    	 }
			    	 
			    	 for(Double d:per_fold_WPLL.get(s)){
			    		 bw2.append(d+"\n");
			    	 }
			    	 bw2.close();
			    	 bw1.close();
			    	 bw.append(s+" "+sum/results_folds.get(s).size()+"\n");
			    	 
			     }
			     bw.close();
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