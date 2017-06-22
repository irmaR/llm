package hybrid.featureGenerator;

import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.utils.CartesianProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenamedConjunctionsCreator {

	/**
	 * Given a list of proposed conjunctions with specified possible renamings, 
	 * apply these renamings and return the list of conjunctions. The renaming is obtained
	 * by creating a Cartesian product of all possible renamings applies to each atom in 
	 * a conjunction. E.g., if this is the following conjunction: Renaming={S->S, S->S1,S->S2}, friend(S,S1),grade(S,C)
	 * then we will have cartesian product between these two sets:
	 * {friend(S,S1),friend(S1,S),friend(S2,S),friend(S1,S2),friend(S2,S1),friend(S,S2)}x{grade(S,C),grade(S1,C),grade(S2,C)}
	 * That is, when the renaming is applied we will have: 18 renamed conjunctions: 6x3
	 * @param proposedConjuctions
	 * @param head
	 * @return
	 */
	protected List<Standard_Conjunction> createAllRenamingCombinations(List<ProposedConjunction> proposedConjuctions, Atom head) {
		List<Standard_Conjunction> tmp=new ArrayList<Standard_Conjunction>();
		CartesianProduct cP=new CartesianProduct();
        boolean possible_to_rename=false;
		List<ProposedConjunction> removedProposedConjunctions=new ArrayList<ProposedConjunction>();
       
		for(ProposedConjunction a:proposedConjuctions){
			List<List<Literal>> testList=new ArrayList<List<Literal>>();
			for(Literal literal:a.getAtomList()){
				if(!a.getRenaming().getRenaming().isEmpty()){
					List<Atom> list1=literal.getAtom().applyRenaming(a.getRenaming());
					List<Literal> literals=literal.getLiterals(list1);
					//if it happens that we have intelligence(S) as the head and we try to add intelligence(S,int0) testRandVarValue, it shouldn't be allowed
					//it is solved by this simple condition
					if(literal.getAtom().getPredicate().equals(head.getPredicate()) && !(literal.getAtom().hasSameArgumentNames(head))){
					  literals.add(literal);
					}				
					testList.add(literals);
				}
				else{
					testList.add(Arrays.asList(literal));
				}
			}
			List<List<Literal>> renamings=cP.cartesianProduct(testList);
			
			for(List<Literal> l:renamings){
				Standard_Conjunction p=null;
				//if duplicate literals in the list, remove
				if(containsCopy(l)){
					continue;
				}
				try{
					p=new Standard_Conjunction(head,l.toArray(new Literal[l.size()]));
					
				}
				catch(ConjunctionConstructionProblem e){
					removedProposedConjunctions.add(a);
					continue;
				}
				catch(NullPointerException e){
					removedProposedConjunctions.add(a);
					continue;
				}
				p.setInputLogvar(head.getArguments());
				possible_to_rename=true;
				tmp.add(p);
			}
			//think if this below needed!
			//if renaming not applicable
			/*if(!possible_to_rename){
				try{
				  Conjunction new_conjunction=new Conjunction(head,a.getAtomList());
				  System.out.println("EXTRAAAAA Conjunction created "+new_conjunction);
				  tmp.add(new_conjunction);
					
				}
				catch(ConjunctionConstructionProblem e){
					removedProposedConjunctions.add(a);
					continue;
				}
				catch(NullPointerException e){
					removedProposedConjunctions.add(a);
					continue;
				}
			}*/
			possible_to_rename=false;
		}
		
        return tmp;
	}
	
	
	/**
	 * Given a list of proposed conjunctions with specified possible renamings, 
	 * apply these renamings and return the list of conjunctions. The renaming is obtained
	 * by creating a Cartesian product of all possible renamings applies to each atom in 
	 * a conjunction. E.g., if this is the following conjunction: Renaming={S->S, S->S1,S->S2}, friend(S,S1),grade(S,C)
	 * then we will have cartesian product between these two sets:
	 * {friend(S,S1),friend(S1,S),friend(S2,S),friend(S1,S2),friend(S2,S1),friend(S,S2)}x{grade(S,C),grade(S1,C),grade(S2,C)}
	 * That is, when the renaming is applied we will have: 18 renamed conjunctions: 6x3
	 * @param proposedConjuctions
	 * @param head
	 * @return
	 */
	protected List<Standard_Conjunction> createAllRenamingCombinationsWithLogvarRestrictions(List<ProposedConjunction> proposedConjuctions, Atom head,String restriction) {
		List<Standard_Conjunction> tmp=new ArrayList<Standard_Conjunction>();
		CartesianProduct cP=new CartesianProduct();
        boolean possible_to_rename=false;
		List<ProposedConjunction> removedProposedConjunctions=new ArrayList<ProposedConjunction>();
       
		for(ProposedConjunction a:proposedConjuctions){
			List<List<Literal>> testList=new ArrayList<List<Literal>>();
			for(Literal literal:a.getAtomList()){
				if(!a.getRenaming().getRenaming().isEmpty()){
					List<Atom> list1=literal.getAtom().applyRenaming(a.getRenaming());
					List<Literal> literals=literal.getLiterals(list1);
					//if it happens that we have intelligence(S) as the head and we try to add intelligence(S,int0) testRandVarValue, it shouldn't be allowed
					//it is solved by this simple condition
					if(literal.getAtom().getPredicate().equals(head.getPredicate()) && !(literal.getAtom().hasSameArgumentNames(head))){
					  literals.add(literal);

					}
					
					testList.add(literals);
				}
				else{
					testList.add(Arrays.asList(literal));
				}
			}
			List<List<Literal>> renamings=cP.cartesianProduct(testList);
			for(List<Literal> l:renamings){
			//	allLiterals.addAll(a.getRenaming().getDifferentLogvarsRestriction(restriction));
				Standard_Conjunction p=null;
				//if duplicate literals in the list, remove
				if(containsCopy(l)){
					continue;
				}
				try{
					
					p=new Standard_Conjunction(head,l);
					p.addRestrictionForLogvar(a.getRenaming().getDifferentLogvarsRestriction(restriction));
					//System.out.println(p);
				}
				catch(ConjunctionConstructionProblem e){
					removedProposedConjunctions.add(a);
					continue;
				}
				
				p.setInputLogvar(head.getArguments());
				possible_to_rename=true;
				tmp.add(p);
			}
			//if renaming not applicable
			if(!possible_to_rename){
				try{
				  Standard_Conjunction new_conjunction=new Standard_Conjunction(head,a.getAtomList());
				  tmp.add(new_conjunction);
					
				}
				catch(ConjunctionConstructionProblem e){
					removedProposedConjunctions.add(a);
					continue;
				}

			}
			possible_to_rename=false;
		}
		
        return tmp;
	}
	
	
	
	 private boolean containsCopy(List<Literal> l) {
	    	Set<Literal> set = new HashSet<Literal>(l);
			if(set.size()<l.size()){
				return true;
			}
			else{
				return false;
			}
		}
	
	
}
