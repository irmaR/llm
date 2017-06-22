import hybrid.dependencies.Dependency;
import hybrid.network.Atom;
import hybrid.network.Predicate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;


public class Script_weka_propositionalized_PKDD_discretized {
}
/*

	//Argument1: path to resultse e.g.: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/HRDN_New_Implementation_results/PKDD/PKDD_Arff_Simple/
    //Argument2: path containing data: /cw/dtaijupiter/NoCsBack/dtai/irma/New_Experiments_July/General_DATA/PKDD_full_space/
	//Argument3: discretization level e.g. 2
	
	public static void main(String[] args) throws Exception{
		String path_to_data=args[0];
		String path_to_results=args[1];
		int[] discretizations=new int[]{Integer.valueOf(args[2]).intValue()};
		int nr_runs=10;
		String result_name="log";
		//String[] predicates=new String[]{"clientAge","avgSalary","gender","clientDistrict","hasLoan","loanAmount","loanStatus","monthlyPayments","freq","avgNrWith","avgSumOfW","avgSumOfInc","stdMonthInc","stdMonthW","ratUrbInhab","hasAccount"};
		String[] predicates=new String[]{"clientAge","avgSumOfInc"};

		//String[] predicates=new String[]{"hasLoan"};

		HashMap<String,ArrayList<Double>> AUCs_for_predicates=new HashMap<String,ArrayList<Double>>();
		for(String predicate:predicates){
			for(int k=0;k<discretizations.length;k++){
				Create_Propositionalized_PKDD_simpler propositionalizedPKK=new Create_Propositionalized_PKDD_simpler();
				HashMap<Predicate,Dependency> propos_dependencies =propositionalizedPKK.getDependenciesForPropositionalizations(discretizations[k],1);

				ArrayList<Double> AUCs_per_fold=new ArrayList<Double>();
				String all_folds_output=path_to_results+"/"+result_name+"/pkddDiscrLevel"+discretizations[k]+"/";

				for(int i=1;i<=nr_runs;i++){
					System.out.println("Run : "+i);
					String input_train_data=path_to_data+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/train/"+predicate+".arff";
					String input_test_data=path_to_data+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/test/"+predicate+".arff";
					String test_groundAtoms=path_to_data+"pkddDiscrLevel"+discretizations[k]+"/Fold"+i+"/test/"+predicate+"_test.arff";

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

					//run J48 algorithm
					String output_results=all_folds_output+"/Fold"+i+"/"+predicate+"/j48_results.output";
					String instance_probs_output=all_folds_output+"/Fold"+i+"/auc/"+"/probs_"+predicate;
					String instance_auc_output=all_folds_output+"/Fold"+i+"/auc/"+"/auc_"+predicate;

					File tmp_1=new File(instance_probs_output);
					tmp_1.getParentFile().mkdirs();
					
					File tmp_2=new File(instance_auc_output);

					String auc_probs_file=all_folds_output+"/Fold"+i+"/"+predicate+"/auc_probs_file/";

					File tmp=new File(output_results);
					tmp.getParentFile().mkdirs();
					Results res=null;
					try{
						
						res=runJ48DecisionTree(training_data, test_data, output_results,test_groundAtoms,instance_probs_output);
						
					}
					catch(Exception e){
						System.out.println(" Problem? ");
						e.printStackTrace();
						continue;
					}
					
					System.out.println("Returned WAUC: "+res.getWeightedAUC());
					FileWriter fw=new FileWriter(tmp_2);
					fw.append(res.getWeightedAUC()+"");
					fw.close();
					AUCs_per_fold.add(res.getWeightedAUC());
				}
				File output_w_aucs=new File(all_folds_output+predicate+"_all_folds.output");
				//output_w_aucs.createNewFile();
				if(output_w_aucs.exists()){
					output_w_aucs.delete();
				}
				FileWriter fw=new FileWriter(output_w_aucs,false);
				int i=0;
				double sum=0.0;
				for(Double auc:AUCs_per_fold){
					fw.append("Fold:"+i+" "+auc+"\n");
					sum+=auc;
					i++;
				}
				fw.append("AVERAGE W-AUC: "+(sum/AUCs_per_fold.size()));
				fw.close();

			}

			//AUCs_for_predicates.put(predicate, AUCs_per_fold);
		}

	}


	public static Results runJ48DecisionTree(Instances training_data,Instances testing_data,String results_output_folder, String test_groundAtoms,String path_to_prob_output) throws Exception{
		BufferedWriter bfw=new BufferedWriter(new FileWriter(path_to_prob_output));
		BufferedReader reader_test_groundAtoms = new BufferedReader(new FileReader(test_groundAtoms));
		System.out.println("Read test ground atoms");

		Script_weka_propositionalized_PKDD_discretized tmp=new Script_weka_propositionalized_PKDD_discretized();   
		Results result=tmp.new Results();
		FileWriter fw=new FileWriter(results_output_folder);
		//System.out.println("Training data:\n"+training_data);
		Classifier cls = new Logistic();
		cls.setOptions(new String[]{"-A"});
		cls.buildClassifier(training_data);
		System.out.println("Built classifier");


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
		//System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		//System.out.println("Weighted AUC " +result.getWeightedAUC());
		//calculating WPLL
		//System.out.println(testing_data.classAttribute());

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

		public void setWeighted_AUC(double auc){
			this.weighted_AUC=auc;
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