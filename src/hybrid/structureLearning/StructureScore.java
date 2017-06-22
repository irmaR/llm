package hybrid.structureLearning;

import java.io.Serializable;

/**
 * This class contains three fields:
 * 1) LL - loglikelihood of a depenceny
 * 2) Penalty - penalty value subtracted from LL 
 * 3) Score - overall score
 * @author irma
 *
 */
public class StructureScore implements Serializable {

	private Double ll;
	private Double penalty;
	private Double score;
	
	public StructureScore(Double ll,Double penalty,Double score){
		this.ll=ll;
		this.penalty=penalty;
		this.score=score;
	}
	
	public String toString(){
		return "LL: "+this.ll+" Penalty: "+this.penalty+" Score: "+this.score;
	}

	public Double getLl() {
		return ll;
	}

	public void setLl(Double ll) {
		this.ll = ll;
	}

	public Double getPenalty() {
		return penalty;
	}

	public void setPenalty(Double penalty) {
		this.penalty = penalty;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
	
	
}
