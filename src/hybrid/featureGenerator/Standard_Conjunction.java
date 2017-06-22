package hybrid.featureGenerator;
import hybrid.network.Atom;
import hybrid.network.CategoricalPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NumericalPredicate;
import hybrid.network.PosLiteral;
import hybrid.network.RelationType;
import hybrid.network.Type;
import hybrid.queryMachine.InternalComparator;
import hybrid.utils.ExtractRenaming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a sequence of atoms (for now  I call it a conjunction)
 * The object of this class can only be created through constructors accepting either a single atom
 * or a list of atoms. This conjunction is considered to be the final entity used in 
 * the feature space. This means that the renaming of logvars already occured. E.g., 
 * if a proposed conjuntion operating only on types was [friend(S,S),friend(S,S)], the final conjunction consists
 * of e.g., [friend(S,S1),friend(S1,S2)]. Thus this class will contain all the renamings for 
 * type S. that is : S --> S,S1,S2
 * @author irma
 *
 */
public class Standard_Conjunction<L extends Literal> implements AbstractConjunction<L>,Serializable{
	private List<Logvar> inputLogvars; //input logvars are those contained in the head predicate
	private List<Logvar> outputLogvars; //list of output logvars (output logvars are the ones containes in the conjunction literals, but not in the head)
	private Atom head; //head literal for this conjunction
	private List<L> atomList; //list of all atoms in the conjunction
	private List<L> booleanLiterals; //only boolean literals
	private Literal non_boolean_atom; //only 1 non-boolean literals
	private List<L> internalBooleanLiterals; //only internal boolean literals (friend(S,S))
	private List<LogvarRestrictionLiteral> restrictions;
	private List<L> sortedLiterals; //sort literals, boolean ones come first (for easier evaluation - they fail sooner)
	private Renaming renaming;
    private boolean construction_connection_ignore=false;
	/**
	 * Create empty conjunction
	 */
	public Standard_Conjunction(Atom head){
		this.head=head;
		this.atomList=new ArrayList<L>();
	}
	
	
	/**
	 * Create conjunction with a list of literals and logvar restriction
	 * @param head - target predicate
	 * @param list - list of literals
	 * @throws ConjunctionConstructionProblem
	 */
	public Standard_Conjunction(Atom head,LogvarRestrictionLiteral[] logvar_restrictions, L... list) throws ConjunctionConstructionProblem {
		construct_conjunction(head,new ArrayList<L>(Arrays.asList(list)));
		this.restrictions=Arrays.asList(logvar_restrictions);
	}
	
	public Standard_Conjunction(Atom head,boolean construction_ignore,LogvarRestrictionLiteral[] logvar_restrictions, L... list) throws ConjunctionConstructionProblem {
		this.construction_connection_ignore=construction_ignore;
		construct_conjunction(head,new ArrayList<L>(Arrays.asList(list)));
		this.restrictions=Arrays.asList(logvar_restrictions);
	}
	
	
	
	/**
	 * Create conjunction with a list of literals
	 * @param head - target predicate
	 * @param list - list of literals
	 * @throws ConjunctionConstructionProblem
	 */
	public Standard_Conjunction(Atom head,boolean construction_ignore, L... list) throws ConjunctionConstructionProblem {
		this.construction_connection_ignore=construction_ignore;
		construct_conjunction(head,new ArrayList<L>(Arrays.asList(list)));
		
	}
	
	/**
	 * Create conjunction with a list of literals
	 * @param head - target predicate
	 * @param list - list of literals
	 * @throws ConjunctionConstructionProblem
	 */
	public Standard_Conjunction(Atom head, L... list) throws ConjunctionConstructionProblem {
		construct_conjunction(head,new ArrayList<L>(Arrays.asList(list)));		
	}
	
	/**
	 * Create conjunction with a list of literals
	 * @param head - target predicate
	 * @param list - list of literals
	 * @throws ConjunctionConstructionProblem
	 */
	public Standard_Conjunction(Atom head, List<L> list) throws ConjunctionConstructionProblem {
		construct_conjunction(head,(ArrayList<L>) list);		
	}



