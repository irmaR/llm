package hybrid.experimenter;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.structureLearning.LearnedDependency;

import java.util.*;
/**
 * This class represents fold statistics
 * @author irma
 *
 */
public class LearnedDependencyStatistics {
	private Atom atom;
	private Dependency learned_dependency;
	private double WPLL_score_test;
	private double WPLL_score_validation;
	private int nr_test_instances;
	private int nr_training_instances;
	private int nr_validation_instances;
	private double time_needed_to_learn=(long) 0;
	private HashMap<Atom,Double> inference_time=new HashMap<Atom, Double>();


	public LearnedDependencyStatistics(Atom a){
		this.atom=a;
	}



	public int getNr_test_instances() {
		return nr_test_instances;
	}

	public void setNr_test_instances(int nr_test_instances) {
		this.nr_test_instances = nr_test_instances;
	}

	public int getNr_training_instances() {
		return nr_training_instances;
	}

	public void setNr_training_instances(int nr_training_instances) {
		this.nr_training_instances = nr_training_instances;
	}

	public int getNr_validation_instances() {
		return nr_validation_instances;
	}

	public void setNr_validation_instances(int nr_validation_instances) {
		this.nr_validation_instances = nr_validation_instances;
	}


	public void setLearningTime(double time){
		this.time_needed_to_learn=time;
	}

	public double getLearningTime(){
		return this.time_needed_to_learn;
	}



	public void setWPLLScore_Validation(double d) {
		// TODO Auto-generated method stub

	}



	public double getWPLL_score_test() {
		return WPLL_score_test;
	}

	public void setWPLL_score_test(double wPLL_score_test) {
		WPLL_score_test = wPLL_score_test;
	}

	public double getWPLL_score_validation() {
		return WPLL_score_validation;
	}

	public void setWPLL_score_validation(double wPLL_score_validation) {
		WPLL_score_validation = wPLL_score_validation;
	}

	public Dependency getLearned_dependency() {
		return learned_dependency;
	}

	public void setLearned_dependency(Dependency learned_dependency) {
		this.learned_dependency = learned_dependency;
	}

	@Override
	public String toString() {
		String tmp= "LearnedDependencyStatistics [atom=" + atom
				+",\n Learned dep: NR:"+this.learned_dependency.getFeatures().size()+" "+this.learned_dependency
				+ ",\n WPLL_test=" + WPLL_score_test
				+ ",\n WPLL_validation=" + WPLL_score_validation
				+ ",\n nr_test_instances=" + nr_test_instances
				+ ",\n nr_training_instances=" + nr_training_instances
				+ ",\n nr_validation_instances=" + nr_validation_instances
				+ ",\n time_needed_to_learn=" + time_needed_to_learn + " seconds ]\n";
		tmp+="\n Inference time per atom: \n";
		for(Atom a:this.inference_time.keySet()){
			tmp+=a+"="+this.inference_time.get(a)+" seconds\n";
		}
		
		
		tmp+="LEARNED: ";
		if(this.learned_dependency.getFeatures().size()==0){
			tmp+="NONE";
		}
		else{
			for(Feature f:this.learned_dependency.getFeatures()){
				tmp+=f.getIndexInFeatureSpace()+" \t";
			}
		}
		return tmp;
	}



	public void setInferenceTime(Atom a, double final_inference_time) {
		inference_time.put(a, final_inference_time);
	}

}
