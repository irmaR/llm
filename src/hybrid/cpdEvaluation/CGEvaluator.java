package hybrid.cpdEvaluation;
import hybrid.cpds.CG;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.NumberValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CGParameters;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.Gaussian;
import hybrid.parameters.GaussianCoefficients;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.Parameters;
import hybrid.parameters.UndefinedAssignmentKey;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.CartesianProduct;
import hybrid.utils.Logarithm2;

import java.util.*;

/**
 * This class is responsible for estimating parameters and evaluating conditional
 * Gaussian distribution. 
 * @author irma
 *
 */

public class CGEvaluator extends ConditionalGaussians implements ContinuousEval<CGParameters> {

	@Override
	public CGParameters estimateParameters(QueryData trainingData) {
		
		List<AssignmentKey> assignmentKeys=this.generateAllKeys(trainingData.getDep());
		List<MarkovBlanket> marginalValues=new ArrayList<MarkovBlanket>(); 
		HashMap<AssignmentKey, List<MarkovBlanket>> filteredData=filterData(assignmentKeys,trainingData);
		CGParameters pars= getStatistics(trainingData.getFlatData(),trainingData.getDep(),assignmentKeys,filteredData);
		trainingData.getDep().getCpd().setParameters(pars);
		return pars;
	}

	/**
	 * Given flat data (list of markov blankets), estimate Gaussian distribution over the target predicate for each 
	 * discrete parent assignment.
	 * @param marginal_values_data
	 * @param dep
	 * @param assignmentKeys
	 * @param filteredData
	 * @return
	 */
	protected CGParameters getStatistics(List<MarkovBlanket> marginal_values_data, Dependency dep,List<AssignmentKey> assignmentKeys,HashMap<AssignmentKey, List<MarkovBlanket>> filteredData) {
		HashMap<AssignmentKey,GaussianCoefficients> g=new HashMap<AssignmentKey, GaussianCoefficients>();
		Gaussian marginal=estimateGaussianStatisticsForHeadValues(marginal_values_data);
		for(AssignmentKey key:filteredData.keySet()){		
			g.put(key, estimateGaussianStatisticsForHeadValues(filteredData.get(key)).getCoefficients());
		}
		return new CGParameters(dep, g,marginal);
	}


