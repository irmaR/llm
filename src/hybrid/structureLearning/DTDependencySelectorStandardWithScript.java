package hybrid.structureLearning;

import hybrid.converters.ParConverterFromCSV;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.LogRegressorsOnlyContinuousFeatures;
import hybrid.parameters.Parameters;
import hybrid.penalties.NoPenalty;
import hybrid.penalties.Penalty;
import hybrid.penalties.SpecialPenalties;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.tocsvmodule.DataToCSV;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.rits.cloning.Cloner;

public class DTDependencySelectorStandardWithScript extends DTDependencySelection {

	String output_directory=null;
	String predicateName="";
	private String[] paramteres;
	private SpecialPenalties p;
	FileWriter timeOverallFtRun ;
	FileWriter writingPythonScriptsRun ;
	FileWriter runningPythonScript ;
	FileWriter filteringRunTime ;
	boolean runtimeReport=false;


	public DTDependencySelectorStandardWithScript(String[] parameters,String outputDirectory,SpecialPenalties p){
		this.paramteres=parameters;
		this.output_directory=outputDirectory;
		this.p=p;
	}

	@Override
	public LearnedDependency selectBestDependency(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score) throws IOException{	
		//Handle discrete target predicate

		if(a.getPredicate().isDiscrete()){
			return selectBestDependencyDiscreteHeadAtomWithScript( a, learned_dep,fts, query_data_training, query_data_validation, query_data_test,filter, pen, parent_score);
		}
		else{
			return selectBestDependencyContinuousHeadAtomWithScript( a, learned_dep,fts, query_data_training, query_data_validation, query_data_test,filter, pen, parent_score);
		}
	}



	public FileWriter getFilteringRunTime() {
		return filteringRunTime;
	}

	public void setFilteringRunTime(FileWriter filteringRunTime) {
		this.filteringRunTime = filteringRunTime;
	}

	public String getOutput_directory() {
		return output_directory;
	}

	public void setOutput_directory(String output_directory) {
		this.output_directory = output_directory;
	}

	public String getPredicateName() {
		return predicateName;
	}

	public void setPredicateName(String predicateName) {
		this.predicateName = predicateName;
	}

	public String[] getParamteres() {
		return paramteres;
	}

	public void setParamteres(String[] paramteres) {
		this.paramteres = paramteres;
	}

	public SpecialPenalties getP() {
		return p;
	}

	public void setP(SpecialPenalties p) {
		this.p = p;
	}

	public FileWriter getTimeOverallFtRun() {
		return timeOverallFtRun;
	}

	public void setTimeOverallFtRun(FileWriter timeOverallFtRun) {
		this.timeOverallFtRun = timeOverallFtRun;
	}

	public FileWriter getWritingPythonScriptsRun() {
		return writingPythonScriptsRun;
	}

	public void setWritingPythonScriptsRun(FileWriter writingPythonScriptsRun) {
		this.writingPythonScriptsRun = writingPythonScriptsRun;
	}

	public FileWriter getRunningPythonScript() {
		return runningPythonScript;
	}

	public void setRunningPythonScript(FileWriter runningPythonScript) {
		this.runningPythonScript = runningPythonScript;
	}

	public boolean isRuntimeReport() {
		return runtimeReport;
	}

	public void setRuntimeReport(boolean runtimeReport) {
		this.runtimeReport = runtimeReport;
	}

