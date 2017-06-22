package hybrid.queryMachine;

import hybrid.network.Atom;
import hybrid.network.Literal;
import hybrid.network.Predicate;
import hybrid.network.RelationType;

import java.util.Comparator;
import java.util.HashMap;



public class InternalComparator implements Comparator<Literal> {

		@Override public int compare(Literal o1, Literal o2) {
			if(!(OrderOfBooleanPredicates.getRankingOfBooleanPreds()==null) && !OrderOfBooleanPredicates.getRankingOfBooleanPreds().isEmpty()){
				//order is not default but is set by the user
				return orderNonDefault(o1,o2,OrderOfBooleanPredicates.getRankingOfBooleanPreds());
			}
			
			if (o1.getAtom().getRelationType().equals(RelationType.INTERNAL) && o1.getAtom().getArguments().size()>1) {
				return -1;
			}
			if((o1.getAtom().getPredicate().isBoolean() && !o1.getAtom().isRandVarTest()) && o2.getAtom().isRandVarTest()){
				return -1;
			}
			else if ((o2.getAtom().getPredicate().isBoolean() && !o2.getAtom().isRandVarTest()) && o1.getAtom().isRandVarTest()){
				return 1;
			}
			else{
				return 0;
			}		
		}

		private int orderNonDefault(Literal o1,Literal o2,HashMap<String, Integer> rankingOfBooleanPreds) {
			Integer index1=null;
			Integer index2=null;
            //System.out.println(rankingOfBooleanPreds);
			try{
		      index1=rankingOfBooleanPreds.get(o1.getAtom().getPredicate().getPredicateName());
			}
			catch (NullPointerException e) {
				index1=-1;
			}
			try{
		     index2=rankingOfBooleanPreds.get(o2.getAtom().getPredicate().getPredicateName());
			}
			catch (NullPointerException e) {
				index2=-1;
			}
			//exception!!!, we are always trying to put booleans and randvars in front
			if((o1.getAtom().isRandVarTest() || o1.getAtom().getPredicate().isBoolean()) && !(o2.getAtom().isRandVarTest() || o2.getAtom().getPredicate().isBoolean())){
				return -1;
			}
			if((o2.getAtom().isRandVarTest() || o2.getAtom().getPredicate().isBoolean()) && !(o1.getAtom().isRandVarTest() || o1.getAtom().getPredicate().isBoolean())){
				return 1;
			}
			//System.out.println(" Lit 1: "+o1+" Lit2: "+o2);
			//System.out.println(" Index1: "+index1+" Index2: "+index2);
			
			if(index1==null && index2==null){
				//in case it is not specified, return default setting
				if (o1.getAtom().getRelationType().equals(RelationType.INTERNAL) && o1.getAtom().getArguments().size()>1) {
					return -1;
				}
				if((o1.getAtom().getPredicate().isBoolean() && !o1.getAtom().isRandVarTest()) && o2.getAtom().isRandVarTest()){
					return -1;
				}
				else if ((o2.getAtom().getPredicate().isBoolean() && !o2.getAtom().isRandVarTest()) && o1.getAtom().isRandVarTest()){
					return 1;
				}
				else{
					return 0;
				}		
			}
			
			if(index1==null){
				return 1;
			}
			if(index2==null){
				return -1;
			}
			
			if(index1<index2){
				return -1;
			}
			if(index1>index2){
				return 1;
			}
			if((index1==index2)){
				return 0;
			}
			
			return 0;
		}

	}