	@Override
	public Double getProbability(MarkovBlanket mB, CGParameters par) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		GaussianPriorEvaluator gP=new GaussianPriorEvaluator();
		Gaussian pars=new Gaussian();
		pars.setCoefficients(par.getParameters(key));
		return gP.getProbability(mB, pars);
	}

	@Override
	public StructureScore calculatePLL(QueryData testData, CGParameters pars,Penalty pen) {
		double sumProb=0;
		int nr_data_points=0;
		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){
				double prob=this.getProbability(mB, pars);	
				nr_data_points++;
				sumProb+=Logarithm2.logarithm2(prob);
			}
		}
		return new StructureScore(sumProb,pen.calculatePenalty(testData.getDep(), nr_data_points),sumProb-pen.calculatePenalty(testData.getDep(), nr_data_points));
	}

	/**
	 * Filter data and assign head values to each combination of discrete parents.
	 * It scans the data and then extracts assignment values of the discrete parents. These
	 * form an "assignment key" and each key maps to a list of markov blankets
	 * E.g., for data:
	 * discrete_ft1 discrete_ft2  target_predicate
	 * low			high		    115
	 * med	        med			 	120
	 * ...
	 * It will create the following assignment keys - > markov blanket mappings:
	 * 
	 * low,high ---> Markov Blankey( head=115, ft1:low, ft2:high)
	 * med,med  ---> Markov Blanket( head=120, ft1:med, ft2:med)
	 * 
	 * @param assignmentKeys 
	 * @param trainingData
	 * @return
	 */
	@Override
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
	 * Given a dependency dep, and mapping from features in the parent set to the values,
	 * extract assignment key which represents the assignments to the discrete features
	 * @param dep
	 * @param featureValues
	 * @return
	 */
	public AssignmentKey extractAssignmentKey(Dependency dep,HashMap<Feature, Value> featureValues) {
		AssignmentKey a=null;
		List<FeatureValuePair> tmp=new ArrayList<FeatureValuePair>();
		for(Feature f:dep.getFeatures()){
			if(featureValues.get(f) instanceof UndefinedValue){
				return new UndefinedAssignmentKey();
			}
			tmp.add(new FeatureValuePair(f,featureValues.get(f)));
		}
		return new AssignmentKey(tmp);
	}


	/**
	 * Given a specific dependency, generate all assignment keys (i.e., finding all possible
	 * assignments to the discrete parents)
	 * @param dep
	 * @return
	 */
	protected List<AssignmentKey> generateAllKeys(Dependency dep) {
		List<AssignmentKey> tmp=new ArrayList<AssignmentKey>();
		List<List<FeatureValuePair>> featureValuePairs= getAllFeatureValuePairs(dep);
		List<List<FeatureValuePair>> cartProd=getCartesianProductsOfFeatureValues(featureValuePairs);
		for(List<FeatureValuePair> f:cartProd){
			tmp.add(new AssignmentKey(f));
		}
		return tmp;
	}

	/**
	 * Get all possible feature value pairs. CG has only discrete parents.
	 */
	protected List<List<FeatureValuePair>> getAllFeatureValuePairs(Dependency dep){
		List<List<FeatureValuePair>> featureValuePairs=new ArrayList<List<FeatureValuePair>>();
		for(Feature ft:dep.getFeatures()){
			List<FeatureValuePair> tmp=new ArrayList<FeatureValuePair>();
			for(Value val:((RangeDiscrete)ft.getRange()).getValues()){
				tmp.add(new FeatureValuePair(ft,val));
			}
			featureValuePairs.add(tmp);
		}
		return featureValuePairs;
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, CGParameters parameters) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		GaussianPriorEvaluator gP=new GaussianPriorEvaluator();
		Gaussian pars=new Gaussian();
		pars.setCoefficients(parameters.getParameters(key));
		return gP.getPrediction(mB,pars);
	}

	@Override
	public Double getProbability(Value val, MarkovBlanket mB, CGParameters par) {
		return this.getProbability(mB, par);
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, CGParameters parameters,Random ran) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		GaussianPriorEvaluator gP=new GaussianPriorEvaluator();
		Gaussian pars=new Gaussian();
		pars.setCoefficients(parameters.getParameters(key));
		return gP.getPrediction(mB,pars,ran);
	}

	@Override
	public Value getPrediction_no_noise(MarkovBlanket mB,CGParameters parameters) {
		AssignmentKey key=this.extractAssignmentKey(mB.getDep(), mB.getFeatureValues());
		GaussianPriorEvaluator gP=new GaussianPriorEvaluator();
		Gaussian pars=new Gaussian();
		pars.setCoefficients(parameters.getParameters(key));
		return gP.getPrediction_no_noise(mB,pars);
	}

	
	/**
	 * Getting error for CG is returning NRMSE
	 */
	@Override
	public double getError(QueryData data,CGParameters parameters,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,parameters);
			try {
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				if(mb.getHead().getValue().toNumber()>y_max){
					y_max=mb.getHead().getValue().toNumber();
				}
				if(mb.getHead().getValue().toNumber()<y_max){
					y_min=mb.getHead().getValue().toNumber();
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		acc_error=Math.sqrt(acc_error/n);
		double normalized_error=acc_error/(y_max-y_min);
		return normalized_error;
	}

	@Override
	public double getUnnormalizedError(QueryData data, CGParameters paramters,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,paramters);
			try {
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				if(mb.getHead().getValue().toNumber()>y_max){
					y_max=mb.getHead().getValue().toNumber();
				}
				if(mb.getHead().getValue().toNumber()<y_max){
					y_min=mb.getHead().getValue().toNumber();
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		return acc_error;
	}

	@Override
	public Parameters loadParametersFromCSV(String pathToCSVFile,Dependency dep) {
		// TODO Auto-generated method stub
		return null;
	}





}
