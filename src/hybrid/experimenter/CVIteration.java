package hybrid.experimenter;

import java.util.List;

import hybrid.interpretations.Interpretation;
/**
 * This class represents on crossvalidation fold iteration
 * @author irma
 *
 */
public class CVIteration {

	private List<Interpretation> test_folds;
	private List<Interpretation> validation_folds;
	private List<Interpretation> training_folds;
	private double score;
	private LearnedDependencyStatistics statistics;
	
	public CVIteration(List<Interpretation> tests, List<Interpretation> trainings, List<Interpretation> validation){
		this.test_folds=tests;
		this.validation_folds=validation;
		this.training_folds=trainings;
	}
	
	public String toString(){
		String tmp= "";
		tmp+="-----------  TEST INTERPRETATIONS  -----------------\n";
		int i=1;
		for(Interpretation s:test_folds){
			tmp+=i+"th"+ " test interpretation ID: "+s.getId()+" path: "+s.getPath_to_interpretation()+"\n";
		}
		
		tmp+="-----------  TRAINING INTERPRETATIONS  -----------------\n";
		i=1;
		for(Interpretation s:training_folds){
			tmp+=i+"th"+ " training interpretation ID: "+s.getId()+" path: "+s.getPath_to_interpretation()+"\n";
		}
		i=1;
		tmp+="-----------  VALIDATION INTERPRETATIONS  -----------------\n";
		for(Interpretation s:validation_folds){
			tmp+=i+"th"+ " validation interpretation ID: "+s.getId()+" path: "+s.getPath_to_interpretation()+"\n";
		}
		return tmp;
	}

	public List<Interpretation> getTest() {
		return test_folds;
	}

	public List<Interpretation> getValidation() {
		return validation_folds;
	}

	public List<Interpretation> getTraining() {
		return training_folds;
	}

	public LearnedDependencyStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(LearnedDependencyStatistics statistics) {
		this.statistics = statistics;
	}
	
	
	
	
	
	
	
	
	
}
