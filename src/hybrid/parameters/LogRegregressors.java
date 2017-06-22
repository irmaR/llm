package hybrid.parameters;

import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.network.WrongValueType;

import java.util.HashMap;

/**
 * This class represents parameters of logistic regression for a specific dependency.
 * Regressors map each value of the target predicate to specific 
 * regression coefficients and intercept for features in the parent set.
 * @author irma
 *
 */
public class LogRegregressors extends Parameters<LogisticCoefficients,LogisticCoefficients> {

	private Dependency dep;
	private LogisticCoefficients coefficients;
	private Double averageTrainingAlpha=null;

	/**
	 * initializes regression parameters with zero weights
	 * @param dep
	 */
	public LogRegregressors(Dependency dep){
		this.dep=dep;
		HashMap<Value,Regression> init=new HashMap<Value,Regression>();
		int count=0;
		int nrValues=((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().size();
		for(Value v:((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues()){
			count++;
			if(count==nrValues){
				MissingRegression r=new MissingRegression(dep.getFeatures());
				init.put(v, r);
			}
			else{
				Regression r=new Regression(dep.getFeatures());
				init.put(v, new Regression(dep.getFeatures()));
			}
			
		}
		this.coefficients=new LogisticCoefficients(init);

	}


	@Override
	public String toString() {
		return coefficients.toString();
	}


	/**
	 * Add a parameter for a specific value of the target predicate
	 * @param val - value from the range of a predicate
	 * @param parameters
	 * @throws WrongParameterNumber
	 * @throws WrongValueType
	 * @throws WrongValueSpecification
	 */
	public void addConditionalParameter(Value val, Regression parameters) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {
		if(dep.getFeatures().size()!=parameters.getWeights().size()){
			throw new WrongParameterNumber("The number of features and regressors is not the same!");
		}
		if(!dep.getHead().hasInRange(val)){
			throw new WrongValueSpecification("There is no: "+val+" in the range of "+dep.getHead());
		}
		for(int i=0;i<dep.getFeatures().size();i++){
			this.coefficients.add(val, parameters);
		}
	}

	/**
	 * Returns intercept value for target predicate's value
	 * @param val
	 * @return
	 */
	public double getInterceptForValue(Value val){
		return this.coefficients.getRegressors().get(val).getIntercept();
	}

	/**
	 * returns feature's ft coefficient for a target predicate's value val
	 * @param val
	 * @param ft
	 * @return
	 */
	public double getCoefficientForValueAndFeature(Value val,Feature ft){
		return this.coefficients.getRegressors().get(val).getWeights().get(ft);
	}

	/**
	 * check if there are regression coefficient for a value 
	 * @param value
	 * @return
	 */
	public boolean hasValues(Value value) {
		if(this.coefficients.getRegressors().containsKey(value)){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int getNumberOfFreeParameters() {
		if(this.coefficients.getNr_free_pars()==null){
			int tmp=1;
			for(Value val:this.coefficients.getRegressors().keySet()){
				if(!this.coefficients.getRegressors().get(val).isNthRegressor()){
					tmp+=this.coefficients.getRegressors().get(val).get_nr_pars();
				}
			}
			this.coefficients.setNr_free_pars(tmp);
		}
		return this.coefficients.getNr_free_pars();
	}

	/**
	 * get regression coefficients for a value
	 * @param val
	 * @return
	 */
	public Regression getRegressionCoefficients(Value val){
		return this.coefficients.getRegressors().get(val);
	}
	/**
	 * This method checks whether there are coefficients specified for this value. If not, it means it is the nth parameter
	 * (where n=|range(head_atom)|), and it needs not to be estimated.
	 * @param val
	 * @return
	 */
	public boolean isNthCoefficient(Value val){
		return this.coefficients.getRegressors().get(val).isNthRegressor();
	}

	/**
	 * Add average alpha for this dependency (if the subsampling occured)
	 * @param averageAlpha
	 */
	public void addAverageTrainingAlpha(Double averageAlpha) {
		this.averageTrainingAlpha=averageAlpha;

	}

	/**
	 * get the average alpha from the training data
	 * @return
	 */
	public Double getTraining_data_averaged_alphas() {
		return this.averageTrainingAlpha;
	}


	@Override
	public LogisticCoefficients getCoefficients(AssignmentKey key) {
		return this.coefficients;
	}


	@Override
	public LogisticCoefficients getCoefficients() {
		return this.coefficients;
	}


	@Override
	public void setCoefficients(LogisticCoefficients coeffs) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((averageTrainingAlpha == null) ? 0 : averageTrainingAlpha
						.hashCode());
		result = prime * result
				+ ((coefficients == null) ? 0 : coefficients.hashCode());
		result = prime * result + ((dep == null) ? 0 : dep.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogRegregressors other = (LogRegregressors) obj;
		if (averageTrainingAlpha == null) {
			if (other.averageTrainingAlpha != null)
				return false;
		} else if (!averageTrainingAlpha.equals(other.averageTrainingAlpha))
			return false;
		if (coefficients == null) {
			if (other.coefficients != null)
				return false;
		} else if (!coefficients.equals(other.coefficients))
			return false;
		if (dep == null) {
			if (other.dep != null)
				return false;
		} else if (!dep.equals(other.dep))
			return false;
		return true;
	}



}
