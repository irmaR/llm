package hybrid.cpdEvaluation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hybrid.cpds.CPD;
import hybrid.cpds.ProbabilityMassFunction;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.RangeDiscrete;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.BadProbabilityDistribution;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.PMF;
import hybrid.parameters.Parameters;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.Logarithm2;

/**
 * Categorical marginal evaluator is responsible for estimating parameters of
 * catgegorical marginals (multinomial distribution)
 * @author irma
 *
 */

public class ProbabilityMassFunctionEvaluator implements DiscreteEval<PMF> {
     
	/**
	 * calculate the probability of this markov Blanket.
	 * Note for Sampling: since we corrected the probabilities when performing parameter estimation for sampled data,
	 * we don't need to do a correction when calculating the probability. This is not the case for logistic regression.
	 */
	@Override
	public Double getProbability(MarkovBlanket mB, PMF pars) {
		if(pars.getParameters()==null){
			throw new NullPointerException("Parameters are not set!!!");
		}
		Double prob= pars.getParameters().get(mB.getHead().getValue());
		if(mB.getHead().getAtom().getPredicate() instanceof DiscretizedPredicate){
			prob=prob/(((DiscretizedPredicate)mB.getHead().getAtom().getPredicate()).getDiscretizationRangeSize());
		}		
		return prob;
	}

	@Override
	public PMF estimateParameters(QueryData trainingData) {
		System.out.println("-------------------- Estimate PMF parameters -----------------------");
		System.out.println(" Dependency: "+trainingData.getDep());
		HashMap<Value,Long> counts=new HashMap<Value,Long>();
		HashMap<Value,Double> normalized_Probs=new HashMap<Value,Double>();
		long number_of_samples=0;

		//Iterate through interpretations and markov blankets to estimate parameters
		//if the predicate was boolean and sampled then fix the probability distribution
		for(Interpretation i:trainingData.getQuery_results().keySet()){
			for(MarkovBlanket mB:trainingData.getQuery_results().get(i)){
				try{
					counts.put(mB.getHead().getValue(), (long) (counts.get(mB.getHead().getValue()).longValue()+1));
				}
				catch(NullPointerException e){
					counts.put(mB.getHead().getValue(), new Long(1));
				}
			}			
		}
		//calculate the number of samples
		number_of_samples=getNumberOfSamples(counts);

		//Initialize the PMF parameters
		PMF mP=null;
		normalized_Probs=calculate_and_normalize_probs(((RangeDiscrete)trainingData.getDep().getHead().getPredicate().getRange()).getValues(),counts, number_of_samples);
		try {
			mP = new PMF(trainingData.getDep(),trainingData.getDep().getHead(),normalized_Probs,trainingData.getAverageAlpha(trainingData.getDep().getHead()));
		} catch (BadProbabilityDistribution e) {
			e.printStackTrace();
		}
		trainingData.getDep().getCpd().setParameters(mP);
		System.out.println(" PMF parameters: "+mP);
		return mP;
	}

	private long getNumberOfSamples(HashMap<Value, Long> counts) {
		long tmp=0;
		for(Value v:counts.keySet()){
			tmp+=counts.get(v);
		}
		return tmp;
	}
	/**
	 * given counts for a specific interpretation and correcting coefficient alpha, normalize counts
	 * @param counts
	 * @param alpha
	 * @return
	 */
	private HashMap<Value, Long> normalizeSampling(HashMap<Value, Long> counts, Double alpha) {
		HashMap<Value, Long> map=new HashMap<Value, Long>();
		for(Value v:counts.keySet()){
			if(((BoolValue) v).getValue()==false){
				map.put(v, (long) (counts.get(v)/alpha));
			}
			else{
				map.put(v, counts.get(v));
			}
		}
		return map;
	}


