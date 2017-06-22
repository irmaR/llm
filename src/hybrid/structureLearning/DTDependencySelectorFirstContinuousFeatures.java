package hybrid.structureLearning;

import hybrid.cpdEvaluation.CLGEvaluator;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CLGParameters;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.Parameters;
import hybrid.penalties.Penalty;
import hybrid.penalties.SpecialPenalties;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.structureLearning.DTDependencySelectorSplitDiscreteAllFeatures.ScoreAndPars;
import hybrid.tocsvmodule.DataToCSV;

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

public class DTDependencySelectorFirstContinuousFeatures extends DTDependencySelection{
	/**
	 * Davide testing: we first add all the continuous features
	 * then we do the split on discrete features
	 * In this script we ingnore loading the parameters since it's hard for
	 * my current implementation. This is only used for test and we need: 
	 * validation score (error) and test error.
	 * @author irma
	 *
	 */
	String output_directory=null;
	String predicateName="";
	private String[] paramteres;
	private boolean linearRegressionModelLearned=false;
	private SpecialPenalties p;

	public DTDependencySelectorFirstContinuousFeatures(String[] parameters,String outputDirectory,SpecialPenalties p){
		this.paramteres=parameters;
		this.output_directory=outputDirectory;
		this.p=p;
	}	
	public String getPredicateName() {
		return predicateName;
	}
	public void setPredicateName(String predicateName) {
		this.predicateName = predicateName;
	}
	@Override
	/**
	 * 
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
	public LearnedDependency selectBestDependency(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score) throws IOException{	
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		DataToCSV dataToCSVConverting=new DataToCSV();
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		Dependency depTempAllFts=null;
		Dependency dep_temp=null;


		List<Feature> continuousFeatures=new ArrayList<Feature>();
		List<Feature> discreteFeatures=new ArrayList<Feature>();

		for(Feature ft:fts){
			if(ft.isContinuousOutput()){
				continuousFeatures.add(ft);
			}
			else{
				discreteFeatures.add(ft);
			}
		}
		System.out.println("DISCRETE FEATURES: "+discreteFeatures.size());
		//add All Continuous features and learn the modes (unless the learning already happened)
		List<String> pars=new ArrayList<String>();
		pars.addAll(Arrays.asList(this.paramteres));
		pars.add(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
		pars.add(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
        System.out.println("Paramters ..."+pars);
		if(!this.linearRegressionModelLearned){
			depTempAllFts=new Dependency(a,continuousFeatures.toArray(new Feature[continuousFeatures.size()]));
			QueryData est_training_data=null;
			QueryData testData=null;
			QueryData validationData=null;
			System.out.println("DEP TEMP: "+depTempAllFts);
			est_training_data=query_data_training.getQueryResults(depTempAllFts);
			validationData=query_data_validation.getQueryResults(depTempAllFts);
			testData=query_data_test.getQueryResults(depTempAllFts);
			if(new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").exists()){
				new File(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv").delete();
			}
			if(new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").exists()){
				new File(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv").delete();
			}
			if(new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").exists()){
				new File(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv").delete();
			}
			FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(est_training_data,depTempAllFts,filter,false));
			bw.close();
			fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
			bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(testData,depTempAllFts,filter,false));
			bw.close();
			//run the script for parameter estimation
			
			try
			{    
				System.out.println("Running : "+this.paramteres);
				Process proc = Runtime.getRuntime().exec(pars.toArray(new String[pars.size()]));;		
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
	            System.out.println("<ERROR>");
	            while ( (line = br.readLine()) != null)
	                System.out.println(line);
	            System.out.println("</ERROR>");
				int exitVal = proc.waitFor();
				System.out.println("Process exitValue: " + exitVal);
			} catch (Throwable t)
			{
				t.printStackTrace();
			}
			//load the paramters
			double extractedScore=-1;
			double extractedtestError=-1;
			StructureScore score=null;
			try{
				System.out.println("DAVIDE's files: ");
				FileReader fr = new FileReader(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					System.out.println(line);
					line = br.readLine();
				}
				br.close();
				double pll=Double.NaN;
				extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
				extractedtestError=extractTestError(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
				System.out.println("EXTRACTED SCORE: "+extractedScore);
				double penalty=query_data_validation.getPenalty().calculatePenalty(depTempAllFts,query_data_validation.getData().getNrRandvars());
				double penaltyScore=extractedScore-penalty;
				score=new StructureScore(extractedScore, penalty, penaltyScore);
				pll=score.getScore();
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			LearnedDependency extendedFeature=new LearnedDependency(depTempAllFts,extractedScore);
			extendedFeature.setScore(extractedScore);
			extendedFeature.setStructure_score(score);
			extendedFeature.setNewest_added_feature(continuousFeatures.get(continuousFeatures.size()-1));
			extendedFeature.setError_test_data(extractedtestError);
			extendedFeature.setTraining_data(est_training_data);
			extendedFeature.setValidation_data(validationData);
			extendedFeature.setNr_data_points_test_data(testData.getNr_groundings_for_head());
			extendedFeature.setNewFeatureLearned(true);
			System.out.println("FEATURE SCORE LINEAR REGRESSION MODEL ONLY!: "+extendedFeature);
			this.linearRegressionModelLearned=true;
			return extendedFeature;
		}
		else{
			for(Feature ft:discreteFeatures){
				System.out.println("ADDING DESCRETE FEATURE: "+ft+" TO : "+learned_dep.getDep());
				try {
					dep_temp= learned_dep.getDep().extend(ft);
					no_feature_possible_to_add=false;
				} catch (FeatureAlreadyExists e1) { //feature already exists catch
					continue;
				}
				//filter training and test data
				//perform filtering data based on this feature
				RangeDiscrete range=(RangeDiscrete)ft.getRange();
				double overallScore=0;
				double overallPenalty=0;
				double overallTestError=0;
				//go over all the values of the discrete feature
				QueryData est_training_data=null;
				QueryData testData=null;
				QueryData validationData=null;

				for(Value v:range.getValues()){
					System.out.println("FILTERING BASED ON VALUE: "+v);
					QueryDataFilter qd_filter=new QueryDataFilter();
					HashMap<Feature,Value> new_filter=new HashMap<Feature,Value>();
					new_filter.putAll(filter);
					new_filter.put(ft, v);
					System.out.println("FILTER: "+new_filter);
					QueryData filtered_est_training=null;
					QueryData validationDataFiltered=null;
					QueryData filteredTestData=null;
					est_training_data=query_data_training.getQueryResults(dep_temp);
					validationData=query_data_validation.getQueryResults(dep_temp);
					filtered_est_training=qd_filter.filterQueryData(dep_temp,new_filter,est_training_data);
					testData=query_data_test.getQueryResults(dep_temp);
					filteredTestData=qd_filter.filterQueryData(dep_temp,new_filter,testData);
					int counter=0;
					//here check
					//a) if the number of randvars is zero for the branch
					//b) if there is undefined value in the data for this split
					//if there is then just put score to -Infinity
					if(filtered_est_training.getNr_groundings_for_head()<=4 || filtered_est_training.isHadUndefinedValue()){
						overallScore=Double.NEGATIVE_INFINITY;
						break;
					}
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
					FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
					BufferedWriter bw = new BufferedWriter(fw);
					bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,new_filter,false));
					bw.close();
					fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
					bw = new BufferedWriter(fw);
					bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,new_filter,false));
					bw.close();
					//run the script for parameter estimation
					try
					{    
						Process proc = Runtime.getRuntime().exec(pars.toArray(new String[pars.size()]));		
						InputStream stderr = proc.getErrorStream();
						InputStreamReader isr = new InputStreamReader(stderr);
						BufferedReader br = new BufferedReader(isr);
						String line = null;
			            System.out.println("<ERROR>");
			            while ((line = br.readLine()) != null)
			                System.out.println(line);
			            System.out.println("</ERROR>");
						int exitVal = proc.waitFor();
						System.out.println("Process exitValue: " + exitVal);
					} catch (Throwable t)
					{
						t.printStackTrace();
					}
					//load the paramters
					try{
						System.out.println("DAVIDE's files: ");
						FileReader fr = new FileReader(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
						BufferedReader br = new BufferedReader(fr);
						String line = br.readLine();
						while (line != null) {
							System.out.println(line);
							line = br.readLine();
						}
						br.close();
						double pll=Double.NaN;
						QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
						QueryData score_filtered=qd_filter.filterQueryData(dep_temp,new_filter,est_validation_data);
						double extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
						double extractedtestError=extractTestError(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
						overallScore+=extractedScore;
						overallTestError+=extractedtestError;
						System.out.println("EXTRACTED SCORE: "+extractedScore);
						double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
						double penaltyScore=extractedScore-this.p.scalePenalty(ft,penalty);
						StructureScore score=new StructureScore(extractedScore, penalty, penaltyScore);
						pll=score.getScore();
					}
					catch(Exception e){
						System.out.println("Problem with feature: "+ft+" for dependency: "+dep_temp);
						e.printStackTrace();
						System.exit(1);
					}
				}
				LearnedDependency extendedFeature=new LearnedDependency(dep_temp,overallScore);
				extendedFeature.setScore(overallScore);
				extendedFeature.setNewest_added_feature(ft);
				extendedFeature.setError_test_data(overallTestError);
				extendedFeature.setTraining_data(est_training_data);
				extendedFeature.setValidation_data(validationData);
				extendedFeature.setNr_data_points_test_data(testData.getNr_groundings_for_head());
				System.out.println("FEATURE SCORE: "+extendedFeature);
				System.out.println("----------------------------------------------");
				if(!new Double(extendedFeature.getScore()).isNaN()){
					extension_scores.add(extendedFeature);
				}
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
			QueryDataFilter qd_filter=new QueryDataFilter();
			System.out.println("Learned dependency: "+dep_temp);
			System.out.println("Best scorE: "+bestScore);
			System.out.println("parent score: "+parent_score);
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
	}

	private double extractTestError(String string) throws IOException {
		File csvFile=new File(string);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		Double result;
		int lineNumber=0;
		Double score=null;
		while (line != null) {
			lineNumber++;
			if(lineNumber==6){ //read regression coefficients
				score=Double.valueOf(line.split(",")[1]);
			}	
			line = br.readLine();
		}
		return score;
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


	private ScoreAndPars handleCLGParameterEstimation(Atom a,Dependency dep_temp, Feature ft, QueryData est_training_data, QueryData validationdata, QueryData testData, HashMap<Feature, Value> filter, QueryMachine query_data_training) throws IOException {
		//filter is a branch filter. We will add different values of the discrete feature to it
		//we will then for each discrete feature value, filter the data according to the filter + feature value
		//we will run Davide's script, get the parameters for this discrete feature value. Create a key, and add it to the parameter set
		CLGEvaluator clgEvaluator=new CLGEvaluator();
		RangeDiscrete range=(RangeDiscrete)ft.getRange();
		QueryDataFilter qd_filter=new QueryDataFilter();
		DataToCSV dataToCSVConverting=new DataToCSV();
		CLGParameters clgPars=new CLGParameters(dep_temp);
		CLGEvaluator clgEval=new CLGEvaluator();
		double score=0;
		double overallPenalty=0;
		for(Value v:range.getValues()){
			HashMap<Feature,Value> new_filter=new HashMap<Feature,Value>();
			new_filter.putAll(filter);
			new_filter.put(ft, v);
			QueryData filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
			QueryData filteredValidation=qd_filter.filterQueryData(dep_temp,filter,validationdata);
			QueryData filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);
			FileWriter fw = new FileWriter(output_directory+"/data_"+a.getPredicate().getPredicateName()+".csv");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,filter,false));
			bw.close();
			fw = new FileWriter(output_directory+"/test_"+a.getPredicate().getPredicateName()+".csv");
			bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,filter,false));
			bw.close();
			//create assignment key from filter
			AssignmentKey key=clgEval.extractAssignmentKey(dep_temp,new_filter);
			LinearGParameters pars=(LinearGParameters) dep_temp.getCpd().getCpdEvaluator().loadParametersFromCSV(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv",dep_temp);
			double extractedScore=extractCVScore(output_directory+"/model_"+a.getPredicate().getPredicateName()+".csv");
			score+=(extractedScore-query_data_training.getPenalty().calculatePenalty(dep_temp,filteredValidation.getNr_groundings_for_head()));
			overallPenalty+=query_data_training.getPenalty().calculatePenalty(dep_temp,filteredValidation.getNr_groundings_for_head());
			clgPars.addParameter(key, pars.getCoefficients());

		}
		return new ScoreAndPars(new StructureScore(score,overallPenalty,score),clgPars);
	}

	class ScoreAndPars{
		private StructureScore score;
		private Parameters parameters;

		public ScoreAndPars(StructureScore score, Parameters pars){
			this.score=score;
			this.parameters=pars;
		}

		public StructureScore getScore() {
			return score;
		}

		public void setScore(StructureScore score) {
			this.score = score;
		}

		public Parameters getParameters() {
			return parameters;
		}

		public void setParameters(Parameters parameters) {
			this.parameters = parameters;
		}



	}

	@Override
	public void reset() {
		this.linearRegressionModelLearned=false;
		
	}

}


