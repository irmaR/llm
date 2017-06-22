package hybrid.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.Renaming;
import hybrid.utils.*;

/**
 * Class representing an atom created from predicate P
 * Fields:
 * - Atom created with predicate p, with logvars "arguments" and represents an internal or external relationship.
 * internal atom represents a relationship between two logvars of same type (e.g., friends/2 is a predicate and friends(A,B) is 
 * a relationship between two people. External atom represents a relationship between two different logvars (e.g., grade(S,C), represents a
 * relationship between a Student and a Course).
 * - 
 * @author irma
 *
 */

public class Atom implements Serializable {

	protected RelationType relType; //relation type denotes internal relation (friend(person,person)) or external relation grade(student, course)
	protected String atom;
	protected Predicate predicate;
	protected List<Logvar> arguments;
	protected long id;


	/**
	 * Create atom with predicate p and a list of logvars
	 * @param p
	 * @param argument
	 */
	public Atom(Predicate p,List<Logvar> arguments){
		this.predicate=p;
		this.arguments=arguments;
		this.atom=this.createFOLTerm();
		determineRelationType();
		this.id = GenerateUniqueIDforAtom.getID();
	}

	/**
	 * Create atom with predicate p and a list of logvars
	 * @param p
	 * @param argument
	 */
	public Atom(Predicate p,Logvar[] arguments){
		this.predicate=p;
		this.arguments=Arrays.asList(arguments);
		this.atom=createFOLTerm();
		determineRelationType();
		this.id = GenerateUniqueIDforAtom.getID();
	}

	/**
	 * This method compares this term with another term. It returns true if the arguments
	 * of both terms (excluding the value term) have the same argument names.
	 * @param term
	 * @return
	 */
	public boolean hasSameArgumentNames(Atom term){
		int arg_index=0;
		if(this.getArguments().size()!=term.getArguments().size()){
			return false;
		}
		for(Logvar l:this.arguments){
			if(!term.getArgument(arg_index++).equals(l)){
				return false; 
			}
		}
		return true;
	}

	/**
	 * Ground atom with constants and assign value val
	 * @param constants
	 * @param val
	 * @return
	 */
	public GroundAtom ground(Constant[] constants,Value val){
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		if(constants.length!=this.arguments.size()){
			throw new NullPointerException("The number of constants doesn't correspond to the number of arguments!");
		}
		int i=0;
		for(Logvar l:this.arguments){
			subst.put(l, constants[i++]);
		}
		GroundAtom g=new GroundAtom(this,new Subst(subst),val);
		return g;
	}


