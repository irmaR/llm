package hybrid.structureLearning;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.Coefficients;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.Parameters;
import hybrid.parameters.UndefinedAssignmentKey;
import hybrid.queryMachine.FeatureValue;
import hybrid.querydata.QueryData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTreeData implements Serializable {
	private Feature this_feature;
	private transient QueryData qd_training;
	private transient QueryData qd_validation;
	private LearnedDependency dependency;
	private List<Value> branching_values;
	private HashMap<Feature,Value> value_mapping;
	private double score;
	private Double testScore;
	private String node_label;
	private static int counter = 0;
	private int nr_data_points_training=0;
	private int nr_data_points_validation=0;
	private int nr_data_points_test=0;
	private HashMap<Value,Double> test_errors;
	private HashMap<Value,Double> normalizedTestErrors;
	private HashMap<Value,Double> training_errors;
	private HashMap<Value,Double> validation_errors;
	private HashMap<Value,Parameters> parameterBranch;
	private HashMap<Value,Integer> test_data_points;
	private HashMap<Value,Integer> training_data_points;
	private HashMap<Value,Integer> validation_data_points;

	private HashMap<Value,Double> scores;
	private HashMap<Value,Double> testSetScores;
	private HashMap<Value,Double> normalizedTestSetScores;
	private int numberOfTestInstances;
	private HashMap<Value,String> branchInfoValues;




	public DecisionTreeData(Feature this_feature,QueryData qd_training,QueryData qd_validation,LearnedDependency dep,HashMap<Feature,Value> value_mapping){
		this.this_feature=this_feature;
		this.qd_training=qd_training;
		this.qd_validation=qd_validation;
		this.dependency=dep;
		this.branching_values=new ArrayList<Value>();
		this.value_mapping=value_mapping;
		this.test_errors=new HashMap<Value,Double>();
		this.normalizedTestErrors=new HashMap<Value,Double>();
		this.training_errors=new HashMap<Value,Double>();
		this.validation_errors=new HashMap<Value,Double>();
		this.testSetScores=new HashMap<Value,Double>();
		this.normalizedTestSetScores=new HashMap<Value,Double>();
		this.test_data_points=new HashMap<Value,Integer>();
		this.training_data_points=new HashMap<Value,Integer>();
		this.validation_data_points=new HashMap<Value,Integer>();
        this.branchInfoValues=new HashMap<Value,String>();
		this.scores=new HashMap<Value, Double>();
		if(dep!=null){
			this.score=dep.getScore();
		}
		else{
			this.score=Double.NaN;
		}
		if(this_feature.getRange().isDiscreteRange()){
			for(Value v:((RangeDiscrete)this_feature.getRange()).getValues()){
				if(!branching_values.contains(v)){
					branching_values.add(v);
				}
				if(AlgorithmParameters.getUseUndefinedValue()){
					if(!this_feature.getRange().isBooleanRange()){
						if(!branching_values.contains(new UndefinedValue())){
							branching_values.add(new UndefinedValue());
						}
					}
				}
			}
		}
		try{
			this.nr_data_points_training=this.qd_training.getNr_groundings_for_head();
		}
		catch(NullPointerException e){
			this.nr_data_points_training=-1;
		}
		try{
			this.nr_data_points_validation=this.qd_validation.getNr_groundings_for_head();
		}
		catch(NullPointerException e){
			this.nr_data_points_validation=-1;
		}
	}


	public int getNr_datapoints_training(){
		return this.nr_data_points_training;
	}

	public int getNr_datapoints_validation(){
		return this.nr_data_points_validation;
	}

	public int getNr_datapoints_test(){
		return this.nr_data_points_test;
	}

	public int setNr_datapoints_test(int nr){
		return this.nr_data_points_test=nr;
	}

	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}



	public List<Value> getBranching_values() {
		return branching_values;
	}


	public void setBranching_values(List<Value> branching_values) {
		this.branching_values = branching_values;
	}


	public Feature getThis_feature() {
		return this_feature;
	}


	public void setThis_feature(Feature this_feature) {
		this.this_feature = this_feature;
	}


	public String getNodeInfo(Node<DecisionTreeData> node){
		Coefficients c=(Coefficients) node.get_data().dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues());
		String tmp=this.this_feature.toString().trim()+" ";

		if(c!=null){
			tmp+=node.get_data().dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues())+"";
		}
		else{
			tmp+="PARS: "+node.get_data().dependency.getDep().getCpd().getParameters()+"";
		}
		return tmp;
	}


	public double getTestError(Node<DecisionTreeData> node){
		if(node.isLeaf){
			return node.getTest_error_data();
		}
		else{
			return 0;
		}
	}

	public double getNormalizedTestError(Node<DecisionTreeData> node){
		if(node.isLeaf){
			System.out.println(node.get_data().getDependency().getNormalizedTestErrorPerBranch());
			System.out.println("LEAF: : "+node.get_data().getDependency().getNormalizedTestError());
			return node.get_data().getDependency().getNormalizedTestError();
		}
		else{
			return 0;
		}
	}

	public double getTestLL(Node<hybrid.structureLearning.DecisionTreeData> node) {
		if(node.isLeaf){
			return node.get_data().testScore;
		}
		else{
			return 0;
		}
	}



