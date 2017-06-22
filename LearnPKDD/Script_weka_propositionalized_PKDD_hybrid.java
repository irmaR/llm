import hybrid.core.Logarithm2;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.network.NumberValue;
import hybrid.network.Predicate;
import hybrid.parameters.Gaussian;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.Instances;
import weka.classifiers.trees.REPTree;


public class Script_weka_propositionalized_PKDD_hybrid {
}
/*
	//Argument1: path to resultse e.g.: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/HRDN_New_Implementation_results/PKDD/PKDD_Arff_Simple/
	//Argument2: path containing data: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/
	//Argument3: regression algorithm - "mp5" or "lr" for linear regression
   
	
	public static void main(String[] args) throws Exception{
		String path_to_data=args[0];
		String path_to_results=args[1];
		String regression_algorithm=args[2];
		String result_folder_name="logistic_"+regression_algorithm;
		int nr_runs=10;
		
		
		String[] predicates=new String[]{"freq","hasAccount","avgSalary","ratUrbInhab","clientAge","gender","clientDistrict","hasLoan","loanAmount","loanStatus","monthlyPayments","avgNrWith","avgSumOfW","avgSumOfInc","stdMonthInc","stdMonthW","ratUrbInhab"};
		//String[] predicates=new String[]{"avgSalary","loanAmount","monthlyPayments","avgNrWith","avgSumOfW","avgSumOfInc","stdMonthInc","stdMonthW","ratUrbInhab","clientAge"};
		//String[] predicates=new String[]{"freq","hasAccount"};

		ArrayList<String> discrete_predicates=new ArrayList<String>();
		discrete_predicates.add("gender");
		discrete_predicates.add("hasLoan");
		discrete_predicates.add("loanStatus");
		discrete_predicates.add("freq");
		discrete_predicates.add("clientDistrict");
		discrete_predicates.add("hasAccount");

		ArrayList<String> cont_predicates=new ArrayList<String>();
		cont_predicates.add("avgSalary");
		cont_predicates.add("loanAmount");
		cont_predicates.add("monthlyPayments");
		cont_predicates.add("avgNrWith");
		cont_predicates.add("avgSumOfW");
		cont_predicates.add("avgSumOfInc");
		cont_predicates.add("stdMonthInc");
		cont_predicates.add("stdMonthW");
		cont_predicates.add("ratUrbInhab");
		cont_predicates.add("clientAge");

	
		//String[] predicates=new String[]{"hasLoan"};

		HashMap<String,ArrayList<Double>> AUCs_for_predicates=new HashMap<String,ArrayList<Double>>();
		File wplls_for_all_predicates_average=new File(path_to_results+"/"+result_folder_name+"/wplls.stat");
		BufferedWriter bw3=new BufferedWriter(new FileWriter(wplls_for_all_predicates_average));
		String[] order_predicates=new String[]{"clientDistrict","gender","hasAccount","freq","hasLoan","loanStatus","clientAge","avgSalary","ratUrbInhab","avgSumOfW","avgSumOfInc","stdMonthW","stdMonthInc","avgNrWith","loanAmount","monthlyPayments"};
		HashMap<String,Double> wplls_map=new HashMap<String, Double>();
		
		for(String predicate:predicates){
			PropositionalizedPKDD_hybrid propositionalizedPKK=new PropositionalizedPKDD_hybrid();
			HashMap<Predicate,Dependency> propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(1);

			ArrayList<Double> AUCs_per_fold=new ArrayList<Double>();
			ArrayList<Double> RMSE_per_fold=new ArrayList<Double>();
			ArrayList<Double> wpll_per_fold=new ArrayList<Double>();
			String all_folds_output=path_to_results+"/"+result_folder_name+"/";

			for(int i=1;i<=nr_runs;i++){
				Random ran=new Random();
				ran.setSeed(123456789);
				
				String input_train_data=path_to_data+"/Fold"+i+"/train/"+predicate+".arff";
				String input_test_data=path_to_data+"/Fold"+i+"/test/"+predicate+".arff";
				String test_groundAtoms=path_to_data+"/Fold"+i+"/test/"+predicate+"_test.arff";

				//Decision tree weka - j48
				BufferedReader reader_training_data = new BufferedReader(new FileReader(input_train_data));
				BufferedReader reader_testing_data = new BufferedReader(new FileReader(input_test_data));


				//get the training data
				Instances training_data = new Instances(reader_training_data);
				reader_training_data.close();

				//get the test data
				//reader_training_data = new BufferedReader(new FileReader(input_train_data));
				Instances test_data = new Instances(reader_testing_data);
				reader_testing_data.close();

				//Setting class attribute 
				training_data.setClassIndex(training_data.numAttributes() - 1);
				test_data.setClassIndex(training_data.numAttributes() - 1);

				String output_results=all_folds_output+"/Fold"+i+"/"+predicate+"/results.output";
				String instance_probs_output=all_folds_output+"/Fold"+i+"/auc/"+"/probs_"+predicate;		
				String instance_auc_output=all_folds_output+"/Fold"+i+"/auc/"+"/auc_"+predicate;		
				String rmse_output=all_folds_output+"/Fold"+i+"/rmse/"+"/"+predicate+".eval";
				
				File tmp_1=new File(instance_probs_output);
				tmp_1.getParentFile().mkdirs();
				
				File tmp_2=new File(rmse_output);
				tmp_2.getParentFile().mkdirs();
				
				File tmp_3=new File(instance_auc_output);
				tmp_3.getParentFile().mkdirs();

				if(discrete_predicates.contains(predicate)){
					//run J48 algorithm for discrete
					String auc_probs_file=all_folds_output+"/Fold"+i+"/"+predicate+"/auc_probs_file/";
					File tmp=new File(output_results);
					tmp.getParentFile().mkdirs();
					Results res=null;
					try{
						res=buildDiscreteClassifier(training_data, test_data, output_results,test_groundAtoms,instance_probs_output);
					}
					catch(Exception e){
						System.out.println(" Problem? ");
						e.printStackTrace();
						continue;
					}
					FileWriter write_auc=new FileWriter(tmp_3);
					System.out.println("Returned WAUC: "+res.getWeightedAUC());
					write_auc.append(res.getWeightedAUC()+"");
					write_auc.close();
					AUCs_per_fold.add(res.getWeightedAUC());
					wpll_per_fold.add(res.getWPLL());
				}

				else{
					//run m5p algorithm for continuous
					File tmp=new File(output_results);
					tmp.getParentFile().mkdirs();
					Results res=null;
					try{
						if(regression_algorithm.equals("mp5")){
						System.out.println("MP5 regression");
						res=buildRegressionClassifierMP5(training_data, test_data, output_results,test_groundAtoms,instance_probs_output,ran);
						}
						else{
						System.out.println("Linear regression");
						res=buildRegressionClassifierLinearRegression(training_data, test_data, output_results,test_groundAtoms,instance_probs_output,ran);
						}
					}
					catch(Exception e){
						System.out.println(" Problem? ");
						e.printStackTrace();
						continue;
					}
					
					FileWriter write_rmse=new FileWriter(tmp_2);
					//System.out.println("Returned NRMSE: "+res.getNRMSE());
					//write_rmse.append("NRMSE "+res.getNRMSE()+"");
					write_rmse.close();
					RMSE_per_fold.add(res.getNRMSE());
					wpll_per_fold.add(res.getWPLL());
				}

			}
			File output_w_aucs=new File(all_folds_output+"/eval/"+predicate+"_all_folds.output");
			if(!output_w_aucs.exists()){
				output_w_aucs.getParentFile().mkdirs();
			}
			
			File output_wpll=new File(all_folds_output+"/wplls/"+predicate+"_wpll.output");
			if(!output_wpll.exists()){
				output_wpll.getParentFile().mkdirs();
			}
			
			//output_w_aucs.createNewFile();
			if(output_w_aucs.exists()){
				output_w_aucs.delete();
			}
			FileWriter fw=new FileWriter(output_w_aucs,false);
			FileWriter fw1=new FileWriter(output_wpll,false);
			
			int i=0;
			double sum=0.0;
            double wpll_sum=0.0;
			
            int counter_wpll=1;
			if(wpll_per_fold.size()!=0){
				for(Double wpll:wpll_per_fold){
					fw1.append(wpll+"\n");
					wpll_sum+=wpll;
					counter_wpll++;
				}
				fw1.append("AVERAGE WPLL: "+(wpll_sum/wpll_per_fold.size()));
				wplls_map.put(predicate, wpll_sum/wpll_per_fold.size());
				
			}
			fw1.close();
			if(AUCs_per_fold.size()!=0){
				for(Double auc:AUCs_per_fold){
					fw.append(auc+"\n");
					sum+=auc;
					i++;
				}
				fw.append("AVERAGE W-AUC: "+(sum/AUCs_per_fold.size()));
			}

			else{
				for(Double rmse:RMSE_per_fold){
					fw.append(rmse+"\n");
					sum+=rmse;
					i++;
				}
				fw.append("AVERAGE NRSME: "+(sum/RMSE_per_fold.size()));
			}
			
			
			
			fw.close();
		}
		bw3.append("ORDER: ");
		for(String s:order_predicates){
			bw3.append(s+"\t");
		}
		bw3.append("\n");
		for(String s:order_predicates){
			bw3.append(wplls_map.get(s)+"\n");
		}

		bw3.close();


	}

	//AUCs_for_predicates.put(predicate, AUCs_per_fold);
	private static Results buildRegressionClassifierMP5(Instances training_data,Instances test_data, String output_results,String test_groundAtoms, String instance_probs_output, Random ran) throws Exception {
		Script_weka_propositionalized_PKDD_hybrid tmp=new Script_weka_propositionalized_PKDD_hybrid();   
		Results result=tmp.new Results();
		double max_target_value=0;
		double min_target_value=Double.POSITIVE_INFINITY;
		double rmse=0;
		System.out.println(" TEST DATA: \n"+test_data);
		FileWriter fw=new FileWriter(output_results);
		weka.classifiers.trees.M5P regr=new weka.classifiers.trees.M5P();
		regr.setOptions(new String[]{"-U"});
		regr.setOptions(new String[]{"-R"});
		regr.buildClassifier(training_data);
		fw.append(regr+"\n");
		System.out.println(regr);
		Evaluation eval = new Evaluation(training_data);
		eval.evaluateModel(regr, test_data);
		double noise_training_data=training_data.variance(training_data.classAttribute());
		double rmse_m5p=eval.rootMeanSquaredError();
		double PLL=0;
		double std=getStdeviation(training_data);
		int nr_points=0;
		
		for(int i=0;i<test_data.numInstances();i++){
			nr_points++;
			double prediction=regr.classifyInstance(test_data.instance(i));
			double true_value=test_data.instance(i).classValue();
			//System.out.println(test_data.instance(i)+ " prediction "+regr.classifyInstance(test_data.instance(i)));
			double mean=prediction;
			double random_nr=ran.nextGaussian();
			double normalized_prediction=prediction;	
			double prob=getGaussianDensity(test_data.instance(i).classValue(), mean,std);
			PLL+=Logarithm2.logarithm2(prob);
			
			if(true_value>max_target_value){
				max_target_value=true_value;
			}
			if(true_value<min_target_value){
				min_target_value=true_value;
			}
			double intermediate=Math.pow((true_value-normalized_prediction), 2);
			rmse+=intermediate;
		}
		System.out.println("RMSE MP5 "+rmse_m5p);
		System.out.println("Nr instances weka: "+test_data.numInstances());
		System.out.println("Nr instances me: "+nr_points);
		double Unrmse=Math.sqrt(rmse/test_data.numInstances());	
		System.out.println("RMSE "+Unrmse);
		double NRMSE=Unrmse/(max_target_value-min_target_value);
		System.out.println("UNRMSE: "+Unrmse);
		System.out.println("Nrmse "+NRMSE);
		result.setNRMSE(NRMSE);
		result.setWPLL(PLL/nr_points);
		fw.append(eval.toSummaryString("\nResults\n======\n", false));
		fw.append("STDEV: "+std+"\n");
		fw.close();
		return result;
	}

	private static Results buildRegressionClassifierLinearRegression(Instances training_data,Instances test_data, String output_results,String test_groundAtoms, String instance_probs_output, Random ran) throws Exception {
		Script_weka_propositionalized_PKDD_hybrid tmp=new Script_weka_propositionalized_PKDD_hybrid(); 
		
		Results result=tmp.new Results();
		double max_target_value=0;
		double min_target_value=Double.POSITIVE_INFINITY;
		double rmse=0;

		FileWriter fw=new FileWriter(output_results);
		LinearRegression regr=new LinearRegression();
		regr.setOptions(new String[]{"-C"});
		regr.buildClassifier(training_data);
		fw.append(regr+"\n");
		Evaluation eval = new Evaluation(training_data);
		eval.evaluateModel(regr, test_data);
		double rmse_m5p=eval.rootMeanSquaredError();
		//double std=Math.sqrt(training_data.variance(training_data.classAttribute()));
		double std=getStdeviation(training_data);
		double PLL=0;
		int nr_points=0;
		
		for(int i=0;i<test_data.numInstances();i++){
			double prediction=regr.classifyInstance(test_data.instance(i));
			double true_value=test_data.instance(i).classValue();
			double mean=prediction;
			//double normalized_prediction=random_nr*noise_training_data+mean;
			double normalized_prediction=prediction;
			//System.out.println("norm: "+normalized_prediction+" true; "+true_value);
			
			double prob=getGaussianDensity(test_data.instance(i).classValue(), mean,std);
			PLL+=Logarithm2.logarithm2(prob);
			nr_points++;
			
			if(true_value>max_target_value){
				max_target_value=true_value;
			}
			if(true_value<min_target_value){
				min_target_value=true_value;
			}
			rmse+=Math.pow((true_value-normalized_prediction), 2);
		}
		//System.out.println("max: "+max_target_value+ " min "+min_target_value);
		//System.out.println(" rmse: "+rmse+ " "+test_data.numInstances());
		double Unrmse=Math.sqrt(rmse/test_data.numInstances());	
		
		double NRMSE=0;
		if(max_target_value==min_target_value){
			NRMSE=Unrmse/(max_target_value-min_target_value);
		}
		else{
			System.out.println(" unrmse: "+Unrmse+ " diff: "+(max_target_value-min_target_value));
		    NRMSE=Unrmse/(max_target_value-min_target_value);
		}
		result.setNRMSE(NRMSE);
        result.setWPLL(PLL/nr_points);
		fw.append(eval.toSummaryString("\nResults\n======\n", false));
		fw.append("STDEV: "+std+"\n");
		fw.close();
		return result;
	}


   public static double getStdeviation(Instances trainingData) throws Exception{
	   
	   LinearRegression regr=new LinearRegression();
		regr.setOptions(new String[]{"-C"});
		regr.buildClassifier(trainingData);
		
		double n = 0;
		double sum = 0;
		double sum2 = 0;
		   for(int i=0;i<trainingData.numInstances();i++){		
			   double mean=regr.classifyInstance(trainingData.instance(i));
				sum2 += (trainingData.instance(i).classValue() - mean) * (trainingData.instance(i).classValue() - mean);
				n++;
			}
		return Math.sqrt(sum2 / (n - 1));
   }



	public static Results buildDiscreteClassifier(Instances training_data,Instances testing_data,String results_output_folder, String test_groundAtoms,String path_to_prob_output) throws Exception{
		BufferedWriter bfw=new BufferedWriter(new FileWriter(path_to_prob_output));
		BufferedReader reader_test_groundAtoms = new BufferedReader(new FileReader(test_groundAtoms));

		Script_weka_propositionalized_PKDD_hybrid tmp=new Script_weka_propositionalized_PKDD_hybrid();   
		Results result=tmp.new Results();
		FileWriter fw=new FileWriter(results_output_folder);
		Classifier cls = new Logistic();
		cls.setOptions(new String[]{"-A"});
		cls.buildClassifier(training_data);
		fw.append(cls+"\n");
		System.out.println(cls);
		Evaluation eval = new Evaluation(training_data);
		eval.evaluateModel(cls, testing_data);
		fw.append(eval.toSummaryString("\nResults\n======\n", false));
		double wauc=eval.weightedAreaUnderROC();
		fw.append("Weighted AUC " +wauc);
		fw.close();
		result.setWeighted_AUC(wauc);
		result.setClassifier(cls);


		double WPLL=0;
		System.out.println(" Nr instances: "+testing_data.numInstances());
		int counter=0;
		for(int i=0;i<testing_data.numInstances();i++){
			counter++;
			double[] prob_dist=cls.distributionForInstance(testing_data.instance(i));

			if(!isBooleanPredicate(testing_data.instance(i).classAttribute())){
				for(int class_index=0;class_index<testing_data.instance(i).numClasses();class_index++){
					String groundAtom=reader_test_groundAtoms.readLine().trim();          
					bfw.append(groundAtom+"\t"+prob_dist[class_index]+"\n");			
				}
			}
			else{
				//this means the predicate is boolean
				if(testing_data.instance(i).stringValue(testing_data.instance(i).classAttribute()).equals("true")){
					String groundAtom=reader_test_groundAtoms.readLine().trim();
					bfw.append(groundAtom+"\t"+prob_dist[0]+"\n");
				}
				else{
					String groundAtom=reader_test_groundAtoms.readLine().trim();
					bfw.append(groundAtom+"\t"+prob_dist[0]+"\n");
				}
			}

			double prob=prob_dist[(int) testing_data.instance(i).classValue()];
			// 	System.out.println(testing_data.instance(i)+" index "+(int) testing_data.instance(i).classValue());
			WPLL+=Math.log(prob) / Math.log(2);
			
			for(int j=0;j<prob_dist.length;j++){
		   		System.out.print(prob_dist[j]+" ");

		   	}
		   	System.out.println();
			
		}
		result.setWPLL(WPLL/testing_data.numInstances());
		System.out.println("WPLL "+WPLL);
		bfw.close();
		return result;

	}

	private static boolean isBooleanPredicate(Attribute classAttribute) {
		if(classAttribute.numValues()>2 || classAttribute.numValues()<=1 ){
			return false;
		}
		if(classAttribute.value(0).equals("true") && classAttribute.value(1).equals("false")){
			return true;
		}

		return false;
	}
	
	public static double getGaussianDensity(double value,double mean,double std){
		double norm = 1 / (Math.sqrt(2 * Math.PI) * std);
		double expon = Math.pow((value - mean), 2) / (2 * Math.pow(std,2));
		return norm * Math.exp(-expon);
	}
	

	class Results{
		double weighted_AUC;
		Classifier cls;
		Evaluation eval;
		double NRMSE;
		double WPLL;

		public void setWeighted_AUC(double auc){
			this.weighted_AUC=auc;
		}

		public void setNRMSE(double nRMSE) {
			this.NRMSE=nRMSE;

		}
		
		public void setWPLL(double wpll) {
			this.WPLL=wpll;

		}
		
		public double getWPLL() {
			return this.WPLL;

		}


		public double getNRMSE() {
			return this.NRMSE;

		}

		public void setClassifier(Classifier cls) {
			this.cls=cls;

		}
		public void setEvaluation(Evaluation eval) {
			this.eval=eval;

		}

		public Evaluation getEvaluator(){
			return this.eval;
		}

		public Classifier getClassifier(){
			return cls;
		}

		public double getWeightedAUC(){
			return this.weighted_AUC;
		}

	}

}
*/