	/**
	 * Creating conjunction
	 * @param l - a list of atoms in the conjunction
	 * @param head - this conjunction is created w.r.t. to the atom head. In this way we can determine what are the 
	 * input and output logvars. 
	 * @throws NotFullyConnectedLogvars 
	 */
	private void construct_conjunction(Atom head,ArrayList<L> l) throws ConjunctionConstructionProblem {
		this.head=head;
		this.restrictions=new ArrayList<LogvarRestrictionLiteral>();
		this.atomList=l;
		this.booleanLiterals=new ArrayList<L>();
		int non_boolean_atoms=0;
		this.internalBooleanLiterals=new ArrayList<L>();
		//set renaming for this conjunction
		this.renaming=ExtractRenaming.extractRenaming(l);
		//determine input and output logvars
		if(head!=null){
			this.setInputLogvar(head.getArguments());
			this.determineOutputLogvars();
		}
		//sort literals (booleans should come first so to fail sooner)
		//delegate atoms
		for(L a:l){
			if((!(a.getAtom().isRandVarTest() || a.getAtom().getPredicate().isBoolean()) & this.non_boolean_atom!=null) || a.getAtom().equals(head)){
				throw new MoreThanTwoBooleanAtoms(" Invalid conjunction: more than two non-boolean atoms!"+l+" non_boolean atom: "+this.non_boolean_atom);
			}
			//if boolean 
			if(a.getAtom().isRandVarTest() || a.getAtom().getPredicate().isBoolean()){
				this.booleanLiterals.add(a);
				//if it is internal
				if(a.getAtom().getRelationType().equals(RelationType.INTERNAL)){
					this.internalBooleanLiterals.add(a);	
				}
			}
			else{
				this.non_boolean_atom=a;
				non_boolean_atoms++;
			}
		}
		sortedLiterals=new ArrayList<L>();
		sortedLiterals.addAll(atomList);
		Collections.sort(sortedLiterals,new InternalComparator());    
		//CONSTRAINTS ON CONJUNCTION
		if(hasDuplicateLiterals(l)){ //conjunction has duplicate literals
			throw new HasDuplicateLiterals(l.toString());
		}
		if(this.construction_connection_ignore==false && !isFullyConnected()){ //conjunction is not fully connected
			throw new NotFullyConnected(" Invalid conjunction"+this+" : literals not fully connected!");
		}
	}



	/**
	 * determine if the list of literals has duplicate literals
	 * @param literal_list
	 * @return
	 */
	public boolean hasDuplicateLiterals(List<L> literal_list) {
		Set<L> set = new HashSet<L>(literal_list);
		if(set.size() < literal_list.size()){
			return true;
		}
		return false;
	}
	/**
	 * Determine if the conjunction literals are fully connected. Fully connection means that
	 * each literal in the conjunction has at least one logvar shared with either head atom or
	 * some of the literals in the body
	 * 
	 * for example: intelligence(S) | friend(S,S1), grade(S1,C) is fully connected because friend(S,S1) has logvar shared with the head and one of the
	 * literals in the body( grade(S1,C)); grade(S1,C) has at least one logvar shared with other literal (friend(S,S1))
	 * BUT: intelligence(S) | friend(S,S1), grade(S,C) is not fully connected, because friend(S,S1) has logvar S1 which is not shared with
	 * any other literal.
	 * @param head
	 * @param list_of_literals
	 * @return
	 */
	protected boolean isFullyConnected() {
		boolean is_connected_with_head=false;
		List<Atom> comparison_literals=new ArrayList<Atom>();
		for(Literal l:this.atomList){
			comparison_literals.add(l.getAtom());
		}
		comparison_literals.add(head);

		for(L lit:this.atomList){
			is_connected_with_head=false;
			//check if the literal has logvar connection with head and vice versa
			if(head.hasCommonArguments(lit.getAtom()) || lit.getAtom().hasCommonArguments(head)){
				is_connected_with_head=true;
			}
			//else, for Boolean literals or randvar tests, ALL logvars of lit have to be contained in at least one of the literals in the body
			//otherwise, we consider these logvars no to be fully connected
			//for instance [intelligence(S) | friend(S,S1), grade(S,C)] makes no sense because S1 is just a free variable
			//HOWEVER, the exception is when we have only one boolean in the feature, e.g., intelligence(S) | friend(S,S1) is
			//perfectly reasonable. 
			if(this.atomList.size()>1 && lit.getAtom().getPredicate().isBoolean() && !lit.getAtom().isRandVarTest()){
				if(!(isFullyConnectedToBodyLiterals(lit,comparison_literals))){
					return false;
				}
			}
			//for non boolean, we need at least one shared logvar with other literals in the body.
			//in this case we would allow features such as, intelligence(S) | friend(S,S1), grade(S1,C)
			//if grade(S1,C) is boolean this feature would be disallowed. But it is a perfectly 
			//valid feature, as we then take mode over grades of a friend S1
			else{
				if(!hasAtLeastOneCommongLogvarWithBody(lit,comparison_literals)){
					return false;
				}
			}
		}
		return true;
	}

