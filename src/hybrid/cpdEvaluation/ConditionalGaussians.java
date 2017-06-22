package hybrid.cpdEvaluation;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.network.NumberValue;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.Gaussian;
import hybrid.querydata.QueryData;
import hybrid.utils.CartesianProduct;

import java.util.HashMap;
import java.util.List;

/**
 * This abstract class contains some methods for working with conditional Gaussian
 * distributions.
 * @author irma
 *
 */

public abstract class ConditionalGaussians {
	
	protected abstract List<List<FeatureValuePair>> getAllFeatureValuePairs(Dependency dep);
	protected abstract HashMap<AssignmentKey, List<MarkovBlanket>> filterData(List<AssignmentKey> assignmentKeys,QueryData trainingData);
	/**
	 * Finding Cartesian product of List of FeatureValuePair
	 * @param list
	 * @return
	 */
	protected List<List<FeatureValuePair>> getCartesianProductsOfFeatureValues(List<List<FeatureValuePair>> list){
		CartesianProduct cartProd=new CartesianProduct<FeatureValuePair>();
		return cartProd.cartesianProduct(list);		
	}
	
	/**
	 * Given a list of Markov blankets, estimate mean and variance over the 
	 * head value
	 * @param list
	 * @return
	 */
	protected Gaussian estimateGaussianStatisticsForHeadValues(List<MarkovBlanket> list) {
		if(list.size()==0){
			return new Gaussian(Double.NaN,Double.NaN);
		}
		double n = list.size();
		double sum = 0;
		double sum2 = 0;
	
		for (MarkovBlanket v : list) {
			sum += ((NumberValue)v.getHead().getValue()).getNumber();
		}
		double mean = sum / n;

		for (MarkovBlanket v : list) {
			sum2 += ((((NumberValue)v.getHead().getValue()).getNumber()) - mean) * ((((NumberValue)v.getHead().getValue()).getNumber()) - mean);
		}
		double std = Math.sqrt(AlgorithmParameters.getBeta_stdev()+sum2/(AlgorithmParameters.getAlpha_stdev()+(n - 1)));
		return new Gaussian(mean,std);
	}
}
