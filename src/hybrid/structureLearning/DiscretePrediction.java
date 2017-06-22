package hybrid.structureLearning;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;

import java.util.HashMap;

/**
 * Thsi class will hold scores (probabilities) by mapping each class from the range
 * to the scores for each markov blanker
 * @author irma
 *
 */
public class DiscretePrediction {

	private Dependency dep;
	private HashMap<Value,MBScore> scores;
	
	public DiscretePrediction(Dependency dep,RangeDiscrete discrete_range){
		this.dep=dep;
		scores=new HashMap<Value, MBScore>();
		for(Value v:discrete_range.getValues()){
			scores.put(v, new MBScore());
		}
	}
	
	

	public void addScore(Value v,MarkovBlanket mb,Double score){
		scores.get(v).structureScores.put(mb, score);
	}
	
	public Double getScore(Value v,MarkovBlanket mb){
		return scores.get(v).structureScores.get(mb);
	}
	
	public String toString(){
		String tmp="";
		for(Value v:scores.keySet()){
			tmp+=v+"["+scores.get(v)+"]";
		}
		return tmp;
	}
	
class MBScore{
	private HashMap<MarkovBlanket,Double> structureScores;
    
	
	public MBScore(){
		structureScores=new HashMap<MarkovBlanket, Double>();
	}
	
	public String toString(){
		String tmp="";
	    int i=0;
		for(MarkovBlanket mB:structureScores.keySet()){
			if(i<structureScores.size()-1){
			tmp+=structureScores.get(mB)+",";
			}
			else{
				tmp+=structureScores.get(mB);
			}
			i++;
		}
		return tmp;
	}

	
}
	
}
