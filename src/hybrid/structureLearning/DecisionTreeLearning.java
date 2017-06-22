package hybrid.structureLearning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.rits.cloning.Cloner;

import hybrid.converters.DC_converter;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.experimenter.LearnedDependencyStatistics;
import hybrid.featureGenerator.FeatureGeneratorAbstract;
import hybrid.features.Feature;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.NetworkInfo;
import hybrid.network.Value;
import hybrid.parameters.Parameters;
import hybrid.penalties.NoPenalty;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.querydata.QueryDataFilter;
import hybrid.tocsvmodule.DataToCSV;
import hybrid.utils.CVFoldPair;
import hybrid.utils.DavideOutputFiles;
import hybrid.utils.MakeDependencyName;

public class DecisionTreeLearning extends StructureSearch{

	String output_directory=System.getProperty("user.dir");
	private boolean output_query_data=false;
	private DTDependencySelection dependdencySelection;


	public DecisionTreeLearning(DTDependencySelection dependencySelection){
		this.dependdencySelection=dependencySelection;
	}


	public boolean isOutput_query_data() {
		return output_query_data;
	}

	public void setOutput_query_data(boolean output_query_data) {
		this.output_query_data = output_query_data;
	}

	public void setOutputDirectory(String output_directory1){
		this.output_directory=output_directory1;
		System.out.println("Will write to: "+this.output_directory);
	}

	public Tree performSearchForAtom(Atom a,List<Feature> features, QueryMachine query_machine_training,QueryMachine query_machine_validation,QueryMachine query_machine_test) {
		this.dependdencySelection.reset();
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
		QueryData testData=query_machine_test.getQueryResults(bestScore.getDep());
		StructureScore testLL=bestScore.getDep().getCpd().getCpdEvaluator().calculatePLL(testData,bestScore.getDep().getCpd().getParameters(),new NoPenalty());
		bestScore.setTestScore(testLL.getScore());
		currentScore.setError_training_data(error_training);
		currentScore.setError_validation_data(error_validation);
		currentScore.setError_test_data(error_test);
		currentScore.setNormalizedTestError(error_test/testData.getNr_groundings_for_head());

		System.out.println("INIT DEP: "+dep_independent);
		System.out.println("INITIAL DEP ERRORS: "+"[ "+error_training+" , "+error_validation+"]");
		Node<DecisionTreeData> root_node=new Node<DecisionTreeData>(new DecisionTreeData(new ValueFt(),init_training, init_validation, currentScore,new HashMap<Feature,Value>()));
		root_node.get_data().setTestScore(testLL.getScore());
		System.out.println("INITIAL NODE: "+root_node+" cl: "+root_node.getClass());
		System.out.println("DEPENDENCY INITIAL ROOT NODE: "+((DecisionTreeData)root_node.get_data()).getDependency().getDep());
		Tree dec_tree=new Tree<DecisionTreeData>(root_node,new DC_converter());
		processChild(root_node,a,features,query_machine_training,query_machine_validation,query_machine_test);
		return dec_tree;
	}

