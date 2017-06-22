package hybrid.cpds;

import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.dependencies.Dependency;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;

public class Poisson extends CPD<LinearGParameters,DiscreteEval<LinearGParameters>> {

	public Poisson(Dependency dep, DiscreteEval<LinearGParameters> evaluator,LinearGParameters pars) {
		super(dep, evaluator, pars);
		// TODO Auto-generated constructor stub
	}
	
	public LinearGParameters getParameters() {
		return (LinearGParameters) this.parameters;
	}

	public void addParameter(LinearGParameters parameters) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {
		this.parameters=parameters;
	}

}
