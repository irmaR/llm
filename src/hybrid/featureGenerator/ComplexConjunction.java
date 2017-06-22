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

public class ComplexConjunction implements AbstractConjunction<Literal>, Serializable {
	private Literal l1;
	private Literal l2;
	private List<Literal> context; //conjunction of context literals
	private Operator op;
	private Atom head;
	private List<LogvarRestrictionLiteral> restrictions;


	public ComplexConjunction(Atom head, Literal l1,Literal l2,Operator op)throws ConjunctionConstructionProblem {
		this.l1=l1;
		this.l2=l2;
		this.op=op;
		this.head=head;
	}

	public ComplexConjunction(Atom head, Literal l1,Literal l2,Literal[] context,Operator op)throws ConjunctionConstructionProblem {
		this.l1=l1;
		this.l2=l2;
		this.op=op;
		this.head=head;
		this.context=Arrays.asList(context);
	}

	public ComplexConjunction(Atom head, Literal l1,Literal l2,Literal[] context,Operator op,LogvarRestrictionLiteral[] logvar_restrictions)throws ConjunctionConstructionProblem {
		this.l1=l1;
		this.l2=l2;
		this.op=op;
		this.head=head;
		this.context=Arrays.asList(context);
		this.restrictions=Arrays.asList(logvar_restrictions);
	}
	
	public ComplexConjunction(Atom head, Literal l1,Literal l2,Operator op,LogvarRestrictionLiteral[] logvar_restrictions)throws ConjunctionConstructionProblem {
		this.l1=l1;
		this.l2=l2;
		this.op=op;
		this.head=head;
		this.restrictions=Arrays.asList(logvar_restrictions);
	}

	public List<Literal> getLiteralList() {
		List<Literal> l=new ArrayList<Literal>();
		l.add(this.l1);
		l.add(this.l2);
		return l;
	}
	


	public String toString(){
		String tmp=null;
		String logvar_restriction="";
		if(this.restrictions!=null){
			logvar_restriction=this.restrictions.toString();
		}
		if(this.l1!=null && this.l2!=null){
			tmp= this.l1+" "+op.toString()+" "+this.l2+ " <- "+this.context+" "+logvar_restriction;
		}
		if(this.l2==null){
			if(this.context==null){
				tmp= this.l1.toString();
			}
			else{
				tmp= this.l1.toString()+ " <- "+this.context+" "+logvar_restriction;
			}
		}

		return tmp;
	}

	public List<Literal> getBooleanAtoms() {
		return this.context;
	}

	public Operator get_operator(){
		return this.op;
	}

	public int getNr_booleanVars() {
		return 0;
	}

	@Override
	public boolean hasInternalLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Literal> getInternalBooleanAtoms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Literal getNon_boolean_literal() {
		// TODO Auto-generated method stub
		return null;
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
	public List<Logvar> getOutputLogvars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Atom getHead() {
		return this.head;
	}

	@Override
	public List<LogvarRestrictionLiteral> getLogvarRestrictions() {
		return this.restrictions;
	}

	@Override
	public void addRestrictionForLogvar(List<LogvarRestrictionLiteral> restrictions) {
		this.restrictions.addAll(restrictions);
	}

	public List<Literal> getContext(){
		return this.context;
	}

	public Literal getFirstLiteral(){
		return this.l1;
	}

	public Literal getSecondLiteral(){
		return this.l2;
	}

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	@Override
	public boolean isWithOperator() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
		result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
		result = prime * result
				+ ((restrictions == null) ? 0 : restrictions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComplexConjunction other = (ComplexConjunction) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context)){
			return false;
		}
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (l1 == null) {
			if (other.l1 != null)
				return false;
		} else if (!l1.equals(other.l1))
			return false;
		if (l2 == null) {
			if (other.l2 != null)
				return false;
		} else if (!l2.equals(other.l2))
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op)){
			return false;
		}
		if (restrictions == null) {
			if (other.restrictions != null)
				return false;
		} else if (!restrictions.equals(other.restrictions)){
			return false;
		}
		return true;
	}
	


}