	public void processChild(Node<DecisionTreeData> node,Atom a,List<Feature> fts,QueryMachine query_machine_training,QueryMachine query_machine_validation,QueryMachine query_machine_test){
		List<HashMap<Feature,Value>> mappings= getValueMappings(node);
		//System.out.println("MAPPING: "+mappings);
		//from this node doins all the branchings
		for(HashMap<Feature,Value> m:mappings){
			//System.out.println("************ BRANCH: *****************"+m);
			//System.out.println(" PARENT: "+node);
			//System.out.println("Test Score per branch for this node: "+node.get_data().getDependency().getTestScores());
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
			//System.out.println("Nr training points after this branch: "+training_filtered.getNr_groundings_for_head());
			//System.out.println("Nr validation points after this branch: "+validation_filtered.getNr_groundings_for_head());
			LearnedDependency dep=null;
			node.get_data().setTestDataPoints(m.get(node.get_data().getThis_feature()), test_filtered.getNr_groundings_for_head());
			node.get_data().setTrainingDataPoints(m.get(node.get_data().getThis_feature()), training_filtered.getNr_groundings_for_head());
			node.get_data().setValidationDataPoints(m.get(node.get_data().getThis_feature()), validation_filtered.getNr_groundings_for_head());
			Double comparisonScore=null;
			//comparisonScore=node.get_data().getDependency().getDep().getCpd().getCpdEvaluator().calculatePLL(validation_filtered, node.get_data().getDependency().getDep().getCpd().getParameters(), query_machine_validation.getPenalty()).getScore();

			comparisonScore=node.get_data().getScores().get(m.get(node.get_data().getDependency().getNewest_added_feature()));
			//System.out.println(node.get_data().getScores());
			if(comparisonScore==null){
				//System.out.println("Warning!");
				/*System.exit(1);*/
				comparisonScore=node.get_data().getScore();
			}
			//System.out.println("------------ PARENT SCORE: ----------- "+comparisonScore);
			//if(enough_training_examples){
			//System.out.println(" There are enough training examples ");
			//System.out.println("QUERY DATA USED FOR SCORE: "+node.get_data().getDependency().getValidation_data());
			if(comparisonScore!=Double.NEGATIVE_INFINITY){
				//System.out.println("===========================================================");
				//System.out.println(" Learning best dependency starting from :\n"+node.get_data().getDependency().getDep()+" \n and branch: \n"+m+" and it's score: \n"+comparisonScore);
				//System.out.println("NON FILTERED SCORE: "+comparison_score1);
				try {
					dep=dependdencySelection.selectBestDependency(a,node.get_data().getDependency(),fts,query_machine_training,query_machine_validation,query_machine_test,m,query_machine_validation.getPenalty(),comparisonScore);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(dep);
				if(dep!=null && dep.isNew_feature_added()){
					System.out.println("------ DEP LEARNED ----------------------------------");
					//System.out.println("Score: "+dep.getStructure_score());
					//System.out.println("Test Score: "+dep.getTestDataScore());
				}
			}			
			//}
			if(dep!=null && dep.isNew_feature_added()){
				//System.out.println("Adding NEW FEATURE "+dep.getNewest_added_feature());
				Node<DecisionTreeData> child_node=new Node<DecisionTreeData>(new DecisionTreeData(dep.getNewest_added_feature(),training_filtered, validation_filtered, dep,m));
				//System.out.println("NEW NODE CREATED ----------- \n: "+child_node);
				//System.out.println("--------------------------------------------");
				//System.out.println("-------- PARENT IS: -------------------"+node+"--------------------");
				node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),dep.getError_test_data());
				node.get_data().setNormalizedTestError(m.get(node.get_data().getThis_feature()),dep.getNormalizedTestError());
				node.get_data().setNormalizedTestScore(m.get(node.get_data().getThis_feature()),dep.getNormalizedTestScore());
				node.get_data().setErrorTraining(m.get(node.get_data().getThis_feature()),dep.getError_training_data());
				node.get_data().setErrorValidation(m.get(node.get_data().getThis_feature()),dep.getError_validation_data());
				for(Value v:dep.getScorePerBranch().keySet()){
					child_node.get_data().setScore(v, dep.getScorePerBranch().get(v));
				}
				node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparisonScore);
				//System.out.println("Setting score of: "+node+"to : "+comparisonScore);
				//System.out.println("------------------------------------------------");
				node.get_data().setTestDataScore(m.get(node.get_data().getThis_feature()), dep.getTestDataScore());
				child_node.get_data().setTestScore(dep.getTestDataScore());
				//System.out.println("setting test error to: "+dep.getError_test_data());
				//System.out.println("setting training error to: "+dep.getError_training_data());
				//System.out.println("setting validation error to: "+dep.getError_validation_data());
				//System.out.println("Setting test data of this node to: "+new Double(dep.getTestDataScore()));
				child_node.setTest_error_data(dep.getError_test_data());
				node.addChild(child_node);
				child_node.setParent(node);
				processChild(child_node,a,fts,query_machine_training,query_machine_validation,query_machine_test);
			}