	/**
	 * Calculate PLL for this PMF
	 */
	@Override
	public StructureScore calculatePLL(QueryData testData, PMF par,Penalty pen) {
		double pll=0;
		boolean subsampling_performed=false;
		long nr_data_points=0;
		double sumProbPositives=0;
		double sumProbNegatives=0;
		long nr_positives=0;
		long nr_negatives=0;
		long all_points=0;
		double non_subsampled_pll=0;

		Atom head=testData.getDep().getHead();

		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){	
				double prob=Double.NaN;
				//Check if subsampling occured, in that case normalize the prob
				if(mB.getHead().getAtom().getPredicate().isBoolean() &&i.getSubSampleInfo() !=null && i.getSubSampleInfo().isSubSamplingPerformed(head)){
					prob=this.getSubsampleSpaceProbability(mB,par,i.getSubSampleInfo().getAlphas().get(mB.getHead().getAtom()));
				}
				//Check if head boolean and subsampling didn't occur. In that case normalize by using the average of training aalphas
				else if(mB.getHead().getAtom().getPredicate().isBoolean() && i.getSubSampleInfo() !=null && !i.getSubSampleInfo().isSubSamplingPerformed(head)){
					prob=this.getSubsampleSpaceProbability(mB,par,((PMF)testData.getDep().getCpd().getParameters()).getTraining_data_averaged_alphas());
				}
				//if not just fetch the estimated probabilities
				else{
					prob=this.getProbability(mB,par);
				}
				
				all_points++;
				//if Boolean!
				
				if((mB.getHead().getValue() instanceof BoolValue)){
					if(((BoolValue)mB.getHead().getValue()).getValue()==true){
						sumProbPositives+=Logarithm2.logarithm2(prob);
						nr_positives++;
					}
					else{
						sumProbNegatives+=Logarithm2.logarithm2(prob);
						nr_negatives++;
					}
				}
				
				non_subsampled_pll+=Logarithm2.logarithm2(prob);
	          //  System.out.println(mB+" = "+prob);

			}
	        //System.out.println(" Prob positives: "+sumProbPositives);
            //System.out.println(" Prob negatives: "+sumProbNegatives);
            //System.out.println(" # positives: "+nr_positives);
            //System.out.println(" # negatives: "+nr_negatives);
			//System.out.println("SUBSAMPLE INFO "+(i.getSubSampleInfo()));
			
