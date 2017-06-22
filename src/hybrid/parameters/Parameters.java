package hybrid.parameters;

import java.io.Serializable;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.network.GroundAtom;
import hybrid.network.Value;

public abstract class Parameters<P extends Coefficients,T extends Coefficients> implements Serializable {
	
	
	protected Dependency dep;
	protected P coeffs;

	public abstract int getNumberOfFreeParameters();
    public abstract P getCoefficients();
    public abstract void setCoefficients(P coeffs);
    public abstract T getCoefficients(AssignmentKey key);
	
	
}