/*	public String getDigraphRepresentation(Node<DecisionTreeData> node){
		//System.out.println("------------ Parsing node: ---------------"+node);
		String result="";
		String this_feature="";
		String parent_identifier=null;
		String pars_parent_identifier=null;
		HashMap<Value,String> parameter_child_identifier=new HashMap<Value,String>();
		if(node.getIdentifier()!=null){
			parent_identifier=node.getIdentifier();
			pars_parent_identifier=node.getIdentifier().replace("F","P");
		}
		else{
			parent_identifier="F"+(counter);
			pars_parent_identifier="P"+(counter);
			node.setIdentifier(parent_identifier);
		}

		if(!node.isLeaf()){
			
			if(node.get_data().getThis_feature().isContinuousOutput()){
				this_feature=parent_identifier+" [label=\"cont_"+node.get_data().getThis_feature().toString().trim()+" // "+node.get_data().getNr_datapoints_training()+" Sc:"+node.get_data().getScore()+"\n Test LL: "+node.get_data().getOverallTestScore()+"\n"+node.get_data().getBranchInfo()+"\" color=Blue, shape=ellipse,style=filled,color=\".6 .2 1.0\"]\n";
			}
			else{
				this_feature=parent_identifier+" [label=\"discr_"+node.get_data().getThis_feature().toString().trim()+" // "+node.get_data().getNr_datapoints_training()+" Sc:"+node.get_data().getScore()+"\n Test LL: "+node.get_data().getOverallTestScore()+"\n"+node.get_data().getBranchInfo()+"\" color=Blue, shape=ellipse,style=filled,color=\".6 .2 1.0\"]\n";
			}
			//parameters
			if(node.get_data().getThis_feature().isContinuousOutput() || node.get_data().getThis_feature().getConjunction()==null){
				if(node.get_data().get_coefficients()!=null){
					this_feature+="\n"+pars_parent_identifier+" [label=\""+node.get_data().get_coefficients().decisionTreeFormat(node.get_data().this_feature)+"\n Unorm. test error:"+node.get_data().getDependency().getError_test_data()+"\n Unorm.Val. Error:"+node.get_data().getDependency().getError_validation_data()+"\n NRMSE: "+node.get_data().getDependency().getNormalizedTestError()+" \n Sc:"+node.get_data().getScore()+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints()+",Val: "+node.get_data().getValidationDataPoints()+",Ts:"+node.get_data().getTestDataPoints()+"\n"+node.get_data().getBranchInfo()+"\" color=Blue, shape=box]\n";		
				}
				else{	
					this_feature+=pars_parent_identifier+" [label=\""+node.get_data().get_coefficients().filterNaN()+" \n T:"+node.get_data().getDependency().getError_test_data()+"\n Unorm.Val. Error:"+node.get_data().getDependency().getError_validation_data()+" \n Sc:"+node.get_data().getScore()+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints()+",Val: "+node.get_data().getValidationDataPoints()+",Ts:"+node.get_data().getTestDataPoints()+"\n"+node.get_data().getBranchInfo()+"\" color=Blue, shape=box]\n";
				}
				this_feature+=parent_identifier+" -> "+pars_parent_identifier+"\n";
			}
			else{
				HashMap<Value,String> parameters=new HashMap<Value,String>();
				if(node.get_data().getDependency().getDep().getHead().getPredicate().isDiscrete() && node.get_data().getDependency().getParametersPerBranch().size()!=0){
					for(Value v:node.get_data().getDependency().getParametersPerBranch().keySet()){
						parameters.put(v, "dummy");
					}
				}
				else{
					parameters=node.get_data().get_coefficients().decisionTreeFormat2(node.get_data().this_feature);
				}
				List<Node<DecisionTreeData>> children=node.getChildren();
				System.out.println("PARS: "+parameters);
				Set<Value> discreteFeatValue=new HashSet<Value>();
				if(parameters.size()==0){
					RangeDiscrete range=(RangeDiscrete)node.get_data().getThis_feature().getRange();
					System.out.println("RANGE: "+range);
					for(Value v:range.getValues()){
						discreteFeatValue.add(v);
					}
				}
				else{
					discreteFeatValue=parameters.keySet();
				}
				boolean featureBoolean=false;
				for(Value v:discreteFeatValue){
					if(v!=null && v.isBoolean()){
						featureBoolean=true;
						break;
					}
				}
				System.out.println("FEATURE: "+node.get_data().getThis_feature());
				for(Value v:discreteFeatValue){
					System.out.println("Processing value: "+v);
					boolean undefined=false;
					counter++;					
					if(v==null){
						if(featureBoolean){ //we don't consider undefined values for booleans
							continue;
						}
						v=new UndefinedValue();
						undefined=true;
					}
					this_feature+=parent_identifier+" -> "+"P"+counter+v+"[label="+v+"]"+"\n";
					if(node.get_data().getDependency().getDep().getContinuousFeatures().size()==0 || (node.get_data().getDependency().getParametersPerBranch().size()!=0)){
						//System.out.println("Cond1");
						List<FeatureValuePair> listFTPair=new ArrayList<FeatureValuePair>(); 
						//Get parent assignments
						listFTPair.addAll(node.get_data().getParentValues().getKey());						
						listFTPair.add(new FeatureValuePair(node.get_data().getThis_feature(), v));
						AssignmentKey assignKey=null;
						if(undefined){
							assignKey=new UndefinedAssignmentKey();
						}
						else{
							assignKey=new AssignmentKey(listFTPair);
						}
						//System.out.println(node.get_data().getDependency().getTestErrorPerBranch());
						//System.out.println(node.get_data().getTest_errors());
						//System.out.println(node.get_data().getDependency().getParametersPerBranch());
						boolean hasUndefinedValue=false;
						for(FeatureValuePair f:assignKey.getKey()){
				    		if(f.getVal() instanceof hybrid.network.UndefinedValue){
				    			hasUndefinedValue=true;
				    			break;
				    		}
				    	}
						boolean notInCPD=false;

						if(node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients(assignKey)==null){
							notInCPD=true;
						}
                        if(hasUndefinedValue && notInCPD){
                        	assignKey=new UndefinedAssignmentKey();
                        }
						//System.out.println(assignKey);

						if(!node.get_data().getDependency().getParametersPerBranch().containsKey(v)){
							continue;
						}
						System.out.println(node.get_data().getDependency().getParametersPerBranch());
						if(node.get_data().getDependency().getTestErrorPerBranch()!=null){
							System.out.println(v);System.out.println(node.get_data().getDependency().getParametersPerBranch().get(v));
							System.out.println(assignKey);
							this_feature+="P"+counter+v+" [label=\""+node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients(assignKey).decisionTreeFormat(node.get_data().getThis_feature())+"Unnorm. test error: "+node.get_data().getDependency().getTestErrorPerBranch().get(v)+"\n NRMSE: "+node.get_data().getDependency().getNormalizedTestErrorPerBranch().get(v)+"\n Sc: "+node.get_data().getDependency().getScorePerBranch().get(v)+ "\n Test LL: "+node.get_data().getDependency().getTestScores().get(v)+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints().get(v)+",Val: "+node.get_data().getValidationDataPoints().get(v)+",Ts:"+node.get_data().getTestDataPoints().get(v)+"\n"+node.get_data().getDependency().getBranchInfo()+"\" color=Blue, shape=box]\n";
							
						}
						else{
							this_feature+="P"+counter+v+" [label=\""+node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients().decisionTreeFormat(node.get_data().getThis_feature())+"\n"+node.get_data().getDependency().getBranchInfo()+"\" color=Blue, shape=box]\n";
						}
					}
					else{
						if(node.get_data().getTest_errors().containsKey(v)){
							System.out.println("cond5");
							this_feature+="P"+counter+v+" [label=\""+parameters.get(v)+" ] \n "+"Unnorm. test error: "+node.get_data().getTestError(v)+"\n Sc: "+node.get_data().getScores().get(v)+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints().get(v)+",Val: "+node.get_data().getValidationDataPoints().get(v)+",Ts:"+node.get_data().getTestDataPoints().get(v)+"\n"+node.get_data().getDependency().getBranchInfo()+"\" color=Blue, shape=box]\n";
						}
						else{
							System.out.println("cond6");
							this_feature+="P"+counter+v+" [label=\""+parameters.get(v)+"\n"+node.get_data().getDependency().getBranchInfo()+"\" color=Blue, shape=box]\n";
						}
					}
					System.out.println("Par child indentifier for "+v);
					parameter_child_identifier.put(v, "P"+counter+v);
					System.out.println(parameter_child_identifier);
				}
			}
		}	
		counter++;
		if(node.get_data().getThis_feature().isContinuousOutput() && node.getChildren().size()!=0){
			System.out.println("Continuous output");
			for(Node<DecisionTreeData> ch:node.getChildren()){
				if(!ch.isLeaf()){
					this_feature+=pars_parent_identifier+" -> "+"F"+counter+"\n";
					ch.setIdentifier("F"+counter);
				}
				else{
					counter++;
				}
			}
		}
		else if(!node.get_data().getThis_feature().isContinuousOutput() && node.getChildren().size()!=0){
			int c=0;
			for(Node<DecisionTreeData> ch:node.getChildren()){
				//System.out.println("CHILD!"+c);
				//System.out.println(node.getChildren().size());
				//System.out.println("BR: "+getBranching_values());
				//System.out.println(ch.get_data().getValue_mapping());
				//System.out.println(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()));
				if(getBranching_values().size()!=0){
					if(!ch.isLeaf()){
						//System.out.println("not leaf");
						//System.out.println(c);
						//System.out.println(getBranching_values());
						this_feature+=parameter_child_identifier.get(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()))+" -> "+"F"+counter+getBranching_values().get(c)+"\n";
						//System.out.println(parameter_child_identifier.get(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()))+" --> "+"F"+counter+getBranching_values().get(c));
						ch.setIdentifier("F"+counter+getBranching_values().get(c));	
						c++;
					}
				}
				else{
					//System.out.println("leaf");
					counter++;
					this_feature+=pars_parent_identifier+" -> "+"F"+counter+"\n";
					c++;
				}

			}
		}
		return this_feature;
	}*/
	
	
	public String getDigraphRepresentation(Node<DecisionTreeData> node){
		//System.out.println("Parsing node: "+node);
		String result="";
		String this_feature="";
		String parent_identifier=null;
		String pars_parent_identifier=null;
		HashMap<Value,String> parameter_child_identifier=new HashMap<Value,String>();
		if(node.getIdentifier()!=null){
			parent_identifier=node.getIdentifier();
			pars_parent_identifier=node.getIdentifier().replace("F","P");
		}
		else{
			parent_identifier="F"+(counter);
			pars_parent_identifier="P"+(counter);
			node.setIdentifier(parent_identifier);
		}

		if(!node.isLeaf()){
			if(node.get_data().getThis_feature().isContinuousOutput()){
				this_feature=parent_identifier+" [label=\"cont_"+node.get_data().getThis_feature().toString().trim()+" // "+node.get_data().getNr_datapoints_training()+" Sc:"+node.get_data().getScore()+"\n Test LL: "+node.get_data().getOverallTestScore()+"\n"+node.get_data().getBranchInfo()+"\n"+"\" color=Blue, shape=ellipse,style=filled,color=\".6 .2 1.0\"]\n";
			}
			else{
				this_feature=parent_identifier+" [label=\"discr_"+node.get_data().getThis_feature().toString().trim()+" // "+node.get_data().getNr_datapoints_training()+" Sc:"+node.get_data().getScore()+"\n Test LL: "+node.get_data().getOverallTestScore()+"\n"+node.get_data().getBranchInfo()+"\n"+"\" color=Blue, shape=ellipse,style=filled,color=\".6 .2 1.0\"]\n";
			}
			//parameters
			if(node.get_data().getThis_feature().isContinuousOutput() || node.get_data().getThis_feature().getConjunction()==null){
				if(node.get_data().get_coefficients()!=null){
					this_feature+="\n"+pars_parent_identifier+" [label=\""+node.get_data().get_coefficients().decisionTreeFormat(node.get_data().this_feature)+"\n Unorm.Val. Error:"+node.get_data().getDependency().getError_validation_data()+"\n Unorm. test error:"+node.get_data().getDependency().getError_test_data()+"\n NRMSE: "+node.get_data().getDependency().getNormalizedTestError()+" \n Sc:"+node.get_data().getScore()+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints()+",Val: "+node.get_data().getValidationDataPoints()+",Ts:"+node.get_data().getTestDataPoints()+"\n"+node.get_data().getBranchInfo()+"\n"+"\" color=Blue, shape=box]\n";		
				}
				else{	
					this_feature+=pars_parent_identifier+" [label=\""+node.get_data().get_coefficients().filterNaN()+" \n T:"+node.get_data().getDependency().getError_test_data()+"\n Unorm.Val. Error:"+node.get_data().getDependency().getError_validation_data()+" \n Sc:"+node.get_data().getScore()+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints()+",Val: "+node.get_data().getValidationDataPoints()+",Ts:"+node.get_data().getTestDataPoints()+"\n"+node.get_data().getBranchInfo()+"\n"+"\" color=Blue, shape=box]\n";
				}
				this_feature+=parent_identifier+" -> "+pars_parent_identifier+"\n";
			}
			else{
				HashMap<Value,String> parameters=new HashMap<Value,String>();
				if(node.get_data().getDependency().getDep().getHead().getPredicate().isDiscrete() && node.get_data().getDependency().getParametersPerBranch().size()!=0){
					for(Value v:node.get_data().getDependency().getParametersPerBranch().keySet()){
						parameters.put(v, "dummy");
					}
				}
				else{
					parameters=node.get_data().get_coefficients().decisionTreeFormat2(node.get_data().this_feature);
				}
				List<Node<DecisionTreeData>> children=node.getChildren();
				System.out.println("PARS: "+parameters);
				Set<Value> discreteFeatValue=new HashSet<Value>();
				if(parameters.size()==0){
					RangeDiscrete range=(RangeDiscrete)node.get_data().getThis_feature().getRange();
					System.out.println("RANGE: "+range);
					for(Value v:range.getValues()){
						discreteFeatValue.add(v);
					}
				}
				else{
					discreteFeatValue=parameters.keySet();
				}
				boolean featureBoolean=false;
				for(Value v:discreteFeatValue){
					if(v!=null && v.isBoolean()){
						featureBoolean=true;
						break;
					}
				}
				System.out.println("FEATURE: "+node.get_data().getThis_feature());
				for(Value v:discreteFeatValue){
					System.out.println("Processing value: "+v);
					boolean undefined=false;
					counter++;					
					if(v==null){
						if(featureBoolean){ //we don't consider undefined values for booleans
							continue;
						}
						v=new UndefinedValue();
						undefined=true;
					}
					this_feature+=parent_identifier+" -> "+"P"+counter+v+"[label="+v+"]"+"\n";
					if(node.get_data().getDependency().getDep().getContinuousFeatures().size()==0 || (node.get_data().getDependency().getParametersPerBranch().size()!=0)){
						//System.out.println("Cond1");
						List<FeatureValuePair> listFTPair=new ArrayList<FeatureValuePair>(); 
						//Get parent assignments
						listFTPair.addAll(node.get_data().getParentValues().getKey());						
						listFTPair.add(new FeatureValuePair(node.get_data().getThis_feature(), v));
						AssignmentKey assignKey=null;
						if(undefined){
							assignKey=new UndefinedAssignmentKey();
						}
						else{
							assignKey=new AssignmentKey(listFTPair);
						}
						//System.out.println(node.get_data().getDependency().getTestErrorPerBranch());
						//System.out.println(node.get_data().getTest_errors());
						//System.out.println(node.get_data().getDependency().getParametersPerBranch());
						boolean hasUndefinedValue=false;
						for(FeatureValuePair f:assignKey.getKey()){
				    		if(f.getVal() instanceof hybrid.network.UndefinedValue){
				    			hasUndefinedValue=true;
				    			break;
				    		}
				    	}
						boolean notInCPD=false;

						if(node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients(assignKey)==null){
							notInCPD=true;
						}
                        if(hasUndefinedValue && notInCPD){
                        	assignKey=new UndefinedAssignmentKey();
                        }
                        
						//System.out.println(assignKey);

						if(!node.get_data().getDependency().getParametersPerBranch().containsKey(v)){
							continue;
						}
						System.out.println(node.get_data().getDependency().getParametersPerBranch());
						if(node.get_data().getDependency().getTestErrorPerBranch()!=null){
							System.out.println(node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients(assignKey).decisionTreeFormat(node.get_data().getThis_feature()));
							this_feature+="P"+counter+v+" [label=\""+node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients(assignKey).decisionTreeFormat(node.get_data().getThis_feature())+"Unnorm. test error: "+node.get_data().getDependency().getTestErrorPerBranch().get(v)+"\n NRMSE: "+node.get_data().getDependency().getNormalizedTestErrorPerBranch().get(v)+"\n Sc: "+node.get_data().getDependency().getScorePerBranch().get(v)+ "\n Test LL: "+node.get_data().getDependency().getTestScores().get(v)+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints().get(v)+",Val: "+node.get_data().getValidationDataPoints().get(v)+",Ts:"+node.get_data().getTestDataPoints().get(v)+"\n"+"\" color=Blue, shape=box]\n";
						}
						else{
							this_feature+="P"+counter+v+" [label=\""+node.get_data().getDependency().getParametersPerBranch().get(v).getCoefficients().decisionTreeFormat(node.get_data().getThis_feature())+"\n"+"\" color=Blue, shape=box]\n";
						}
					}
					else{
						if(node.get_data().getTest_errors().containsKey(v)){
							System.out.println("cond5");
							
							this_feature+="P"+counter+v+" [label=\""+parameters.get(v)+" ] \n "+"Unnorm. test error: "+node.get_data().getTestError(v)+"\n Sc: "+node.get_data().getScores().get(v)+"\n DataPoints: Tr:"+node.get_data().getTrainingDataPoints().get(v)+",Val: "+node.get_data().getValidationDataPoints().get(v)+",Ts:"+node.get_data().getTestDataPoints().get(v)+"\n"+"\" color=Blue, shape=box]\n";
						}
						else{
							System.out.println("cond6");
							this_feature+="P"+counter+v+" [label=\""+parameters.get(v)+"\n"+"\" color=Blue, shape=box]\n";
						}
					}
					System.out.println("Par child indentifier for "+v);
					parameter_child_identifier.put(v, "P"+counter+v);
					System.out.println(parameter_child_identifier);
				}
			}
		}	
		counter++;
		if(node.get_data().getThis_feature().isContinuousOutput() && node.getChildren().size()!=0){
			System.out.println("Continuous output");
			for(Node<DecisionTreeData> ch:node.getChildren()){
				if(!ch.isLeaf()){
					this_feature+=pars_parent_identifier+" -> "+"F"+counter+"\n";
					ch.setIdentifier("F"+counter);
				}
				else{
					counter++;
				}
			}
		}
		else if(!node.get_data().getThis_feature().isContinuousOutput() && node.getChildren().size()!=0){
			int c=0;
			for(Node<DecisionTreeData> ch:node.getChildren()){
				//System.out.println("CHILD!"+c);
				//System.out.println(node.getChildren().size());
				//System.out.println("BR: "+getBranching_values());
				//System.out.println(ch.get_data().getValue_mapping());
				//System.out.println(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()));
				if(getBranching_values().size()!=0){
					if(!ch.isLeaf()){
						//System.out.println("not leaf");
						//System.out.println(c);
						//System.out.println(getBranching_values());
						this_feature+=parameter_child_identifier.get(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()))+" -> "+"F"+counter+getBranching_values().get(c)+"\n";
						//System.out.println(parameter_child_identifier.get(ch.get_data().getValue_mapping().get(node.get_data().getThis_feature()))+" --> "+"F"+counter+getBranching_values().get(c));
						ch.setIdentifier("F"+counter+getBranching_values().get(c));	
						c++;
					}
				}
				else{
					//System.out.println("leaf");
					counter++;
					this_feature+=pars_parent_identifier+" -> "+"F"+counter+"\n";
					c++;
				}

			}
		}
		return this_feature;
	}
	

	private double getTestError(Value v) {
		return this.test_errors.get(v);
	}


	public HashMap<Feature, Value> getValue_mapping() {
		return value_mapping;
	}


	public void setValue_mapping(HashMap<Feature, Value> value_mapping) {
		this.value_mapping = value_mapping;
	}


	public LearnedDependency getDependency() {
		return dependency;
	}


	public void setDependency(LearnedDependency dependency) {
		this.dependency = dependency;
	}




	public HashMap<Value, Double> getScores() {
		return scores;
	}


	public String getNode_label() {
		return node_label;
	}


	public void setNode_label(String node_label) {
		this.node_label = node_label;
	}


	public String toString(){
		String tmp="[";
		try{
			tmp+="Parent values: "+this.value_mapping+"\n";
			tmp+="Feature: "+this.this_feature+"\n";
			tmp+="Dependency: "+this.dependency+"\n";
			Coefficients c=(Coefficients) this.dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues());
			if(c!=null){
				//tmp+="Paramters: "+this.dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues())+"]\n";
			}
			else{
				//tmp+="Paramters: "+this.dependency.getDep().getCpd().getParameters()+"]\n";
			}
			tmp+="Branch info: "+this.branchInfoValues;
		}
		catch(NullPointerException e){
			tmp+="LEAF Feature: "+this.this_feature+"\n";
			tmp+="Parent values: "+this.value_mapping+"\n";
            
			//tmp+="Paramters: "+this.dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues());
			tmp+="-----------------------------------------------------------------\n";
		}

		return tmp;
	}

	public Coefficients get_coefficients(){
		if(getParentValues().getKey().size()==0){
			return this.dependency.getDep().getCpd().getParameters().getCoefficients();
		}

		Coefficients c=(Coefficients) this.dependency.getDep().getCpd().getParameters().getCoefficients(getParentValues());
		if(c==null){
			//System.out.println(this.dependency.getDep().getCpd().getParameters());
			//System.out.println(getParentValues());

			return this.dependency.getDep().getCpd().getParameters().getCoefficients();
		}
		return c;
	}

	public AssignmentKey getParentValues(){
		List<FeatureValuePair> ftvaluepair=new ArrayList<FeatureValuePair>();
		for(Feature f:this.dependency.getDep().getFeatures()){
			if(this.value_mapping.containsKey(f)){
				ftvaluepair.add(new FeatureValuePair(f, this.value_mapping.get(f)));
			}
		}
		return new AssignmentKey(ftvaluepair);
	}


	public HashMap<Value, Double> getTest_errors() {
		return test_errors;
	}

	public HashMap<Value, Integer> getTestDataPoints() {
		return this.test_data_points;
	}

	public HashMap<Value, Integer> getTrainingDataPoints() {
		return this.training_data_points;
	}

	public HashMap<Value, Integer> getValidationDataPoints() {
		return this.validation_data_points;
	}

	public HashMap<Value, Double> getTraining_errors() {
		return training_errors;
	}


	public HashMap<Value, Double> getTestSetScores() {
		return testSetScores;
	}

	public HashMap<Value, Double> getNormalizedTestScores() {
		return normalizedTestSetScores;
	}

	public HashMap<Value, Double> getNormalizedTestErrors() {
		return normalizedTestErrors;
	}

	public Double getOverallTestScore(){
		return this.testScore;
	}


	public void setTraining_errors(HashMap<Value, Double> training_errors) {
		this.training_errors = training_errors;
	}


	public HashMap<Value, Double> getValidation_errors() {
		return validation_errors;
	}


	public void setValidation_errors(HashMap<Value, Double> validation_errors) {
		this.validation_errors = validation_errors;
	}


	public void setTest_errors(HashMap<Value, Double> test_errors) {
		this.test_errors = test_errors;
	}


	public void setErrorTest(Value value, double error_test_data) {
		this.test_errors.put(value, error_test_data);		
	}

	public void setTestDataPoints(Value value, int test_data_points) {
		this.test_data_points.put(value, test_data_points);		
	}

	public void setTrainingDataPoints(Value value, int test_data_points) {
		this.training_data_points.put(value, test_data_points);		
	}

	public void setValidationDataPoints(Value value, int test_data_points) {
		this.validation_data_points.put(value, test_data_points);		
	}

	public void setErrorTraining(Value value, double error_train_data) {
		this.training_errors.put(value, error_train_data);		
	}

	public void setScore(Value value, double score) {
		this.scores.put(value, score);		
	}

	public void setNormalizedTestScore(Value value, double score) {
		this.normalizedTestSetScores.put(value, score);		
	}

	public void setNormalizedTestError(Value value, double score) {
		this.normalizedTestErrors.put(value, score);		
	}

	public void setTestScore(double d) {
		this.testScore=d;	
	}

	public void setErrorValidation(Value value, double error_validation_data) {
		this.validation_errors.put(value, error_validation_data);		
	}


	public void setNumberOfTestRandvars(int nr_groundings_for_head) {
		this.numberOfTestInstances=nr_groundings_for_head;

	}


	public int getNumberOfTestInstances() {
		return numberOfTestInstances;
	}


	public void setTestDataScore(Value value, double testScore) {
		this.testSetScores.put(value, testScore);		
	}


	public void setBranchInfo(Value value, String branchInfo) {
		this.branchInfoValues.put(value,branchInfo);
		
	}
	
	public HashMap<Value,String> getBranchInfo(){
		return this.branchInfoValues;
	}






}
