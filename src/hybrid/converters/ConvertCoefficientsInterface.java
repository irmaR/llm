package hybrid.converters;

import hybrid.dependencies.Dependency;
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CLGCoefficients;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.LogisticCoefficients;

public interface ConvertCoefficientsInterface {

	public abstract void convert(LinearGaussianCoeff coeff,AssignmentKey key,Dependency dep);

	public abstract void convert(CLGCoefficients clgCoefficients,AssignmentKey key,Dependency dep);

	public abstract void convert(LogisticCoefficients logisticCoefficients,AssignmentKey key, Dependency dep);
	
}
