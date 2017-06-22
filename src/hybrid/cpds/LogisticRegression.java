package hybrid.cpds;

import java.util.HashMap;

import hybrid.cpdEvaluation.CPDEvaluator;
import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.cpdEvaluation.LogisticRegressionWeka;
import hybrid.dependencies.Dependency;
import hybrid.network.Atom;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.LogisticRegressionSetter;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;

/**
 * This class represents Logistic Regression CPD. Its parameters are determined by the features in the dependency.
 * For estimating paramaeters and scoring create an object of this class by specifying a specific cpd evaluator. Otherwise, a default
 * interface evaluator will be LogisticRegressionWeka. 
 * @author irma
 *
 */

public class LogisticRegression extends CPD<LogRegregressors,DiscreteEval<LogRegregressors>>{	
	/**
	 * Creating Logistic Regression CPD for a specific dependency Dep
	 * @param dep - dependency denoting a parent set of an atom 
	 */

	public LogisticRegression(Dependency dep, DiscreteEval evaluator,LogRegregressors pars){
		super(dep,evaluator,pars);
	}

	


	public LogRegregressors getParameters() {
		return (LogRegregressors) this.parameters;
	}

 
	public void addParameter(Value val,Regression parameters) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {
		((LogRegregressors)this.parameters).addConditionalParameter(val,parameters);
	}



}
