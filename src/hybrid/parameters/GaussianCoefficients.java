package hybrid.parameters;

import hybrid.converters.ConvertCoefficientsInterface;
import hybrid.converters.ConvertPoolInterface;
import hybrid.converters.Converter;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GaussianCoefficients extends Coefficients implements Serializable{
    
	private double mean;
	private double sigma;
	
	public GaussianCoefficients(double mean,double sigma){
		this.mean=mean;
		this.sigma=sigma;
	}

	public GaussianCoefficients() {
		this.mean=1.0;
		this.sigma=0.0;
	}

	public double getMean() {
		return mean;
	}

	
	public double getSigma() {
		return sigma;
	}

	@Override
	public String convert(ConvertPoolInterface converter) {
		return converter.convert(this);
		
	}
	
	public String toString(){
		return this.mean+","+this.sigma;
	}

	@Override
	public String filterNaN() {
		return "N("+this.mean+","+this.sigma+")";
	}
	
	public String decisionTreeFormat(Feature select_to_print_feature) {
		return "N("+new BigDecimal(this.mean).setScale(3, RoundingMode.HALF_UP).doubleValue()+","+new BigDecimal(this.sigma).setScale(3, RoundingMode.HALF_UP).doubleValue()+")";
	}

	@Override
	public void convert(ConvertCoefficientsInterface converter,AssignmentKey key,Dependency dep) {
		// TODO Auto-generated method stub
		
	}


	
	
}
