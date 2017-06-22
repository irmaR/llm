package hybrid.parameters;

import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.network.Value;
import hybrid.network.WrongValueType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Parameters for linear gaussian. 
 * @author irma
 *
 */

public class LinearGParameters extends Parameters<LinearGaussianCoeff,LinearGaussianCoeff> implements Serializable {

	private Dependency dep;

	/**
	 * Create empty linear gaussian parameters
	 * @param dep
	 */
	public LinearGParameters(Dependency dep) {
		this.dep=dep;
		this.coeffs=new LinearGaussianCoeff(dep);
	}

	/**
	 * Create linear gaussian parameters 
	 * @param dep TODO
	 * @param parameters
	 * @param std
	 */
	public LinearGParameters(Dependency dep, Regression parameters, Double std){
		this.dep=dep;
		this.coeffs=new LinearGaussianCoeff(parameters, std);	
	}


	@Override
	public String toString() {
		return this.coeffs.toString();
	}


	public LinearGaussianCoeff getPars() {
		return coeffs;
	}


	public void setPars(LinearGaussianCoeff pars) {
		this.coeffs = pars;
	}



	@Override
	public int getNumberOfFreeParameters() {
		System.out.println(coeffs.getReg_coeff());
		return 1+coeffs.getReg_coeff().get_nr_pars();
	}

	@Override
	public LinearGaussianCoeff getCoefficients() {
	     return this.coeffs;
	}

	@Override
	public LinearGaussianCoeff getCoefficients(AssignmentKey key) {
		return this.coeffs;
	}

	@Override
	public void setCoefficients(LinearGaussianCoeff coeffs) {
		this.coeffs=coeffs;
		
	}





}
