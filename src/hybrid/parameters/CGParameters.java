package hybrid.parameters;

import hybrid.cpdEvaluation.CGEvaluator;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.Value;
import hybrid.utils.CartesianProduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class representing CG parameters
 * @author irma
 *
 */
public class CGParameters extends Parameters<CGCoefficients,GaussianCoefficients> implements Serializable {

	
	//Creating empty parameters
	public CGParameters(Dependency dep) {
		List<AssignmentKey> list=generateAllKeys(dep);
		this.coeffs=new CGCoefficients(list);
	}

	/**
	 * Create CG parameter for dependency dep by having a separate Gaussian distribution over head atom for each assignments of discrete 
	 * parents and, finally, a Gaussian marginal distribution for the head.
	 * @param dep
	 * @param g
	 * @param marginal_distr
	 */
	public CGParameters(Dependency dep, HashMap<AssignmentKey, GaussianCoefficients> g,Gaussian marginal_distr) {
		this.dep=dep;
		this.coeffs=new CGCoefficients(g,marginal_distr);
	}



	@Override
	public String toString() {
		return this.coeffs.toString();
	}

	
	/**
	 * Get parameters for dependency dep, parent
	 * @param dep
	 * @param parentValues
	 * @param par
	 * @return
	 */
	public GaussianCoefficients getParameters(Dependency dep,HashMap<Feature, Value> parentValues, CGParameters par, CGEvaluator cgEval) {
		return this.coeffs.getParameters().get(cgEval.extractAssignmentKey(dep, parentValues));
		
	}
	
	/**
	 * Get marginal probability for the head atom
	 * @return
	 */
	public Gaussian getMarginalProb(){
		return this.coeffs.getMarginal();
	}

	public GaussianCoefficients getParameters(AssignmentKey key){
		return this.coeffs.getParameters().get(key);
	}

	public HashMap<AssignmentKey, GaussianCoefficients> getParameters() {
		return this.coeffs.getParameters();
	}

	public Gaussian getMarginal() {
		return this.coeffs.getMarginal();
	}

	public int getNumberOfFreeParameters() {
		int tmp=0;
		Iterator entries = this.coeffs.getParameters().entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			tmp+=2;
		}
		return tmp;
	}
	
	protected List<AssignmentKey> generateAllKeys(Dependency dep) {
		List<AssignmentKey> tmp=new ArrayList<AssignmentKey>();
		List<List<FeatureValuePair>> featureValuePairs= getAllFeatureValuePairs(dep);
		List<List<FeatureValuePair>> cartProd=getCartesianProductsOfFeatureValues(featureValuePairs);
		for(List<FeatureValuePair> f:cartProd){
			tmp.add(new AssignmentKey(f));
		}
		return tmp;
	}

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

	protected List<List<FeatureValuePair>> getCartesianProductsOfFeatureValues(List<List<FeatureValuePair>> list){
		CartesianProduct cartProd=new CartesianProduct<FeatureValuePair>();
		return cartProd.cartesianProduct(list);		
	}

	@Override
	public CGCoefficients getCoefficients() {
		return this.coeffs;
	}

	@Override
	public GaussianCoefficients getCoefficients(AssignmentKey key) {
		return this.coeffs.getParameters().get(key);
	}

	@Override
	public void setCoefficients(CGCoefficients coeffs) {
		this.coeffs=coeffs;
		
	}


	
	
	
	
	
}
