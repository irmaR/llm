package hybrid.interpretations;

import hybrid.network.Atom;
import hybrid.network.Constant;

import java.util.List;

public abstract class SubSamplingConstraints {

	public abstract boolean conformsToConstraint(List<Constant> l,Atom a);
	
}
