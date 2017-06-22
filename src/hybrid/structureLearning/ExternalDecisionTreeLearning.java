package hybrid.structureLearning;

import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.Value;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.tocsvmodule.DataToCSV;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class with which we estimate paramters with external applications
 * Developed For Davide's robotics experiments
 * @author irma
 *
 */
public class ExternalDecisionTreeLearning extends StructureSearch {
	String output_directory=System.getProperty("user.dir");
	String pathToExternalApplicationExecutable=null;

	public ExternalDecisionTreeLearning(String pathToExternalApplicationExecutable){
		this.pathToExternalApplicationExecutable=pathToExternalApplicationExecutable;
	}

	public void setOutputDirectory(String output_directory1){
		this.output_directory=output_directory1;
		System.out.println("Will write to: "+this.output_directory);
	}

	public Tree performSearchForAtom(Atom a,List<Feature> features, QueryMachine query_machine_training,QueryMachine query_machine_validation,QueryMachine query_machine_test) {
		//first determine score with empty parent set
		System.out.println("Will write to: "+this.output_directory);
		Dependency dep_independent=new Dependency(a,new Feature[]{});
		LearnedDependency bestScore=getParsAndScoreMarginal(a,query_machine_training,query_machine_validation,dep_independent);
		System.out.println("Marginal score: "+bestScore);
		System.out.println("Pars: "+bestScore.getDep().getCpd().getParameters());
		LearnedDependency currentScore=new LearnedDependency(dep_independent,bestScore.getScore());
		List<LearnedDependency> extension_scores=null;

		//initial data
		QueryData init_training=query_machine_training.getQueryResults(dep_independent);
		QueryData init_validation=query_machine_validation.getQueryResults(dep_independent);
		QueryData init_test=query_machine_test.getQueryResults(dep_independent);
		double error_training=dep_independent.getCpd().getCpdEvaluator().getUnnormalizedError(init_training, dep_independent.getCpd().getParameters(),false);
		double error_validation=dep_independent.getCpd().getCpdEvaluator().getUnnormalizedError(init_validation, dep_independent.getCpd().getParameters(),false);
		double error_test=dep_independent.getCpd().getCpdEvaluator().getUnnormalizedError(init_test, dep_independent.getCpd().getParameters(),false);
        
		currentScore.setError_training_data(error_training);
		currentScore.setError_validation_data(error_validation);
		currentScore.setError_test_data(error_test);
        
		System.out.println("INIT DEP: "+dep_independent);
		System.out.println("INITIAL DEP ERRORS: "+"[ "+error_training+" , "+error_validation+"]");
		Node root_node=new Node<DecisionTreeData>(new DecisionTreeData(new ValueFt(),init_training, init_validation, currentScore,new HashMap<Feature,Value>()));
		System.out.println("DEPENDENCY INITIAL ROOT NODE: "+((DecisionTreeData)root_node.get_data()).getDependency().getDep());
		Tree dec_tree=new Tree<DecisionTreeData>(root_node,new DC_converter());
		//if(currentScore.isNew_feature_added()){
		processChild(root_node,a,features,query_machine_training,query_machine_validation,query_machine_test);
		//}
		return dec_tree;
	}

