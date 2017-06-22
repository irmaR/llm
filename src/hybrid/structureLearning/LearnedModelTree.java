package hybrid.structureLearning;

import hybrid.experimenter.LearnedDependencyStatistics;
import hybrid.network.Atom;

import java.util.HashMap;

public class LearnedModelTree extends Model {

	private HashMap<Atom,Tree> learnedDependency;
	
	public LearnedModelTree(){
		learnedDependency=new HashMap<Atom, Tree>();
	}
	
	public void addLearnedDependency(Atom a,Tree dep){
		this.learnedDependency.put(a, dep);
	}

	public HashMap<Atom, Tree> getLearnedDependency() {
		return learnedDependency;
	}
	
	public String toString(){
		String tmp="---------- LEARNED STRUCTURE ------\n";
		for(Atom a:learnedDependency.keySet()){
			tmp+=a+" <= "+learnedDependency.get(a);
		}
		return tmp;
	}
	
	
}
