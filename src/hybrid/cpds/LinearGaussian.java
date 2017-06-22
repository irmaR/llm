package hybrid.cpds;

import hybrid.cpdEvaluation.ContinuousEval;
import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.dependencies.Dependency;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;

/**
 * Class representing linear Gaussian created with LinearGParameters and continuous evaluator
 * working on LinearGParameters
 * @author irma
 *
 */
public class LinearGaussian extends CPD<LinearGParameters,ContinuousEval<LinearGParameters>>{

	
	/**
	 * Constructor with the dependency and a continuous evaluator
	 */
	public LinearGaussian(Dependency dep,ContinuousEval ev,LinearGParameters pars){
		super(dep,ev,pars);
        this.parameters=new LinearGParameters(dep);
	}
	
	
	public LinearGParameters getParameters() {
		return (LinearGParameters) this.parameters;
	}

 
	public void addParameter(LinearGParameters parameters) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {
		this.parameters=parameters;
	}


	



}
