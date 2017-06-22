package hybrid.structureLearning;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.CVIteration;
import hybrid.experimenter.CrossValidation;
import hybrid.featureGenerator.FeatureGeneratorNoRestrictions;
import hybrid.featureGenerator.FeatureGeneratorAbstract;
import hybrid.featureGenerator.SampleFeatureSpace;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.penalties.NoPenalty;
import hybrid.queryMachine.ComputedFeatureSpace;
import hybrid.queryMachine.QueryMachine;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.utils.FileSearching;
import hybrid.utils.GenerateUniqueIDforAtom;

/**
 * This class is used to learn the structure for a network specified in NetworkInfo, with a specific query machine,
 * training data (used for parameter estimation), test_data (used for scoring the learned structure) and validation data (used to select
 * the structure).
 * @author irma
 *
 */
public class StructureLearner {
	private FeatureGeneratorAbstract feature_generator;
	private StructureSearch structure_search;
	private NetworkInfo ntw;
	private QueryMachine query_training;
	private QueryMachine query_test;
	private QueryMachine query_validate;
	private int[] selected_feature_indices; //select only particular features for learning; indices correspond to their feature space index

	public StructureLearner(FeatureGeneratorAbstract f_generator,StructureSearch struct_search,NetworkInfo ntw,QueryMachine training,QueryMachine validation,QueryMachine test){
		this.feature_generator=f_generator;
		this.structure_search=struct_search;
		this.ntw=ntw;
		this.query_training=training;
		this.query_validate=validation;
		this.query_test=test;
		this.ntw.initializeRandvarTests();
	}

	public void setSelectedFeatureIndices(int[] indices){
		System.out.println("Setting selected feature indices .....");
		this.selected_feature_indices=indices;
	}

