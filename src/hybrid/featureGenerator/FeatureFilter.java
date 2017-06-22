package hybrid.featureGenerator;

import hybrid.network.Logvar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Deprecated
public class FeatureFilter {

	public List<Standard_Conjunction> filterConjunction(List<Standard_Conjunction> proposed_conjunctions){
		List<Standard_Conjunction> tmp=new ArrayList<Standard_Conjunction>();
		for(AbstractConjunction p:proposed_conjunctions){
			/*if(!hasIdenticalAtoms(p) & succesfulconnectionBasedFilter(p)){
			  tmp.add(p);
			}*/
		}
		return tmp;
	}

	public boolean allowedByFilter(AbstractConjunction p) {
		//filter one	
		if(hasIdenticalAtoms(p)){
			return true;
		}
		else{
			
		}
		
		
		
		// TODO Auto-generated method stub
		return false;
	}

	
	
	protected boolean hasIdenticalAtoms(AbstractConjunction p) {
		boolean tmp=false;
		for(int i=0;i<p.getLiteralList().size();i++){
			for(int j=i+1;j<p.getLiteralList().size();j++){
				if(p.getLiteralList().get(i).equals(p.getLiteralList().get(j))){
					return true;
				}
			}
		}
		return false;
	}
	

	/*protected boolean succesfulconnectionBasedFilter(Conjunction p) {
		return true;
		if(p.getLogvarConnections().isEmpty()){
			return true;
		}
		List<HashSet<Logvar>> connectionSets=new ArrayList<HashSet<Logvar>>();
		for(Logvar l:((List<Logvar>)p.getInputLogvars())){
			connectionSets.add((HashSet<Logvar>) p.getLogvarConnections().get(l));
		}
		if(nonEmptyIntersection(connectionSets)){
			return true;
		}
		
		return false;
	}*/


	protected boolean nonEmptyIntersection(List<HashSet<Logvar>> connectionSets) {	
		try{
		return getIntersection(connectionSets).size()>0?true:false;
		}
		catch(NullPointerException e){
			return false;
		}
	}
	
	protected HashSet<Logvar> getIntersection(List<HashSet<Logvar>> connectionSets){
		HashSet<Logvar> intersection=new HashSet<Logvar>();
		if(connectionSets.size()==1){
			return connectionSets.get(0);
		}
		for(Logvar l:connectionSets.get(0)){
			if(connectionSets.get(1).contains(l)){
				intersection.add(l);
			}
		}
		return intersection;
	}
	
}
