package hybrid.structureLearning;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.*;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.LearnedDependencyStatistics;
import hybrid.features.Feature;
import hybrid.network.Value;
import hybrid.parameters.Parameters;
import hybrid.queryMachine.FeatureCache;
import hybrid.querydata.QueryData;

/**
 * This class represents a pair of dependency and its score in the current
 * learnin procedure
 * @author irma
 *
 */
public class LearnedDependency extends DNdependency implements Comparable, Serializable {
	private LearnedDependencyStatistics statistics;
	private Dependency dep;
	private double score;
	private StructureScore structure_score;
	private Feature newest_added_feature;
	boolean new_feature_added=false;
	double error_training_data;
	double error_validation_data;
	double normalizedTestError;
	double normalizedTestScore;
	double error_test_data;
	int nr_data_points_test_data;
	private transient QueryData training_data;
	private transient QueryData validation_data;
	private HashMap<Value, Parameters> parametersPerBranch;
	private HashMap<Value, Double> scorePerBranch;
	private HashMap<Value, Double> testScores;
	private double testDataScore;
	private HashMap<Value, Double> normalizedTestScorePerBranch;
	private HashMap<Value, Double> normalizedScorePerBranch;
	private HashMap<Value, Double> normalizedTestErrorPerBranch;
	private HashMap<Value, Double> testErrorPerBranch;
	private String branchInfo;

	public double getError_test_data() {
		return error_test_data;
	}

	public double getNormalizedTestError() {
		return this.normalizedTestError;
	}

	public void setNormalizedTestError(double normalizedTestError) {
		this.normalizedTestError = normalizedTestError;
	}

	public double getNormalizedTestScore() {
		return normalizedTestScore;
	}





	public void setNormalizedTestScore(double normalizedTestScore) {
		this.normalizedTestScore = normalizedTestScore;
	}





	public int getNr_data_points_test_data() {
		return nr_data_points_test_data;
	}




	public String getBranchInfo() {
		return branchInfo;
	}

	public void setBranchInfo(String branchInfo) {
		this.branchInfo = branchInfo;
	}

	public StructureScore getStructure_score() {
		return structure_score;
	}



	public void setStructure_score(StructureScore structure_score) {
		this.structure_score = structure_score;
	}



	public void setNr_data_points_test_data(int nr_data_points_test_data) {
		this.nr_data_points_test_data = nr_data_points_test_data;
	}



	public void setError_test_data(double error_test_data) {
		this.error_test_data = error_test_data;
	}

	public Feature getNewest_added_feature() {
		return newest_added_feature;
	}

	public void setNewest_added_feature(Feature newest_added_feature) {
		this.newest_added_feature = newest_added_feature;
	}



	public QueryData getTraining_data() {
		return training_data;
	}

	public void setTraining_data(QueryData training_data) {
		this.training_data = training_data;
	}

	public QueryData getValidation_data() {
		return validation_data;
	}

	public void setValidation_data(QueryData validation_data) {
		this.validation_data = validation_data;
	}

	public double getError_training_data() {
		return error_training_data;
	}

	public void setError_training_data(double error_training_data) {
		this.error_training_data = error_training_data;
	}

	public double getError_validation_data() {
		return error_validation_data;
	}

	public void setError_validation_data(double error_validation_data) {
		this.error_validation_data = error_validation_data;
	}

	public boolean isNew_feature_added() {
		return new_feature_added;
	}


	public LearnedDependency(Dependency dep,double score){
		this.dep=dep;
		this.score=score;

	}

	public Dependency getDep() {
		return dep;
	}

	public void setDep(Dependency dep) {
		this.dep = dep;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String toString(){
		try{
			return "Score("+dep+") = "+score+" pars: "+dep.getCpd().getParameters()+ "errors: ["+error_training_data+","+error_validation_data+","+error_test_data+"]"+"NRMSE:"+this.normalizedTestError+" TEST SCORES: "+this.testScores;
		}
		catch(NullPointerException e){
			return "NullPointerException";
		}
	}

	@Override
	public int compareTo(Object arg0) {

		if(this.score>((LearnedDependency)arg0).score){
			return -1;
		}
		if(this.score<((LearnedDependency)arg0).score){
			return 1;
		}
		return 0;
	}

	public boolean betterThan(LearnedDependency currentScore) {
		if(this.score>currentScore.getScore()){
			return true;
		}
		return false;
	}

	public LearnedDependencyStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(LearnedDependencyStatistics statistics) {
		this.statistics = statistics;
	}

	public void setNewFeatureLearned(boolean b) {
		this.new_feature_added=b;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dep == null) ? 0 : dep.hashCode());
		result = prime * result + (new_feature_added ? 1231 : 1237);
		result = prime
				* result
				+ ((newest_added_feature == null) ? 0 : newest_added_feature
						.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((statistics == null) ? 0 : statistics.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LearnedDependency other = (LearnedDependency) obj;
		if (dep == null) {
			if (other.dep != null)
				return false;
		} else if (!dep.equals(other.dep))
			return false;
		if (new_feature_added != other.new_feature_added)
			return false;
		if (newest_added_feature == null) {
			if (other.newest_added_feature != null)
				return false;
		} else if (!newest_added_feature.equals(other.newest_added_feature))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (statistics == null) {
			if (other.statistics != null)
				return false;
		} else if (!statistics.equals(other.statistics))
			return false;
		return true;
	}



	public void setParametersPerBranch(HashMap<Value, Parameters> parametersPerBranch) {
		this.parametersPerBranch=parametersPerBranch;

	}

	public HashMap<Value, Parameters> getParametersPerBranch(){
		return this.parametersPerBranch;
	}



	public void setScorePerBranch(HashMap<Value, Double> scorePerBranch) {
		this.scorePerBranch=scorePerBranch;		
	}

	public HashMap<Value, Double> getScorePerBranch(){
		return this.scorePerBranch;
	}



	public void setTestScores(HashMap<Value, Double> testScorePerBranch) {
		this.testScores=testScorePerBranch;

	}



	public HashMap<Value, Double> getTestScores() {
		return this.testScores;
	}



	public void setTestScore(double testDataScore) {
		this.testDataScore=testDataScore;

	}



	public double getTestDataScore() {
		return testDataScore;
	}





	public void setNormalizedTestScorePerBranch(HashMap<Value, Double> normalizedTestScorePerBranch) {
		this.normalizedTestScorePerBranch=normalizedTestScorePerBranch;

	}





	public void setNormalizedScorePerBranch(HashMap<Value, Double> normalizedScorePerBranch) {
		this.normalizedScorePerBranch=normalizedScorePerBranch;

	}





	public void setNormalizedTestErrorPerBranch(HashMap<Value, Double> normalizedTestErrorPerBranch) {
		this.normalizedTestErrorPerBranch=normalizedTestErrorPerBranch;

	}





	public HashMap<Value, Double> getNormalizedTestScorePerBranch() {
		return normalizedTestScorePerBranch;
	}





	public HashMap<Value, Double> getNormalizedScorePerBranch() {
		return normalizedScorePerBranch;
	}





	public HashMap<Value, Double> getNormalizedTestErrorPerBranch() {
		return normalizedTestErrorPerBranch;
	}





	public void setTestErrorPerBranch(HashMap<Value, Double> testErrorPerBranch) {
		this.testErrorPerBranch=testErrorPerBranch;

	}





	public HashMap<Value, Double> getTestErrorPerBranch() {
		return testErrorPerBranch;
	}

	public void setInfoString(String branchFailureReason) {
		this.branchInfo=branchFailureReason;
		
	}



















}

