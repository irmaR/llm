package hybrid.cpds;
import hybrid.cpdEvaluation.CGEvaluator;
import hybrid.cpdEvaluation.CLGEvaluator;
import hybrid.cpdEvaluation.GaussianPriorEvaluator;
import hybrid.cpdEvaluation.LinearGaussianEvaluator;
import hybrid.cpdEvaluation.LogisticRegressionWeka;
import hybrid.cpdEvaluation.LogisticRegressionWekaOnlyContinuous;
import hybrid.cpdEvaluation.ProbabilityMassFunctionEvaluator;
import hybrid.dependencies.*;
import hybrid.parameters.CGParameters;
import hybrid.parameters.CLGParameters;
import hybrid.parameters.Gaussian;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.LogRegressorsOnlyContinuousFeatures;
import hybrid.parameters.LogisticRegressionOnlyContinuous;
import hybrid.parameters.PMF;
/**
 * This class inspects a given dependency and initialized an appropriate CPD 
 * @author irma
 *
 */
public class CPDGenerator {

/**
 * Get appropriate CPD for a specific dependency
 * (this is determined given types of features in the dependency)
 * @param dep
 * @return
 */
	public CPD getAppropriateCPD(Dependency dep){

		//check 1: if marginal?
		if(dep.getFeatures().size()==0){
			//is it discrete?
			if(dep.getHead().getPredicate().isDiscrete()){
				return new ProbabilityMassFunction(dep, new ProbabilityMassFunctionEvaluator(),new PMF(dep));
			}
			else{
				return new GaussianPrior(dep, new GaussianPriorEvaluator(),new Gaussian());
			}
		}
		//check 2: has parents
		else{
			//if head discrete it is logistic regression then
			if(dep.getHead().getPredicate().isDiscrete()){
				
				if(dep.getContinuousFeatures().size()>0 && dep.getDiscreteFeatures().size()>0 && dep.isLogRegOnlyContinuous()){
					return new LogisticRegressionOnlyContinuous(dep, new LogisticRegressionWekaOnlyContinuous(),new LogRegressorsOnlyContinuousFeatures(dep));
				}
				if(dep.getContinuousFeatures().size()==0 && dep.isLogRegOnlyContinuous()){
					return new ProbabilityMassFunction(dep, new ProbabilityMassFunctionEvaluator(),new PMF(dep));

				}
				return new LogisticRegression(dep, new LogisticRegressionWeka(),new LogRegregressors(dep));
			}
			else{
				//is it linear Gaussian (all parents continuous)?
				if(dep.getDiscreteFeatures().size()==0){
					return new LinearGaussian(dep, new LinearGaussianEvaluator(),new LinearGParameters(dep));
				}
				//if only discrete parents - return Conditional Gaussian
				if(dep.getContinuousFeatures().size()==0){
					return new CG(dep, new CGEvaluator(),new CGParameters(dep));
				}
				if(dep.getContinuousFeatures().size()!=0 && dep.getDiscreteFeatures().size()!=0){
					return new CLG(dep,new CLGEvaluator(),new CLGParameters(dep));
				}
			}
		}

		return null;

	}
	
	/**
	 * Get appropriate CPD for a specific dependency
	 * (this is determined given types of features in the dependency)
	 * @param dep
	 * @return
	 */
		public CPD getAppropriateCPDLROnlyForContinuous(Dependency dep){

			//check 1: if marginal?
			if(dep.getFeatures().size()==0){
				//is it discrete?
				if(dep.getHead().getPredicate().isDiscrete()){
					return new ProbabilityMassFunction(dep, new ProbabilityMassFunctionEvaluator(),new PMF(dep));
				}
				else{
					return new GaussianPrior(dep, new GaussianPriorEvaluator(),new Gaussian());
				}
			}
			//check 2: has parents
			else{
				//if head discrete it is logistic regression then
				if(dep.getHead().getPredicate().isDiscrete()){
					return new LogisticRegression(dep, new LogisticRegressionWeka(),new LogRegregressors(dep));
				}
				else{
					//is it linear Gaussian (all parents continuous)?
					if(dep.getDiscreteFeatures().size()==0){
						return new LinearGaussian(dep, new LinearGaussianEvaluator(),new LinearGParameters(dep));
					}
					//if only discrete parents - return Conditional Gaussian
					if(dep.getContinuousFeatures().size()==0){
						return new CG(dep, new CGEvaluator(),new CGParameters(dep));
					}
					if(dep.getContinuousFeatures().size()!=0 && dep.getDiscreteFeatures().size()!=0){
						return new CLG(dep,new CLGEvaluator(),new CLGParameters(dep));
					}
				}
			}

			return null;

		}
	

}
