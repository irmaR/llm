package hybrid.cpds;

import hybrid.cpdEvaluation.ContinuousEval;
import hybrid.dependencies.Dependency;
import hybrid.network.Value;
import hybrid.parameters.Gaussian;

/**
 * Create Gaussian prior CPD associated with class implementing Continuous Evaluator
 * @author irma
 *
 */
public class GaussianPrior extends CPD<Gaussian,ContinuousEval<Gaussian>> {

	
	public GaussianPrior(Dependency dep,ContinuousEval ev,Gaussian pars){
		super(dep,ev,pars);
	}
	
	public String toString(){
		return " Gaussian Prior "+this.dep+ " pars --->"+this.parameters;
	}

	



	
	
}
