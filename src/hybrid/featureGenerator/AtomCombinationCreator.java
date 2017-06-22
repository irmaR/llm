package hybrid.featureGenerator;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.RelationType;
import hybrid.network.TestRandvarValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 * Getting all possible permutations for the atoms in the list (up to length n)
 * @author irma
 *
 */
public class AtomCombinationCreator {

	/**
	 * Create all possible combinations of literals up made of atoms in argument name "atoms", of a predefined length, based on
	 * head atom and maximum number of logvars per conjunction. 
	 * @param atoms - atoms making the conjunction
	 * @param length - the length of the conjunction
	 * @param head - head atom
	 * @param max_num_logvars - maximum number of logvars
	 * @return
	 */
	public List<ProposedConjunction> getallPossibleCombinationUpToLength(List<? extends Literal> atoms,int length, Atom head, int max_num_logvars,int max_num_boolean_literals){
		return this.getallPossibleCombinationUpToLength(atoms.toArray(new Literal[atoms.size()]), length, head, max_num_logvars,max_num_boolean_literals);
	}

	/**
	 * Create all possible combinations of only POSITIVE literals up made of atoms in argument name "atoms", of a predefined length, based on
	 * head atom and maximum number of logvars per conjunction. 
	 * @param atoms - positive literals making the conjunction
	 * @param length - the length of the conjunction
	 * @param head - head atom
	 * @param max_num_logvars - maximum number of logvars
	 * @return
	 */
	public List<ProposedConjunction> getallPossibleCombinationUpToLength(Literal[] atoms,int length, Atom head, int max_num_logvars,int max_num_boolean_literals){
		List<ProposedConjunction> tmp=new ArrayList<ProposedConjunction>();
		List<ProposedConjunction> currentConjunctions=new ArrayList<ProposedConjunction>();
		int length_tmp=1;
		//creating length 1

		currentConjunctions=getAllUnaryProposedConjunctions(atoms,head,max_num_logvars);
		tmp.addAll(currentConjunctions);
		//Creating other combinations
		while(length_tmp<length){	
			currentConjunctions=addAtomToAllCurrentConjunctions(currentConjunctions,atoms, head,max_num_logvars,max_num_boolean_literals);
			tmp.addAll(currentConjunctions);
			length_tmp++;
		}
		return tmp;	
	}
	/**
	 * Getting all the unary proposed conjunctions // there is some filtering here
	 * @param atoms
	 * @param head
	 * @param max_num_logvars
	 * @return
	 */
	protected List<ProposedConjunction> getAllUnaryProposedConjunctions(Literal[] atoms,Atom head,int max_num_logvars){
		List<ProposedConjunction> tmp=new ArrayList<ProposedConjunction>();
		for(Literal at:atoms){	
			ProposedConjunction newProp=new ProposedConjunction(at, max_num_logvars);
			//if there is no common logvars with this atom and head, discard the proposed conjunction
			if(hybrid.utils.SetManipulations.getIntersection(newProp.getBindingTypes(), head.getTypes()).size()==0){
				continue;
			}

			//if no renaming possible and the atom is the same as head don't add
			if((at.getAtom().getPredicate().getPredicateName().equals(head.getPredicate().getPredicateName())) && (newProp.getRenaming().getRenaming().isEmpty())){
				//System.out.println("If atom is a randvar test, there is no renaming and its predicate is equvalent to the predicate ");
				continue;
			}

			if(head.getArguments().size()>1 && head.getRelationType().equals(RelationType.INTERNAL)){
				newProp.addToRenaming(head.getArgument(0));
				newProp.addToRenaming(head.getArgument(1));
			}

			tmp.add(newProp);
		}
		return tmp;
	}

	/**
	 * This method is for assigning new atoms to the current proposed conjunctions. This method contains a number of 
	 * restrictions for this additions
	 * @param currentConjunctions
	 * @param atoms
	 * @param head
	 * @return
	 */
	private List<ProposedConjunction> addAtomToAllCurrentConjunctions(List<ProposedConjunction> currentConjunctions, Literal[] atoms, Atom head,int max_num_logvars,int max_num_boolean_literals) {
		List<ProposedConjunction> tmp=new ArrayList<ProposedConjunction>();
		HashSet<Long> idSums=new HashSet<Long>();

		for(ProposedConjunction p:currentConjunctions){
			for(Literal a:atoms){	
				if(a.getAtom() instanceof TestRandvarValue & p.getNrRandvarTests()>=AlgorithmParameters.getNrRandvarTestPerFeature()){
					continue;
				}

				if(a.getAtom() instanceof TestRandvarValue & p.hasRandvarTestFor(a.getAtom().getPredicate())){
					continue;
				}

				if(a.getAtom() instanceof TestRandvarValue & p.getAtomList().contains(a)){
					continue;
				}

				//if we are adding a test rand var and in the head we have the same 
				if((a.getAtom() instanceof TestRandvarValue) & (a.getAtom().getPredicate().getPredicateName().equals(head.getPredicate().getPredicateName()) & (a.getAtom().hasSameArgumentNames(head)))){
					continue;
				}

				//if we are adding a literal, and there is a randvar test on this literal already included don't create this conjunction
				if(!(a.getAtom() instanceof TestRandvarValue || a.getAtom().getPredicate().isBoolean()) & p.hasBooleanLiteralWithPredicate(a.getAtom().getPredicate())){
					continue;
				}

				//if we are adding a test rand var and the feature already contains a more general literal 
				if((a.getAtom() instanceof TestRandvarValue) & p.hasNonBooleanLiteralWithPredicate(a.getAtom().getPredicate())){
					continue;
				}

				List<Literal> tmp_conjunction=new ArrayList<Literal>();
				tmp_conjunction.add(a);
				tmp_conjunction.addAll(p.getAtomList());
				ProposedConjunction newProp = null;

				try {
					try {
						newProp = p.extend(a);
					} catch (DuplicateAtom e) {
						continue;
					}
				} catch (NoCommonTypeWithCurrentProposedConjunction e) {
					continue;
				}

				if(newProp.getNr_booleanVars()>max_num_boolean_literals){
					continue;
				}

				if(newProp.isHasMoreThanOneNumericCategoricalAtom()){
					continue;
				}

				//If the proposed conjunction has duplicate boolean atoms but no possible renaming,
				//don't add this combination to the possible set.
				if(hasDuplicateAtomAndNoRenaming(newProp)){
					continue;
				}

				/**
				 * No duplicate combination inspected by comparing the sum of ids for this
				 * combination to the ones hashed in the idSums.
				 */
				if(noDuplicateCombinations(idSums,newProp)){
					tmp.add(newProp);
					idSums.add(newProp.getSumOfIds());
				}
			}
		}
		return tmp;
	}

	private boolean noDuplicateCombinations(HashSet<Long> idSums, ProposedConjunction newProp) {
		if(!idSums.contains(newProp.getSumOfIds())){
			return true;
		}
		return false;
	}

	protected boolean hasDuplicateAtomAndNoRenaming(ProposedConjunction newProp) {
		if(newProp.isHasDuplicateBooleanAtoms() && newProp.getRenaming()==null){
			return true;
		}
		else{
			return false;
		}
	}
}
