package hybrid.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class containing info of atoms and logvars
 * @author irma
 *
 */
public class NetworkInfo {


	private List<Atom> literals; //literals in the network
	private List<Type> types; //types in the networks
	private HashMap<String,Atom> predicateNameToAtom; //mapping from predicate name to corresponding atom
	private HashMap<DiscretePredicate,List<TestRandvarValue>> discretizedAtomsToRandVarValueTests; //mapping from discrete predicates to the list of possible randvar-value tests
	private List<Atom> discretizedAtoms;//atoms that are discretized
	private List<TestRandvarValue> allRandvarTests;//list of all randvar-value tests
	
	

	//constructors
	//////////////////////////////////////////////////////////////////////////////////////////////
	public NetworkInfo(ArrayList<Atom> atoms,ArrayList<Type> logvars){
		discretizedAtoms=new ArrayList<Atom>();
		allRandvarTests=new ArrayList<TestRandvarValue>();
		predicateNameToAtom=new HashMap<String, Atom>();
		discretizedAtomsToRandVarValueTests=new HashMap<DiscretePredicate, List<TestRandvarValue>>();
		this.literals=atoms;
		this.types=logvars;
		initializeRandvarTests();
		discretizedAtoms=getDiscretizedPredicates(atoms);
		this.predicateNameToAtom=getMappingPredicateNameToAtom(atoms);
	}
	
