package hybrid.cpdEvaluation;

import java.util.HashMap;

import hybrid.cpds.ProbabilityMassFunction;
import hybrid.cpds.LinearGaussian;
import hybrid.cpds.LogisticRegression;
import hybrid.dependencies.MarkovBlanket;
import hybrid.network.Value;
import hybrid.parameters.*;
import hybrid.querydata.QueryData;

/**
 * CPD evaluator of discrete CPDs
 * @author irma
 *
 * @param <P>
 */
public interface DiscreteEval<P extends Parameters> extends CPDEvaluator<P>{

	public abstract Double getProbability(Value val,MarkovBlanket mB,P par);
	public abstract HashMap<Value,Double> getProbabilityDistributionAllValues(MarkovBlanket mB, P par);

}
