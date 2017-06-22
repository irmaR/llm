package hybrid.cpds;

import java.io.Serializable;

import hybrid.cpdEvaluation.CPDEvaluator;
import hybrid.dependencies.Dependency;
import hybrid.parameters.Parameters;


/**
 * A class representing a Conditional Probability Distribution
 * @author irma
 *
 * @param <P> - parameters of a CPD
 * @param <T> - CPD Evaluator of a CPD
 */

public abstract class CPD<P extends Parameters, T extends CPDEvaluator> implements Serializable {
    
	protected P parameters;
	protected Dependency dep;
	protected T cpdEvaluator;
	
    /**
     * Create a CPD with associating evaluator and parameters
     * @param dep
     * @param evaluator
     */
	public CPD(Dependency dep, T evaluator,P pars){
        this.dep=dep;
        this.cpdEvaluator=evaluator;
        this.parameters=pars;
	}
	
	
	
	public Parameters getParameters() {
		return parameters;
	}
	


	public void setParameters(P parameters) {
		this.parameters = parameters;
	}

	public Dependency getDep() {
		return dep;
	}

	public void setDep(Dependency dep) {
		this.dep = dep;
	}

	public T getCpdEvaluator() {
		return cpdEvaluator;
	}

	public void setCpdEvaluator(T cpdEvaluator) {
		this.cpdEvaluator = cpdEvaluator;
	}
	
	
	
}