	public NetworkInfo(Atom[] atoms,Type[] logvars){
		discretizedAtoms=new ArrayList<Atom>();
		allRandvarTests=new ArrayList<TestRandvarValue>();
		predicateNameToAtom=new HashMap<String, Atom>();
		discretizedAtomsToRandVarValueTests=new HashMap<DiscretePredicate, List<TestRandvarValue>>();
		this.literals=new ArrayList(Arrays.asList(atoms));
		this.types=new ArrayList(Arrays.asList(logvars));
		initializeRandvarTests();
		discretizedAtoms=getDiscretizedPredicates(new ArrayList(Arrays.asList(atoms)));
		this.predicateNameToAtom=getMappingPredicateNameToAtom(Arrays.asList(atoms));

	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get a term with a specifi name
	 * @param name
	 * @return
	 */
	public TermRDN getTerm(String name){
		for(Atom a:literals){
			if(a.getPredicate().getPredicateName().equals(name)){
				return new Literal(a);
			}
		}
		return null;

	}
	
	/**
	 * Get an atom with a specific name
	 * @param name
	 * @return
	 */
	public Atom getAtom(String name){
		for(Atom a:literals){
			if(a.getPredicate().getPredicateName().equals(name)){
				return ((Atom)a);
			}
		}
		return null;
	}

	/**
	 * Get only boolean atoms
	 * @return
	 */
	public ArrayList<Atom> getBooleanAtoms() {
		ArrayList<Atom> at=new ArrayList<Atom>();
		for(Atom a: this.literals){
			if(a.getPredicate() instanceof BooleanPred){
				at.add(a);
			}
		}
		return at;
	}

	public List<TestRandvarValue> getRandvarValueTests() {
		return allRandvarTests;
	}
	
	public List<Literal> getRandvarValueTestsAsPositive_And_Negative_Lit() throws LiteralNotDefined {
		List<Literal> literals=new ArrayList();
		for(TestRandvarValue l:this.allRandvarTests){
			literals.add(new PosLiteral(l));
			literals.add(new NegLiteral(l));
		}
		return literals;
	}
	
	public List<Literal> getRandvarValueTestsAsPositiveLit() throws LiteralNotDefined {
		List<Literal> literals=new ArrayList();
		for(TestRandvarValue l:this.allRandvarTests){
			literals.add(new PosLiteral(l));
		}
		return literals;
	}
	
	public List<Literal> getRandvarValueTestsAsNegativeLit() throws LiteralNotDefined {
		List<Literal> literals=new ArrayList();
		for(TestRandvarValue l:this.allRandvarTests){
			literals.add(new NegLiteral(l));
		}
		return literals;
	}
	
	public String toString(){
		String tmp=" ";
		tmp+= " Logvars :\n";
		for(Type l:this.types){
			tmp+=l+ " -- ";
		}
		tmp+="\n";
		for(Atom a: this.literals){
			tmp+=a;
			if(this.discretizedAtomsToRandVarValueTests.containsKey(a.getPredicate())){
			tmp+=" ---> "+this.discretizedAtomsToRandVarValueTests.get(a.getPredicate()); 
			}
			tmp+="\n";
		}
		return tmp;
	}

	public List<Atom> getAtomsAndEqualityConstraints() {
		List<Atom> tmp=new ArrayList<Atom>();
		tmp.addAll(this.literals);
		tmp.addAll(this.allRandvarTests);
		return tmp;
	}
	
	public List<Atom> getEqualityConstraints() {
		List<Atom> tmp=new ArrayList<Atom>();
		tmp.addAll(this.allRandvarTests);
		return tmp;
	}

	public HashMap<String, Atom> getPredicateNameToAtom() {
		return predicateNameToAtom;
	}
	
	public List<Atom> getDiscretizedAtoms(){
		return this.discretizedAtoms;
				
	}

	public List<Atom> getLiterals() {
		return literals;
	}

	public List<Type> getTypes() {
		return types;
	}
	
	/**
	 * Check if there is an atom with a specific name
	 * @param name
	 * @return
	 */
	public boolean isAtom(String name){
		for(Atom a:literals){
			if(a.getPredicate().getPredicateName().equals(name)){
				return true;
			}
		}
		return false;

	}
	
	/**
	 * Check if there is a type with a specific name
	 * @param name
	 * @return
	 */
	public boolean isType(String name){
		for(Type a:types){
			if(a.getName().equals(name)){
				return true;
			}
		}
		return false;

	}
	
	public HashMap<DiscretePredicate, List<TestRandvarValue>> getDiscretizedAtomsToRandVarValueTests() {
		return discretizedAtomsToRandVarValueTests;
	}

	public void setDiscretizedAtomsToRandVarValueTests(
			HashMap<DiscretePredicate, List<TestRandvarValue>> discretizedAtomsToRandVarValueTests) {
		this.discretizedAtomsToRandVarValueTests = discretizedAtomsToRandVarValueTests;
	}

	
	
	private boolean areRandVarValueTestsSetForAllPreds(){
		for(Atom a: getDiscretePredicates(this.literals)){
			if(!this.discretizedAtomsToRandVarValueTests.containsKey(a.getPredicate()) || this.discretizedAtomsToRandVarValueTests.get(a.getPredicate()).size()==0){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Initialize randvar tests for categorical predicates
	 */
	public void initializeRandvarTests() {
		this.allRandvarTests=new ArrayList<TestRandvarValue>();
		for(Atom a:literals){
			if(a.getPredicate() instanceof CategoricalPred || a.getPredicate() instanceof DiscretizedPredicate){
				List<TestRandvarValue> tmp=new ArrayList<TestRandvarValue>();
				for(Value v:((RangeDiscrete)a.getPredicate().getRange()).getValues()){
					TestRandvarValue t=new TestRandvarValue(a.getPredicate(),a.getArguments(),v);
					this.allRandvarTests.add(t);
					tmp.add(t);
					try{
						discretizedAtomsToRandVarValueTests.get(a.getPredicate()).add(t);
					}
					catch(NullPointerException e){
						discretizedAtomsToRandVarValueTests.put((DiscretePredicate) a.getPredicate(), new ArrayList<TestRandvarValue>());
						discretizedAtomsToRandVarValueTests.get(a.getPredicate()).add(t);
					}
				}
			}
		}		
	}
	
	/**
	 * Get discretized predicates in the network
	 * @param atoms
	 * @return
	 */
	private List<Atom> getDiscretizedPredicates(ArrayList<Atom> atoms) {
		List<Atom> tmp=new ArrayList<Atom>();
		for(Atom a:atoms){
			if(a.getPredicate().isDiscretized()){
				tmp.add(a);
			}
		}
		return tmp;
	}
	
	/**
	 * get all discrete predicates
	 * @param atoms
	 * @return
	 */
	private List<Atom> getDiscretePredicates(List<Atom> atoms) {
		List<Atom> tmp=new ArrayList<Atom>();
		for(Atom a:atoms){
			if(a.getPredicate().isDiscrete()){
				tmp.add(a);
			}
		}
		return tmp;
	}


	private HashMap<String,Atom> getMappingPredicateNameToAtom(List<Atom> atoms) {
		HashMap<String,Atom> tmp=new HashMap<String, Atom>();
		for(Atom a:atoms){
			tmp.put(a.getPredicate().getPredicateName(), a);
		}
		return tmp;
		
	}



	
	

	

	
	
	
	
	
		
}
