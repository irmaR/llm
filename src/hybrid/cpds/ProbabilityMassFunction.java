package hybrid.cpds;

import hybrid.cpdEvaluation.DiscreteEval;
import hybrid.dependencies.Dependency;
import hybrid.parameters.PMF;


/**
 * CPD representing probability mass function created with PMF parameters and discrete evaluator
 * @author irma
 *
 */
public class ProbabilityMassFunction extends CPD<PMF,DiscreteEval<PMF>> {


	
	public ProbabilityMassFunction(Dependency dep, DiscreteEval ev,PMF pars){
		super(dep,ev,pars);
	} 
	
	public PMF getParameters(){
		return (PMF) this.parameters;
	}

}
