package hybrid.utils;

import hybrid.querydata.QueryData;

public class CVFoldPair {
	
	private QueryData training;
	
	
	public CVFoldPair(QueryData training, QueryData validation) {
		super();
		this.training = training;
		this.validation = validation;
	}
	public QueryData getTraining() {
		return training;
	}
	public void setTraining(QueryData training) {
		this.training = training;
	}
	public QueryData getValidation() {
		return validation;
	}
	public void setValidation(QueryData validation) {
		this.validation = validation;
	}
	private QueryData validation;
	
	

}
