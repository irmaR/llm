package hybrid.parameters;

import hybrid.cpdEvaluation.CGEvaluator;
import hybrid.cpdEvaluation.CLGEvaluator;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.utils.CartesianProduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing CLG parameters
 * @author irma
 *
 */
public class CLGParameters extends Parameters<CLGCoefficients,LinearGaussianCoeff> implements Serializable {
	/**
	 * Create empty CLG parametera for dependency dep
	 * @param dep
	 * @param g
	 * @param marginal_distr
	 */
	public CLGParameters(Dependency dep) {
		this.dep=dep;
		HashMap<AssignmentKey, LinearGaussianCoeff> pars=new HashMap<AssignmentKey, LinearGaussianCoeff>();
		List<AssignmentKey> list=generateAllKeys(dep);
		for(AssignmentKey a:list){
			pars.put(a,new LinearGaussianCoeff(dep));
		}
		this.coeffs=new CLGCoefficients(pars);
	}
	

	/**
	 * Create CLG parameter for dependency dep by having a separate Linear Gaussian distribution over head atom for each assignments of discrete 
	 * parents and, finally, a Gaussian marginal distribution for the head.
	 * @param dep
	 * @param g
	 * @param marginal_distr
	 */
	public CLGParameters(Dependency dep, HashMap<AssignmentKey, LinearGaussianCoeff> g,Gaussian marginal_distr) {
		this.dep=dep;
		this.coeffs=new CLGCoefficients(g);
		this.coeffs.setMarginal(marginal_distr);
	}
	
	@Override
	public String toString() {
		return this.coeffs.toString();
	}
	
	
	public void addParameter(AssignmentKey key,LinearGaussianCoeff lg){
		this.coeffs.getParameters().put(key, lg);
	}
	
	
	/**
	 * Get parameters for dependency dep, parent
	 * @param dep
	 * @param parentValues
	 * @param par
	 * @return
	 */
	public LinearGaussianCoeff getParameters(Dependency dep,HashMap<Feature, Value> parentValues) {
		return this.coeffs.getParameters().get(new CLGEvaluator().extractAssignmentKey(dep, parentValues));
	}
	
	public LinearGaussianCoeff setParameters(Dependency dep,HashMap<Feature, Value> parentValues,LinearGaussianCoeff pars) {
		return this.coeffs.getParameters().put(new CLGEvaluator().extractAssignmentKey(dep, parentValues),pars);
		
	}
	
	
	public LinearGaussianCoeff getParameters(AssignmentKey key){
		//System.out.println("===================== KEY ================== "+key);
		return this.coeffs.getParameters().get(key);
	}


	@Override
	public int getNumberOfFreeParameters() {
		return this.coeffs.getParameters().size()*(this.dep.getContinuousFeatures().size()+2);
	}


	@Override
	public CLGCoefficients getCoefficients() {
	     return this.coeffs;
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
		for(Feature ft:dep.getDiscreteFeatures()){
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
	public LinearGaussianCoeff getCoefficients(AssignmentKey key) {
		for(AssignmentKey k:this.coeffs.getParameters().keySet()){
			for(FeatureValuePair f:k.getKey()){
			}
			
		}
		return this.coeffs.getParameters().get(key);
	}


	@Override
	public void setCoefficients(CLGCoefficients coeffs) {
		this.coeffs=coeffs;
		
	}
	
}
