package hybrid.structureLearning;

import hybrid.converters.DC_converter;
import hybrid.cpdEvaluation.CLGEvaluator;
import hybrid.cpdEvaluation.LinearGaussianEvaluator;
import hybrid.cpds.LinearGaussian;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CLGParameters;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.Parameters;
import hybrid.parameters.Regression;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.tocsvmodule.DataToCSV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class DTDependencySelectorExternalScript extends DTDependencySelection{
	String output_directory=null;
	private String[] paramteres;

	public DTDependencySelectorExternalScript(String[] parameters,String outputDirectory){
		this.paramteres=parameters;
		this.output_directory=outputDirectory;
	}


	@Override
	public LearnedDependency selectBestDependency(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score) throws IOException{	
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		DataToCSV dataToCSVConverting=new DataToCSV();
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		List<Feature> featuresToProcess=new ArrayList<Feature>();
		Dependency dep_temp=null;
		featuresToProcess.addAll(fts);
		for(Feature ft:featuresToProcess){
			try {
				dep_temp= learned_dep.getDep().extend(ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//filter training and test data
			QueryDataFilter qd_filter=new QueryDataFilter();
			QueryData filtered_est_training=null;
			QueryData est_training_data=null;
			QueryData validationData=null;
			QueryData validationDataFiltered=null;
			QueryData testData=null;
			QueryData filteredTestData=null;
			est_training_data=query_data_training.getQueryResults(dep_temp);
			filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
			testData=query_data_test.getQueryResults(dep_temp);
			filteredTestData=qd_filter.filterQueryData(dep_temp,filter,testData);

			//first remove all the data files if they exist, including model file as well
			if(new File("data.csv").exists()){
				new File("data.csv").delete();
			}
			if(new File("test.csv").exists()){
				new File("test.csv").delete();
			}
			if(new File("model.csv").exists()){
				new File("model.csv").delete();
			}
			//write down the filtered training and test data to the current directory. Data is in csv formats
			FileWriter fw = new FileWriter("data.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println("DEP TEMP: "+dep_temp);
			bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,filter,false));
			bw.close();
			fw = new FileWriter("test.csv");
			bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,filter,false));
			bw.close();
			//run the script for parameter estimation
			try
			{    
				Process proc = Runtime.getRuntime().exec(this.paramteres);		
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				int exitVal = proc.waitFor();
				System.out.println("Process exitValue: " + exitVal);
			} catch (Throwable t)
			{
				t.printStackTrace();
			}
			//load the paramters

			//handleEach of the CPDs differentyl
			if(dep_temp.getDiscreteFeatures().size()==0 && dep_temp.getContinuousFeatures().size()!=0){
				//linear gaussian
				//dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().loadParametersFromCSV("model.csv",dep_temp));
			}
			else if(dep_temp.getDiscreteFeatures().size()!=0 && dep_temp.getContinuousFeatures().size()==0){
				//conditional gaussian (Davide doesn't handle the case when there are no continuous features
				est_training_data=query_data_training.getQueryResults( dep_temp);
				filtered_est_training=qd_filter.filterQueryData( dep_temp,filter,est_training_data);
				validationData=query_data_training.getQueryResults( dep_temp);
				validationDataFiltered=qd_filter.filterQueryData( dep_temp,filter,est_training_data);
				testData=query_data_test.getQueryResults( dep_temp);
				filteredTestData=qd_filter.filterQueryData( dep_temp,filter,testData);	
				dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));

			}
			else if(dep_temp.getDiscreteFeatures().size()!=0 && dep_temp.getContinuousFeatures().size()!=0){
				//conditional linear gaussian
				//here handle differently. Data needs to be split for each value of the discrete features (generate assignment keys).
				dep_temp.getCpd().setParameters(handleCLGParameterEstimation(dep_temp,ft,est_training_data,validationDataFiltered,testData,filter,query_data_training).getParameters());
			}

			System.out.println(dep_temp.getCpd().getClass());
			if(dep_temp.getDiscreteFeatures().size()!=0){
				dep_temp.setCpd(new LinearGaussian(dep_temp, new LinearGaussianEvaluator(), new LinearGParameters(dep_temp)));
			}
			if(dep_temp.getContinuousFeatures().size()==0){
				dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));
			}
			else{
				dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().loadParametersFromCSV("model.csv",dep_temp));
			}
			System.out.println("PARS: "+dep_temp.getCpd().getParameters());
			double error_training=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filtered_est_training, dep_temp.getCpd().getParameters(),false);
			try{
				double pll=Double.NaN;
				QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
				QueryData score_filtered=qd_filter.filterQueryData(dep_temp,filter,est_validation_data);

				double extractedScore=extractCVScore("model.csv");
				double penalty=query_data_validation.getPenalty().calculatePenalty(dep_temp,query_data_validation.getData().getNrRandvars());
				double penaltyScore=extractedScore-penalty;
				StructureScore score=new StructureScore(extractedScore, penalty, penaltyScore);
				pll=score.getScore();

				//double pll=extractCVScore("model.csv");
				double error_validation=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(score_filtered, dep_temp.getCpd().getParameters(),false);

				LearnedDependency extendedFeature=new LearnedDependency(dep_temp,pll);
				QueryData est_queries=query_data_test.getQueryResults(dep_temp);
				QueryData test_filtered=qd_filter.filterQueryData(dep_temp,filter,est_queries);
				double error_test=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, dep_temp.getCpd().getParameters(),false);
				extendedFeature.setError_test_data(error_test);
				extendedFeature.setNr_data_points_test_data(test_filtered.getNr_groundings_for_head());
				extendedFeature.setTraining_data(filtered_est_training);
				extendedFeature.setValidation_data(score_filtered);
				extendedFeature.setError_training_data(error_training);
				extendedFeature.setError_validation_data(error_validation);
				extendedFeature.setNewest_added_feature(ft);
				extendedFeature.setStructure_score(score);
				System.out.println("FEATURE SCORE: "+extendedFeature);
				System.out.println("DAVIDE's files: ");
				FileReader fr = new FileReader("model.csv");
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					System.out.println(line);
					line = br.readLine();
				}

				br.close();
				System.out.println("----------------------------------------------");
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

		QueryDataFilter qd_filter=new QueryDataFilter();

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


	private ScoreAndPars handleCLGParameterEstimation(Dependency dep_temp, Feature ft, QueryData est_training_data, QueryData validationdata, QueryData testData, HashMap<Feature, Value> filter, QueryMachine query_data_training) throws IOException {
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
			FileWriter fw = new FileWriter("data.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training,dep_temp,filter,false));
			bw.close();
			fw = new FileWriter("test.csv");
			bw = new BufferedWriter(fw);
			bw.append(dataToCSVConverting.dataToCSVFile(filteredTestData,dep_temp,filter,false));
			bw.close();
			//create assignment key from filter
			AssignmentKey key=clgEval.extractAssignmentKey(dep_temp,new_filter);
			LinearGParameters pars=(LinearGParameters) dep_temp.getCpd().getCpdEvaluator().loadParametersFromCSV("model.csv",dep_temp);
			double extractedScore=extractCVScore("model.csv");
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
		// TODO Auto-generated method stub
		
	}

}
