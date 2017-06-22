package hybrid.cpdEvaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CLGParameters;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.Parameters;
import hybrid.parameters.UndefinedAssignmentKey;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.Logarithm2;

/**
 * This class is responsible for estimating parameters and evaluating conditional
 * linear Gaussian distribution. 
 * @author irma
 *
 */

public class CLGEvaluator extends ConditionalGaussians implements ContinuousEval<CLGParameters> {


	/**
	 * Estimate CLG parameters given training data
	 */
	@Override
	public CLGParameters estimateParameters(QueryData trainingData) {
		List<AssignmentKey> assignmentKeys=this.generateAllKeys(trainingData.getDep());
		List<MarkovBlanket> marginalValues=new ArrayList<MarkovBlanket>(); 
		HashMap<AssignmentKey, List<MarkovBlanket>> filteredData=filterData(assignmentKeys,trainingData);
		HashMap<AssignmentKey,LinearGaussianCoeff> pars=new HashMap<AssignmentKey, LinearGaussianCoeff>();
		LinearGaussianEvaluator lGeval=new LinearGaussianEvaluator();
		for(AssignmentKey k:filteredData.keySet()){
			try {
				try {
					pars.put(k, (LinearGaussianCoeff) lGeval.estimateParameters(filteredData.get(k), trainingData.getDep()).getCoefficients());
				} catch (NotEnoughDataForLinearRegression e) {
					//not enough data to estimate the parameters
					//for now I put null for this Linear Gaussian
					pars.put(k, null);
				}
				//    System.out.println("--------------------------------------------------------------------");
			} catch (BadParentSpecification e) {
				e.printStackTrace();
			}
		}
		CLGParameters return_pars=new  CLGParameters(trainingData.getDep(), pars, estimateGaussianStatisticsForHeadValues(marginalValues));
		trainingData.getDep().getCpd().setParameters(return_pars);
		return return_pars;
	}