			else if(!enough_training_examples || (dep!=null && !dep.isNew_feature_added())){
				System.out.println("\n ///////////// LEAF /////////////////");
				//System.out.println("BRACH INFO: "+dep.getBranchInfo());
				
				Cloner cloner=new Cloner();
				LearnedDependency clonedDep=cloner.deepClone(node.get_data().getDependency());
				//clonedDep.setBranchInfo(dep.getBranchInfo());
				DecisionTreeData d=new DecisionTreeData(node.get_data().getThis_feature(),training_filtered, validation_filtered,clonedDep,m);
				
				//System.out.println("DEPENDENCY IN THIS LEAF: "+clonedDep);
				//System.out.println("TEST LL: "+clonedDep.getTestDataScore());
				//System.out.println("VALIDATION SCORE: "+clonedDep.getScore());
				//System.out.println(" TEST DATA NR DATA POINTS:"+test_filtered.getNr_groundings_for_head());
				//System.out.println(node.get_data().getThis_feature());	
				if(node.get_data().getThis_feature().isDiscreteOutput()){
					Value v=m.get(node.get_data().getThis_feature());
					//System.out.println(node.get_data().getThis_feature()+" = "+m.get(node.get_data().getThis_feature()));
					System.out.println(node.get_data().getDependency().getTestErrorPerBranch());
					node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().getTestErrorPerBranch().get(v));
					node.get_data().setNormalizedTestError(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().getNormalizedTestErrorPerBranch().get(v));
					node.get_data().setNormalizedTestScore(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().getNormalizedTestScorePerBranch().get(v));
					node.get_data().setNumberOfTestRandvars(test_filtered.getNr_groundings_for_head());
					node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparisonScore);
					node.get_data().setBranchInfo(m.get(node.get_data().getThis_feature()),dep.getBranchInfo());
					//System.out.println("Original DEP: "+node.get_data().getDependency());
					//System.out.println("CLONED DEP: "+clonedDep);
					//System.out.println("SCORES STORED IN PARENT: "+node.get_data().getScores());
					//System.out.println("TEST SCORES STORED IN PARENT: "+node.get_data().getTestSetScores());
					//System.out.println("THIS FEATURE: "+node.get_data().getThis_feature());
					clonedDep.setScore(node.get_data().getDependency().getScorePerBranch().get(m.get(node.get_data().getThis_feature())));
					clonedDep.setTestScore(node.get_data().getDependency().getTestScores().get(m.get(node.get_data().getThis_feature())));
					clonedDep.setError_test_data(node.get_data().getDependency().getTestErrorPerBranch().get(m.get(node.get_data().getThis_feature())));
					clonedDep.setNormalizedTestError(node.get_data().getDependency().getNormalizedTestErrorPerBranch().get(m.get(node.get_data().getThis_feature())));
					clonedDep.setNormalizedTestScore(node.get_data().getDependency().getNormalizedTestScorePerBranch().get(m.get(node.get_data().getThis_feature())));
                    //clonedDep.setError_validation_data(node.get_data().getDependency().getTestErrorPerBranch().get(m.get(node.get_data().getThis_feature())));
					clonedDep.setBranchInfo(dep.getBranchInfo());
					//System.out.println("THIS DEP: "+clonedDep.getDep());
					//System.out.println("Set score in dependency: "+clonedDep.getScore());
					//System.out.println("Test error in dependency: "+clonedDep.getError_test_data());
				}
				else{
					node.get_data().setErrorTest(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().getError_test_data());
					node.get_data().setErrorValidation(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().getError_validation_data());
					node.get_data().setNormalizedTestError(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().normalizedTestError);
					node.get_data().setNormalizedTestScore(m.get(node.get_data().getThis_feature()),node.get_data().getDependency().normalizedTestScore);
					node.get_data().setNumberOfTestRandvars(test_filtered.getNr_groundings_for_head());
					node.get_data().setScore(m.get(node.get_data().getThis_feature()), comparisonScore);
					//System.out.println("LEA:"+"Validation error: "+node.get_data().getDependency().getError_validation_data());
					node.get_data().setBranchInfo(m.get(node.get_data().getThis_feature()),dep.getBranchInfo());
				}
				if(AlgorithmParameters.getOutputScriptResults()){
					//System.out.println("writing data file");
					//System.out.println(DavideOutputFiles.getDataCSV().keySet());
					
					if(DavideOutputFiles.getDataCSV().containsKey(MakeDependencyName.makeName(clonedDep.getDep(), m))){
						String identifier=MakeDependencyName.makeName(clonedDep.getDep(),m);
						File data=new File(AlgorithmParameters.output_path+"/data_"+a.getPredicate().getPredicateName()+identifier);
						BufferedWriter fw;
						try {
							fw = new BufferedWriter(new FileWriter(data));
							fw.append(DavideOutputFiles.getDataCSV().get(identifier));
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}
					if(DavideOutputFiles.getModelCSV().containsKey(MakeDependencyName.makeName(clonedDep.getDep(),m))){
						String identifier=MakeDependencyName.makeName(clonedDep.getDep(),m);
						File data=new File(AlgorithmParameters.output_path+"/model_"+a.getPredicate().getPredicateName()+identifier);
						BufferedWriter fw;
						try {
							fw = new BufferedWriter(new FileWriter(data));
							fw.append(DavideOutputFiles.getModelCSV().get(identifier));
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(DavideOutputFiles.getTestCSV().containsKey(MakeDependencyName.makeName(clonedDep.getDep(), m))){
						String identifier=MakeDependencyName.makeName(clonedDep.getDep(),m);
						File data=new File(AlgorithmParameters.output_path+"/test_"+a.getPredicate().getPredicateName()+identifier);
						BufferedWriter fw;
						try {
							fw = new BufferedWriter(new FileWriter(data));
							fw.append(DavideOutputFiles.getTestCSV().get(identifier));
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
				}
				//System.out.println("Mapping: "+m);
				//System.out.println("NODE : \n"+node);
				//System.out.println("---------------------------------------");
				LeafNode<DecisionTreeData> child_node=new LeafNode<DecisionTreeData>(d);
				//System.out.println("setting test error to: "+node.get_data().getDependency().getTestErrorPerBranch());
				//child_node.setTest_error_data(dep.getError_test_data());
				child_node.setIsLeaf();
				//System.out.println("Mapping :"+m);
				//System.out.println(m.get(node.get_data().getThis_feature()));
				//System.out.println(node.get_data().getThis_feature());
				//System.out.println("SCORES SET TO PARENT: "+node.get_data().getScores());
		        //System.out.println("Adding child node: "+child_node+"\n *****************************");
				//System.out.println("NODE: "+child_node.get_data().getDependency().getBranchInfo());
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





	public LearnedDependency select_best_dependency(Atom a,LearnedDependency learned_dep,List<Feature> fts,QueryMachine query_data_training,QueryMachine query_data_validation,QueryMachine query_data_test,HashMap<Feature,Value> filter,Penalty pen,double parent_score){	
		LearnedDependency bestScore=learned_dep;
		List<LearnedDependency> extension_scores=null;
		extension_scores=new ArrayList<LearnedDependency>();
		System.out.println("LEARNING A NEW DEPENDENCY WITH A FILTER:\n "+filter+"\n --------------------------------------- ");
		boolean no_feature_possible_to_add=true;
		Feature best_feature=null;
		for(Feature ft:fts){
			Dependency dep_temp;
			try {
				//System.out.println("EXTENDING DEP: "+learned_dep.getDep()+" WITH : "+ft);
				dep_temp= learned_dep.getDep().extend(ft);
				no_feature_possible_to_add=false;
			} catch (FeatureAlreadyExists e1) { //feature already exists catch
				continue;
			}
			//System.out.println("---------- ESTIMATING PARAMETERS: --------------");
			QueryDataFilter qd_filter=new QueryDataFilter();
			QueryData est_training_data=query_data_training.getQueryResults(dep_temp);
			QueryData filtered_est_training=qd_filter.filterQueryData(dep_temp,filter,est_training_data);
			dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));
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
			dep_temp.getCpd().setParameters(dep_temp.getCpd().getCpdEvaluator().estimateParameters(filtered_est_training));
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
			try {
				BufferedWriter fw=new BufferedWriter(new FileWriter(AlgorithmParameters.output_path+"/"+dataToCSVConverting.createNameForBranchData(bestScore.getDep(),filter)+".csv"));
				fw.append(dataToCSVConverting.dataToCSVFile(bestScore.getTraining_data(), bestScore.getDep(), filter,false));
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bestScore;
		}
		else{
			System.out.println("No new feature added:");
			bestScore.setNewFeatureLearned(false);
			return bestScore;
		}
	}




	public DTDependencySelection getDependdencySelection() {
		return dependdencySelection;
	}


	public void setDependdencySelection(DTDependencySelection dependdencySelection) {
		this.dependdencySelection = dependdencySelection;
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
		System.out.println("---------- PARAMETER ESTIMATION DONE -------------- FOR "+a);
        //getting score of the independent model
		hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency parameters " +dep_independent.getCpd().getParameters());
		System.out.println("---------- CALCULATING THE SCORE PARAMETERS MARGINAL: --------------");
		double score=0;
		List<CVFoldPair> pairs=data_marginal.splitIntoFolds(5);
		if(AlgorithmParameters.getInternalCV()){
			for(CVFoldPair c:pairs){
				QueryData trainingCV=c.getTraining();
				QueryData validationCV=c.getValidation();
				Parameters p=dep_independent.getCpd().getCpdEvaluator().estimateParameters(trainingCV);
				dep_independent.getCpd().setParameters(p);
				score+=dep_independent.getCpd().getCpdEvaluator().calculatePLL(validationCV, dep_independent.getCpd().getParameters(), query_machine_validation.getPenalty()).getScore();
			}
			score=score/5;
			//parameters are estimated on all the data
			dep_independent.getCpd().setParameters(dep_independent.getCpd().getCpdEvaluator().estimateParameters(data_marginal));
		}
		else{
			dep_independent.getCpd().setParameters(dep_independent.getCpd().getCpdEvaluator().estimateParameters(data_marginal));
			score=dep_independent.getCpd().getCpdEvaluator().calculatePLL(query_machine_validation.getQueryResults(dep_independent), dep_independent.getCpd().getParameters(),query_machine_validation.getPenalty()).getScore();
		}
		System.out.println("---------- SCORING COMPLETED: -------------- "+a);
		hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency " +dep_independent);
		return new LearnedDependency(dep_independent, score);
	}



}