	/**
	 * Learn dependency for continuous predicate atom
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
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		for(Feature ft:fts){
			Dependency dep_temp=null;
			Dependency filtering=null;
			HashMap<Value,Parameters> parametersPerBranch=new HashMap<Value, Parameters>();
			HashMap<Value,Double> scorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> testScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedTestScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedScorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> normalizedTestErrorPerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> testErrorPerBranch=new HashMap<Value, Double>();
			try {
				System.out.println("EXTENDING DEP: "+learned_dep.getDep()+" WITH : "+ft+"-> "+ft.getIndexInFeatureSpace());
				dep_temp= learned_dep.getDep().extend(ft);
				filtering=dep_temp;
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//System.out.println("Feature added, now doing data filtering ...");
			//discrete feature
			double overallScore=0;
			double overallPenalty=0;
			double overallTestError=0;
			double extractedScore=0;
			double testDataScore=0;
			double normalizedTestDataScore=0;
			double normalizedTestError=0;
			StructureScore score=null;

			if(ft.isDiscreteOutput()){
				//for discrete features we need to obtain the overall score by
				//scoring each brach and then finding a sum
				//System.out.println("DISCRETE FEATURE!!!");
				RangeDiscrete range=(RangeDiscrete)ft.getRange();
				Double[] scores=this.getScoreBranch(a,testErrorPerBranch,normalizedTestErrorPerBranch,scorePerBranch,normalizedScorePerBranch,testScorePerBranch,normalizedTestScorePerBranch,parametersPerBranch,"/regression.py", range, filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
				overallScore=scores[0];
				overallTestError=scores[1];
				testDataScore=scores[2];
				normalizedTestDataScore=scores[4];
				normalizedTestError=scores[3];
				extractedScore=overallScore;
				System.out.println("OVERALL TEST SCORE DISCRETE BRANCH: "+testDataScore);
				System.out.println("OVERALL SCORE DISCRETE BRANCH: "+overallScore);
				System.out.println("Normalized TEST SCORE DISCRETE BRANCH: "+normalizedTestDataScore);
				score=new StructureScore(extractedScore, 0.0, extractedScore);
			}
			//Continuous feature
			else{
				Double[] scores=this.getScoreContinuousFeature(a,"/regression.py",filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
				extractedScore=scores[0];
				overallTestError=scores[1];
				testDataScore=scores[2];
				normalizedTestDataScore=scores[4];
				normalizedTestError=scores[3];
				System.out.println("OVERALL SCORE CONTINUOUS BRANCH: "+extractedScore);
				System.out.println("OVERALL TEST SCORE CONTINUOUS BRANCH: "+testDataScore);
				System.out.println("Normalized TEST SCORE CONTINUOUS BRANCH: "+normalizedTestDataScore);
				System.out.println("Normalized TEST ERROR CONTINUOUS BRANCH: "+normalizedTestError);
				score=new StructureScore(extractedScore, 0.0, extractedScore);
			}

			//Learned dependency
			//System.out.println("########## LEARNED DEP #################");
			LearnedDependency extendedFeature=new LearnedDependency(filtering,score.getScore());
			//System.out.println(extendedFeature);
			extendedFeature.setTestErrorPerBranch(testErrorPerBranch);
			extendedFeature.setScorePerBranch(scorePerBranch);
			extendedFeature.setParametersPerBranch(parametersPerBranch);
			extendedFeature.setTestScores(testScorePerBranch);
			extendedFeature.setNormalizedTestError(normalizedTestError);
			extendedFeature.setNormalizedTestScore(normalizedTestDataScore);
			extendedFeature.setNormalizedTestErrorPerBranch(normalizedTestErrorPerBranch);
			extendedFeature.setNormalizedTestScorePerBranch(normalizedTestScorePerBranch);
			extendedFeature.setError_test_data(overallTestError);
			extendedFeature.setTestScore(testDataScore);
			extendedFeature.setNewest_added_feature(ft);
			extendedFeature.setStructure_score(score);
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
		//Choose the best score
		//System.out.println("Best scorE: "+bestScore);
		//System.out.println("parent score: "+parent_score);
		if(!no_feature_possible_to_add && bestScore.getScore()>=parent_score && bestScore.getDep().getFeatures().size()<=AlgorithmParameters.getModelSize()){
			bestScore.setNewFeatureLearned(true);
			FileWriter fw=new FileWriter(AlgorithmParameters.getLearnedFeatureIndicesFile(),true);
			FileReader fr=new FileReader(AlgorithmParameters.getLearnedFeatureIndicesFile());
			BufferedReader bfr=new BufferedReader(fr);
			String line=bfr.readLine();
			System.out.println(bestScore.getNewest_added_feature());
			if((line!=null) && !line.contains(String.valueOf(bestScore.getNewest_added_feature().getIndexInFeatureSpace()))){
				fw.append(String.valueOf(bestScore.getNewest_added_feature().getIndexInFeatureSpace())+" , ");
				fw.close();
			}
			else{
				fw.append(String.valueOf(bestScore.getNewest_added_feature().getIndexInFeatureSpace())+" , ");
				fw.close();
			}

			return bestScore;
		}
		else{
			System.out.println("No new feature added:");
			bestScore.setNewFeatureLearned(false);
			return bestScore;
		}
	}

	public Double[] getScoreBranch(Atom a,HashMap<Value, Double> testErrorPerBranch, HashMap<Value, Double> normalizedTestErrorPerBranch, HashMap<Value, Double> scorePerBranch, HashMap<Value, Double> normalizedScorePerBranch,HashMap<Value,Double> testScorePerBranch,HashMap<Value,Double> normalizedTestScorePerBranch, HashMap<Value,Parameters> parametersPerBranch, String scriptName,RangeDiscrete range,HashMap<Feature,Value> filter,Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,Dependency dep_temp) throws IOException{
		System.out.println("****** HANDLING DISCRETE FEATURE: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		QueryData est_training_data=null;
		QueryData testData=null;

		Double overallScore=0.0;
		Double testDataScore=0.0;
		Double overallTestError=0.0;

		DataToCSV dataToCSVConverting=new DataToCSV();
		List<String> pars=new ArrayList<String>();
		pars.addAll(Arrays.asList(this.paramteres));
		pars.add(AlgorithmParameters.getExternalScript()+scriptName);
		pars.add(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
		//System.out.println("Pars: "+pars);

		ParConverterFromCSV parconverter=new ParConverterFromCSV();
		parconverter.setPathToCSVFile(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
		if(a.getPredicate().isDiscrete()){
			pars.add(String.valueOf(((RangeDiscrete)a.getPredicate().getRange()).getValues().size()));
		}
		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long filteringTime=0;
		long overallTimeRunningScripts=0;
		long queryingTime=0;
		long initQueryingTime=System.nanoTime();
		est_training_data=query_data_training.getQueryResults(dep_temp);
		testData=query_data_test.getQueryResults(dep_temp);
		queryingTime+=System.nanoTime()-initQueryingTime;
		int testDataPoints=0;
		for(Value v:range.getValues()){
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
			QueryData filtered_est_training=null;
			QueryData filteredTestData=null;

			long initFiltering=System.nanoTime();
			filtered_est_training=qd_filter.filterQueryData(dep_temp,new_filter,est_training_data);
			filteredTestData=qd_filter.filterQueryData(dep_temp,new_filter,testData);
			filteringTime+=System.nanoTime()-initFiltering;

			if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
				return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0};
			}
			//In case we only have discrete features (CG), Davide's script cannot handle it
			if(dep_temp.getDiscreteFeatures().size()!=0 && dep_temp.getContinuousFeatures().size()==0){
				Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training);
				dep_temp.getCpd().setParameters(p);
				parametersPerBranch.put(v, p);
				initQueryingTime=System.nanoTime();
				QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
				queryingTime+=System.nanoTime()-initQueryingTime;

				initFiltering=System.nanoTime();
				QueryData score_filtered=qd_filter.filterQueryData(dep_temp,new_filter,est_validation_data);
				filteringTime+=System.nanoTime()-initFiltering;

				StructureScore extractedScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(score_filtered, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty());
				System.out.println("Score: "+extractedScore);
				double penalty=this.p.scalePenalty(ft,extractedScore.getPenalty());
				overallScore+=extractedScore.getScore();
				QueryData est_queries=query_data_test.getQueryResults(dep_temp);
				QueryData test_filtered=qd_filter.filterQueryData(dep_temp,new_filter,est_queries);
				StructureScore testScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(test_filtered, dep_temp.getCpd().getParameters(), new NoPenalty());
				double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, dep_temp.getCpd().getParameters(),false);
				overallTestError+=error_test;
				testDataPoints+=test_filtered.getNr_groundings_for_head();
				scorePerBranch.put(v, new Double(extractedScore.getScore()));
				testScorePerBranch.put(v, testScore.getScore());
				testDataScore+=testScore.getScore();
				normalizedScorePerBranch.put(v,extractedScore.getScore()/filtered_est_training.getNr_groundings_for_head());
				normalizedTestErrorPerBranch.put(v,error_test/filteredTestData.getNr_groundings_for_head());
				normalizedTestScorePerBranch.put(v,testScore.getScore()/filteredTestData.getNr_groundings_for_head());
				testErrorPerBranch.put(v, error_test);
			}
			else{
				int counter=0;
				//here check
				//a) if the number of randvars is zero for the branch
				//b) if there is undefined value in the data for this split
				//if there is then just put score to -Infinity
				//first remove all the data files if they exist, including model file as well
				if(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").exists()){
					new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").delete();
				}
				if(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").exists()){
					new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").delete();
				}
				if(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").exists()){
					new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").delete();
				}
				//write down the filtered training and test data to the current directory. Data is in csv formats
				long timeWritingParams=System.nanoTime();
				FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
				BufferedWriter bw = new BufferedWriter(fw);
				bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,new_filter,false));
				bw.close();
				fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
				bw = new BufferedWriter(fw);
				bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,new_filter,false));
				bw.close();
				overallTimeForWritingCSVs+=(System.nanoTime() - timeWritingParams);
				//run the script for parameter estimation
				long scriptRunningStart=System.nanoTime();
				try
				{    
					Process proc = Runtime.getRuntime().exec(pars.toArray(new String[pars.size()]));		
					InputStream stderr = proc.getErrorStream();
					InputStreamReader isr = new InputStreamReader(stderr);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					//System.out.println("<ERROR>");
					//while ((line = br.readLine()) != null)
					//	System.out.println(line);
					//System.out.println("</ERROR>");
					int exitVal = proc.waitFor();
					//System.out.println("Process exitValue: " + exitVal);
				} catch (Throwable t)
				{
					t.printStackTrace();
				}
				overallTimeRunningScripts+=System.nanoTime() - scriptRunningStart;
				//load the paramters
				try{
					FileReader fr = new FileReader(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
					//load parameters
					AssignmentKey key=new AssignmentKey(discreteFts, valueSdiscreteFts);
					dep_temp.getCpd().getParameters().getCoefficients().convert(parconverter, key, dep_temp);
					Cloner cloner=new Cloner();
					Parameters p=cloner.deepClone(dep_temp.getCpd().getParameters());
					parametersPerBranch.put(v, p);
					double pll=Double.NaN;
					double extractedScore=0;
					double extraxtedTestScore=0;
					if(a.getPredicate().isDiscrete()){
						extractedScore=extractCVScoreLR(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
					}
					else{
						extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
					}
					extraxtedTestScore=extractTestScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
					double extractedtestError=extractTestError(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
					overallScore+=extractedScore;
					testDataScore+=extraxtedTestScore;
					overallTestError+=extractedtestError;
					double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
					double penaltyScore=extractedScore-this.p.scalePenalty(ft,penalty);
					StructureScore score=new StructureScore(extractedScore, penalty, penaltyScore);
					pll=score.getScore();
					scorePerBranch.put(v, new Double(pll));
					testScorePerBranch.put(v, extraxtedTestScore);
					testDataPoints+=filteredTestData.getNr_groundings_for_head();
					normalizedScorePerBranch.put(v,extractedScore/filtered_est_training.getNr_groundings_for_head());

					double normalizedTestError=0;
					double normalizedTestScore=0;
					if(filteredTestData.getNr_groundings_for_head()!=0){
						normalizedTestError=extractedtestError/filteredTestData.getNr_groundings_for_head();
						normalizedTestScore=extraxtedTestScore/filteredTestData.getNr_groundings_for_head();
					}
					normalizedTestErrorPerBranch.put(v,normalizedTestError);
					normalizedTestScorePerBranch.put(v,normalizedTestScore);
					testErrorPerBranch.put(v, extractedtestError);
				}
				catch(Exception e){
					System.out.println("Problem with feature: "+ft+" for dependency: "+dep_temp);
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		System.out.println("Overall score: "+overallScore);
		System.out.println("Score per branch: "+scorePerBranch);
		System.out.println("Test score per branch: "+testScorePerBranch);
		System.out.println("Unnorm Test error per branch: "+testErrorPerBranch);
		System.out.println("Nr test data points: "+testDataPoints);
		System.out.println("Normalized Score per branch: "+normalizedScorePerBranch);
		System.out.println("Normalized Test score per branch: "+normalizedTestScorePerBranch);
		System.out.println("Normalized test error per branch:" +normalizedTestErrorPerBranch);
		long endTimeOverallRun = System.nanoTime();
		long durationOverall = endTimeOverallRun - startTimeOverallRun;
		if(this.runtimeReport){
			this.timeOverallFtRun.append(dep_temp+","+NANOSECONDS.toSeconds(durationOverall)+","+NANOSECONDS.toMillis(durationOverall)+"\n");
			this.writingPythonScriptsRun.append(dep_temp+","+NANOSECONDS.toSeconds(overallTimeForWritingCSVs)+","+NANOSECONDS.toMillis(overallTimeForWritingCSVs)+"\n");
			this.runningPythonScript.append(dep_temp+","+NANOSECONDS.toSeconds(overallTimeRunningScripts)+","+NANOSECONDS.toMillis(overallTimeRunningScripts)+"\n");
			this.filteringRunTime.append(dep_temp+","+NANOSECONDS.toSeconds(filteringTime)+","+NANOSECONDS.toMillis(filteringTime)+"\n");
		}
		if(dep_temp.getDiscreteFeatures().size()!=0 && dep_temp.getContinuousFeatures().size()==0){
			Parameters p=dep_temp.getCpd().getCpdEvaluator().estimateParameters(est_training_data);
			dep_temp.getCpd().setParameters(p);
		}
		System.out.println("Nr test data points: "+testDataPoints);
		double normalizedOverallTestScore=0.0;
		double normalizedOverallTestError=0.0;
		if(testDataPoints!=0){
			normalizedOverallTestError=overallTestError/testDataPoints;
			normalizedOverallTestScore=testDataScore/testDataPoints;
		}
		return new Double[]{overallScore,overallTestError,testDataScore,normalizedOverallTestError,normalizedOverallTestScore};
	}



	/**
	 * Get score of continuous feature being added
	 * @param a
	 * @param filter
	 * @param ft
	 * @param query_data_training
	 * @param query_data_validation
	 * @param query_data_test
	 * @param dep_temp
	 * @return
	 * @throws IOException
	 */
	private Double[] getScoreContinuousFeature(Atom a,String scriptName,HashMap<Feature, Value> filter, Feature ft,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,Dependency dep_temp) throws IOException {
		//System.out.println("****** HANDLING: "+dep_temp+" CPD: "+dep_temp.getCpd().getClass());
		long filteringTime=0;
		QueryDataFilter qd_filter=new QueryDataFilter();
		QueryData est_training_data=query_data_training.getQueryResults(dep_temp);
		QueryData filteredTestData=null;
		QueryData testData=query_data_test.getQueryResults(dep_temp);
		long trainingFilterInit=System.nanoTime();
		QueryData filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
		filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);
		int testDatapoints=filteredTestData.getNr_groundings_for_head();
		filteringTime+=System.nanoTime()-trainingFilterInit;
		filtered_est_training.setHadUndefinedValue(est_training_data.isHadUndefinedValue());
		//System.out.println("TEST ---------------");
		double normalizedTestError=0;
		double normalizedTestScore=0;
		DataToCSV dataToCSVConverting=new DataToCSV();
		ParConverterFromCSV parconverter=new ParConverterFromCSV();
		double extractedScore=0;
		double testDataScore=0;
		StructureScore score=null;
		List<String> pars=new ArrayList<String>();
		pars.addAll(Arrays.asList(this.paramteres));
		pars.add(AlgorithmParameters.getExternalScript()+scriptName);
		//if head atom is discrete add argument having the number of values for head