	public boolean hasInRange(Value val) throws WrongValueType {
		if(this.predicate.getRange().isInRange(val)){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Apply renaming and exclude the input atom
	 * @param renaming
	 * @param atom TODO
	 * @return - the list of renamed atoms
	 */
	public List<Atom> applyRenaming(Renaming renaming){
		//there is no applicable renaming - return the original atom
		if(!renaming.possible(this)){
			ArrayList<Atom> tmp=new ArrayList<Atom>();
			tmp.add(this);
			return tmp;
		}
		//apply the renaming
		//Is it EXTERNAL atom?
		if(this instanceof TestRandvarValue){
			System.out.println("RENAMING TEST RANDVAD @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			return ((TestRandvarValue) this).renameExternalAtom(renaming);
		}
		if(this.relType.equals(RelationType.EXTERNAL)){
			//System.out.println(" Renaming external + size is 2");
			return this.renameExternalAtom(renaming);
		}
		//else it is INTERNAL
		else{
			return this.renameINTERNAL_Atom(renaming);	
		}
	}

	/**
	 * Renaming internal atom (e.g., friend(S,S))
	 * With internal atoms it's a bit different because their logvars are 
	 * of the same type. So if we give the following renaming: student --> S1, S2 we can have 
	 * different combinations of renaming for friend(S,S):
	 * friend(S1,S2)
	 * friend(S2,S1)
	 * friend(S1,S1) ---> disallowed
	 * friend(S2,S2) ---> disallowed
	 * @param renaming
	 * @return
	 */
	public List<Atom> renameINTERNAL_Atom(Renaming renaming) {
		List<Atom> tmp=new ArrayList<Atom>();
		Type firstArgumentType=this.arguments.get(0).getType();

		//only one argument atom
		if(this.arguments.size()==1){
			for(Logvar l1:renaming.getRenaming().get(firstArgumentType)){
				Atom newAtom=new Atom(this.predicate,new Logvar[]{l1});
				tmp.add(newAtom);
			}
			return tmp;
		}
		//the internal atom has two possible arguments
		else{
			HashSet<Logvar> renamings=renaming.getRenaming().get(this.arguments.get(0).getType());
			for(Logvar l1:renamings){
				for(Logvar l2:renamings){
					if(!l1.equals(l2)){
						Logvar[] logvarTemp=new Logvar[2];
						logvarTemp[0]=l1;
						logvarTemp[1]=l2;
						Atom newAtom=new Atom(this.predicate,logvarTemp);
						tmp.add(newAtom);
					}
				}
			}
			return tmp;
		}
	}
	/**
	 * Renaming external atom
	 * @param renaming - renaming is an object saying what should be renamed logvar type -> to a number of new logvars
	 * @param atom - return all renamed atoms according to the renaming
	 * @return
	 */
	protected List<Atom> renameExternalAtom(Renaming renaming) {
		List<Atom> tmp=new ArrayList<Atom>();
		Type firstArgumentType=this.arguments.get(0).getType();
		Type secondArgumentType=this.arguments.get(1).getType();
		HashSet<Logvar> first_argument_renamings=null;
		HashSet<Logvar> second_argument_renamings=null;

		//first argument renamings
		if(renaming.getRenaming().containsKey(firstArgumentType)){
			first_argument_renamings=renaming.getRenaming().get(firstArgumentType);
		}
		else{
			first_argument_renamings=new HashSet<Logvar>();
			first_argument_renamings.add(this.arguments.get(0));
		}

		//second argument renamings
		if(renaming.getRenaming().containsKey(secondArgumentType)){
			second_argument_renamings=renaming.getRenaming().get(secondArgumentType);
		}
		else{
			second_argument_renamings=new HashSet<Logvar>();
			second_argument_renamings.add(this.arguments.get(1));
		}

		for(Logvar l1:first_argument_renamings){
			//second argument type
			for(Logvar l2:second_argument_renamings){
				Logvar[] logvarTemp=new Logvar[2];
				logvarTemp[0]=l1;
				logvarTemp[1]=l2;
				Atom newAtom=new Atom(this.predicate,logvarTemp);
				tmp.add(newAtom);
			}
		}
		return tmp;
	}




	public RelationType getRelType() {
		return relType;
	}

	public String getAtom() {
		return atom;
	}

	public long getId() {
		return id;
	}

	public String toString(){
		return this.createFOLTerm();
	}


	public Logvar getArgument(int i){
		return this.arguments.get(i);

	}

	public boolean isInternal(){
		if(this.relType.equals(RelationType.INTERNAL)){
			return true;
		}
		else{
			return false;
		}
	}


	public List<Logvar> getArguments() {
		return arguments;
	}

	public RelationType getRelationType(){
		return this.relType;
	}


	@Override
	public int hashCode() {
		int result=0;
		int i=1;
		for(Logvar l:this.arguments){
			result+=l.hashCode()*(i++);
		}
		return result+predicate.hashCode();	
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

	public boolean hasCommonArguments(Atom atom) {
		for(Logvar l:this.arguments){
			if(atom.getArguments().contains(l)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if this logvar is contained in the arguments of this atom
	 * @param logvar
	 * @return
	 */
	public boolean doArgumentsContainLogvar(Logvar logvar){
		for(Logvar l:this.arguments){
			if(this.arguments.contains(logvar)){
				return true;
			}
		}
		return false;
	}


	/**
	 * Find the intersection of logvar of this object with the logvars of the input atom
	 * @param atom
	 * @return
	 */
	public List<Logvar> findArgumentIntersectionWith(Atom atom) {
		List<Logvar> tmp=new ArrayList<Logvar>();
		for(Logvar l:this.arguments){
			if(atom.getArguments().contains(l)){
				tmp.add(l);
			}
		}
		return tmp;
	}

	/**
	 * Find logvars present in this object that are not present in the input atom
	 * @param atom
	 * @return
	 */
	public List<Logvar> findArgumentDifference(Atom atom) {
		List<Logvar> tmp=new ArrayList<Logvar>();
		for(Logvar l:atom.arguments){
			if(this.arguments.contains(l)){
				continue;
			}
			tmp.add(l);
		}
		return tmp;
	}

	/**
	 * determining the relation type. If the logvars are same (having the same 
	 * type) then the relationship is internal (friend(S,S1)). Otherwise, it is external, e.g., 
	 * grade(S,C)
	 */
	private void determineRelationType() {
		if(arguments.size()==1){
			this.relType=RelationType.INTERNAL;
		}
		else{
			if(arguments.size()==0){
				this.relType=RelationType.INTERNAL;
				return;
			}
			if(arguments.get(0).getType().equals(arguments.get(1).getType())){
				this.relType=RelationType.INTERNAL;
				//do renaming of the second argument
				if(arguments.get(0).getType().equals(arguments.get(1).getType())){
					Logvar l1=this.arguments.get(0).copyAndRename("");
					Logvar l2=this.arguments.get(0).copyAndRename("1");
					this.arguments=new ArrayList<Logvar>();
					this.arguments.add(l1);
					this.arguments.add(l2);
				}
			}
			else{
				this.relType=RelationType.EXTERNAL;
			}
		}
	}

	/**
	 * get types present in this atom
	 * @return
	 */
	public Set<Type> getTypes(){
		Set<Type> types=new HashSet<Type>();
		for(Logvar l:this.arguments){
			types.add(l.getType());
		}
		return types;
	}

	public Predicate getPredicate() {
		return predicate;
	}



	public boolean isRandVarTest() {
		return false;
	}

	public void setRelationType(RelationType rel_type) {
		this.relType=rel_type;
	}


	public String createFOLTerm() {
		if(this.arguments.size()==0){
			return this.predicate.getPredicateName();
		}
		if(this.arguments.size()==1){
			return this.predicate.getPredicateName()+"("+this.arguments.get(0).getSymbol()+")";
		}
		String tmp=this.predicate.getPredicateName()+"(";
		for(int i=0;i<this.arguments.size()-1;i++){
			tmp+=this.arguments.get(i).getSymbol()+",";
		}
		tmp+=this.arguments.get(this.arguments.size()-1).getSymbol()+")";
		return tmp;
	}





}
