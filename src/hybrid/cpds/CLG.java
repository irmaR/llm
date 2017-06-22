package hybrid.cpds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hybrid.cpdEvaluation.CPDEvaluator;
import hybrid.cpdEvaluation.ContinuousEval;
import hybrid.dependencies.Dependency;
import hybrid.network.Value;
import hybrid.parameters.CGParameters;
import hybrid.parameters.CLGParameters;

public class CLG extends CPD<CLGParameters,ContinuousEval<CLGParameters>> {

	public CLG(Dependency dep, ContinuousEval evaluator,CLGParameters pars) {
		super(dep, evaluator,pars);
	}

	

	

	
	
}
