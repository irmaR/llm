package hybrid.structureLearning;

import hybrid.converters.ParConverterFromCSV;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.Parameters;
import hybrid.penalties.NoPenalty;
import hybrid.penalties.Penalty;
import hybrid.penalties.SpecialPenalties;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.tocsvmodule.DataToCSV;
import hybrid.utils.CVFoldPair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DTWithFeatureSelection extends DTDependencySelection {
	private SpecialPenalties p;
	String output_directory=null;
	private QueryData trainingData;
	private List<Integer> indicesOfFeaturesToTrack;
	private HashMap<Integer,Double> ftSpaceIndicesScore;
	private HashMap<Integer,Feature> featuresToTrack;
	private boolean headerWritten=false;
	private QueryData qdAllFeatures=null;
	private int branchCounter=0;
    private String branchFailureReason="";

	public DTWithFeatureSelection(String outputDirectory,SpecialPenalties p){
		this.output_directory=outputDirectory;
		this.p=p;
		this.indicesOfFeaturesToTrack=new ArrayList<Integer>();
		this.ftSpaceIndicesScore=new HashMap<Integer,Double>();
		this.featuresToTrack=new HashMap<Integer,Feature>();
	}


	@Override
	public LearnedDependency selectBestDependency(Atom a,
			LearnedDependency learned_dep, List<Feature> fts,
			QueryMachine query_data_training,
			QueryMachine query_data_validation, QueryMachine query_data_test,
			HashMap<Feature, Value> filter, Penalty pen, double parent_score)
					throws IOException {

		return selectBestDependencyContinuousHeadAtomWithScript(a, learned_dep,fts, query_data_training, query_data_validation, query_data_test,filter, pen, parent_score);

	}
	
	class InternalSelectionFeatures{
		List<Feature> preSelectFeatures;
		List<Integer> integersOfPreselectedFeatures;
		
		
		
		public InternalSelectionFeatures(List<Feature> preSelectFeatures,List<Integer> integersOfPreselectedFeatures) {
			super();
			this.preSelectFeatures = preSelectFeatures;
			this.integersOfPreselectedFeatures = integersOfPreselectedFeatures;
		}
		public List<Feature> getPreSelectFeatures() {
			return preSelectFeatures;
		}
		public void setPreSelectFeatures(List<Feature> preSelectFeatures) {
			this.preSelectFeatures = preSelectFeatures;
		}
		public List<Integer> getIntegersOfPreselectedFeatures() {
			return integersOfPreselectedFeatures;
		}
		public void setIntegersOfPreselectedFeatures(
				List<Integer> integersOfPreselectedFeatures) {
			this.integersOfPreselectedFeatures = integersOfPreselectedFeatures;
		}
		
		
	}

	InternalSelectionFeatures preSelectFeatures(Atom a,LearnedDependency learnedDep, List<Feature> fts,QueryMachine query_data_training, QueryMachine query_data_test,HashMap<Feature, Value> filter) throws IOException{
		branchCounter++;
		List<Feature> tmp=new ArrayList<Feature>();
		//filter the training data based on the filter
		System.out.println("Creating dependency...");
		Dependency dep=new Dependency(a,fts.toArray(new Feature[fts.size()]),true,true);
		System.out.println("Dependency created ...");
		if(this.qdAllFeatures==null){
			System.out.println("Querying...");
			this.qdAllFeatures=query_data_training.getQueryResults(dep);
		}

		//QueryData testData=query_data_test.getQueryResults(dep);
		QueryDataFilter qd_filter=new QueryDataFilter();
		QueryData filteredTrainingData=qd_filter.filterQueryData(dep,filter,this.qdAllFeatures);
		System.out.println("data is filtered...");
		//QueryData filteredTestData=qd_filter.filterQueryData(dep,filter,testData);

		//make csv file for Davide
		DataToCSV dataToCSVConverting=new DataToCSV();
		FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(dataToCSVConverting.dataToCSVFile(filteredTrainingData,dep,filter,true));
		bw.close();
		System.out.println("Data converted to CSV...");
		//call the script
		List<String> pars=new ArrayList<String>();
		pars.addAll(Arrays.asList(new String[]{AlgorithmParameters.getPython()}));
		pars.add(AlgorithmParameters.getExternalScript()+"/feature_selection.py");
		pars.add(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/selection_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(AlgorithmParameters.getNrPreselectedFeatures().toString());
		pars.add(AlgorithmParameters.getPercentile().toString());

		try{  
			System.out.println("Running script");
			System.out.println(pars);
			long initTime=System.nanoTime();
			ProcessBuilder pb = new ProcessBuilder(pars);
			Process p = pb.start();
			p.waitFor();
			System.out.println("Done...");
			System.out.println("Took : "+TimeUnit.SECONDS.convert((System.nanoTime()-initTime),TimeUnit.NANOSECONDS)+" seconds");
		} catch (Throwable t)
		{
			t.printStackTrace();
		}
		//read the selected index file
		System.out.println("Extracting scores ...");
		//Files.copy(Paths.get(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv"), Paths.get(output_directory+"/branch_"+branchCounter+"_data"+a.getPredicate().getPredicateName()+".csv"));
		//Files.copy(Paths.get(output_directory+"/selection_"+a.getPredicate().getPredicateName()+".csv"), Paths.get(output_directory+"/branch_"+branchCounter+"_selection_"+a.getPredicate().getPredicateName()+".csv"));
		List<Integer> indices=extractSelectedFeaturesIndices(output_directory+"/selection_"+a.getPredicate().getPredicateName()+".csv");
		for(Integer i:indices){
			if(!learnedDep.getDep().getFeatures().contains(fts.get(i))){
				tmp.add(fts.get(i));
			}
		}
		return new InternalSelectionFeatures(tmp,indices);
	}

	/*public static void main(String[] args){
		String path="/cw/dtailocal/irma/Shared_With_Davide/Scripts/test.txt";
		try {
			List<Integer> indices=DTWithFeatureSelection.extractSelectedFeaturesIndices(path);
			System.out.println(indices.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	private static List<Integer> extractSelectedFeaturesIndices(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		List<Integer> indices=new ArrayList<Integer>();
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			String[] s=line.split(",");
			for(String str:s){
				indices.add(Integer.valueOf(str));
			}
			line = br.readLine();
		}

		return indices;
	}

	/**
	 * Learn dependency for continuous predicate atom
	 * @param node 
	 * @param a
	 * @param learned_dep
	 * @param fts
	 * @param query_data_training
	 * @param query_data_validation
	 * @param query_data_test
	 * @param filter
	 * @param pen
	 * @param parent_score
	 * @return
	 * @throws IOException 
	 */
	private LearnedDependency selectBestDependencyContinuousHeadAtomWithScript(Atom a, LearnedDependency learned_dep, List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,HashMap<Feature, Value> filter, Penalty pen, double parent_score) throws IOException {
		//Handle continuous target predicate
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		Dependency newDep=learned_dep.getDep().copy();
		LearnedDependency bestScore=new LearnedDependency(newDep,learned_dep.getScore());
		LearnedDependency original=new LearnedDependency(newDep,learned_dep.getScore());
		System.out.println("Preselecting features");
		//preselect features
		InternalSelectionFeatures selFts=preSelectFeatures(a,learned_dep,fts,query_data_training,query_data_test,filter);
		List<Feature> preselectedFeatures=selFts.getPreSelectFeatures();
		List<Integer> indicesFeatures=selFts.getIntegersOfPreselectedFeatures();
		System.out.println("PRESELECTED FEATURES"+preselectedFeatures.size());
		
		for(Feature ft:preselectedFeatures){
			Dependency dep_temp=null;
			HashMap<Value,Parameters> parametersPerBranch=new HashMap<Value, Parameters>();
			HashMap<Value,Double> scorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> testScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedTestScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedTestErrorPerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> testErrorPerBranch=new HashMap<Value, Double>();
			try {
				System.out.println("EXTENDING DEP: "+newDep+" WITH : "+ft+"-> "+ft.getIndexInFeatureSpace()+ "MAP: "+filter);
				dep_temp= newDep.extend(ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//System.out.println("Feature added, now doing data filtering ...");
			//discrete feature
			double overallScore=0;
			double overallPenalty=0;
			double overallTestError=0;
			double overallValidationError=0;
			double testDataScore=0;
			double normalizedTestDataScore=0;
			double normalizedTestError=0;
			StructureScore score=null;
			if(ft.isDiscreteOutput()){
				//for discrete features we need to obtain the overall score by
				//scoring each brach and then finding a sum
				//System.out.println("DISCRETE FEATURE!!!");
				RangeDiscrete range=(RangeDiscrete)ft.getRange();
				Double[] scores=null;
				if(!AlgorithmParameters.getInternalCV()){
					scores=this.getScoreBranch(a,testErrorPerBranch,normalizedTestErrorPerBranch,scorePerBranch,normalizedScorePerBranch,testScorePerBranch,normalizedTestScorePerBranch,parametersPerBranch,"/regression.py", range, filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
				}
				else{
					System.out.println("Doing internal cv");
					scores=this.getScoreBranchCV(a,testErrorPerBranch,normalizedTestErrorPerBranch,scorePerBranch,normalizedScorePerBranch,testScorePerBranch,normalizedTestScorePerBranch,parametersPerBranch,"/regression.py", range, filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);

				}
				System.out.println("Nr scores: "+scores.length);
				overallScore=scores[0];
				overallTestError=scores[1];
				testDataScore=scores[2];
				normalizedTestDataScore=scores[4];
				normalizedTestError=scores[3];
				overallValidationError=scores[5];
				//In case we want normalized score, 
				if(AlgorithmParameters.getNormalizedScore()){
					overallScore=0;
					for(Value v:normalizedScorePerBranch.keySet()){
						overallScore+=normalizedScorePerBranch.get(v);
					}
					overallScore=overallScore/normalizedScorePerBranch.size();
				}
				System.out.println("OVERALL TEST SCORE DISCRETE BRANCH: "+testDataScore);
				System.out.println("OVERALL SCORE DISCRETE BRANCH: "+overallScore);
				System.out.println("Normalized TEST SCORE DISCRETE BRANCH: "+normalizedTestDataScore);
				System.out.println("Normalized TEST ERROR DISCRETE BRANCH: "+normalizedTestError);
				score=new StructureScore(overallScore, 0.0, overallScore);
			}
			//Continuous feature
			else{
				Double[] scores=null;
				if(!AlgorithmParameters.getInternalCV()){
					scores=this.getScoreContinuousFeature(a,"/regression_new.py",filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
				}
				else{
					scores=this.getScoreContinuousFeatureCV(a,"/regression_new.py",filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);

				}
				overallScore=scores[0];
				overallTestError=scores[1];
				testDataScore=scores[2];
				normalizedTestDataScore=scores[4];
				normalizedTestError=scores[3];
				overallValidationError=scores[6];
				if(AlgorithmParameters.getNormalizedScore()){
					overallScore=scores[5];
				}
				System.out.println("OVERALL TEST SCORE CONTINUOUS BRANCH: "+testDataScore);
				System.out.println("OVERALL SCORE CONTINUOUS BRANCH: "+overallScore);
				System.out.println("Normalized TEST SCORE CONTINUOUS BRANCH: "+normalizedTestDataScore);
				System.out.println("Normalized TEST Error CONTINUOUS BRANCH: "+normalizedTestError);
				score=new StructureScore(overallScore, 0.0, overallScore);
			}

			//Learned dependency
			//System.out.println("########## LEARNED DEP #################");
			if(this.indicesOfFeaturesToTrack.contains(ft.getIndexInFeatureSpace())){
				this.featuresToTrack.put(ft.getIndexInFeatureSpace(), ft);
				this.ftSpaceIndicesScore.put(ft.getIndexInFeatureSpace(),overallScore);
			}
			LearnedDependency extendedFeature=new LearnedDependency(dep_temp,score.getScore());
			extendedFeature.setScorePerBranch(scorePerBranch);
			extendedFeature.setNormalizedTestScorePerBranch(normalizedTestScorePerBranch);
			extendedFeature.setNormalizedScorePerBranch(normalizedScorePerBranch);
			extendedFeature.setNormalizedTestErrorPerBranch(normalizedTestErrorPerBranch);
			extendedFeature.setParametersPerBranch(parametersPerBranch);
			extendedFeature.setTestScores(testScorePerBranch);
			extendedFeature.setNormalizedTestError(normalizedTestError);
			extendedFeature.setNormalizedTestScore(normalizedTestDataScore);
			extendedFeature.setError_test_data(overallTestError);
			extendedFeature.setError_validation_data(overallValidationError);
			extendedFeature.setTestScore(testDataScore);
			extendedFeature.setTestErrorPerBranch(testErrorPerBranch);
			extendedFeature.setNewest_added_feature(ft);
			extendedFeature.setStructure_score(score);
			extendedFeature.setTraining_data(this.trainingData);
			System.out.println("LEARNED DEP: WITH SCORE "+score);
			if(!new Double(extendedFeature.getScore()).isNaN()){
				extension_scores.add(extendedFeature);
			}
			hybrid.loggers.DetailedStructureLearningForAtom.println("SCORE: "+dep_temp+" = "+score); 
			if(extension_scores.size()==0){
				return null;
			}	
			try{
				Collections.sort(extension_scores);
			}
			catch(IllegalArgumentException e){
				List<LearnedDependency> failureFts=new ArrayList<LearnedDependency>();
				for(LearnedDependency s:extension_scores){
					if(Double.isNaN(s.getScore()) || Double.isInfinite(s.getScore())){
						failureFts.add(s);
					}
				}
				//System.out.println("-------- The list of undefined scores: -------");
				for(LearnedDependency sc:failureFts){
					System.out.println(sc);
				}
			}
			bestScore=extension_scores.get(0);
			
		}
		extension_scores=null;
		
		if(AlgorithmParameters.getFeatureSelectionOnlyCriterion()){
			//if(!no_feature_possible_to_add && bestScore.getScore()>parent_score && bestScore.getDep().getFeatures().size()<=AlgorithmParameters.getModelSize()){
			if(bestScore.getScore()!=Double.NEGATIVE_INFINITY && !no_feature_possible_to_add){
				bestScore.setNewFeatureLearned(true);
				return bestScore;
			}
			else{
				branchFailureReason="not possible to add features";
				System.out.println("No new feature added:");
				bestScore.setNewFeatureLearned(false);
				return bestScore;
			}
		}
		else{
			if(!no_feature_possible_to_add && (bestScore.getScore()>parent_score && bestScore.getDep().getFeatures().size()<=AlgorithmParameters.getModelSize())){
				branchFailureReason="ok";
				bestScore.setNewFeatureLearned(true);
				//bestScore.setBranchInfo(branchFailureReason);
				return bestScore;
			}
			else{
				branchFailureReason="fail";
				if(no_feature_possible_to_add){
					 branchFailureReason="not possible to add more features";
				}
				if(bestScore.getScore()<parent_score){
				   branchFailureReason="no increase in LL";
				}
				if(bestScore.getDep().getFeatures().size()>=AlgorithmParameters.getModelSize()){
					branchFailureReason="depth limit reached";
				}
				System.out.println(bestScore.getDep());
				//bestScore.setInfoString(String.valueOf(branchCounter));
				bestScore.setNewFeatureLearned(false);
				PrintWriter writer = new PrintWriter(AlgorithmParameters.getOutput_path()+"/"+"/branch_"+branchCounter+a.getPredicate().getPredicateName()+".info", "UTF-8");
				writer.println("Branch stop. Info:");
				writer.println(bestScore);
				writer.println("----------------------------------");
				writer.println("Test values: "+filter);
				writer.println("Branch failure reason: "+branchFailureReason);
				
				writer.println("Indices of selected fts: \n"+indicesFeatures+" \n-------------------------");
				writer.println("Parent score: "+parent_score);
				writer.println("This score: "+bestScore.getScore());
				
				writer.println("Test error per branch: "+bestScore.getError_test_data());
				writer.println("Score (validation) per branch: "+bestScore.getScore());
				writer.println(" If discrete -----------------------------------------------");
				writer.println("Test error per branch: "+bestScore.getTestErrorPerBranch());
				writer.println("Score (validation) per branch: "+bestScore.getScorePerBranch());
				writer.println("Parameters per branch: "+bestScore.getParametersPerBranch());
				writer.close();
				bestScore.setNewFeatureLearned(false);
				return bestScore;
			}
		}
	}

	/**
	 * Get score of continuous feature being added
	 * @param a
	 * @param normalizedTestScorePerBranch 
	 * @param normalizedScorePerBranch 
	 * @param normalizedTestErrorPerBranch 
	 * @param filter
	 * @param ft
	 * @param query_data_training
	 * @param query_data_validation
	 * @param query_data_test
	 * @param dep_temp
	 * @return
	 * @throws IOException
	 */
	private Double[] getScoreContinuousFeature(Atom a, String scriptName,HashMap<Feature, Value> filter, Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,Dependency dep_temp) throws IOException {
		//System.out.println("****** HANDLING: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		long filteringTime=0;
		long trainingFilterInit=System.nanoTime();
		double normalizedTestError=0;
		double normalizedTestScore=0;
		QueryDataFilter qd_filter=new QueryDataFilter();
		QueryData trainingData=query_data_training.getQueryResults(dep_temp);
		QueryData validationData=query_data_validation.getQueryResults(dep_temp);
		QueryData testData=query_data_test.getQueryResults(dep_temp);
		//Filtering
		QueryData filteredTrainingData=qd_filter.filterQueryData(dep_temp,filter,trainingData);
		this.trainingData=filteredTrainingData;
		QueryData filteredValidationData=qd_filter.filterQueryData(dep_temp,filter,validationData);
		QueryData filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);
		int testDatapoints=filteredTestData.getNr_groundings_for_head();

		filteringTime+=System.nanoTime()-trainingFilterInit;
		//filteredTrainingData.setHadUndefinedValue(trainingData.isHadUndefinedValue());
		DataToCSV dataToCSVConverting=new DataToCSV();
		ParConverterFromCSV parconverter=new ParConverterFromCSV();
		parconverter.setPathToCSVFile(AlgorithmParameters.getScriptOutput()+"/model_"+a.getPredicate().getPredicateName()+".csv");
		double extractedScore=0;
		double testDataScore=0;
		StructureScore score=null;
		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long overallTimeRunningScripts=0;
		if(filteredTrainingData.getNr_groundings_for_head()<=4 || filteredTrainingData.isHadUndefinedValue()){
			return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0,0.0};
		}

		Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
		Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];
		int i=0;
		for(Feature f:dep_temp.getDiscreteFeatures()){
			discreteFts[i]=f;
			valueSdiscreteFts[i]=filter.get(f);
			i++;
		}
		AssignmentKey key=new AssignmentKey(discreteFts, valueSdiscreteFts);
		long initExtractingScores=System.nanoTime();
		if(!AlgorithmParameters.getScripts()){
			dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filteredTrainingData));
			extractedScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredValidationData, dep_temp.getCpd().getParameters(),query_data_validation.getPenalty()).getScore();

		}
		else{
			System.out.println("USING DAVIDE'S SCRIPTS!!!!");
			DavideScripts scripts=new DavideScripts(new String[]{AlgorithmParameters.getPython()},"/regression_new.py",AlgorithmParameters.getScriptOutput(),a);
			extractedScore=scripts.call(filteredTrainingData,filteredTestData,dep_temp,filter,key);
			dep_temp.getCpd().getParameters().getCoefficients().convert(parconverter,key,dep_temp);
		}
		//System.out.println("Time needed to calculate validation score: "+TimeUnit.MILLISECONDS.convert((System.nanoTime()-initExtractingScores),TimeUnit.NANOSECONDS));
		testDataScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredTestData, dep_temp.getCpd().getParameters(),query_data_test.getPenalty()).getScore();
		//System.out.println("Nr data points validation: "+filteredValidationData.getNr_groundings_for_head());
		//System.out.println("Branch score: "+extractedScore);
		double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,filteredValidationData.getNr_groundings_for_head());
		double penaltyScore=extractedScore-this.p.scalePenalty(ft,penalty);
		double extractedtestError=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredTestData, dep_temp.getCpd().getParameters(), false);
		//System.out.println("TEST ERROR!: "+extractedtestError);
		score=new StructureScore(extractedScore, penalty, penaltyScore);
		double pll=score.getScore();
		//System.out.println("----------------- EXTRACTED TEST ERROR --------------"+extractedScore+" NR TEST DATA POINTS: "+testDatapoints);
		if(testDatapoints!=0){
			normalizedTestError=extractedtestError/testDatapoints;
			normalizedTestScore=testDataScore/testDatapoints;
		}
		double normalizedScore=score.getScore()/filteredValidationData.getNr_groundings_for_head();
		return new Double[]{score.getScore(),Double.valueOf(extractedtestError),testDataScore,normalizedTestError,normalizedTestScore,normalizedScore};
	}


	private Double[] getScoreContinuousFeatureCV(Atom a, String scriptName,HashMap<Feature, Value> filter, Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,Dependency dep_temp) throws IOException {
		System.out.println("****** CV HANDLING: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		long filteringTime=0;
		long trainingFilterInit=System.nanoTime();
		double normalizedTestError=0;
		double normalizedTestScore=0;
		QueryDataFilter qd_filter=new QueryDataFilter();
		QueryData trainingData=query_data_training.getQueryResults(dep_temp);
		QueryData validationData=query_data_validation.getQueryResults(dep_temp);
		QueryData testData=query_data_test.getQueryResults(dep_temp);
		//Filtering
		QueryData filteredTrainingData=qd_filter.filterQueryData(dep_temp,filter,trainingData);
		this.trainingData=filteredTrainingData;
		QueryData filteredValidationData=qd_filter.filterQueryData(dep_temp,filter,validationData);
		QueryData filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);
		int testDatapoints=filteredTestData.getNr_groundings_for_head();

		filteringTime+=System.nanoTime()-trainingFilterInit;
		//filteredTrainingData.setHadUndefinedValue(trainingData.isHadUndefinedValue());
		double testDataScore=0;
		StructureScore score=null;


		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long overallTimeRunningScripts=0;
		//System.out.println("Filtered training data: "+filteredTrainingData.getNr_groundings_for_head());
		//System.out.println("Has undefined values: "+filteredTrainingData.isHadUndefinedValue());
		if(filteredTrainingData.getNr_groundings_for_head()<=4 || filteredTrainingData.isHadUndefinedValue()){
			return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0,0.0};
		}

		Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
		Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];
		int i=0;
		for(Feature f:dep_temp.getDiscreteFeatures()){
			discreteFts[i]=f;
			valueSdiscreteFts[i]=filter.get(f);
			i++;
		}
		System.out.println("NR DATA POINTS: "+filteredTrainingData.getNr_groundings_for_head());

		int nrFolds=5;
		if(AlgorithmParameters.getNrInternalCVFolds()!=null){
			nrFolds=AlgorithmParameters.getNrInternalCVFolds();
			if(nrFolds==-1){
				nrFolds=filteredTrainingData.getNr_groundings_for_head();
			}
		}
		List<CVFoldPair> pairs=filteredTrainingData.splitIntoFolds(nrFolds);
		//System.out.println("Doing CV with "+nrFolds+" folds");
		//System.out.println("Nr pairs created: "+pairs.size());
		double validationScore=0;
		double normalizedValidationScore=0;
		double overallValidationError=0;
		for(CVFoldPair c:pairs){
			QueryData trainingCV=c.getTraining();
			QueryData validationCV=c.getValidation();
			Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(trainingCV);
			dep_temp.getCpd().setParameters(p);
			StructureScore foldScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(validationCV, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty());
			double validationScoreCV=foldScore.getScore()-this.p.scalePenalty(ft,foldScore.getPenalty());
			validationScore+=validationScoreCV;
			normalizedValidationScore+=(validationScoreCV)/validationCV.getFlatData().size();
		}

		validationScore=validationScore/nrFolds;
		normalizedValidationScore=normalizedValidationScore/nrFolds;
		//System.out.println("average score: "+validationScore);

		AssignmentKey key=new AssignmentKey(discreteFts, valueSdiscreteFts);
		//System.out.println("Number of training data for parameters estimation: "+filteredTrainingData.getNr_groundings_for_head());
		dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filteredTrainingData));

		testDataScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredTestData, dep_temp.getCpd().getParameters(),query_data_test.getPenalty()).getScore();
		//System.out.println("Branch score: "+validationScore);
		//double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,filteredValidationData.getNr_groundings_for_head());
		//double penaltyScore=validationScore-this.p.scalePenalty(ft,penalty);
		double extractedtestError=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredTestData, dep_temp.getCpd().getParameters(), false);
		double validationError=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredValidationData, dep_temp.getCpd().getParameters(), false);
		//System.out.println("TEST ERROR!: "+extractedtestError);
		System.out.println("----------------- EXTRACTED TEST ERROR --------------"+validationScore+" NR TEST DATA POINTS: "+testDatapoints);
		if(testDatapoints!=0){
			normalizedTestError=extractedtestError/testDatapoints;
			normalizedTestScore=testDataScore/testDatapoints;
		}
		//double normalizedScore=score.getScore()/filteredValidationData.getNr_groundings_for_head();
		return new Double[]{validationScore,Double.valueOf(extractedtestError),testDataScore,normalizedTestError,normalizedTestScore,normalizedValidationScore,validationError};
	}




	public Double[] getScoreBranch(Atom a,HashMap<Value, Double> testErrorPerBranch, HashMap<Value, Double> normalizedTestErrorPerBranch, HashMap<Value, Double> scorePerBranch, HashMap<Value, Double> normalizedScorePerBranch,HashMap<Value,Double> testScorePerBranch,HashMap<Value,Double> normalizedTestScorePerBranch, HashMap<Value,Parameters> parametersPerBranch, String scriptName,RangeDiscrete range,HashMap<Feature,Value> filter,Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,Dependency dep_temp) throws IOException{
		System.out.println("****** HANDLING DISCRETE FEATURE: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		QueryData trainingData=null;
		QueryData validationData=null;
		QueryData testData=null;

		Double overallScore=0.0;
		Double testDataScore=0.0;
		Double overallTestError=0.0;

		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long filteringTime=0;
		long overallTimeRunningScripts=0;
		long queryingTime=0;
		long initQueryingTime=System.nanoTime();

		trainingData=query_data_training.getQueryResults(dep_temp);
		this.trainingData=trainingData;
		validationData=query_data_validation.getQueryResults(dep_temp);
		testData=query_data_test.getQueryResults(dep_temp);

		queryingTime+=System.nanoTime()-initQueryingTime;
		int testDataPoints=0;
		//System.out.println("RANGE GET VALUES: "+range.getValues());
		List<Value> branchingValues=new ArrayList<Value>();
		branchingValues.addAll(range.getValues());
		if(AlgorithmParameters.getUseUndefinedValue()){
			if(!range.getValues().get(0).isBoolean()){
				branchingValues.add(new UndefinedValue()); //if using undefined value as one of the range values
			}
		}
		//System.out.println("Branching values: "+branchingValues);

		for(Value v:branchingValues){
			System.out.println("!!  > > > FILTERING BASED ON VALUE: < < < !! "+v);
			QueryDataFilter qd_filter=new QueryDataFilter();
			HashMap<Feature,Value> new_filter=new HashMap<Feature,Value>();
			new_filter.putAll(filter);
			new_filter.put(ft, v);
			Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
			Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];
			int i=0;
			for(Feature f:dep_temp.getDiscreteFeatures()){
				discreteFts[i]=f;
				valueSdiscreteFts[i]=new_filter.get(f);
				i++;
			}
			QueryData filteredTrainingData=null;
			QueryData filteredValidationData=null;
			QueryData filteredTestData=null;

			double normalizedTestError=0;
			double normalizedTestScore=0;

			long initFiltering=System.nanoTime();
			filteredTrainingData=qd_filter.filterQueryData(dep_temp,new_filter,trainingData);

			filteredValidationData=qd_filter.filterQueryData(dep_temp,new_filter,validationData);
			filteredTestData=qd_filter.filterQueryData(dep_temp,new_filter,testData);
			filteringTime+=System.nanoTime()-initFiltering;
			System.out.println("Nr values in this branch: "+filteredTrainingData.getNr_groundings_for_head());
			System.out.println("Has undefined values? "+filteredTrainingData.isHadUndefinedValue());
			//System.out.println("Data: "+filteredTrainingData);
			if(!AlgorithmParameters.getUseUndefinedValue() && filteredTrainingData.isHadUndefinedValue()){
				overallScore=Double.NEGATIVE_INFINITY;
				return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0};
			}
			if(filteredTrainingData.getNr_groundings_for_head()<=3){
				overallScore=Double.NEGATIVE_INFINITY;
				return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0};
				//continue;
			}
			//In case we only have discrete features (CG), Davide's script cannot handle it


			initQueryingTime=System.nanoTime();
			queryingTime+=System.nanoTime()-initQueryingTime;
			initFiltering=System.nanoTime();
			filteringTime+=System.nanoTime()-initFiltering;
			double validationScore=0;
			double penaltyValidation=0;


			if(!AlgorithmParameters.getScripts() || dep_temp.getContinuousFeatures().size()==0){
				Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(filteredTrainingData);
				dep_temp.getCpd().setParameters(p);
				validationScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredValidationData, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty()).getScore();
			}
			else{
				System.out.println("CALLING DAVIDE's SCRIPTS!!!!!");
				ParConverterFromCSV parconverter=new ParConverterFromCSV();
				parconverter.setPathToCSVFile(AlgorithmParameters.getScriptOutput()+"/model_"+a.getPredicate().getPredicateName()+".csv");
				AssignmentKey key=new AssignmentKey(discreteFts, valueSdiscreteFts);
				DavideScripts scripts=new DavideScripts(new String[]{AlgorithmParameters.getPython()},"/regression_new.py",AlgorithmParameters.getScriptOutput(),a);
				validationScore=scripts.call(filteredTrainingData,filteredTestData,dep_temp,new_filter,key);

			}
			parametersPerBranch.put(v, dep_temp.getCpd().getParameters());
			double penalty=this.p.scalePenalty(ft,query_data_validation.getPenalty().calculatePenalty(dep_temp,filteredValidationData.getNr_groundings_for_head()));
			overallScore+=validationScore;
			int testDataPointsInThisBranch=filteredTestData.getNr_groundings_for_head();
			StructureScore testScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredTestData, dep_temp.getCpd().getParameters(), new NoPenalty());
			double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredTestData, dep_temp.getCpd().getParameters(),false);
			overallTestError+=error_test;
			if(testDataPointsInThisBranch!=0){
				normalizedTestError=error_test/testDataPointsInThisBranch;
				normalizedTestScore=testScore.getScore()/testDataPointsInThisBranch;
			}
			testDataPoints+=testDataPoints;
			scorePerBranch.put(v, new Double(validationScore));
			testScorePerBranch.put(v, testScore.getScore());
			testDataScore+=testScore.getScore();
			//System.out.println("SCORE: "+validationScore);
			//System.out.println("Nr data points validation :"+filteredValidationData.getNr_groundings_for_head());
			//System.out.println("Normalized score: "+validationScore/filteredValidationData.getNr_groundings_for_head());
			normalizedScorePerBranch.put(v,validationScore/filteredValidationData.getNr_groundings_for_head());
			normalizedTestErrorPerBranch.put(v,normalizedTestError);
			normalizedTestScorePerBranch.put(v,normalizedTestScore);
			testErrorPerBranch.put(v, error_test);
		}
		if(dep_temp.getDiscreteFeatures().size()!=0){
			Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(trainingData);
			dep_temp.getCpd().setParameters(p);
		}
		System.out.println("Overall score: "+overallScore);
		System.out.println("Score per branch: "+scorePerBranch);
		System.out.println("Test score per branch: "+testScorePerBranch);
		System.out.println("Unnorm Test error per branch: "+testErrorPerBranch);
		System.out.println("Nr test data points: "+testDataPoints);
		System.out.println("Normalized Score per branch: "+normalizedScorePerBranch);
		System.out.println("Normalized Test score per branch: "+normalizedTestScorePerBranch);
		System.out.println("Normalized test error per branch:" +normalizedTestErrorPerBranch);
		double normalizedOverAllTestError=0;
		double normalizedOverAllTestScore=0;
		if(testDataPoints!=0){
			normalizedOverAllTestError=overallTestError/testDataPoints;
			normalizedOverAllTestScore=testDataScore/testDataPoints;
		}
		return new Double[]{overallScore,overallTestError,testDataScore,normalizedOverAllTestError,normalizedOverAllTestScore};
	}


	public Double[] getScoreBranchCV(Atom a,HashMap<Value, Double> testErrorPerBranch, HashMap<Value, Double> normalizedTestErrorPerBranch, HashMap<Value, Double> scorePerBranch, HashMap<Value, Double> normalizedScorePerBranch,HashMap<Value,Double> testScorePerBranch,HashMap<Value,Double> normalizedTestScorePerBranch, HashMap<Value,Parameters> parametersPerBranch, String scriptName,RangeDiscrete range,HashMap<Feature,Value> filter,Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,Dependency dep_temp) throws IOException{
		System.out.println("****** HANDLING DISCRETE FEATURE CV: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		QueryData trainingData=null;
		QueryData validationData=null;
		QueryData testData=null;
		Double overallScore=0.0;
		Double testDataScore=0.0;
		Double overallTestError=0.0;
		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long filteringTime=0;
		long overallTimeRunningScripts=0;
		long queryingTime=0;
		long initQueryingTime=System.nanoTime();
		trainingData=query_data_training.getQueryResults(dep_temp);
		this.trainingData=trainingData;
		validationData=query_data_validation.getQueryResults(dep_temp);
		testData=query_data_test.getQueryResults(dep_temp);
		queryingTime+=System.nanoTime()-initQueryingTime;
		int testDataPoints=0;
		//System.out.println("RANGE GET VALUES: "+range.getValues());
		List<Value> branchingValues=new ArrayList<Value>();
		branchingValues.addAll(range.getValues());
		if(AlgorithmParameters.getUseUndefinedValue()){
			if(!range.getValues().get(0).isBoolean()){
				branchingValues.add(new UndefinedValue()); //if using undefined value as one of the range values
			}
		}
		double normalizedOverAllTestError=0;
		double overallValidationError=0;
		for(Value v:branchingValues){
			System.out.println("!!  > > > FILTERING BASED ON VALUE: < < < !! "+v);
			QueryDataFilter qd_filter=new QueryDataFilter();
			HashMap<Feature,Value> new_filter=new HashMap<Feature,Value>();
			new_filter.putAll(filter);
			new_filter.put(ft, v);
			Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
			Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];
			int i=0;
			for(Feature f:dep_temp.getDiscreteFeatures()){
				discreteFts[i]=f;
				valueSdiscreteFts[i]=new_filter.get(f);
				i++;
			}
			QueryData filteredTrainingData=null;
			QueryData filteredValidationData=null;
			QueryData filteredTestData=null;

			double normalizedTestError=0;
			double normalizedTestScore=0;

			long initFiltering=System.nanoTime();

			filteredTrainingData=qd_filter.filterQueryData(dep_temp,new_filter,trainingData);
			filteredValidationData=qd_filter.filterQueryData(dep_temp,new_filter,validationData);
			filteredTestData=qd_filter.filterQueryData(dep_temp,new_filter,testData);

			System.out.println("Nr values in this branch: "+filteredTrainingData.getNr_groundings_for_head());
			System.out.println("Has undefined values? "+filteredTrainingData.isHadUndefinedValue());

			if(!AlgorithmParameters.getUseUndefinedValue() && filteredTrainingData.isHadUndefinedValue()){
				overallScore=Double.NEGATIVE_INFINITY;
				return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0};
			}
			if(filteredTrainingData.getNr_groundings_for_head()<=3){
				overallScore=Double.NEGATIVE_INFINITY;
				return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0,0.0};
			}
			//default=5
			int nrFolds=5;
			if(AlgorithmParameters.getNrInternalCVFolds()!=null){
				nrFolds=AlgorithmParameters.getNrInternalCVFolds();
				if(nrFolds==-1){
					nrFolds=filteredTrainingData.getNr_groundings_for_head();
				}
			}
			//System.out.println("Nr folds: "+nrFolds);
			//System.out.println("Nr training interpretations: "+filteredTrainingData.getQuery_results().keySet().size());
			List<CVFoldPair> pairs=filteredTrainingData.splitIntoFolds(nrFolds);
			double validationScore=0;
			double validationScoreNormalized=0;
			for(CVFoldPair c:pairs){
				QueryData trainingCV=c.getTraining();
				QueryData validationCV=c.getValidation();
				Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(trainingCV);
				dep_temp.getCpd().setParameters(p);
				StructureScore foldScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(validationCV, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty());
				double validationScoreCV=foldScore.getScore() -this.p.scalePenalty(ft,foldScore.getPenalty());
				validationScore+=validationScoreCV;
				validationScoreNormalized+=(validationScoreCV)/validationCV.getFlatData().size();

			}
			validationScore=validationScore/nrFolds;
			//System.out.println("Validation score: "+validationScore);
			validationScoreNormalized=validationScoreNormalized/nrFolds;
			//estimate pars on entire data
			Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(filteredTrainingData);
			dep_temp.getCpd().setParameters(p);
			parametersPerBranch.put(v, dep_temp.getCpd().getParameters());
			overallScore+=validationScore;
			int testDataPointsInThisBranch=filteredTestData.getNr_groundings_for_head();
			StructureScore testScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(filteredTestData, dep_temp.getCpd().getParameters(), new NoPenalty());
			double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredTestData, dep_temp.getCpd().getParameters(),false);
			double validation_error=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredValidationData, dep_temp.getCpd().getParameters(),false);
			double error_training=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filteredTrainingData, dep_temp.getCpd().getParameters(),false);

			overallTestError+=error_test;
			overallValidationError+=validation_error;

			if(testDataPointsInThisBranch!=0){
				normalizedTestError=error_test/testDataPointsInThisBranch;
				normalizedTestScore=testScore.getScore()/testDataPointsInThisBranch;
				normalizedOverAllTestError+=normalizedTestError;
			}
			testDataPoints+=testDataPoints;
			scorePerBranch.put(v, new Double(validationScore));
			testScorePerBranch.put(v, testScore.getScore());
			testDataScore+=testScore.getScore();
			//System.out.println("SCORE: "+validationScore);
			//System.out.println("Normalized score: "+validationScoreNormalized);
			normalizedScorePerBranch.put(v,validationScoreNormalized);
			normalizedTestErrorPerBranch.put(v,normalizedTestError);
			normalizedTestScorePerBranch.put(v,normalizedTestScore);
			testErrorPerBranch.put(v, error_test);
		}
		if(dep_temp.getDiscreteFeatures().size()!=0){
			Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(trainingData);
			dep_temp.getCpd().setParameters(p);
		}
		System.out.println("Overall score: "+overallScore);
		System.out.println("Score per branch: "+scorePerBranch);
		System.out.println("Test score per branch: "+testScorePerBranch);
		System.out.println("Unnorm Test error per branch: "+testErrorPerBranch);
		System.out.println("Nr test data points: "+testDataPoints);
		System.out.println("Normalized Score per branch: "+normalizedScorePerBranch);
		System.out.println("Normalized Test score per branch: "+normalizedTestScorePerBranch);
		System.out.println("Normalized test error per branch:" +normalizedTestErrorPerBranch);

		double normalizedOverAllTrainingError=0;
		double normalizedOverAllTestScore=0;
		if(testDataPoints!=0){
			normalizedOverAllTestScore=testDataScore/testDataPoints;
		}
		return new Double[]{overallScore,overallTestError,testDataScore,normalizedOverAllTestError,normalizedOverAllTestScore,overallValidationError};
	}

	private LearnedDependency selectBestDependencyDiscreteHeadAtom(Atom a,LearnedDependency learned_dep, List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,HashMap<Feature, Value> filter, Penalty pen, double parent_score) {
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		for(Feature ft:fts){
			System.out.println("ADDING FT: "+ft+" "+learned_dep.getDep());
			Dependency dep_temp=null;
			Dependency filtering=null;
			try {
				//System.out.println("EXTENDING DEP: "+learned_dep.getDep()+" WITH : "+ft);
				dep_temp= learned_dep.getDep().extend(ft);
				filtering=dep_temp;
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//System.out.println("---------- ESTIMATING PARAMETERS: --------------");
			QueryDataFilter qd_filter=new QueryDataFilter();
			QueryData est_training_data=query_data_training.getQueryResults(filtering);
			QueryData filtered_est_training=qd_filter.filterQueryData(filtering,filter,est_training_data);

			filtering.getCpd().setParameters(filtering.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));
			double error_training=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filtered_est_training, dep_temp.getCpd().getParameters(),false);

			try{
				double pll=Double.NaN;
				if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
					System.out.println("THIS BRANCH HAS NDVAL!");
					continue;
				}
				QueryData est_validation_data=query_data_validation.getQueryResults(filtering);
				QueryData score_filtered=qd_filter.filterQueryData(filtering,filter,est_validation_data);
				StructureScore score=filtering.getCpd().getCpdEvaluator().calculatePLL(score_filtered,filtering.getCpd().getParameters(),pen);
				pll=score.getScore();
				dep_temp.extend(ft);
				double error_validation=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(score_filtered, dep_temp.getCpd().getParameters(),false);
				LearnedDependency extendedFeature=new LearnedDependency(dep_temp,pll);
				extendedFeature.setTraining_data(filtered_est_training);
				extendedFeature.setValidation_data(score_filtered);
				extendedFeature.setError_training_data(error_training);
				extendedFeature.setError_validation_data(error_validation);
				extendedFeature.setNewest_added_feature(ft);
				extendedFeature.setStructure_score(score);
				if(!new Double(extendedFeature.getScore()).isNaN()){
					extension_scores.add(extendedFeature);
				}
				hybrid.loggers.DetailedStructureLearningForAtom.println("SCORE: "+dep_temp+" = "+pll); 
			}
			catch(Exception e){
				System.out.println("Problem with feature: "+ft+" for dependency: "+dep_temp);
				e.printStackTrace();
				System.exit(1);
			}
			//in case any better extensions are possible, return the current best score
			if(extension_scores.size()==0){
				return null;
			}	
			try{
				Collections.sort(extension_scores);
			}
			catch(IllegalArgumentException e){
				List<LearnedDependency> failureFts=new ArrayList<LearnedDependency>();
				for(LearnedDependency s:extension_scores){
					if(Double.isNaN(s.getScore()) || Double.isInfinite(s.getScore())){
						failureFts.add(s);
					}
				}
				System.out.println("-------- The list of undefined scores: -------");
				for(LearnedDependency sc:failureFts){
					System.out.println(sc);
				}
			}
			bestScore=extension_scores.get(0);
		}
		System.out.println("Best scorE: "+bestScore);
		System.out.println("parent score: "+parent_score);
		QueryDataFilter qd_filter=new QueryDataFilter();
		QueryData est_queries=query_data_test.getQueryResults(bestScore.getDep());
		QueryData test_filtered=qd_filter.filterQueryData(bestScore.getDep(),filter,est_queries);
		double error_test=bestScore.getDep().getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, bestScore.getDep().getCpd().getParameters(),false);
		bestScore.setError_test_data(error_test);
		bestScore.setNr_data_points_test_data(test_filtered.getNr_groundings_for_head());
		if(!no_feature_possible_to_add && bestScore.getScore()>parent_score && bestScore.getDep().getFeatures().size()<10){
			bestScore.setNewFeatureLearned(true);
			return bestScore;
		}
		else{
			System.out.println("No new feature added:");
			bestScore.setNewFeatureLearned(false);
			return bestScore;
		}

	}


	@Override
	public void reset() {
		this.trainingData=null;
		this.ftSpaceIndicesScore=null;
		this.qdAllFeatures=null;
		this.branchCounter=0;

	}

}