	protected boolean hasAtLeastOneCommongLogvarWithBody(L lit,List<Atom> list_of_literals) {
		for(Logvar l:lit.getAtom().getArguments()){
			for(Atom literal:list_of_literals){
				if(literal.equals(lit.getAtom())){
					continue;
				}
				if(lit.getAtom().hasCommonArguments(literal)){
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isFullyConnectedToBodyLiterals(L lit,List<Atom> list_of_literals) {
		if(lit.getAtom().getArguments().size()==2){
			Logvar argument1=lit.getAtom().getArguments().get(0);
			Logvar argument2=lit.getAtom().getArguments().get(1);

			boolean arg1_exists=false;
			boolean arg2_exists=false;	

			for(Atom l:list_of_literals){
				if(l.equals(lit.getAtom())){
					continue;
				}
				else{
					if(l.doArgumentsContainLogvar(argument1)){
						arg1_exists=true;
						break;
					}
				}
			}
			for(Atom l:list_of_literals){
				if(l.equals(lit.getAtom())){
					continue;
				}
				else{
					if(l.doArgumentsContainLogvar(argument2)){
						arg2_exists=true;
						break;
					}
				}
			}
			//if both arguments have connection with other literals
			return arg1_exists && arg2_exists;
		}

		else if(lit.getAtom().getArguments().size()==1){
			Logvar argument1=lit.getAtom().getArguments().get(0);
			boolean arg1_exists=false;
			for(Atom l:list_of_literals){
				if(l.equals(lit.getAtom())){
					continue;
				}
				else{
					if(l.doArgumentsContainLogvar(argument1)){
						arg1_exists=true;
						break;
					}
				}
			}
			return arg1_exists;

		}
		return false;



	}

	@Override
	public boolean hasInternalLiteral() {
		return this.internalBooleanLiterals.size()>0 ? true:false;
	}


	public void addToRenaming(Logvar argument) {
		if(this.renaming==null){
			this.renaming=new Renaming();
		}
		this.renaming.addRenaming(argument.getType(), argument);
	}

	public Renaming getRenaming() {
		return renaming;
	}

	public void setRenaming(Renaming renaming) {
		this.renaming = renaming;
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getBooleanAtoms()
	 */
	@Override
	public List<L> getBooleanAtoms() {
		return booleanLiterals;
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getInternalBooleanAtoms()
	 */
	@Override
	public List<L> getInternalBooleanAtoms() {
		return internalBooleanLiterals;
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getNon_boolean_literal()
	 */
	@Override
	public Literal getNon_boolean_literal() {
		return non_boolean_atom;
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getNr_booleanVars()
	 */
	@Override
	public int getNr_booleanVars() {
		return this.booleanLiterals.size();
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getNr_non_booleanPredicates()
	 */
	@Override
	public int getNr_non_booleanPredicates() {
		if(this.non_boolean_atom!=null){
			return 1;
		}
		else{
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getInputLogvars()
	 */
	@Override
	public List<Logvar> getInputLogvars() {
		if(inputLogvars==null){
			throw new NullPointerException("You have to explicitly set the input logvars for a conjunction - via method setInputLogvar(List<Logvar>");
		}
		return inputLogvars;
	}
	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#isDeterministic()
	 */
	@Override
	public boolean isDeterministic() {
		if(this.outputLogvars.size()==0){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * By setting the input logvars for the conjunction, you immeadiately determine what are the input and 
	 * what the output logvars are
	 * @param arguments
	 */
	public void setInputLogvar(List<Logvar> arguments) {
		this.inputLogvars=arguments;
		determineOutputLogvars();
	}


	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getLiteralList()
	 */
	@Override
	public List<Literal> getLiteralList() {
		return (List<Literal>) atomList;
	}


	public String toString(){
		String tmp="";
		for(Literal a:atomList){
			tmp+=a.toString()+" ";
		}
		for(LogvarRestrictionLiteral logvarRe:this.restrictions){
			tmp+=logvarRe.toString()+" ";
		}
		return tmp;
	}

	public AbstractConjunction copyProposedConjunction() {
		Standard_Conjunction p=new Standard_Conjunction(this.head);
		p.addToLiteralList(this.atomList);
		p.booleanLiterals.addAll(this.booleanLiterals);
		p.internalBooleanLiterals.addAll(this.internalBooleanLiterals);
		p.restrictions.addAll(this.restrictions);
		if(this.renaming!=null){
			p.renaming=new Renaming();
			for(Type t:this.renaming.getRenaming().keySet()){
				for(Logvar l:this.renaming.getRenaming().get(t)){
					p.renaming.addRenaming(t, l);
				}
			}
		}
		return p;
	}



	public long getSumOfIds(){
		long tmp=0;
		for(Literal a:atomList){
			tmp+=Math.exp(a.getAtom().getId());
		}
		return tmp;
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getOutputLogvars()
	 */
	@Override
	public List<Logvar> getOutputLogvars() {
		return outputLogvars;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atomList == null) ? 0 : atomList.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this.hashCode()==obj.hashCode()){
			return true;
		}
		else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getHead()
	 */
	@Override
	public Atom getHead(){
		return this.head;
	}


	/* (non-Javadoc)
	 * @see hybrid.featureGenerator.AbstractConjunction#getLogvarRestrictions()
	 */
	@Override
	public List<LogvarRestrictionLiteral> getLogvarRestrictions(){
		return this.restrictions;
	}

	/**
	 * Add atom to the list. If the atom is an internal atom e.g., friend(S,S) this method will add a possible 
	 * logvar renaming. Eg., if the current proposed conjunction is friend(S,S), adding a new friend(S,S) will denote that
	 * S can be renamed to both S1 and S2 for feature creation of, for example, the following form: friend(S,S1),friend(S1,S2)
	 * @param literal
	 */
	protected void addToLiteralList(L literal) {
		if(literal.getAtom().getPredicate() instanceof CategoricalPred || literal.getAtom().getPredicate() instanceof NumericalPredicate){
			//we already have one non-boolean atom (it can be at most 1)
			if(containsListCategoricalOrNumericPredicate(literal)){
				return;
			}
			else{
				this.non_boolean_atom=literal;
			}
		}
		if(literal.getAtom().getRelationType().equals(RelationType.INTERNAL)){
			this.booleanLiterals.add(literal);
			this.internalBooleanLiterals.add(literal);
		}
		this.atomList.add(literal);
	}
	
	private boolean containsListCategoricalOrNumericPredicate(Literal atom) {
		for(Literal a:this.atomList){
			if(a.getAtom().getPredicate() instanceof CategoricalPred || a.getAtom().getPredicate() instanceof NumericalPredicate){
				return true;
			}
		}
		return false;
	}
	
	private void determineOutputLogvars() {
		this.outputLogvars=new ArrayList<Logvar>();
		for(Literal a:this.atomList){
			if(a.getAtom().getArguments().size()==2){
				if(!this.inputLogvars.contains(a.getAtom().getArguments().get(0))){
					this.outputLogvars.add(a.getAtom().getArguments().get(0));
				}
				if(!this.inputLogvars.contains(a.getAtom().getArguments().get(1))){
					this.outputLogvars.add(a.getAtom().getArguments().get(1));
				}
			}
			if(a.getAtom().getArguments().size()==1){
				if(!this.inputLogvars.contains(a.getAtom().getArguments().get(0))){
					this.outputLogvars.add(a.getAtom().getArguments().get(0));
				}
			}
		}
	}
	
	private void addToLiteralList(List<L> atomList) {
		for(L a:atomList){
			addToLiteralList(a);
		}
	}
	
	public void addRestrictionForLogvar(List<LogvarRestrictionLiteral> differentLogvarsRestriction) {
		this.restrictions.addAll(differentLogvarsRestriction);
	}
	
	@Override
	public boolean isWithOperator() {
		return false;
	}

}
