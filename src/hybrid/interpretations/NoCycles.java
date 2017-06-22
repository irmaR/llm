package hybrid.interpretations;

import hybrid.network.Atom;
import hybrid.network.Constant;

import java.util.List;
/**
 * SUBSAMPLING CONSTRAINT
 * Checks whether newly created (sampled) atom represents a circular dependency. For example, 
 * this constraint doesn't let to have friend(pete,pete) i.e, a person to be a friend with him/her-self.
 * @author irma
 *
 */
public class NoCycles extends SubSamplingConstraints {

	@Override
	public boolean conformsToConstraint(List<Constant> l, Atom a) {
		if(l.get(0).equals(l.get(1))){
			return false;
		}
		else return true;
	}

}
