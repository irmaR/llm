package hybrid.featureGenerator;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NumericalPredicate;
import hybrid.network.Predicate;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a conjunction of predicates determined only by the type of their logvars. 
 * E.g, a proposed conjunction might be [friend(S,S),friend(S,S)]. With this proposed conjunction, this class then
 * extracts all possible renamings. E.g., since there are three persons can be involved in this conjunction (e.g., 
 * [friend(pete,ann),friend(ann,john)], we allow three possible renamings: S,S1 and S2. So the final conjunction can be
 * [friend(S,S1),friend(S1,S2)], (where for the previous grounded example: S=pete, S1=ann, S2=john.
 * @author irma
 *
 */

public class ProposedConjunction implements Serializable{	
	private List<Literal> atomList;
	private boolean hasMoreThanOneNumericCategoricalAtom=false;
	private boolean hasInternalAtom=false;
	private Renaming renaming;
	private int nr_booleanVars;
	private int nr_non_booleanPredicates;
	private boolean hasDuplicateBooleanAtoms=false;
	private Set<Type> bindingTypes; 
	private int max_num_logvars;

	/**
	 * Create an empty proposed conjunction
	 */
	public ProposedConjunction(){
		this.bindingTypes=new HashSet<Type>();
		this.atomList=new ArrayList<Literal>();
	}

	/**
	 * Create proposed conjunction with a list of literals
	 * @param literals
	 * @param max_num_logvars
	 */
	public ProposedConjunction(List<Literal> literals,int max_num_logvars){
		ProposedConjunction initial=new ProposedConjunction(literals.get(0),max_num_logvars);
		ProposedConjunction tmp=null;
		for(int i=1;i<literals.size();i++){
			tmp=null;
			try {
				tmp=initial.extend(literals.get(i));

			} catch (NoCommonTypeWithCurrentProposedConjunction e) {
				e.printStackTrace();
			} catch (DuplicateAtom e) {
				e.printStackTrace();
			}
		}
		this.renaming=tmp.renaming.deep_copy();
		this.atomList=new ArrayList<Literal>();
		this.bindingTypes=new HashSet<Type>();
		this.atomList.addAll(tmp.atomList);
		this.bindingTypes.addAll(tmp.bindingTypes);
		this.hasDuplicateBooleanAtoms=tmp.hasDuplicateBooleanAtoms;
		this.hasInternalAtom=tmp.hasInternalAtom;
		this.nr_non_booleanPredicates=tmp.nr_non_booleanPredicates;
		this.max_num_logvars=tmp.max_num_logvars;
		this.nr_booleanVars=tmp.nr_booleanVars;
		this.nr_non_booleanPredicates=tmp.nr_non_booleanPredicates;
	}

	/**
	 * Creating proposed conjunction of length 1
	 * @param a
	 * @param max_num_logvars TODO
	 * @throws NoCommonTypeWithCurrentProposedConjunction
	 */
	public ProposedConjunction(Literal a, int max_num_logvars){
		this.bindingTypes=new HashSet<Type>();
		this.determineBindingTypes(a);
		this.atomList=new ArrayList<Literal>();
		this.atomList.add(a);
		this.max_num_logvars=max_num_logvars;
		determinePredicateTypes(a);
		performLogvarRenaming(max_num_logvars);
		determineBindingTypes(a);
	}

	private void determineBindingTypes(Literal a) {
		for(Logvar arg:a.getAtom().getArguments()){
			this.bindingTypes.add(arg.getType());
		}

	}
	
   /**
    * Extend this proposed conjunction with a literal
    * @param a
    * @return
    * @throws NoCommonTypeWithCurrentProposedConjunction
    * @throws DuplicateAtom
    */
	public ProposedConjunction extend(Literal a) throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{		
		if(!this.hasCommonTypes(a.getAtom())){
			throw new NoCommonTypeWithCurrentProposedConjunction();
		}
		//If the conjunction already contains a non-boolean predicate and we want to add a predicate that is non boolean or not a randvar test
		//then we throw DuplicateAtom exception
		if(nr_non_booleanPredicates>0 && !(a.getAtom().getPredicate().isBoolean() || a.getAtom().isRandVarTest())){
			throw new DuplicateAtom();
		}
		ProposedConjunction tmp=new ProposedConjunction();
		tmp.renaming=this.renaming.deep_copy();
		tmp.atomList.addAll(this.atomList);
		tmp.bindingTypes.addAll(this.bindingTypes);
		tmp.hasDuplicateBooleanAtoms=this.hasDuplicateBooleanAtoms;
		tmp.hasInternalAtom=this.hasInternalAtom;
		tmp.nr_non_booleanPredicates=this.nr_non_booleanPredicates;
		tmp.atomList.add(a);
		tmp.max_num_logvars=this.max_num_logvars;
		if(a.getAtom().getPredicate() instanceof BooleanPred){
			tmp.nr_booleanVars=this.nr_booleanVars++;
		}
		else{
			tmp.nr_booleanVars=this.nr_booleanVars;
		}
		tmp.determineBindingTypes(a);
		tmp.performLogvarRenaming(max_num_logvars);		
		return tmp;
	}

    /**
     * Get the list of the atoms
     * @return
     */
	public List<Literal> getAtomList() {
		return atomList;
	}

	
	
	private void determinePredicateTypes(Literal a) {
		if(a.getAtom().getPredicate() instanceof BooleanPred){
			this.nr_booleanVars++;
		}
		else{
			this.nr_non_booleanPredicates++;
		}

	}


	public String toString(){
		String tmp=this.renaming.toString();
		for(Literal a:atomList){
			tmp+=a.toString()+" ";
		}
		return tmp+ "\n";
	}
	/**
	 * Check if the list of literal (excluding booleans and randvar tests) contains a literal originating from predicate pred
	 * @param pred
	 * @return
	 */
	public boolean hasNonBooleanLiteralWithPredicate(Predicate pred){
		for(Literal lit:this.atomList){
			if(!(lit.getAtom().isRandVarTest() | lit.getAtom().getPredicate().isBoolean())){
				if(lit.getAtom().getPredicate().getPredicateName().equals(pred.getPredicateName())){
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasBooleanLiteralWithPredicate(Predicate pred) {
		for(Literal lit:this.atomList){
			if(lit.getAtom().isRandVarTest() | lit.getAtom().getPredicate().isBoolean()){
				if(lit.getAtom().getPredicate().getPredicateName().equals(pred.getPredicateName())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if this proposed conjunction has randvar tests
	 * @return
	 */
	public boolean hasRandvarTest() {
		for(Literal lit:this.atomList){
			if(lit.getAtom().isRandVarTest()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the number of randvar tests in this proposed conjunction
	 * @return
	 */
	public int getNrRandvarTests() {
		int i=0;
		for(Literal lit:this.atomList){
			if(lit.getAtom().isRandVarTest()){
				i++;
			}
		}
		return i;
	}


	/**
	 * The method performs renaming of the proposed conjunction
	 */
	private void performLogvarRenaming(int max_num_logvars){
		if(this.renaming==null){
			this.renaming=new Renaming();
		}
		for(Literal a:this.atomList){
			//In case the atom is internal we have to introduce renamings for their logvars
			if(a.getAtom().getRelType().equals(hybrid.network.RelationType.INTERNAL) && a.getAtom().getArguments().size()>1){	
				this.hasInternalAtom=true;
				if(this.renaming.getRenaming().containsKey(a.getAtom().getArgument(0).getType())){
					if(!this.renaming.getRenaming().isEmpty() && max_num_logvars>0 && this.renaming.getRenaming().get(a.getAtom().getArgument(0).getType()).size()>=max_num_logvars){
						return;
					}
				}
				else if(this.renaming.getRenaming().containsKey(a.getAtom().getArgument(1).getType())){
					if(!this.renaming.getRenaming().isEmpty() && max_num_logvars>0 && this.renaming.getRenaming().get(a.getAtom().getArgument(1).getType()).size()>=max_num_logvars){
						return;
					}
				}
				//add main argument name
				addToRenaming(a.getAtom().getArgument(0));

				//add one more renaming
				if(!this.renaming.getRenaming().isEmpty() && max_num_logvars>0 && this.renaming.getRenaming().get(a.getAtom().getArgument(1).getType()).size()>=max_num_logvars){
					return;
				}
				if(a.getAtom().getArgument(0).getType().equals(a.getAtom().getArgument(1))){
					addToRenaming(new Logvar(a.getAtom().getArgument(0).getSymbol()+(this.renaming.getRenaming().get(a.getAtom().getArgument(0).getType()).size()),a.getAtom().getArgument(0).getType()));
				}
				else{
					addToRenaming(new Logvar(a.getAtom().getArgument(0).getSymbol()+(this.renaming.getRenaming().get(a.getAtom().getArgument(0).getType()).size()),a.getAtom().getArgument(0).getType()));
					//addToRenaming(new Logvar(a.getAtom().getArgument(1).getSymbol()+(this.renaming.getRenaming().get(a.getAtom().getArgument(1).getType()).size()),a.getAtom().getArgument(1).getType()));
				}
			}
		}

	}
    
    /**
     * Get sum of hashcodes of literals in this 
     * proposed conjunction
     * @return
     */
	public long getSumOfIds(){
		long tmp=0;
		for(Literal a:atomList){
			tmp+=a.hashCode();
		}
		return tmp;
	}


	/**
	 * return true if the boolean atom is already included in this conjunction
	 * @param atom
	 * @return
	 */
	protected boolean setDuplicateBoolean(Literal atom) {
		if(this.hasDuplicateBooleanAtoms==false){
			if(atom.getAtom().getPredicate() instanceof BooleanPred && this.atomList.contains(atom)){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}

	private boolean containsListCategoricalOrNumericPredicate(Atom atom) {
		for(Literal a:this.atomList){
			if(a.getAtom() instanceof TestRandvarValue){
				continue;
			}
			if(a.getAtom().getPredicate() instanceof CategoricalPred || a.getAtom().getPredicate() instanceof NumericalPredicate){
				return true;
			}
		}
		return false;

	}

	public boolean isHasMoreThanOneNumericCategoricalAtom() {
		return hasMoreThanOneNumericCategoricalAtom;
	}

	public void setHasMoreThanOneNumericCategoricalAtom(boolean hasMoreThanOneNumericCategoricalAtom) {
		this.hasMoreThanOneNumericCategoricalAtom = hasMoreThanOneNumericCategoricalAtom;
	}

	public boolean hasInternalAtom() {
		return hasInternalAtom;
	}


	public void addToRenaming(Logvar argument) {
		if(this.renaming==null){
			this.renaming=new Renaming();
		}
		this.renaming.addRenaming(argument.getType(), argument);
	}

	public Renaming getRenaming(){
		return this.renaming;
	}

	public boolean isHasDuplicateBooleanAtoms() {
		return hasDuplicateBooleanAtoms;
	}

	public boolean isHasInternalAtom() {
		return hasInternalAtom;
	}

	/**
	 * Get the number of boolean variables
	 * in this proposed conjunction
	 * @return
	 */
	public int getNr_booleanVars() {
		int tmp=0;
		for(Literal a:this.atomList){
			if(a.getAtom().getPredicate() instanceof BooleanPred){
				tmp++;
			}
		}
		return tmp;
	}

	public int getNr_non_booleanPredicates() {
		return nr_non_booleanPredicates;
	}

	public Set<Type> getBindingTypes() {
		return bindingTypes;
	}

	/**
	 * Check if the proposed conjunction has
	 * common logvar types with atom a
	 * @param a
	 * @return
	 */
	public boolean hasCommonTypes(Atom a) {
		for(Logvar t:a.getArguments()){
			if(this.bindingTypes.contains(t.getType())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if this proposed conjunction has a randvar
	 * test on this predicate
	 * @param predicate
	 * @return
	 */
	public boolean hasRandvarTestFor(Predicate predicate) {
		for(Literal a:this.atomList){
			if(a.getAtom() instanceof TestRandvarValue){
				if(a.getAtom().getPredicate().getPredicateName().equals(predicate.getPredicateName())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get number of occurences of literal a
	 * in this proposed conjunction
	 * @param a
	 * @return
	 */
	public int getNrOccurrences(Literal a) {
		int counter=0;
		for(Literal lit:this.atomList){
			if(lit.equals(a)){
				counter++;
			}
		}
		return counter;
	}
    
	/**
	 * Check if this literal addition to the proposed conjunction
	 * will make it to contain a duplicate literal which doesn't
	 * introduce more renaming. For instance, if having proposed conjunction
	 * friend(S,S), adding another friend(S,S) is ok because we can have a renaming of logvars
	 * e.g., [friend(S,S1),friend(S1,S)]
	 * @param a
	 * @return
	 */
	public boolean isDuplicateAndUnrenamable(Literal a) {
		int nr_occurrences=this.getNrOccurrences(a);
		List<Logvar> logvars=a.getAtom().getArguments();
		for(Logvar l:logvars){
			if(this.renaming.getRenaming().containsKey(l.getType())){
				if(nr_occurrences>=1 && (nr_occurrences>=(this.renaming.getRenaming().get(l.getType()).size()-1))){
					return true;				
				}
			}

		}
		return false;
	}







}
