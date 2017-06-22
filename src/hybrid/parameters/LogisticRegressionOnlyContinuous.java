package hybrid.parameters;

import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.cpds.CPD;
import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.network.Value;
import hybrid.network.WrongValueType;

public class LogisticRegressionOnlyContinuous extends CPD<LogRegressorsOnlyContinuousFeatures,DiscreteEval<LogRegressorsOnlyContinuousFeatures>>{
	/**
	 * This class represents Logistic Regression CPD. Its parameters are determined by the features in the dependency.
	 * For estimating paramaeters and scoring create an object of this class by specifying a specific cpd evaluator. Otherwise, a default
	 * interface evaluator will be LogisticRegressionWeka. 
	 * @author irma
	 *
	 */

	/**
	 * Creating Logistic Regression CPD for a specific dependency Dep
	 * @param dep - dependency denoting a parent set of an atom 
	 */

	public LogisticRegressionOnlyContinuous(Dependency dep, DiscreteEval evaluator,LogRegressorsOnlyContinuousFeatures pars){
		super(dep,evaluator,pars);
	}




	public LogRegressorsOnlyContinuousFeatures getParameters() {
		return (LogRegressorsOnlyContinuousFeatures) this.parameters;
	}


	public void addParameter(Value val,Regression parameters) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {

	}
}
