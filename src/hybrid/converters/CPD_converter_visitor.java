package hybrid.converters;

import hybrid.parameters.CGCoefficients;
import hybrid.parameters.LinearGaussianCoeff;


public interface CPD_converter_visitor<T> {

	public String convert(T coeff);

	
}
