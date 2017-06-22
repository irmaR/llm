package hybrid.featureGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.operators.Operator;

public class ComparisonConjunction implements AbstractConjunction<Literal>,Serializable {

	private Literal l1;
	private Atom head;
	private List<Literal> context; //conjunction of context literals
	
	public ComparisonConjunction(Atom head, Literal l1)throws ConjunctionConstructionProblem {
		this.l1=l1;
	    this.head=head;
	}
	
	public ComparisonConjunction(Atom head, Literal l1,Literal[] context)throws ConjunctionConstructionProblem {
		this.l1=l1;
	    this.head=head;
	    this.context=Arrays.asList(context);
	}
	
	@Override
	public boolean hasInternalLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Literal> getBooleanAtoms() {
		return this.context;
	}

	@Override
	public List<Literal> getInternalBooleanAtoms() {
		List<Literal> tmp=new ArrayList<Literal>();
		for(Literal l:this.context){
			if(l.getAtom().isInternal()){
				tmp.add(l);
			}
		}
		return tmp;
	}

	@Override
	public Literal getNon_boolean_literal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNr_booleanVars() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNr_non_booleanPredicates() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Logvar> getInputLogvars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDeterministic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Literal> getLiteralList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Logvar> getOutputLogvars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Atom getHead() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LogvarRestrictionLiteral> getLogvarRestrictions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRestrictionForLogvar(
			List<LogvarRestrictionLiteral> restrictions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWithOperator() {
		return false;
	}

}
