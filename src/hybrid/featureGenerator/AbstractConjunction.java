package hybrid.featureGenerator;

import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;

import java.io.Serializable;
import java.util.List;

public interface AbstractConjunction<L extends Literal> extends Serializable {

	public abstract boolean hasInternalLiteral();

	public abstract List<L> getBooleanAtoms();

	public abstract List<L> getInternalBooleanAtoms();

	public abstract Literal getNon_boolean_literal();

	public abstract int getNr_booleanVars();
	
	public abstract boolean isWithOperator();

	/**
	 * get number of non-boolean predicates (at most 1)
	 * @return
	 */
	public abstract int getNr_non_booleanPredicates();

	public abstract List<Logvar> getInputLogvars();

	/**
	 * Is this conjunction deterministic w.r.t. the head atom it was created for.
	 * @param head
	 * @return
	 */
	public abstract boolean isDeterministic();

	public abstract List<Literal> getLiteralList();

	public abstract List<Logvar> getOutputLogvars();

	public abstract Atom getHead();

	public abstract List<LogvarRestrictionLiteral> getLogvarRestrictions();

	public abstract void addRestrictionForLogvar(List<LogvarRestrictionLiteral> restrictions);


	

}