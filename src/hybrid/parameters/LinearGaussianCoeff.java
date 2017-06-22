package hybrid.parameters;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.converters.Converter;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * This class represents a coefficient of a linear Gaussian for a specific . It consists of two parts:
 * - Regression coefficients - which determine the linear combination of continuous parents representing the mean of the Gaussian
 * - std - representing the standard deviation
 * @author irma
 *
 */
public class LinearGaussianCoeff extends Coefficients implements Serializable {

	private Regression reg_coeff;
	private double std;
	
	public LinearGaussianCoeff(Regression reg_coeff,double std){
		this.reg_coeff=reg_coeff;
		this.std=std;
	}
	
	public LinearGaussianCoeff(Dependency dep){
		this.reg_coeff=new Regression(dep.getContinuousFeatures());
		this.std=Double.NaN;
	}

	public Regression getReg_coeff() {
		return reg_coeff;
	}

	
	
	public void setReg_coeff(Regression reg_coeff) {
		this.reg_coeff = reg_coeff;
	}

	public double getStd() {
		return std;
	}
	
	

	public void setStd(double std) {
		this.std = std;
	}

	@Override
	public String toString() {
		try{
		return "N(mean=" + reg_coeff +",std="+new BigDecimal(std).setScale(3, RoundingMode.HALF_UP).doubleValue()+ ")";
		}
		catch(NumberFormatException e){
			return "N(nan,nan)";
		}
	}

	

	@Override
	public String convert(ConvertPoolInterface converter) {
        return converter.convert(this);		
	}
	
	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
        converter.convert(this,key,dep);		
	}

	@Override
	public String filterNaN() {
		return "N(mean=" + reg_coeff +",std="+new BigDecimal(std).setScale(3, RoundingMode.HALF_UP).doubleValue()+ ")";
	}
	
	public String decisionTreeFormat(Feature select_to_print_feature) {
		try{
		return "N(mean=" + reg_coeff +",std="+new BigDecimal(std).setScale(3, RoundingMode.HALF_UP).doubleValue()+ ")";
		}
		catch(NumberFormatException e){
			return "N(nan,nan)";
			//return "N(mean=" + reg_coeff +",std="+std+ ")";
		}
	}
	
	
	
	
}