		pars.add(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");

		long startTimeOverallRun = System.nanoTime();
		long overallTimeForWritingCSVs=0;
		long overallTimeRunningScripts=0;

		if(a.getPredicate().isDiscrete()){
			pars.add(String.valueOf(((RangeDiscrete)a.getPredicate().getRange()).getValues().size()));
		}
		parconverter.setPathToCSVFile(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");

		if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
			//System.out.println("Has undefined values ... discard feature and continue");
			return new Double[]{Double.NEGATIVE_INFINITY,0.0,0.0,0.0,0.0};
		}
		if(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		if(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		if(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").exists()){
			new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").delete();
		}
		long timeWritingParams=System.nanoTime();
		FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,filter,false));
		bw.close();
		fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
		bw = new BufferedWriter(fw);
		bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,filter,false));
		bw.close();
		overallTimeForWritingCSVs+=System.nanoTime() - timeWritingParams;
		long scriptRunningStart=System.nanoTime();
		//System.out.println("Running : "+pars);

		try{  
			Process proc = Runtime.getRuntime().exec(pars.toArray(new String[pars.size()]));;		
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			//System.out.println("<ERROR>");
			//while ( (line = br.readLine()) != null)
			//	System.out.println(line);
			//System.out.println("</ERROR>");
			int exitVal = proc.waitFor();
			//System.out.println("Process exitValue: " + exitVal);
		} catch (Throwable t)
		{
			t.printStackTrace();
		}
		overallTimeRunningScripts+=System.nanoTime() - scriptRunningStart;
		//load the paramters
		double extractedtestError=-1;

		try{
			//System.out.println("DAVIDE's files: ");
			FileReader fr = new FileReader(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
			double pll=Double.NaN;
			if(a.getPredicate().isDiscrete()){
				extractedScore=extractCVScoreLR(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
			}
			else{
				extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
			}
			testDataScore=extractTestScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
			Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
			Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];

			int i=0;
			for(Feature f:dep_temp.getDiscreteFeatures()){
				discreteFts[i]=f;
				valueSdiscreteFts[i]=filter.get(f);
			}
			AssignmentKey key=new AssignmentKey(discreteFts, valueSdiscreteFts);
			dep_temp.getCpd().getParameters().getCoefficients().convert(parconverter,key,dep_temp);
			extractedtestError=extractTestError(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");

			if(filteredTestData.getNr_groundings_for_head()!=0){
				normalizedTestError=extractedtestError/filteredTestData.getNr_groundings_for_head();
				normalizedTestScore=testDataScore/filteredTestData.getNr_groundings_for_head();
			}
			double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
			double penaltyScore=extractedScore-this.p.scalePenalty(ft,penalty);
			score=new StructureScore(extractedScore, penalty, penaltyScore);
			pll=score.getScore();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		long endTimeOverallRun = System.nanoTime();
		long durationOverall = endTimeOverallRun - startTimeOverallRun;
		if(this.runtimeReport){
			this.timeOverallFtRun.append(dep_temp+","+NANOSECONDS.toSeconds(durationOverall)+","+NANOSECONDS.toMillis(durationOverall)+"\n");
			this.writingPythonScriptsRun.append(dep_temp+","+NANOSECONDS.toSeconds(overallTimeForWritingCSVs)+","+NANOSECONDS.toMillis(overallTimeForWritingCSVs)+"\n");
			this.runningPythonScript.append(dep_temp+","+NANOSECONDS.toSeconds(overallTimeRunningScripts)+","+NANOSECONDS.toMillis(overallTimeRunningScripts)+"\n");
			this.filteringRunTime.append(dep_temp+","+NANOSECONDS.toSeconds(filteringTime)+","+NANOSECONDS.toMillis(filteringTime)+"\n");
		}
		System.out.println("----------------- EXTRACTED TEST ERROR --------------"+extractedScore+" NR TEST DATA POINTS: "+testDatapoints);
		return new Double[]{score.getScore(),Double.valueOf(extractedtestError),testDataScore,normalizedTestError,normalizedTestScore};
	}

	private LearnedDependency selectBestDependencyDiscreteHeadAtom(Atom a,LearnedDependency learned_dep, List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,HashMap<Feature, Value> filter, Penalty pen, double parent_score) {
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		QueryData est_training_data=null;
		QueryData testData=null;
		QueryData validationData=null;
		for(Feature ft:fts){
			double overallScore=0;
			double overallPenalty=0;
			double overallTestError=0;
			Dependency dep_temp=null;
			Dependency filtering=null;
			try {
				if(ft.isContinuousOutput()){
					learned_dep.getDep().setLogRegressionOnlyContinuous();
					dep_temp= learned_dep.getDep().extend(ft);
					System.out.println("CPD CREATED: "+dep_temp.getCpd().getClass());
					System.out.println(dep_temp.getCpd().getParameters());
					filtering=learned_dep.getDep().extend(ft);
				}
				else{
					learned_dep.getDep().setLogRegressionOnlyContinuous();
					dep_temp= learned_dep.getDep().extend(ft);
					System.out.println("CPD CREATED: "+dep_temp.getCpd().getClass());
					System.out.println(dep_temp.getCpd().getParameters());
					filtering=learned_dep.getDep().extend(ft);
				}
				System.out.println("EXTENDING DEP: "+learned_dep.getDep()+" WITH : "+ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			QueryDataFilter qd_filter=new QueryDataFilter();
			StructureScore score=null;
			if(ft.isDiscreteOutput()){
				RangeDiscrete range=(RangeDiscrete)ft.getRange();
				for(Value v:range.getValues()){
					System.out.println("FILTERING BASED ON VALUE: "+v);
					HashMap<Feature,Value> new_filter=new HashMap<Feature,Value>();
					new_filter.putAll(filter);
					new_filter.put(ft, v);
					Feature[] discreteFts=new Feature[dep_temp.getDiscreteFeatures().size()];
					Value[] valueSdiscreteFts=new Value[dep_temp.getDiscreteFeatures().size()];
					int i=0;
					for(Feature f:dep_temp.getDiscreteFeatures()){
						discreteFts[i]=f;
						valueSdiscreteFts[i]=new_filter.get(f);
					}
					QueryData filtered_est_training=null;
					QueryData validationDataFiltered=null;
					QueryData filteredTestData=null;
					est_training_data=query_data_training.getQueryResults(dep_temp);
					validationData=query_data_validation.getQueryResults(dep_temp);
					filtered_est_training=qd_filter.filterQueryData(dep_temp,new_filter,est_training_data);
					testData=query_data_test.getQueryResults(dep_temp);
					filteredTestData=qd_filter.filterQueryData(dep_temp,new_filter,testData);

					if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
						System.out.println("Not enough data");
						overallScore=Double.NEGATIVE_INFINITY;
						break;
					}

					System.out.println("Have to run PMF .... noo continuous features ...");
					//conditional gaussian (Davide doesn't handle the case when there are no continuous features
					est_training_data=query_data_training.getQueryResults( dep_temp);
					filtered_est_training=qd_filter.filterQueryData( dep_temp,new_filter,est_training_data);
					validationData=query_data_training.getQueryResults( dep_temp);
					validationDataFiltered=qd_filter.filterQueryData( dep_temp,new_filter,est_training_data);
					testData=query_data_test.getQueryResults( dep_temp);
					filteredTestData=qd_filter.filterQueryData( dep_temp,new_filter,testData);

					dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));
					QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
					QueryData score_filtered=qd_filter.filterQueryData(dep_temp,new_filter,est_validation_data);
					StructureScore extractedScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(score_filtered, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty());
					double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
					System.out.println(" VALUE = "+v+" SCORE= "+extractedScore);
					System.out.println(" PMF PARS: "+dep_temp.getCpd().getParameters());
					overallScore+=extractedScore.getScore();
					QueryData est_queries=query_data_test.getQueryResults(dep_temp);
					QueryData test_filtered=qd_filter.filterQueryData(dep_temp,new_filter,est_queries);
					double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, dep_temp.getCpd().getParameters(),false);
					overallTestError+=error_test;
				}
			}
			else{
				System.out.println("Continuous feature");
				QueryData filtered_est_training=null;
				QueryData validationDataFiltered=null;
				QueryData filteredTestData=null;
				est_training_data=query_data_training.getQueryResults(dep_temp);
				validationData=query_data_validation.getQueryResults(dep_temp);
				filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
				testData=query_data_test.getQueryResults(dep_temp);
				filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);
				/*if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
					System.out.println("Not enough data");
					overallScore=Double.NEGATIVE_INFINITY;
					break;
				}*/
				//conditional gaussian (Davide doesn't handle the case when there are no continuous features
				est_training_data=query_data_training.getQueryResults( dep_temp);
				filtered_est_training=qd_filter.filterQueryData( dep_temp,filter,est_training_data);
				validationData=query_data_training.getQueryResults( dep_temp);
				validationDataFiltered=qd_filter.filterQueryData( dep_temp,filter,est_training_data);
				testData=query_data_test.getQueryResults( dep_temp);
				filteredTestData=qd_filter.filterQueryData( dep_temp,filter,testData);	
				Parameters pars=dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training);
				//dep_temp.getCpd().getParameters().getCoefficients().
				QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
				QueryData score_filtered=qd_filter.filterQueryData(dep_temp,filter,est_validation_data);
				StructureScore extractedScore=dep_temp.getCpd().getCpdEvaluator().calculatePLL(score_filtered, dep_temp.getCpd().getParameters(), query_data_validation.getPenalty());
				double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
				System.out.println(" PARS: "+dep_temp.getCpd().getParameters());
				overallScore+=extractedScore.getScore();
				QueryData est_queries=query_data_test.getQueryResults(dep_temp);
				QueryData test_filtered=qd_filter.filterQueryData(dep_temp,filter,est_queries);
				double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, dep_temp.getCpd().getParameters(),false);
				overallTestError+=error_test;	
				System.out.println("TEST ERROR: "+error_test);
			}
			//Learned dependency
			System.out.println("########## LEARNED DEP #################");
			LearnedDependency extendedFeature=new LearnedDependency(dep_temp,Double.POSITIVE_INFINITY);
			System.out.println(extendedFeature);
			System.out.println("#######################################");
			extendedFeature.setError_test_data(overallTestError);
			extendedFeature.setNewest_added_feature(ft);
			extendedFeature.setStructure_score(score);
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
				System.out.println("-------- The list of undefined scores: -------");
				for(LearnedDependency sc:failureFts){
					System.out.println(sc);
				}
			}
			bestScore=extension_scores.get(0);
		}
		//Choose the best score
		System.out.println("Best scorE: "+bestScore);
		System.out.println("parent score: "+parent_score);
		if(!no_feature_possible_to_add && bestScore.getScore()>=parent_score && bestScore.getDep().getFeatures().size()<10){
			bestScore.setNewFeatureLearned(true);
			return bestScore;
		}
		else{
			System.out.println("No new feature added:");
			bestScore.setNewFeatureLearned(false);
			return bestScore;
		}

	}


	private LearnedDependency selectBestDependencyDiscreteHeadAtomWithScript(Atom a,LearnedDependency learned_dep, List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation, QueryMachine query_data_test,HashMap<Feature, Value> filter, Penalty pen, double parent_score) throws IOException {
		/*LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		QueryData est_training_data=null;
		QueryData testData=null;
		QueryData validationData=null;

		for(Feature ft:fts){
			double overallScore=0;
			double overallPenalty=0;
			double overallTestError=0;
			StructureScore score=null;
			Dependency dep_temp=null;
			Dependency filtering=null;
			HashMap<Value,Parameters> parametersPerBranch=new HashMap<Value, Parameters>();
			HashMap<Value,Double> scorePerBranch=new HashMap<Value, Double>();
			HashMap<Value,Double> testScorePerBranch=new HashMap<Value, Double>();
			try {
				learned_dep.getDep().setLogRegressionOnlyContinuous();
				dep_temp= learned_dep.getDep().extend(ft);
				//System.out.println("CPD CREATED: "+dep_temp.getCpd().getClass());
				//System.out.println(dep_temp.getCpd().getParameters());
				filtering=learned_dep.getDep().extend(ft);
				if(ft.isDiscreteOutput()){
					//System.out.println("DISCRETE FEATURE!!!");
					RangeDiscrete range=(RangeDiscrete)ft.getRange();
					Double[] scores=this.getScoreBranch(a,testErrorPerBranch,normalizedTestErrorPerBranch,scorePerBranch,normalizedScorePerBranch,testScorePerBranch,normalizedTestScorePerBranch,parametersPerBranch,"/regression.py", range, filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
					//System.out.println("PARAMETERS PER BRANCH!!! : "+parametersPerBranch);
					overallScore=scores[0];
					overallTestError=scores[1];
					score=new StructureScore(overallScore, 0.0, overallScore);	
				}
				else{
					//System.out.println("Adding continuous feature");
					Double[] scores=this.getScoreContinuousFeature(a,"/classification.py",filter, ft, query_data_training, query_data_validation, query_data_test, dep_temp);
					overallScore=scores[0];
					overallTestError=scores[1];
					score=new StructureScore(overallScore, 0.0, overallScore);	
				}
				//System.out.println("EXTENDING DEP: "+learned_dep.getDep()+" WITH : "+ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//Learned dependency
			System.out.println("########## LEARNED DEP #################");
			System.out.println("SCORE: "+score.getScore());
			LearnedDependency extendedFeature=null;
			extendedFeature=new LearnedDependency(dep_temp,score.getScore());
			extendedFeature.setParametersPerBranch(parametersPerBranch);
			extendedFeature.setScorePerBranch(scorePerBranch);
			//System.out.println("#######################################");
			extendedFeature.setError_test_data(overallTestError);
			extendedFeature.setNewest_added_feature(ft);
			extendedFeature.setStructure_score(score);
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
				System.out.println("-------- The list of undefined scores: -------");
				for(LearnedDependency sc:failureFts){
					System.out.println(sc);
				}
			}
			bestScore=extension_scores.get(0);
		}
		//Choose the best score
		System.out.println("Best scorE: "+bestScore);
		System.out.println("parent score: "+parent_score);
		if(!no_feature_possible_to_add && bestScore.getScore()>=parent_score && bestScore.getDep().getFeatures().size()<10){
			bestScore.setNewFeatureLearned(true);
			return bestScore;
		}
		else{
			System.out.println("No new feature added:");
			bestScore.setNewFeatureLearned(false);
			return bestScore;
		}*/
		return null;

	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	private Double extractCVScore(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			lineNumber++;
			if(lineNumber==3){ //read regression coefficients
				score=Double.valueOf(line.split(",")[1]);
			}	
			line = br.readLine();
		}
		return score;
	}

	private Double extractTestScore(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=0.0;
		while (line != null) {
			lineNumber++;
			if(line.startsWith("sum loglikelihood test")){ //read regression coefficients
				try{
					score=Double.valueOf(line.split(",")[1]);
				}
				catch(ArrayIndexOutOfBoundsException e){
					break;
				}
			}	
			line = br.readLine();
		}
		return score;
	}

	private Double extractCVScoreLR(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			lineNumber++;
			if(line.startsWith("sum loglikelihood CV train")){ //read regression coefficients
				score=Double.valueOf(line.split(",")[1]);
			}	
			line = br.readLine();
		}
		return score;
	}


	private double extractTestError(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=0.0;
		while (line != null) {
			lineNumber++;
			if(line.startsWith("sum squared error test")){ //read regression coefficients
				try{
					score=Double.valueOf(line.split(",")[1]);
				}
				catch(ArrayIndexOutOfBoundsException e){
					break;
				}
			}	
			line = br.readLine();
		}
		return score;
	}



}



