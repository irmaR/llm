package hybrid.parameters;

import java.io.Serializable;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.network.NumberValue;

/**
 * The class represents the Gaussian parameters
 * mean and standard deviation
 * @author irma
 *
 */

public class Gaussian extends Parameters<GaussianCoefficients,GaussianCoefficients> implements Serializable {
    
	
	/**
	 * Create a gaussian parameter with specific mean and standard deviation
	 * @param mean
	 * @param std
	 */
	public Gaussian(double mean,double std){
		this.coeffs=new GaussianCoefficients(mean, std);
	}
	
	/**
	 * Create a standardized Gaussian with mean=0 and standard deviation=1
	 */
	public Gaussian() {
		this.coeffs=new GaussianCoefficients(0, 1);
	}

	/**
	 * Get mean of this Gaussian distribution
	 * @return
	 */
	public double getMean() {
		return this.coeffs.getMean();
	}
	/**
	 * Set mean for this Gaussian distribution
	 * @param mean
	 */
	/*public void setMean(double mean) {
		this.mean = mean;
	}*/
	/**
	 * Get standard deviation for this Gaussian distribution
	 * @return
	 */
	public double getStd() {
		return this.coeffs.getSigma();
	}
	/**
	 * Set standard deviation for this Gaussian distribution
	 * @param std
	 */
	/*public void setStd(double std) {
		this.std = std;
	}*/
	
	@Override
	public String toString() {
		return "Mean: "+this.coeffs.getMean()+ " STD: "+this.coeffs.getSigma();
		}

	@Override
	public int getNumberOfFreeParameters() {
		return 2;
	}

	@Override
	public GaussianCoefficients getCoefficients() {
		return this.coeffs;
	}

	@Override
	public GaussianCoefficients getCoefficients(AssignmentKey key) {
		return this.coeffs;
	}

	@Override
	public void setCoefficients(GaussianCoefficients coeffs) {
		this.coeffs=coeffs;
	}

	
	
	
	
	
}
