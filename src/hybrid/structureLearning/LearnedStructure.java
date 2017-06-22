package hybrid.structureLearning;
import java.io.Serializable;
import java.util.HashMap;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.LearnedDependencyStatistics;
import hybrid.network.*;

public class LearnedStructure extends Model implements Serializable {

	private HashMap<Atom,LearnedDependency> learnedDependency;
	private transient LearnedDependencyStatistics statistics;
	
	public LearnedStructure(){
		learnedDependency=new HashMap<Atom, LearnedDependency>();
	}
	
	public void addLearnedDependency(Atom a,LearnedDependency dep){
		this.learnedDependency.put(a, dep);
	}

	public HashMap<Atom, LearnedDependency> getLearnedDependency() {
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