	@Override
	public Double getProbability(MarkovBlanket mB, CLGParameters par) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		LinearGaussianEvaluator lgEval=new LinearGaussianEvaluator();
		//if no parameters estimated for this combination - do Naive Bayes?
		if(par.getParameters(key)==null){
			return Double.MIN_VALUE;
		}
		LinearGParameters pars=new LinearGParameters(mB.getDep());
		pars.setCoefficients(par.getParameters(key));
		return lgEval.getProbability(mB, pars);
	}

	@Override
	public StructureScore calculatePLL(QueryData testData, CLGParameters pars,Penalty pen) {
		double sumProb=0;
		int nr_data_points=0;
		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){
				sumProb+=Logarithm2.logarithm2(this.getProbability(mB, pars));
				nr_data_points++;
			}
		}
		double penalty=pen.calculatePenalty(testData.getDep(), nr_data_points);
		double result=Double.NEGATIVE_INFINITY;
		if(nr_data_points!=0){
			return new StructureScore(sumProb, penalty,sumProb-penalty );
		}
		return new StructureScore(result, penalty,result);
	}


	/**
	 * Generate all possible keys for discrete features
	 * @param dep
	 * @return
	 */
	public List<AssignmentKey> generateAllKeys(Dependency dep) {
		List<AssignmentKey> tmp=new ArrayList<AssignmentKey>();
		List<List<FeatureValuePair>> featureValuePairs= getAllFeatureValuePairs(dep);
		List<List<FeatureValuePair>> cartProd=getCartesianProductsOfFeatureValues(featureValuePairs);
		for(List<FeatureValuePair> f:cartProd){
			tmp.add(new AssignmentKey(f));
		}
		return tmp;
	}

	/**
	 * Get all possible feature-value pairs for discrete features in the dependency
	 */
	protected List<List<FeatureValuePair>> getAllFeatureValuePairs(Dependency dep){
		List<List<FeatureValuePair>> featureValuePairs=new ArrayList<List<FeatureValuePair>>();
		for(Feature ft:dep.getDiscreteFeatures()){
			List<FeatureValuePair> tmp=new ArrayList<FeatureValuePair>();
			List<Value> range=new ArrayList<Value>();
			range.addAll(((RangeDiscrete)ft.getRange()).getValues());
			if(AlgorithmParameters.getUseUndefinedValue() && !range.get(0).isBoolean()){
				range.add(new UndefinedValue());
			}
			for(Value val:range){
				tmp.add(new FeatureValuePair(ft,val));

			}
			featureValuePairs.add(tmp);
		}
		return featureValuePairs;
	}


	/**
	 * Filter data and assign head values to each combination of discrete parents. We also obtain 
	 * arraylist of head values. 
	 * @param marginalValues - array of marginal values filled with this 
	 * @param trainingData
	 * @return
	 */
	protected HashMap<AssignmentKey, List<MarkovBlanket>> filterData(List<AssignmentKey> assignmentKeys,QueryData trainingData) {
		HashMap<AssignmentKey, List<MarkovBlanket>> tmp=new HashMap<AssignmentKey, List<MarkovBlanket>>();
		for(AssignmentKey k:assignmentKeys){
			tmp.put(k, new ArrayList<MarkovBlanket>());
		}
		tmp.put(new UndefinedAssignmentKey(), new ArrayList<MarkovBlanket>());

		for(Interpretation i:trainingData.getQuery_results().keySet()){
			for(MarkovBlanket mB:trainingData.getQuery_results().get(i)){	
				AssignmentKey key=extractAssignmentKey(trainingData.getDep(),mB.getFeatureValues());
				tmp.get(key).add(mB);
			}
		}
		return tmp;
	}


	/**
	 * Extract Assignment Key for discrete parents
	 * @param dep
	 * @param featureValues
	 * @return
	 */
	public AssignmentKey extractAssignmentKey(Dependency dep,HashMap<Feature, Value> featureValues) {
		AssignmentKey a=null;
		List<FeatureValuePair> tmp=new ArrayList<FeatureValuePair>();
		//for creating the key of CLG we use only discrete features in the order specified when the dependency was created!
		for(Feature f:dep.getDiscreteFeatures()){
			if(AlgorithmParameters.getUseUndefinedValue()){
				if(!f.getRange().isDiscreteRange()){
					if(featureValues.get(f) instanceof UndefinedValue){
						return new UndefinedAssignmentKey();
					}
				}
			}
			else{
				if(featureValues.get(f) instanceof UndefinedValue){
					return new UndefinedAssignmentKey();
				}
			}
			
			tmp.add(new FeatureValuePair(f,featureValues.get(f)));
		}
		return new AssignmentKey(tmp);
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, CLGParameters parameters) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		LinearGaussianEvaluator lgEval=new LinearGaussianEvaluator();
		LinearGParameters pars=new LinearGParameters(mB.getDep());
		pars.setCoefficients(parameters.getParameters(key));
		return lgEval.getPrediction(mB,pars);
	}

	@Override
	public Double getProbability(Value val, MarkovBlanket mB, CLGParameters par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, CLGParameters parameters,Random ran) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		LinearGaussianEvaluator lgEval=new LinearGaussianEvaluator();
		LinearGParameters pars=new LinearGParameters(mB.getDep());
		pars.setCoefficients(parameters.getParameters(key));
		return lgEval.getPrediction(mB,pars,ran);
	}

	@Override
	public Value getPrediction_no_noise(MarkovBlanket mB,CLGParameters parameters) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		LinearGaussianEvaluator lgEval=new LinearGaussianEvaluator();
		LinearGParameters pars=new LinearGParameters(mB.getDep());
		pars.setCoefficients(parameters.getParameters(key));
		return lgEval.getPrediction_no_noise(mB, pars);
	}

	@Override
	public double getError(QueryData data, CLGParameters paramters,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,paramters);
			try {
				if(print){
					//System.out.println("Head: "+(mb.getHead().getValue().toNumber()+" "+d.toNumber()));
				}
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				if(mb.getHead().getValue().toNumber()>y_max){
					y_max=mb.getHead().getValue().toNumber();
				}
				if(mb.getHead().getValue().toNumber()<y_min){
					y_min=mb.getHead().getValue().toNumber();
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		if(print){
			System.out.println("Acc error: "+acc_error+" norm const: "+(y_max-y_min));
		}
		acc_error=Math.sqrt(acc_error/n);
		double normalized_error=acc_error/new Double((y_max-y_min));
		return normalized_error;
	}

	@Override
	public double getUnnormalizedError(QueryData data, CLGParameters paramters,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		/*FileWriter fw=null;
		try {
			fw=new FileWriter(new File("file"+data.getDep().toString()+".csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,paramters);
			try {
				if(print){
					System.out.println("Head: "+(mb.getHead().getValue().toNumber()+" "+d.toNumber()));
				}
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				/*try {
					fw.append(mb.getHead().getValue().toNumber()+","+acc_error+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				if(mb.getHead().getValue().toNumber()>y_max){
					y_max=mb.getHead().getValue().toNumber();
				}
				if(mb.getHead().getValue().toNumber()<y_min){
					y_min=mb.getHead().getValue().toNumber();
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		if(print){
			System.out.println("Acc error: "+acc_error+" norm const: "+(y_max-y_min));
		}
		/*try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return acc_error;
	}

	@Override
	public Parameters loadParametersFromCSV(String pathToCSVFile,Dependency dep) {
		// TODO Auto-generated method stub
		return null;
	}






}
