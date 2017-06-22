package hybrid.experimenter;

import hybrid.interpretations.Interpretation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrossValidation {
	private List<Interpretation> interpretations;
	private double perc_training;
	private double perc_validation;
	private int k;
	
	/**
	 * Initialize Crossvalidation class 
	 * @param allInterpretations - all interpretations considered in the learning procedure
	 * @param nr_training_inter - 
	 * @param nr_validation_inter
	 * @param nr_test_iter
	 */
	public CrossValidation(List<Interpretation> allInterpretations){
		this.interpretations=allInterpretations;
	}
	
	
	public List<CVIteration> obtainFolds(){
		//if(this.k==this.interpretations.size()){
			//in this case it is leave one out cross validation
			return this.initializeAllFoldsLeaveOneOut(this.interpretations,(int)Math.ceil((interpretations.size()-1)*this.perc_training/100),(int) Math.ceil((interpretations.size()-1)*perc_validation/100));
		//}
		//return null;
	}
	
	protected List<CVIteration> initializePercentageCrossValidation(List<Interpretation> interpretations,double percentage_training,double percentage_validation,double percentage_test_){
		if((percentage_training+percentage_test_+percentage_validation)!=100){
			throw new NullPointerException("The percentages for folds don't sum up to 100%.");
		}
		return null;
		
	}
	
	
	/**
	 * Initialize all folds for leave one out cross-validation. Only the number of training and
	 * validation instances should be specified.
	 * @param nr_training
	 * @param nr_validation
	 * @return
	 */
	public List<CVIteration> initializeAllFoldsLeaveOneOut(List<Interpretation> interpretations,int nr_training,int nr_validation){
		System.out.println(" Nr training: "+nr_training+" Nr validation: "+nr_validation);
		List<CVIteration> folds=new ArrayList<CVIteration>();
		List<Integer> indices=new ArrayList<Integer>();
		//initialize indices for interpretations
		for(int i = 0; i < interpretations.size(); i++) { indices.add(new Integer(i));}
		
		//loop through interpretations
		for(int i=0;i<interpretations.size();i++){
			List<Integer> otherIndices=new ArrayList<Integer>();
			//extract indices different from test interpretation index
			for(int k=0;k<indices.size();k++){
				if(k==i){
					continue;
				}
				else{
					otherIndices.add(k);
				}
			}
			
			//shuffle the indices
			Collections.shuffle(otherIndices);
			List<Interpretation> trainingInterpretations=new ArrayList<Interpretation>();
			List<Interpretation> testInterpretations=new ArrayList<Interpretation>();
			List<Interpretation> validationInterpretation=new ArrayList<Interpretation>();
            //fill in m training interpretations
			for(int m=0;m<nr_training;m++){
				trainingInterpretations.add(interpretations.get(otherIndices.get(m)));
			}
			//fill in n validation interpretations
			for(int n=nr_training;n<nr_validation+nr_training;n++){
				validationInterpretation.add(interpretations.get(otherIndices.get(n)));
			}
			testInterpretations.add(interpretations.get(i));
			folds.add(new CVIteration(testInterpretations,trainingInterpretations,validationInterpretation));	
		}
		return folds;
		
	}
	
}
