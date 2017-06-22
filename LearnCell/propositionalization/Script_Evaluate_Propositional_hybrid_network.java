package propositionalization;
import hybrid.dependencies.Dependency;
import hybrid.network.Predicate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.Instances;


public class Script_Evaluate_Propositional_hybrid_network {
	//Argument1: path to resultse e.g.: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/HRDN_New_Implementation_results/PKDD/PKDD_Arff_Simple/
		//Argument2: path containing data: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/
		//Argument3: regression algorithm - "mp5" or "lr" for linear regression
	   
		
		public static void main(String[] args) throws Exception{
			String path_to_data=args[0];
			String path_to_results=args[1];
			String regression_algorithm=args[2];
			String result_folder_name="logistic_"+regression_algorithm;
			int nr_runs=10;
			
			
			String[] predicates=new String[]{"cell_doublingtime","cell_lengthatbirth","cell_avglength","cell_lengthgrowthrate","cell_age","cell_oldpole","parent"};


			ArrayList<String> discrete_predicates=new ArrayList<String>();
			discrete_predicates.add("parent");
			discrete_predicates.add("cell_age");
			discrete_predicates.add("cell_oldpole");


			ArrayList<String> cont_predicates=new ArrayList<String>();
			cont_predicates.add("cell_doublingtime");
			cont_predicates.add("cell_lengthatbirth");
			cont_predicates.add("cell_avglength");
			cont_predicates.add("cell_lengthgrowthrate");
			



			//String[] predicates=new String[]{"hasLoan"};

			HashMap<String,ArrayList<Double>> AUCs_for_predicates=new HashMap<String,ArrayList<Double>>();
			for(String predicate:predicates){
				//HashMap<Predicate,Dependency> propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(1);

				ArrayList<Double> AUCs_per_fold=new ArrayList<Double>();
				ArrayList<Double> RMSE_per_fold=new ArrayList<Double>();
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
					}

				}
				File output_w_aucs=new File(all_folds_output+predicate+"_all_folds.output");
				//output_w_aucs.createNewFile();
				if(output_w_aucs.exists()){
					output_w_aucs.delete();
				}
				FileWriter fw=new FileWriter(output_w_aucs,false);
				int i=0;
				double sum=0.0;

				if(AUCs_per_fold.size()!=0){
					for(Double auc:AUCs_per_fold){
						fw.append(auc+"\t");
						sum+=auc;
						i++;
					}
					fw.append("AVERAGE W-AUC: "+(sum/AUCs_per_fold.size()));
				}

				else{
					for(Double rmse:RMSE_per_fold){
						fw.append(rmse+"\t");
						sum+=rmse;
						i++;
					}
					fw.append("AVERAGE NRSME: "+(sum/RMSE_per_fold.size()));
				}


				fw.close();
			}


		}

		//AUCs_for_predicates.put(predicate, AUCs_per_fold);
		private static Results buildRegressionClassifierMP5(Instances training_data,Instances test_data, String output_results,String test_groundAtoms, String instance_probs_output, Random ran) throws Exception {
			Script_Evaluate_Propositional_hybrid_network tmp=new Script_Evaluate_Propositional_hybrid_network();   
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
			
			
			
			int nr_data_points=0;
			for(int i=0;i<test_data.numInstances();i++){
				nr_data_points++;
				double prediction=regr.classifyInstance(test_data.instance(i));
				double true_value=test_data.instance(i).classValue();
				//System.out.println(test_data.instance(i)+ " prediction "+regr.classifyInstance(test_data.instance(i)));
				double mean=prediction;
				double random_nr=ran.nextGaussian();
				double normalized_prediction=prediction;	
				//System.out.println("True value: "+true_value+" predicted value: "+prediction+ " rmse: ");
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
			System.out.println("Nr instances me: "+nr_data_points);
			//System.out.println(rmse_m5p);
			//System.out.println("data points: "+nr_data_points);
			//System.out.println(" Nr instances "+ test_data.numInstances());
			double Unrmse=Math.sqrt(rmse/test_data.numInstances());	
			System.out.println("RMSE "+Unrmse);
			double NRMSE=Unrmse/(max_target_value-min_target_value);
			System.out.println("UNRMSE: "+Unrmse);
			System.out.println("Nrmse "+NRMSE);
			result.setNRMSE(NRMSE);

			fw.append(eval.toSummaryString("\nResults\n======\n", false));
			fw.close();
			return result;
		}

		private static Results buildRegressionClassifierLinearRegression(Instances training_data,Instances test_data, String output_results,String test_groundAtoms, String instance_probs_output, Random ran) throws Exception {
			Script_Evaluate_Propositional_hybrid_network tmp=new Script_Evaluate_Propositional_hybrid_network();   
			Results result=tmp.new Results();
			double max_target_value=0;
			double min_target_value=Double.POSITIVE_INFINITY;
			double rmse=0;

			FileWriter fw=new FileWriter(output_results);
			LinearRegression regr=new LinearRegression();
			//regr.setOptions(new String[]{"-C"});
			regr.buildClassifier(training_data);
			fw.append(regr+"\n");
			
			Evaluation eval = new Evaluation(training_data);
			eval.evaluateModel(regr, test_data);
			double rmse_m5p=eval.rootMeanSquaredError();
			double noise_training_data=Math.sqrt(training_data.variance(training_data.classAttribute()));
			
			for(int i=0;i<test_data.numInstances();i++){
				double prediction=regr.classifyInstance(test_data.instance(i));
				double true_value=test_data.instance(i).classValue();
				double mean=prediction;
				double random_nr=ran.nextGaussian();
				//double normalized_prediction=random_nr*noise_training_data+mean;
				double normalized_prediction=prediction;
				//System.out.println("norm: "+normalized_prediction+" true; "+true_value);
				
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

			fw.append(eval.toSummaryString("\nResults\n======\n", false));
			fw.close();
			return result;
		}






		public static Results buildDiscreteClassifier(Instances training_data,Instances testing_data,String results_output_folder, String test_groundAtoms,String path_to_prob_output) throws Exception{
			BufferedWriter bfw=new BufferedWriter(new FileWriter(path_to_prob_output));
			BufferedReader reader_test_groundAtoms = new BufferedReader(new FileReader(test_groundAtoms));
			Script_Evaluate_Propositional_hybrid_network tmp=new Script_Evaluate_Propositional_hybrid_network();   
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
				/*for(int j=0;j<prob_dist.length;j++){
			   		System.out.print(prob_dist[j]+" ");

			   	}
			   	System.out.println();*/
			}
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

		class Results{
			double weighted_AUC;
			Classifier cls;
			Evaluation eval;
			double NRMSE;

			public void setWeighted_AUC(double auc){
				this.weighted_AUC=auc;
			}

			public void setNRMSE(double nRMSE) {
				this.NRMSE=nRMSE;

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