	//TBA: cross validation experiments
	public HashMap<Atom,Double> DTLearnStructureAndEvaluateCrossValidation(Atom[] learn_structure_for,List<Interpretation> allInterpretations) throws IOException, ResultAlreadyExistsException{		
		CrossValidation cval=new CrossValidation(allInterpretations);
		int validation_fold=8;
		int training_folds=allInterpretations.size()-1-8;
		List<CVIteration> cviters=cval.initializeAllFoldsLeaveOneOut(allInterpretations,training_folds,validation_fold);
		List<String> particular_predicates=hybrid.experimenter.AlgorithmParameters.predicates;
		HashMap<Atom,Double> averaged_statistics=new HashMap<Atom, Double>();
		for(Atom a:learn_structure_for){
			System.out.println(" Learning structure for: "+a);
			//check if the predicate of this atom is equal to the particular predicate we set (if the flag is set to particular predicate)
			if(particular_predicates!=null){
				if(!particular_predicates.contains(a.getPredicate().getPredicateName())){
					continue;
				}
			}
			double stat=0;
			int fold=1;
			for(CVIteration cv:cviters){
				this.query_training=new TuPrologQueryMachine(new Data(cv.getTraining()), AlgorithmParameters.getPenaltyType());
				this.query_validate=new TuPrologQueryMachine(new Data(cv.getValidation()), AlgorithmParameters.getPenaltyType());
				this.query_test=new TuPrologQueryMachine(new Data(cv.getTest()), AlgorithmParameters.getPenaltyType());
				LearnedModelTree lndstr=learnModelTree(learn_structure_for);
				Tree learned_tree=lndstr.getLearnedDependency().get(a);
				new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"/"+"fold"+fold+"/").mkdirs();
				FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"/"+"fold"+fold+"/"+a.getPredicate().getPredicateName()+".dot"));
				fw.append(learned_tree.makeDigraph(learned_tree.getRoot(),""));
				fw.close();
				FileOutputStream fileOut =new FileOutputStream(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"/"+"fold"+fold+"/"+a.getPredicate().getPredicateName()+".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(learned_tree);
				out.close();
				fileOut.close();
				System.out.printf(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName());
				double accError_tree=learned_tree.getAccumulatedError(learned_tree.getRoot());
				stat+=accError_tree;
				FileWriter fw1=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"/"+"fold"+fold+"/"+a.getPredicate().getPredicateName()+".er"));
				fw1.append(String.valueOf(accError_tree));
				fw1.close();
				fold++;

			}
			averaged_statistics.put(a, stat/cviters.size());
			FileWriter fw=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".stat"));
			fw.append(String.valueOf(stat/cviters.size()));
			fw.close();
		}

		return averaged_statistics;
	}

	/**
	 * General structure learnin method
	 * @param learn_structure_for - a list of literals we will learn the structure for
	 * @param ntw - network containing all the atoms
	 * @param query_machine_training - training query machine
	 * @param query_machine_validation - validation query machine
	 * @param query_machine_test - test query machine
	 * @param fgen - feature generator
	 * @param search - structure search method
	 * @return
	 * @throws IOException
	 * @throws ResultAlreadyExistsException
	 */
	public LearnedStructure learnStandardCPTs(Atom[] learn_structure_for) throws IOException, ResultAlreadyExistsException{
		LearnedStructure learnedStructure=new LearnedStructure();
		List<String> particular_predicates=hybrid.experimenter.AlgorithmParameters.predicates; //load particular predicates from algorithm parameters (if set)
		hybrid.structureLearning.NetworkWPLLStat.setOutputFile(AlgorithmParameters.getOutput_path()+"/wpll_stat"); //set output file for all the wplls

		double total_time=0;
		double total_wpll = 0;
		HashMap<Atom,Boolean> result_exists=new HashMap<Atom, Boolean>();	

		//iterate through atoms
		for(Atom a:learn_structure_for){
			System.out.println(" Learning structure for: "+a);
			//check if the results exist for this atom
			if(exist_results(a,result_exists,total_wpll)){
				continue;
			}

			//check if the predicate of this atom is equal to the particular predicate we set (if the flag is set to particular predicate)
			if(particular_predicates!=null){
				if(!particular_predicates.contains(a.getPredicate().getPredicateName())){
					continue;
				}
			}
			//output some information: nr training randvars
			hybrid.loggers.DetailedStructureLearningForAtom.setOutFile(a,AlgorithmParameters.output_path+"/structure_learning_Log_");
			hybrid.loggers.DetailedStructureLearningForAtom.println("Nr Training randvars for target predicate "+this.query_training.getData().getNrGroundingsInData(a));
			hybrid.loggers.DetailedStructureLearningForAtom.println("Nr Training randvars (all) "+this.query_training.getData().getNrRandvars());
			//generate general feature space for atom a based on feature generator fgen
			List<Feature> feature_space=generateFeatureSpace(a,this.feature_generator,ntw);
			//ouput feature size
			hybrid.loggers.DetailedStructureLearningForAtom.println("FEATURE SPACE SIZE: "+feature_space.size());
			//learn dependency for this atom
			double cacheing_time=0;
			LearnedDependency learned=null;
			try {
				System.out.println(" Learn dependency for "+a);
				learned = learnDependency(a,this.query_training,this.query_validate,query_test,ntw,feature_space,this.structure_search);
				learnedStructure.addLearnedDependency(a, learned);
			} catch (ResultAlreadyExistsException e) {
				continue;

			} catch (ResultNotObtainedError e) {
				e.printStackTrace(hybrid.loggers.DetailedStructureLearningForAtom.getStream());
				continue;
			}
			//do some outputing
			if(learned!=null){
				report(learned,learnedStructure,a,cacheing_time);
				//Perform evaluation of the learned structure
				if(AlgorithmParameters.isEvaluation_flag()){
					evaluateLearnedStructure(a,learned,this.query_test);
				}
			}

		}
		File total_wpll_file=new File(AlgorithmParameters.getOutput_path()+"/wpll_stat");
		FileWriter fW=new FileWriter(total_wpll_file);
		fW.append(String.valueOf(total_wpll));
		fW.close();
		return learnedStructure;
	}




	/**
	 * General structure learnin method
	 * @param learn_structure_for - a list of literals we will learn the structure for
	 * @param ntw - network containing all the atoms
	 * @param query_machine_training - training query machine
	 * @param query_machine_validation - validation query machine
	 * @param query_machine_test - test query machine
	 * @param fgen - feature generator
	 * @param search - structure search method
	 * @return
	 * @throws IOException
	 * @throws ResultAlreadyExistsException
	 */
	public LearnedModelTree learnModelTree(Atom[] learn_structure_for) throws IOException, ResultAlreadyExistsException{
		LearnedModelTree learnedStructure=new LearnedModelTree();
		List<String> particular_predicates=hybrid.experimenter.AlgorithmParameters.predicates; //load particular predicates from algorithm parameters (if set)
		hybrid.structureLearning.NetworkWPLLStat.setOutputFile(AlgorithmParameters.getOutput_path()+"/wpll_stat"); //set output file for all the wplls
		double total_time=0;
		double total_wpll = 0;
		HashMap<Atom,Boolean> result_exists=new HashMap<Atom, Boolean>();	
		//iterate through atoms
		for(Atom a:learn_structure_for){
			long initTimeLearning=System.nanoTime();
			System.out.println(" Learning structure for: "+a);
			//check if the results exist for this atom
			if(exist_results(a,result_exists,total_wpll)){
				continue;
			}
			//check if the predicate of this atom is equal to the particular predicate we set (if the flag is set to particular predicate)
			if(particular_predicates!=null){
				if(!particular_predicates.contains(a.getPredicate().getPredicateName())){
					continue;
				}
			}
			//output some information: nr training randvars
			hybrid.loggers.DetailedStructureLearningForAtom.setOutFile(a,AlgorithmParameters.output_path+"/structure_learning_Log_");
			hybrid.loggers.DetailedStructureLearningForAtom.println("Nr Training randvars for target predicate "+this.query_training.getData().getNrGroundingsInData(a));
			hybrid.loggers.DetailedStructureLearningForAtom.println("Nr Training randvars (all) "+this.query_training.getData().getNrRandvars());
			//generate general feature space for atom a based on feature generator fgen
			List<Feature> feature_space=generateFeatureSpace(a,this.feature_generator,ntw);
			File writer_features=new File(AlgorithmParameters.getOutput_path()+"/"+a.getPredicate().getPredicateName()+"_features.info");
			FileWriter fW=new FileWriter(writer_features);
			int counter=0;
			System.out.println("----- WRITING FEATURES AND SETTING INDICES -----");
			for(Feature f:feature_space){
				f.setIndexInFeatureSpace(counter);
				counter++;
				fW.append(f+"\n");
			}
			fW.close();
			//ouput feature size
			hybrid.loggers.DetailedStructureLearningForAtom.println("FEATURE SPACE SIZE: "+feature_space.size());
			//learn dependency for this atom
			double cacheing_time=0;
			Tree learned=null;
			try {
				System.out.println(" Learn dependency for "+a);
				learned = learnTree(a,this.query_training,this.query_validate,this.query_test,ntw,feature_space,this.structure_search);
				long overallTimeRunningTime=NANOSECONDS.toSeconds(System.nanoTime() - initTimeLearning);
				FileWriter overallTime=new FileWriter(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"_time.info");
				overallTime.append(overallTimeRunningTime+"");
				overallTime.close();
				try{
					reportLearnedTree(a,learned);
				}
				catch(NullPointerException e){
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsString = sw.toString();
					File total_wpll_file=new File(AlgorithmParameters.getOutput_path()+"/error_"+a.getPredicate().getPredicateName());
					FileWriter fW1=new FileWriter(total_wpll_file);
					fW1.append(exceptionAsString);
					fW1.close();
					continue;
				}
				learnedStructure.addLearnedDependency(a, learned);
			} catch (ResultAlreadyExistsException e) {
				continue;

			} catch (ResultNotObtainedError e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				File total_wpll_file=new File(AlgorithmParameters.getOutput_path()+"/error_"+a.getPredicate().getPredicateName());
				FileWriter fW1=new FileWriter(total_wpll_file);
				fW1.append(exceptionAsString);
				fW1.close();
				continue;
			}
			
		}
		File total_wpll_file=new File(AlgorithmParameters.getOutput_path()+"/wpll_stat");
		FileWriter fW=new FileWriter(total_wpll_file);
		fW.append(String.valueOf(total_wpll));
		fW.close();
		return learnedStructure;
	}

	
	
	public static double returnSumofTestLLs(Node<DecisionTreeData> node){
		//System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getTestError((Node<hybrid.structureLearning.DecisionTreeData>) node));
		        double tmp=0;
		        if(node.isLeaf()){
		        	 Double value=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getTestDataScore();
		        	 if(value.equals(Double.NEGATIVE_INFINITY)){
		        		 value=0.0;
		        	 }
		             tmp+=value;
		        }
				for(Node<DecisionTreeData> ch:node.getChildren()){
					double tmp1=returnSumofTestLLs(ch);
					tmp+=tmp1;
					
				}
				return tmp;
	}
	
	public static double returnSumofValidationScores(Node<DecisionTreeData> node){
        double tmp=0;
        if(node.isLeaf()){
        	//System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getScore());
             tmp+=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getScore();
        }
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=returnSumofValidationScores(ch);
			tmp+=tmp1;
			
		}
		return tmp;
}
	
	public static double returnSumofNormalizedTestLL(Node<DecisionTreeData> node){
        double tmp=0;
        if(node.isLeaf()){
        	//System.out.println(node);
        	System.out.println(((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestScore());
        	double val=((hybrid.structureLearning.DecisionTreeData) node.get_data()).getDependency().getNormalizedTestScore();
        	if(val==Double.NEGATIVE_INFINITY){
        		val=0;
        	}
             tmp+=val;
        }
		for(Node<DecisionTreeData> ch:node.getChildren()){
			double tmp1=returnSumofNormalizedTestLL(ch);
			tmp+=tmp1;
			
		}
		return tmp;
}
	

	private void reportLearnedTree(Atom a,Tree learned_tree) throws IOException {
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
		FileWriter fw1=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".rmse"));
		System.out.println("TEST ACC: "+acc_error);
		System.out.println("TEST NUMBER OF DATA POINTS: "+query_test.getData().getNrGroundingsInData(a));
		Double nrmse=Math.sqrt(acc_error/query_test.getData().getNrGroundingsInData(a));
		
		fw1.append(String.valueOf(nrmse)+"\n");
		fw1.append(String.valueOf(query_test.getData().getNrGroundingsInData(a)));
		fw1.close();
		
		double acc_validation_error=learned_tree.getAccumulatedValidationError(learned_tree.getRoot());
		FileWriter fw3=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".validation_rmse"));
		System.out.println("VALIDATION ACC: "+acc_validation_error);
		System.out.println("VALIDATION NUMBER OF DATA POINTS: "+query_validate.getData().getNrGroundingsInData(a));
		Double rmse_validarion=Math.sqrt(acc_validation_error/query_validate.getData().getNrGroundingsInData(a));
		fw3.append(String.valueOf(rmse_validarion)+"\n");
		fw3.close();
		
		
		FileWriter fw5=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".dclause"));
		fw5.append(learned_tree.printTree_DC(learned_tree.getRoot(), new DC_converter()));
		fw5.close();	
		
		
		
		double testDataLL=returnSumofTestLLs(learned_tree.getRoot());
		FileWriter fwll=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".testLL"));
		fwll.append(String.valueOf(testDataLL)+"\n");
		fwll.close();
		
		double normtestDataLL=returnSumofNormalizedTestLL(learned_tree.getRoot());
		FileWriter normfwll=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".normTestLL"));
		normfwll.append(String.valueOf(normtestDataLL)+"\n");
		normfwll.close();
		
		double valScore=returnSumofValidationScores(learned_tree.getRoot());
		FileWriter fwscore=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".score"));
		fwscore.append(String.valueOf(valScore)+"\n");
		fwscore.close();

		double accNormError=learned_tree.getAccumulatedNormalizedError(learned_tree.getRoot());
		int nr_leavesNonZero=learned_tree.gerNrLeavesWithNonZeroNRMSE(learned_tree.getRoot());
		Double avgnrmse=Math.sqrt(accNormError/Double.valueOf(nr_leavesNonZero));
		FileWriter fw4=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+".avgnrmse"));
		fw4.append(String.valueOf(avgnrmse));
		System.out.println("Number of leaves: "+learned_tree.getNrLeaves(learned_tree.getRoot()));
		fw4.close();

		
		FileWriter fw2=new FileWriter(new File(AlgorithmParameters.output_path+"/"+a.getPredicate().getPredicateName()+"_indices"+".info"));
		fw2.append(learned_tree.getIndicesOfFeatures(learned_tree.getRoot(), ""));
		fw2.close();



		String filename="";
		String filename1="";
		String overallReportingPath="";
		int counter=0;
		for(String s:AlgorithmParameters.getOutput_path().split("/")){
			counter++;
			if(counter!=(AlgorithmParameters.getOutput_path().split("/").length)){
			overallReportingPath+="/"+s;
			}
		}
		try
		{
			filename= overallReportingPath+"/"+a.getPredicate().getPredicateName()+"_RMSEsPerFold.res";
			filename1= overallReportingPath+"/"+a.getPredicate().getPredicateName()+"_ValidationRMSEsPerFold.res";
			//filename1= overallReportingPath+"/"+a.getPredicate().getPredicateName()+"_AVGRMSESPerFold.res";
			FileWriter fwnrmse = new FileWriter(filename,true); //the true will append the new data
			FileWriter fwavgrmse_validation = new FileWriter(filename1,true);
			fwnrmse.write(String.valueOf(nrmse)+"\n");//appends the string to the file
			fwavgrmse_validation.write(String.valueOf(rmse_validarion)+"\n");
			fwnrmse.close();
			fwavgrmse_validation.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}

		if(AlgorithmParameters.getCv()){
			try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
				List<Double> nrmsesPerFold=new ArrayList<Double>();
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
					nrmsesPerFold.add(Double.valueOf(line.trim()));
				}
				Double avg=0.0;
				if(nrmsesPerFold.size()==AlgorithmParameters.getnrFolds()){
					Double sum=0.0;
					for(Double d:nrmsesPerFold){
						sum+=d;
					}
					avg=sum/nrmsesPerFold.size();
				}
				String avgNRMSEsCV= AlgorithmParameters.getOutput_path()+"/"+a.getPredicate().getPredicateName()+"_RMSEsCV.res";
				FileWriter fwnrmse = new FileWriter(avgNRMSEsCV);
				fwnrmse.append(String.valueOf(avg));
				fwnrmse.close();
			}

			try(BufferedReader br = new BufferedReader(new FileReader(filename1))) {
				List<Double> nrmsesPerFold=new ArrayList<Double>();
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
					nrmsesPerFold.add(Double.valueOf(line.trim()));
				}
				Double avg=0.0;
				if(nrmsesPerFold.size()==AlgorithmParameters.getNrFolds()){
					Double sum=0.0;
					for(Double d:nrmsesPerFold){
						sum+=d;
					}
					avg=sum/nrmsesPerFold.size();
				}
				String avgNRMSEsCV= AlgorithmParameters.getOutput_path()+"/"+a.getPredicate().getPredicateName()+"_AVGRSMEsCV.res";
				FileWriter fwnrmse = new FileWriter(avgNRMSEsCV);
				fwnrmse.append(String.valueOf(avg));
				fwnrmse.close();
			}
		}


		
	}

	private void report(LearnedDependency learned, LearnedStructure learnedStructure, Atom a, double cacheing_time) {
		//output the learned dependency
		hybrid.loggers.DetailedStructureLearningForAtom.print("LEARNED: ");  
		//if no features learned output NONE
		if(learned.getDep().getFeatures().size()==0){
			hybrid.loggers.DetailedStructureLearningForAtom.print("NONE"+"\n");
		}
		for(Feature f:learned.getDep().getFeatures()){
			hybrid.loggers.DetailedStructureLearningForAtom.print(f.getIndexInFeatureSpace()+"\t");
		}
		hybrid.loggers.DetailedStructureLearningForAtom.println("\nParameters: "+learnedStructure.getLearnedDependency().get(a).getDep().getCpd().getParameters().toString());
		learned.getStatistics().setLearningTime(learned.getStatistics().getLearningTime()+cacheing_time);
		hybrid.loggers.DetailedStructureLearningForAtom.close();
	}

	private boolean exist_results(Atom a, HashMap<Atom, Boolean> result_exists, double total_wpll) throws FileNotFoundException {
		FileSearching fS=new FileSearching();	
		if(!AlgorithmParameters.isredoingExperiment() && fS.containsString("WPLL_test",AlgorithmParameters.output_path+"/"+(a.getPredicate().getPredicateName()+"_stat.res"))){
			result_exists.put(a, true);
			double wpll=fS.extractWpll(AlgorithmParameters.output_path+"/"+(a.getPredicate().getPredicateName()+"_stat.res"),"WPLL_test=",1);
			total_wpll+=wpll;
			System.out.println("***************************************************");
			System.out.println(" The result exists! for "+a+ ". Wpll is "+wpll);
			System.out.println("***************************************************");
			return true;
		}
		else{
			System.out.println(" No results for "+a);
			result_exists.put(a, false);
			return false;
		}
	}
	/**
	 * Evaluate the learned structure on test data		
	 * @param a
	 * @param learned
	 * @param tuPrologQueryMachine_test
	 * @throws IOException
	 */
	private void evaluateLearnedStructure(Atom a,LearnedDependency learned,QueryMachine tuPrologQueryMachine_test) throws IOException {
		//Evaluate atoms designated to be evaluated immediately
		hybrid.structureLearning.LearnedDepStatistics.setOutFile(AlgorithmParameters.output_path+"/", a.getPredicate().getPredicateName()+"_stat.res");				
		System.out.println(" EVALUATION FOR: "+a+ "EVALUATING : "+tuPrologQueryMachine_test.getData().getNrGroundingsInData(a));
		hybrid.structureLearning.LearnedDepStatistics.println(" DATA INFO: \n");
		hybrid.structureLearning.LearnedDepStatistics.println(" TEST DATA: \n"+tuPrologQueryMachine_test.getData().getInfo());
		hybrid.structureLearning.LearnedDepStatistics.println("--------");
		try{
			evaluate(tuPrologQueryMachine_test,learned);
			System.out.println("Evaluation completed ...");
			hybrid.structureLearning.LearnedDepStatistics.println(learned.getStatistics().toString());
			hybrid.structureLearning.LearnedDepStatistics.close();
			hybrid.structureLearning.NetworkWPLLStat.println(a.getPredicate().getPredicateName(),learned.getStatistics().getWPLL_score_test());						
			hybrid.structureLearning.NetworkWPLLStat.flush();
		}
		catch(Exception e){
			e.printStackTrace();
			hybrid.structureLearning.LearnedDepStatistics.println("PROBLEM: "+a);
			hybrid.structureLearning.LearnedDepStatistics.close();
			hybrid.structureLearning.NetworkWPLLStat.println(a.getPredicate().getPredicateName(),Double.NaN);
			hybrid.structureLearning.NetworkWPLLStat.flush();
		}
		hybrid.structureLearning.LearnedDepStatistics.close();
		hybrid.structureLearning.NetworkWPLLStat.close();		
	}

	/**
	 * Generate feature space
	 * @param a
	 * @param fgen
	 * @param ntw
	 * @return
	 * @throws IOException
	 */
	private List<Feature> generateFeatureSpace(Atom a, FeatureGeneratorAbstract fgen,NetworkInfo ntw) throws IOException {
		if(AlgorithmParameters.isDebuggingFlag()){
			hybrid.loggers.Debugger.setOutFile(a,AlgorithmParameters.output_path+"/debugging");
		}
		List<Feature> features=null;

		if(!AlgorithmParameters.isLearn_independent_model()){
			hybrid.loggers.DetailedStructureLearningForAtom.println("Generating features based on: "+ntw.getAtomsAndEqualityConstraints().toString());
			features=fgen.generateFeatures(a, ntw.getAtomsAndEqualityConstraints());
		}
		else{
			features=new ArrayList<Feature>();
		}
		//if space limit set, N features will be sampled from the complete feature space
		if(AlgorithmParameters.getFeatureSpace_Sampling_Limit()!=-1){
			SampleFeatureSpace sFS=new SampleFeatureSpace(AlgorithmParameters.getFeatureSpace_Sampling_Limit());
			return sFS.samplefeatures(features);
		}
		//if cutoff determined return a subset of all features
		else if(AlgorithmParameters.getFeatureSpaceCutoff()!=-1){
			if(AlgorithmParameters.getFeatureSpaceCutoff()>=features.size()){
				return features;
			}
			else{
				return features.subList(0, AlgorithmParameters.getFeatureSpaceCutoff());
			}
		}
		List<Feature> selected_features=null;
		System.out.println("Selected features indices: "+selected_feature_indices);
		if(this.selected_feature_indices!=null){
			selected_features=new ArrayList<Feature>();
			for(int i:this.selected_feature_indices){
				selected_features.add(features.get(i));
			}
		}
		if(selected_features!=null){
			return selected_features;
		}
		else{
			return features;
		}
	}

	/**
	 * Procedure for learning a dependency for one atom.
	 * @param a
	 * @param result_exists
	 * @param query_machine_training
	 * @param query_machine_validation
	 * @param ntw
	 * @param feature_space
	 * @param search
	 * @return
	 * @throws FileNotFoundException
	 * @throws ResultAlreadyExistsException
	 * @throws ResultNotObtainedError
	 */
	private LearnedDependency learnDependency(Atom a,QueryMachine query_machine_training, QueryMachine query_machine_validation,QueryMachine query_machine_test, NetworkInfo ntw, List<Feature> feature_space, StructureSearch search) throws FileNotFoundException, ResultAlreadyExistsException, ResultNotObtainedError {
		System.out.println("********************************* LEARNING STRUCTURE FOR: "+a+" ***********************************");
		//if the results for this atom still exist, skip
		double cache_calculation_time=0;
		//outputing data infos
		hybrid.loggers.DetailedStructureLearningForAtom.println(" DATA INFO: \n");
		hybrid.loggers.DetailedStructureLearningForAtom.println(" TRAINING DATA: \n"+query_machine_training.getData().getInfo());
		hybrid.loggers.DetailedStructureLearningForAtom.println(" VALIDATION DATA: \n"+query_machine_validation.getData().getInfo());
		hybrid.loggers.DetailedStructureLearningForAtom.println("FEATURES for "+a);
		long time_begin=System.nanoTime();
		hybrid.loggers.DetailedStructureLearningForAtom.println(" FEATURE SPACE SIZE: "+feature_space.size());

		//setting CACHE
		double cache_begin=System.nanoTime();
		System.out.println(" SETTING CACHE FOR TRAINING DATA: ......");
		query_machine_training.setCache(a, query_machine_training.calculateCache(a, feature_space,"training"));
		hybrid.loggers.DetailedStructureLearningForAtom.println(" Training data cache time: "+((double)(System.nanoTime()-cache_begin))/1000000000.0);
		System.out.println(" SETTING CACHE FOR VALIDATION DATA: ......");
		cache_begin=System.nanoTime();
		query_machine_validation.setCache(a, query_machine_validation.calculateCache(a, feature_space,"validation"));
		hybrid.loggers.DetailedStructureLearningForAtom.println(" Validation data cache time: "+((double)(System.nanoTime()-cache_begin))/1000000000.0);
		cache_calculation_time=((double)(System.nanoTime()-time_begin))/1000000000.0;
		hybrid.loggers.DetailedStructureLearningForAtom.println(" CACHE CALCULATION TIME: "+cache_calculation_time+ " seconds");

		//PERFORMIN THE SEARCH
		try{
			double search_time=0;
			LearnedDependency learnedDependency=(LearnedDependency) search.performSearchForAtom(a,feature_space,query_machine_training,query_machine_validation,query_machine_test);
			if(learnedDependency!=null){
				search_time=learnedDependency.getStatistics().getLearningTime();
				learnedDependency.getStatistics().setLearningTime(search_time+cache_calculation_time);
				System.out.println(" LEARNED STRUCTURE: "+learnedDependency);
			}
			return learnedDependency;
		}

		catch(Exception e){
			e.printStackTrace();
			throw new ResultNotObtainedError();
		}
	}

	/**
	 * Procedure for learning a dependency for one atom.
	 * @param a
	 * @param result_exists
	 * @param query_machine_training
	 * @param query_machine_validation
	 * @param ntw
	 * @param feature_space
	 * @param search
	 * @return
	 * @throws FileNotFoundException
	 * @throws ResultAlreadyExistsException
	 * @throws ResultNotObtainedError
	 */
	private Tree learnTree(Atom a,QueryMachine query_machine_training, QueryMachine query_machine_validation,QueryMachine query_machine_test, NetworkInfo ntw, List<Feature> feature_space, StructureSearch search) throws FileNotFoundException, ResultAlreadyExistsException, ResultNotObtainedError {
		System.out.println("********************************* LEARNING STRUCTURE FOR: "+a+" ***********************************");
		//if the results for this atom still exist, skip
		double cache_calculation_time=0;
		//outputing data infos
		hybrid.loggers.DetailedStructureLearningForAtom.println(" DATA INFO: \n");
		hybrid.loggers.DetailedStructureLearningForAtom.println(" TRAINING DATA: \n"+query_machine_training.getData().getInfo());
		hybrid.loggers.DetailedStructureLearningForAtom.println(" VALIDATION DATA: \n"+query_machine_validation.getData().getInfo());
		hybrid.loggers.DetailedStructureLearningForAtom.println("FEATURES for "+a);
		long time_begin=System.nanoTime();
		hybrid.loggers.DetailedStructureLearningForAtom.println(" FEATURE SPACE SIZE: "+feature_space.size());

		//setting CACHE
		double cache_begin=System.nanoTime();
		System.out.println(" SETTING CACHE FOR TRAINING DATA: ......"+" FEATURE SPACE SIZE: "+feature_space.size());
		query_machine_training.setCache(a, query_machine_training.calculateCache(a, feature_space,"training"));
		hybrid.loggers.DetailedStructureLearningForAtom.println(" Training data cache time: "+((double)(System.nanoTime()-cache_begin))/1000000000.0);
		System.out.println(" SETTING CACHE FOR VALIDATION DATA: ......");
		cache_begin=System.nanoTime();
		if(AlgorithmParameters.isTrainValidationEqual()){
			System.out.println("Setting cache validation");
			System.out.println(query_machine_training.getCache(a));
			query_machine_validation.setCache(a,query_machine_training.getCache(a));
		}
		else{
		    query_machine_validation.setCache(a, query_machine_validation.calculateCache(a, feature_space,"validation"));
		}
		hybrid.loggers.DetailedStructureLearningForAtom.println(" Validation data cache time: "+((double)(System.nanoTime()-cache_begin))/1000000000.0);
		cache_calculation_time=((double)(System.nanoTime()-time_begin))/1000000000.0;
		hybrid.loggers.DetailedStructureLearningForAtom.println(" CACHE CALCULATION TIME: "+cache_calculation_time+ " seconds");

		//PERFORMIN THE SEARCH
		try{
			double search_time=0;
			Tree learnedDependency=(Tree) search.performSearchForAtom(a,feature_space,query_machine_training,query_machine_validation,query_machine_test);
			
			return learnedDependency;
		}

		catch(Exception e){
			e.printStackTrace();
			throw new ResultNotObtainedError();
		}
	}




	/**
	 * Evaluate a learned dependency with a query machine test containing all neccessary interpretations.
	 * The statistics of the learned dependency is accordignly updated to contains scores on test data.
	 * @param query_machine_test
	 * @param learned
	 */
	public void evaluate(QueryMachine query_machine_test,LearnedDependency learned) {
		hybrid.loggers.DetailedStructureLearningForAtom.println("EVALUATING : "+learned+" ------------------------------");
		System.out.println("EVALUATING : "+learned+" ------------------------------");
		if(learned==null){
			return;
		}

		long time_begin=System.nanoTime();
		QueryData qD=query_machine_test.getQueryResults(learned.getDep());
		double wpll=learned.getDep().getCpd().getCpdEvaluator().calculatePLL(qD,learned.getDep().getCpd().getParameters(),query_machine_test.getPenalty()).getScore();
		double final_inference_time=((double)(System.nanoTime()-time_begin))/1000000000.0;

		learned.getStatistics().setNr_test_instances(query_machine_test.getData().getNrGroundingsInData(learned.getDep().getHead()));		
		learned.getStatistics().setWPLL_score_test(wpll/learned.getStatistics().getNr_test_instances());		
		learned.getStatistics().setInferenceTime(learned.getDep().getHead(),final_inference_time);
		hybrid.loggers.DetailedStructureLearningForAtom.println("TIME PER GROUND ATOM (INFERENCE): "+qD.getInference_time_per_ground_atom().get(learned.getDep().getHead()));
		hybrid.loggers.DetailedStructureLearningForAtom.close();
	}

}
