package hybrid.cpds;

import hybrid.cpdEvaluation.ContinuousEval;
import hybrid.dependencies.Dependency;
import hybrid.parameters.CGParameters;

public class CG extends CPD<CGParameters,ContinuousEval<CGParameters>> {
	/**
	 * Constructor with parameters and the dependency
	 */
	
	public CG(Dependency dep, ContinuousEval ev,CGParameters pars){
		super(dep,ev,pars);
	}


}