	public void processChild(Node<DecisionTreeData> node,Atom a,List<Feature> fts,QueryMachine query_machine_training,QueryMachine query_machine_validation,QueryMachine query_machine_test){
		List<HashMap<Feature,Value>> mappings= getValueMappings(node);
		System.out.println("MAPPING: "+mappings);
		//from this node doins all the branchings
		for(HashMap<Feature,Value> m:mappings){
			System.out.println("************ BRANCH: *****************"+m);
			System.out.println(" PARENT: "+node);
			for(Feature f:node.get_data().getDependency().getDep().getFeatures()){
				System.out.print(f.getIndexInFeatureSpace()+",");
			}
			System.out.println();
			System.out.println(" LATEST FEATURE VALUE: "+m.get(node.get_data().getThis_feature()));
			//filter data
			QueryData training_filtered=null;
			QueryData validation_filtered=null;
			QueryData test_filtered=null;
			QueryDataFilter qdf=new QueryDataFilter();
			training_filtered=qdf.filterQueryData(node.get_data().getDependency().getDep(),m,query_machine_training.getQueryResults(node.get_data().getDependency().getDep()));
			validation_filtered=qdf.filterQueryData(node.get_data().getDependency().getDep(),m,query_machine_validation.getQueryResults(node.get_data().getDependency().getDep()));
			test_filtered=qdf.filterQueryData(node.get_data().getDependency().getDep(),m,query_machine_test.getQueryResults(node.get_data().getDependency().getDep()));
			//check if enough training data to do learning for this node
			boolean enough_training_examples=true;
			if(training_filtered.getNr_groundings_for_head()<=4 || validation_filtered.getNr_groundings_for_head()<=4){
				enough_training_examples=false;
			}
			System.out.println("Nr training points after this branch: "+training_filtered.getNr_groundings_for_head());
			System.out.println("Nr validation points after this branch: "+validation_filtered.getNr_groundings_for_head());
			LearnedDependency dep=null;
			
			node.get_data().setTestDataPoints(m.get(node.get_data().getThis_feature()), test_filtered.getNr_groundings_for_head());
			node.get_data().setTrainingDataPoints(m.get(node.get_data().getThis_feature()), training_filtered.getNr_groundings_for_head());
			node.get_data().setValidationDataPoints(m.get(node.get_data().getThis_feature()), validation_filtered.getNr_groundings_for_head());
			Double comparison_score=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().calculatePLL(validation_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(), query_machine_validation.getPenalty()).getScore();
			if(enough_training_examples){
				//System.out.println("QUERY DATA USED FOR SCORE: "+node.get_data().getDependency().getValidation_data());
				if(comparison_score!=Double.NEGATIVE_INFINITY){
					System.out.println("===========================================================");
					System.out.println(" Learning best dependency starting from :\n"+node.get_data().getDependency().getDep()+" \n and branch: \n"+m+" and it's score: \n"+comparison_score);
					//System.out.println("NON FILTERED SCORE: "+comparison_score1);
					dep=selectBestDependencyWithDataOutput(a,node.get_data().getDependency(),fts,query_machine_training,query_machine_validation,query_machine_test,m,query_machine_validation.getPenalty(),comparison_score);
					System.out.println("------ DEP LEARNED -------:\n"+dep+"\n---------------------------");
					System.out.println("Score: "+dep.getStructure_score());
				}			
			}
			
			if(!enough_training_examples || dep==null){
				System.out.println(" Enough training examples? "+enough_training_examples);
				System.out.println("Dependency learned? "+dep);
				System.out.println("PARENT DEPENDENCY: "+node.getParent().get_data().getDependency());
				
				DecisionTreeData d=new DecisionTreeData(node.get_data().getThis_feature(),training_filtered, validation_filtered,node.get_data().getDependency(),m);
				Double test_error_of_parent=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),true);
				Double training_error_of_parent=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(training_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),true);
				Double validation_error_of_parent=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(validation_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),true);

				node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),test_error_of_parent);
				node.get_data().setErrorTraining(m.get(node.get_data().getThis_feature()),training_error_of_parent);
				node.get_data().setErrorValidation(m.get(node.get_data().getThis_feature()),validation_error_of_parent);
				node.get_data().setNumberOfTestRandvars(test_filtered.getNr_groundings_for_head());
				node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparison_score);
				

				//System.out.println("SET CHILD NODE ERROR TEST FOR THIS ONE AS: "+node.get_data().getTest_errors());
				LeafNode<DecisionTreeData> child_node=new LeafNode<DecisionTreeData>(d);
				System.out.println("HALOOOOOOOOOO setting test error to: "+test_error_of_parent);
				child_node.setTest_error_data(test_error_of_parent);
				child_node.setIsLeaf();
				node.addChild(child_node);
				child_node.setParent(node);
				continue;
			}
			if(dep!=null && dep.isNew_feature_added()){
				System.out.println("Adding NEW FEATURE "+dep.getNewest_added_feature());
				Node<DecisionTreeData> child_node=new Node<DecisionTreeData>(new DecisionTreeData(dep.getNewest_added_feature(),training_filtered, validation_filtered, dep,m));
				System.out.println("CHILD ADDED: "+child_node);
				node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),dep.getError_test_data());
				node.get_data().setErrorTraining(m.get(node.get_data().getThis_feature()),dep.getError_training_data());
				node.get_data().setErrorValidation(m.get(node.get_data().getThis_feature()),dep.getError_validation_data());
				node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparison_score);

				System.out.println("setting test error to: "+dep.getError_test_data());
				System.out.println("setting training error to: "+dep.getError_training_data());
				System.out.println("setting validation error to: "+dep.getError_validation_data());
				child_node.setTest_error_data(dep.getError_test_data());
				node.addChild(child_node);
				child_node.setParent(node);
				processChild(child_node,a,fts,query_machine_training,query_machine_validation,query_machine_test);
			}
			else{
				DecisionTreeData d=new DecisionTreeData(node.get_data().getThis_feature(),training_filtered, validation_filtered,node.get_data().getDependency(),m);
				System.out.println(" TEST DATA NR DATA POINTS:"+test_filtered.getNr_groundings_for_head());
				Double test_error_of_this_node=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(test_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),false);
				Double training_error_this_node=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(training_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),false);
				Double validation_error_this_node=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().getUnnormalizedError(validation_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(),false);

				System.out.println("TEST ERROR: "+test_error_of_this_node);
				System.out.println("Training ERROR: "+training_error_this_node);
				System.out.println("Validation ERROR: "+training_error_this_node);
				node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),test_error_of_this_node);
				node.get_data().setErrorTraining(m.get(node.get_data().getThis_feature()),training_error_this_node);
				node.get_data().setErrorValidation(m.get(node.get_data().getThis_feature()),validation_error_this_node);
				node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparison_score);
				LeafNode<DecisionTreeData> child_node=new LeafNode<DecisionTreeData>(d);
				System.out.println("setting test error to: "+test_error_of_this_node);
				child_node.setTest_error_data(test_error_of_this_node);
				child_node.setIsLeaf();
				node.addChild(child_node);
				child_node.setParent(node);
				continue;
			}
		}
	}

	public List<HashMap<Feature,Value>> getValueMappings(Node<DecisionTreeData> node){
		List<HashMap<Feature,Value>> tmp=new ArrayList<HashMap<Feature,Value>>();
		HashMap<Feature,Value> parent_mapping=new HashMap<Feature, Value>();
		parent_mapping=node.get_data().getValue_mapping();
		if(node.get_data().getBranching_values().size()!=0){
			for(Value v:node.get_data().getBranching_values()){
				HashMap<Feature,Value> t=new HashMap<Feature,Value>();
				for(Feature f:parent_mapping.keySet()){
					t.put(f, parent_mapping.get(f));
				}
				t.put(node.get_data().getThis_feature(),v);
				tmp.add(t);
			}
		}
		else{
			tmp.add(parent_mapping);
		}
		return tmp;
	}
	
	public LearnedDependency selectBestDependencyWithDataOutput(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score){	
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		DataToCSV dataToCSVConverting=new DataToCSV();
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
        boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		for(Feature ft:fts){
			Dependency dep_temp;
			try {
				dep_temp= learned_dep.getDep().extend(ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//System.out.println("---------- ESTIMATING PARAMETERS: --------------");
			QueryDataFilter qd_filter=new QueryDataFilter();
			QueryData est_training_data=query_data_training.getQueryResults(dep_temp);
			QueryData filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
			QueryData est_querie_tests=query_data_test.getQueryResults(dep_temp);
			QueryData test_filtered=qd_filter.filterQueryData(dep_temp,filter,est_querie_tests);
			//remove all the data
			File f = new File(AlgorithmParameters.output_path+"/"+"data.csv");
			if(f.exists() && !f.isDirectory()) { 
				f.delete();
			}
			File f1 = new File(AlgorithmParameters.output_path+"/"+"test.csv");
			if(f1.exists() && !f1.isDirectory()) { 
				f1.delete();
			}
			try {
				BufferedWriter fw=new BufferedWriter(new FileWriter(AlgorithmParameters.output_path+"/"+"data.csv"));
				fw.append(dataToCSVConverting.dataToCSVFile(filtered_est_training, dep_temp, filter,false));
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BufferedWriter fw=new BufferedWriter(new FileWriter(AlgorithmParameters.output_path+"/"+"test.csv"));
				fw.append(dataToCSVConverting.dataToCSVFile(test_filtered, dep_temp, filter,false));
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//remove model if it exists
			File f2 = new File(AlgorithmParameters.output_path+"/"+"model.csv");
			if(f2.exists() && !f2.isDirectory()) { 
				f2.delete();
			}
			//call script
			try {
				Process process = new ProcessBuilder(this.pathToExternalApplicationExecutable,"regression.py").start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().loadParametersFromCSV(AlgorithmParameters.output_path+"/"+"model.csv",dep_temp));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			double error_training=dep_temp.getCpd().getCpdEvaluator().getUnnormalizedError(filtered_est_training, dep_temp.getCpd().getParameters(),false);
			try{
				double pll=Double.NaN;
				QueryData est_validation_data=query_data_validation.getQueryResults(dep_temp);
				QueryData score_filtered=qd_filter.filterQueryData(dep_temp,filter,est_validation_data);
				StructureScore score=dep_temp.getCpd().getCpdEvaluator().calculatePLL(score_filtered,dep_temp.getCpd().getParameters(),pen);
				pll=score.getScore();
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
	/**
	 * Estimate parameters and getvscore for marginal
	 * @param a
	 * @param query_machine_training
	 * @param query_machine_validation
	 * @param dep_independent
	 * @return
	 */
	private LearnedDependency getParsAndScoreMarginal(Atom a,QueryMachine query_machine_training, QueryMachine query_machine_validation, Dependency dep_independent) {
		System.out.println("---------- ESTIMATING PARAMETERS MARGINAL: -------------- FOR "+a);
		QueryData data_marginal=query_machine_training.getQueryResults(dep_independent);

		dep_independent.getCpd().setParameters(dep_independent.getCpd().getCpdEvaluator().estimateParameters(data_marginal));
		System.out.println("---------- PARAMETER ESTIMATION DONE -------------- FOR "+a);

		//getting score of the independent model
		hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency parameters " +dep_independent.getCpd().getParameters());
		System.out.println("---------- CALCULATING THE SCORE PARAMETERS MARGINAL: --------------");
		double score=0;

		score=dep_independent.getCpd().getCpdEvaluator().calculatePLL(query_machine_validation.getQueryResults(dep_independent), dep_independent.getCpd().getParameters(),query_machine_validation.getPenalty()).getScore();

		System.out.println("---------- SCORING COMPLETED: -------------- "+a);
		hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency " +dep_independent);
		return new LearnedDependency(dep_independent, score);
	}



}
