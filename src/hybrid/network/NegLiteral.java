package hybrid.network;

import java.util.ArrayList;
import java.util.List;
/**
 * Negated literal
 * @author irma
 *
 */
public class NegLiteral extends Literal{

	public NegLiteral(Atom a) throws LiteralNotDefined{
		super(a);
		if(!(a instanceof TestRandvarValue || a.getPredicate() instanceof BooleanPred)){
			throw new LiteralNotDefined();
		}
		this.a=a;
	}

	@Override
	public String createFOLTerm() {
		return "not("+this.a.createFOLTerm()+")";
	}

	public String toString(){
		return "not("+this.a.createFOLTerm()+")";
	}

	/**
	 * Given a list of atoms, the class created negative literals for all
	 * atoms for which negation is defined. E.g., we cannot create negation
	 * for numeric or categorical atoms.
	 */
	@Override
	public List<Literal> getLiterals(List<Atom> list) {
		List<Literal> negLiterals=new ArrayList<Literal>();
		for(Atom a:list){
			try {
				negLiterals.add(new NegLiteral(a));
			} catch (LiteralNotDefined e) {
				continue;
			}
		}
		return negLiterals;
	}


}