			if(i.getSubSampleInfo()!=null && i.getSubSampleInfo().isSubSamplingPerformed(head) && head.getPredicate().isBoolean()){
				System.out.println("Subsampling performed ...");
				double per_negative_pll=sumProbNegatives/nr_negatives;
				double per_positive_pll=sumProbPositives/nr_positives;
				subsampling_performed=true;
				double correctedNegatives=per_negative_pll*(i.getSubSampleInfo().get_true_number_of_groundigs(head)-nr_positives);
				double correctedPositives=per_positive_pll*(nr_positives);
				//pll+=(sumProbNegatives/nr_negatives)*(i.getSubSampleInfo().get_true_number_of_groundigs(head)-nr_positives)+(sumProbPositives);
				pll+=correctedNegatives+correctedPositives;
				nr_data_points+=i.getSubSampleInfo().get_true_number_of_groundigs(head);
			}
			else{
				//System.out.println("Subsampling not performed ...");
				pll+=non_subsampled_pll;
				/*if(head.getPredicate().isBoolean()){
					nr_data_points+=i.getSubSampleInfo().get_true_number_of_groundigs(head);
				}*/
				//else{
					nr_data_points+=all_points;
				//}
			}
		}
		Double score=null;
		if(subsampling_performed){
			//pll=pll/nr_data_points;
		}
		else{
			//System.out.println("TEST DATA: sybsampling not performed "+pll+" "+all_points);
			//pll=pll/all_points;
			nr_data_points=all_points;
		}
		System.out.println(" SCORE NO PENALTY: "+pll+ "PENALTY: "+pen.calculatePenalty(testData.getDep(), nr_data_points));
		score= pll-pen.calculatePenalty(testData.getDep(), nr_data_points);
		System.out.println(" PENALTY TYPE: "+pen.getClass());
		System.out.println(" SCORE PMF: "+score);
		return new StructureScore(pll,pen.calculatePenalty(testData.getDep(), nr_data_points),score);

	}
	
	
	private double getSubsampleSpaceProbability(MarkovBlanket mB, PMF pars, Double alpha) {
		double non_normalized_prob=this.getProbability(mB, pars);
		double true_prob=pars.getParameters().get(new BoolValue(true));
		double false_prob=pars.getParameters().get(new BoolValue(false));
		if(true_prob+false_prob!=1.0){
			throw new NullPointerException("Probs for "+mB+ " don't sum up to one");
		}
		double nom=1-true_prob;
		double denom=true_prob*alpha;
		double nomBig=1+nom/denom;
		if(((BoolValue)mB.getHead().getValue()).getValue()==false){
			return 1-1/nomBig;
		}
		else{
			return 1/nomBig;
		}
	}

	/**
	 * Calculating probabilities based on Laplace correction for given counts for values
	 * @param head_values 
	 * @param counts
	 * @param number_of_samples TODO
	 * @return
	 */
	protected HashMap<Value, Double> calculate_and_normalize_probs(List<Value> head_values, HashMap<Value, Long> counts, long number_of_samples) {
		HashMap<Value,Double> probs=new HashMap<Value,Double>();
		DecimalFormat df = new DecimalFormat("#.000000");
		for(Value val:head_values){
			//probability for a value=v calculated as #value=v/#samples+#classes
			try{
				Double prob=new Double(counts.get(val)+1)/(number_of_samples+head_values.size());
			    probs.put(val, Double.valueOf(df.format(prob)));
			}
			catch(NullPointerException e){
				Double prob=1.0/(number_of_samples+head_values.size());
				probs.put(val, Double.valueOf(df.format(prob)));
			}
		}
		return probs;
	}

	/**
	 * Get probability of a specific value of class atom given the markov blanket and PMF parameters
	 */
	@Override
	public Double getProbability(Value val, MarkovBlanket mB,PMF pars) {
		Double prob= pars.getParameters().get(val);
		if(mB.getHead().getAtom().getPredicate() instanceof DiscretizedPredicate){
			prob=prob/(((DiscretizedPredicate)mB.getHead().getAtom().getPredicate()).getDiscretizationRangeSize());
		}
		return prob;
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}


	@Override
	public Value getPrediction(MarkovBlanket mB, PMF parameters) {
		Random random=new Random();
		double nr=random.nextDouble();
		double boundary=0.0;
		for(Value v:((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues()){
			if(nr<(parameters.getParameters().get(v)+boundary)){
				return v;
			}
			else{
				boundary+=parameters.getParameters().get(v);
			}
		}
		return null;
	}

	@Override
	public HashMap<Value, Double> getProbabilityDistributionAllValues(MarkovBlanket mB, PMF par) {
		HashMap<Value,Double> probs=new HashMap<Value, Double>();
		for(Value v:((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues()){
			probs.put(v, this.getProbability(v, mB, par));
		}
		return probs;
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, PMF parameters, Random random) {
		double nr=random.nextDouble();
		double boundary=0.0;
		for(Value v:((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues()){
			if(nr<(parameters.getParameters().get(v)+boundary)){
				return v;
			}
			else{
				boundary+=parameters.getParameters().get(v);
			}
		}
		return null;
	}

	@Override
	//return value with the highest probability
	public Value getPrediction_no_noise(MarkovBlanket mB, PMF parameters) {
		double max_prob=0;
		Value return_value=null;
		double boundary=0.0;
		for(Value v:((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues()){
			if(parameters.getParameters().get(v)>max_prob){
				max_prob=parameters.getParameters().get(v);
				return_value=v;
			}
		}
		return return_value;
	}

	@Override
	public double getError(QueryData data, PMF paramters,boolean print) {
		return Double.NaN;
	}

	@Override
	public double getUnnormalizedError(QueryData data, PMF paramters,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,paramters);
			try {
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				if(mb.getHead().getValue().toNumber()>y_max){
					y_max=mb.getHead().getValue().toNumber();
				}
				if(mb.getHead().getValue().toNumber()<y_max){
					y_min=mb.getHead().getValue().toNumber();
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		return acc_error;
	}

	@Override
	public Parameters loadParametersFromCSV(String pathToCSVFile,Dependency dep) {
		// TODO Auto-generated method stub
		return null;
	}


}
