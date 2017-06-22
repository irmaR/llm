package hybrid.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import weka.core.Debug.Random;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.Interpretation;
import hybrid.network.GroundAtom;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

/**
 * Class for calculating RMSE for a dependency and test data
 * @author irma
 *
 */
public class CalculateRMSE {

	/**
	 * calculate RMSE for a dependency and test data
	 * @param dep - dependency
	 * @param test_data - tuprolog query machine made for test data
	 * @param result_file
	 * @param ran
	 * @return
	 * @throws WrongValueType
	 * @throws IOException
	 */
	public double calculateRMSE(Dependency dep,TuPrologQueryMachine test_data,File result_file, Random ran) throws WrongValueType, IOException{
		double rmse=0.0;
		QueryData markov_blankets=test_data.getQueryResults(dep);
		double max_value=0.0;
		double min_value=Double.POSITIVE_INFINITY;
		int nr_points=0;
		
		double stdev=test_data.getStandardDeviationOfTargetPredicate(dep);
		
		for(Interpretation i:markov_blankets.getQuery_results().keySet()){
		  for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
			  double true_value=mB.getHead().getValue().toNumber();
			  double predicted_value=dep.getCpd().getCpdEvaluator().getPrediction(mB,dep.getCpd().getParameters(),ran).toNumber();
			 
			  if(true_value>max_value){
				  max_value=true_value;
			  }
			  if(true_value<min_value){
				  min_value=true_value;
			  }
			  
			  rmse+=Math.pow((true_value-predicted_value), 2);
			  nr_points++;
		  }
		}
		double difference=0;
		if(max_value==min_value){
			difference=max_value;
		}
		else{
			difference=max_value-min_value;
		}
		double Unrmse=Math.sqrt(rmse/nr_points);	
		double NRMSE=Unrmse/difference;
		FileWriter fw=new FileWriter(result_file);
		fw.append("NRMSE "+NRMSE);
		fw.close();
		return NRMSE;
		
	}
	
	public double calculateRMSE_no_noise(Dependency dep,TuPrologQueryMachine test_data,File result_file) throws WrongValueType, IOException{
		double rmse=0.0;
		QueryData markov_blankets=test_data.getQueryResults(dep);
		double max_value=0.0;
		double min_value=Double.POSITIVE_INFINITY;
		int nr_points=0;
		double stdev=test_data.getStandardDeviationOfTargetPredicate(dep);
		
		for(Interpretation i:markov_blankets.getQuery_results().keySet()){
		  for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
			  double true_value=mB.getHead().getValue().toNumber();
			  double predicted_value=dep.getCpd().getCpdEvaluator().getPrediction_no_noise(mB,dep.getCpd().getParameters()).toNumber();
			 
			  if(true_value>max_value){
				  max_value=true_value;
			  }
			  if(true_value<min_value){
				  min_value=true_value;
			  }
			  
			  rmse+=Math.pow((true_value-predicted_value), 2);
			  nr_points++;
		  }
		}
		double difference=0;
		if(max_value==min_value){
			difference=max_value;
		}
		else{
			difference=max_value-min_value;
		}
		double Unrmse=Math.sqrt(rmse/nr_points);	
		double NRMSE=Unrmse/difference;
		FileWriter fw=new FileWriter(result_file);
		fw.append("NRMSE "+NRMSE);
		fw.close();
		return NRMSE;
		
	}
	
}
