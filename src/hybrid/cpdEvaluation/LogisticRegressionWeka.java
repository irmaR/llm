package hybrid.cpdEvaluation;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.sun.org.apache.regexp.internal.recompile;

import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.NominalToBinary;
import hybrid.cpds.CPD;
import hybrid.cpds.LinearGaussian;
import hybrid.cpds.LogisticRegression;
import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.*;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.MissingRegression;
import hybrid.parameters.PMF;
import hybrid.parameters.Parameters;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.Logarithm2;


/**
 * This class implements DiscreteEval with LogRegressors parameters, and is responsible for estimating parameters of 
 * logistic regression using weka
 * @author irma
 *
 */
public class LogisticRegressionWeka implements DiscreteEval<LogRegregressors>,WekaClassifiers {
	private Logistic learned_estimator=null;
	private Instances dataSceleton=null;


	//Implements getProbability from discreteEval question: is it ok that here I cast to LGregressors in the method body,
	//because LGregressors has some methods not used for all parameters (e.g., get coefficients for some feature values_
	@Override
	public Double getProbability(MarkovBlanket mB, LogRegregressors par) {
		double prob=0.0;
		double norm=0.0;
		try{
			//calculate normalizing constant for logistic regression
			norm=getNormalizingConstant(mB,(LogRegregressors)par);
		}		
		catch(LogisticRegressionParameterNotExisting nonExistingParameterForThisMarkovBlanket){
			//if no parameter specified for this markov blanket, return probability of head having this value (prior)
			return 0.0;
		}
		//calculate the probability
		prob= getNominator(mB,(LogRegregressors)par)/norm;
		
		if(mB.getHead().getAtom().getPredicate() instanceof DiscretizedPredicate){
			prob=prob/(((DiscretizedPredicate)mB.getHead().getAtom().getPredicate()).getDiscretizationRangeSize());
		}
		
		return prob;
	}
    
	/**
	 * Get probability distribution over the values of target predicate given
	 * a specific Markov blanket and parameters
	 * @param mB - markov blanket
	 * @param par - logistic regression parameters
	 * @return  - multinomial distribution over values of target predicate
	 */
	public List<Double> getProbabilityDistribution(MarkovBlanket mB, LogRegregressors par) {
		List<Double> probs=new ArrayList<Double>();
		List<Value> range=((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues();

		//In case we have unknown values we let weka's logistic regression to obtain the probabilities
		Instances instSceleton= initializeWekaInstanceSceleton(mB.getDep());
		if(mB.hasUndefinedValue()){
			try {
				double[] probs_log=this.learned_estimator.distributionForInstance(this.createInstance(instSceleton, mB));
					for(Value value:range){
						probs.add(probs_log[mB.getHead().getAtom().getPredicate().getRange().getIndexOfValue(value)]);
					}
					return probs;
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
		
		
		double prob=0.0;
		double norm=0.0;
		try {
			norm=getNormalizingConstant(mB,(LogRegregressors)par);
		} catch (LogisticRegressionParameterNotExisting e) {
			probs.add(Double.NaN);
		}	

		for(Value value:range){
			probs.add(getNominator(mB,(LogRegregressors)par,value)/norm);	
		}
		return probs;
	}

	@Override
	/**
	 * Get probability distribution for each class value
	 * @param mB
	 * @param par
	 * @return
	 */
	public HashMap<Value,Double> getProbabilityDistributionAllValues(MarkovBlanket mB, LogRegregressors par) {
		HashMap<Value,Double> probs=new HashMap<Value,Double>();
		List<Value> range=((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues();
		
		//In case we have unknown values we let weka's logistic regression to obtain the probabilities
		Instances instSceleton= initializeWekaInstanceSceleton(mB.getDep());
		if(mB.hasUndefinedValue()){
		try {
			double[] probs_log=this.learned_estimator.distributionForInstance(this.createInstance(instSceleton, mB));
			for(Value value:range){
				probs.put(value,probs_log[mB.getHead().getAtom().getPredicate().getRange().getIndexOfValue(value)]);
			}
			return probs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		
	
		double norm=0.0;
		try {
			norm=getNormalizingConstant(mB,(LogRegregressors)par);
		} catch (LogisticRegressionParameterNotExisting e) {
			e.printStackTrace();
		}
		
        double prob=0;
		for(Value value:range){
			double probab=(getNominator(mB,(LogRegregressors)par,value)/norm);
			prob+=probab;
			if(mB.getHead().getAtom().getPredicate() instanceof DiscretizedPredicate){
				probab=probab/(((DiscretizedPredicate)mB.getHead().getAtom().getPredicate()).getDiscretizationRangeSize());
			}
			probs.put(value,probab);	
		}
		return probs;
	}


	@Override
	public StructureScore calculatePLL(QueryData testData, LogRegregressors pars,Penalty pen) {
		System.out.println("******************* Scoring logistic regression "+ testData.getDep()+" ******************");
		Atom head=testData.getDep().getHead();
		
		if(head.getPredicate().isBoolean()){
			return getPLLBoolean(testData,pars,pen);
		}
		else{
			return getPLLNonBooleanTargetHead(testData,pars,pen);
		}
	}

	/**
	 * Get pseudolikelihood for test data, specific logistic regression parameters and penalty for
	 * non boolean head. This means that the target predicate is categorical, or even
	 * discretized (for discretized predicates probabilities  have to be turned into
	 * densities)
	 * @param testData
	 * @param pars
	 * @param pen
	 * @return
	 */
	private StructureScore getPLLNonBooleanTargetHead(QueryData testData,LogRegregressors pars, Penalty pen) {
		Atom head=testData.getDep().getHead();
        Instances instancesSceleton=initializeWekaInstanceSceleton(testData.getDep());
		this.dataSceleton=instancesSceleton;
		double sumProb=0;
		int nr_data_points=0;
		
		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){	
				//	double prob=this.getProbability(mB, pars);
				double prob=Double.NaN;
				double[] probs=null;
				try {
					probs=this.learned_estimator.distributionForInstance(this.createInstance(instancesSceleton, mB));
				} catch (Exception e) {
					e.printStackTrace();
				}
	            prob=probs[mB.getHead().getAtom().getPredicate().getRange().getIndexOfValue(mB.getHead().getValue())];
	     	    double normalized_prob=prob/head.getPredicate().getDiscretizationRangeSize();
				sumProb+=Logarithm2.logarithm2(normalized_prob);
				nr_data_points++;
			}
		}	
		Double score=sumProb;
		score= score-pen.calculatePenalty(testData.getDep(), nr_data_points);
		return new StructureScore(sumProb,pen.calculatePenalty(testData.getDep(), nr_data_points),score);
	}

	/**
	 * Method for calculating pll for boolean target predicate. The difference from non-boolean one is if the
	 * subsampling occurred. In that case plls have to be corrected
	 * @param testData
	 * @param pars
	 * @param pen
	 * @return
	 */
	private StructureScore getPLLBoolean(QueryData testData, LogRegregressors pars,Penalty pen) {
		Atom head=testData.getDep().getHead();
		Instances instancesSceleton=initializeWekaInstanceSceleton(testData.getDep());
		this.dataSceleton=instancesSceleton;
		double sumProbPositives=0;
		double sumProbNegatives=0;
		long nr_positives=0;
		long nr_negatives=0;
		int nr_data_points=0;
		boolean subsampling_performed=false;
		double pll=0;
		
		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){	
				double prob=Double.NaN;
				double[] probs=null;
				try {
					probs=this.learned_estimator.distributionForInstance(this.createInstance(instancesSceleton, mB));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for(int k=0;k<((RangeDiscrete)head.getPredicate().getRange()).values.size();k++){
					if(mB.getHead().getValue().equals(((RangeDiscrete)head.getPredicate().getRange()).values.get(k))){
						prob=probs[k];
					}
				}				
				//normalize further if sampling occured for a boolean target atom
				if(mB.getHead().getAtom().getPredicate().isBoolean() && i.getSubSampleInfo()!=null && i.getSubSampleInfo().isSubSamplingPerformed(head)){
					prob=this.getSubsampleSpaceProbability(mB,probs,i.getSubSampleInfo().getAlphas().get(mB.getHead().getAtom()));
				}
				//Check if head boolean and subsampling didn't occur. In that case normalize by using the average of training aalphas
				else if(mB.getHead().getAtom().getPredicate().isBoolean() && i.getSubSampleInfo()!=null && !i.getSubSampleInfo().isSubSamplingPerformed(head)){
					prob=this.getSubsampleSpaceProbability(mB,probs,((LogRegregressors)testData.getDep().getCpd().getParameters()).getTraining_data_averaged_alphas());
				}
				if(((BoolValue)mB.getHead().getValue()).getValue()==true){
						sumProbPositives+=Logarithm2.logarithm2(prob);
						nr_positives++;
					}
					else{
						sumProbNegatives+=Logarithm2.logarithm2(prob);
						nr_negatives++;
					}
				
			}
			if(i.getSubSampleInfo()!=null && i.getSubSampleInfo().isSubSamplingPerformed(head)){
				double per_negative_pll=sumProbNegatives/nr_negatives;
				double per_positive_pll=sumProbPositives/nr_positives;
				subsampling_performed=true;
				double correctedNegatives=per_negative_pll*(i.getSubSampleInfo().get_true_number_of_groundigs(head)-nr_positives);
				double correctedPositives=per_positive_pll*(nr_positives);
				pll+=correctedNegatives+correctedPositives;
				nr_data_points+=i.getSubSampleInfo().get_true_number_of_groundigs(head);
			    
			}
			else{
				pll+=sumProbPositives+sumProbNegatives;
				nr_data_points+=(nr_positives+nr_negatives);
				}
		}
		Double score=null;
		score= pll-pen.calculatePenalty(testData.getDep(), nr_data_points);
		return new StructureScore(pll,pen.calculatePenalty(testData.getDep(), nr_data_points),score);
	}

	private double getSubsampleSpaceProbability(MarkovBlanket mB,double[] probs, Double alpha) {
		double true_prob=probs[0];
		double false_prob=probs[1];
		double nom=1-true_prob;
		double denom=true_prob*alpha;
		double nomBig=1+nom/denom;
		if(((BoolValue)mB.getHead().getValue()).getValue()==false){
			//System.out.println(" CORRECTED: "+" True= "+1/nomBig+" False= "+(1-1/nomBig));
			return 1-1/nomBig;
		}
		else{
			return 1/nomBig;
		}
	}

	/**
	 * Normalize calcualted probabilities for Boolean target predicate when
	 * the subsampling has been performed
	 * @param groundhead
	 * @param target_atom
	 * @param normalized_probs
	 * @param i
	 * @return
	 */
	private double normalizeSampling(GroundAtom groundhead, Atom target_atom,double[] normalized_probs, Interpretation i) {
		if(!i.getSubSampleInfo().isSubSamplingPerformed(target_atom)){
			if(((BoolValue)groundhead.getValue()).getValue()==true){
				return normalized_probs[0];
			}
			else{
				return normalized_probs[1];
			}
		}
		else{
			double alpha=i.getSubSampleInfo().getAlphas().get(target_atom);
			double true_prob=normalized_probs[0];
			double nom=1-true_prob;
			double denom=true_prob*alpha;
			double nomBig=1+nom/denom;
			if(((BoolValue)groundhead.getValue()).getValue()==false){
				return 1-1/nomBig;
			}
			else{
				return 1/nomBig;
			}

		}
	}

	@Override
	public LogRegregressors estimateParameters(QueryData trainingData) {
		Instances instancesSceleton=initializeWekaInstanceSceleton(trainingData.getDep());
		Instances trainingInstances=fillInTheValueS(trainingData,instancesSceleton);
		this.dataSceleton=instancesSceleton;
		LogRegregressors pars= trainClassifier(trainingData,trainingInstances);
		pars.addAverageTrainingAlpha(trainingData.getAverageAlpha(trainingData.getDep().getHead()));
		trainingData.getDep().getCpd().setParameters(pars);
		return pars;
	}

	protected double getNominator(MarkovBlanket mB, LogRegregressors parameters) {
		Instances instSceleton= initializeWekaInstanceSceleton(mB.getDep());
		if(parameters.isNthCoefficient(mB.getHead().getValue())){
			return 1.0;
		}

		else{
			double sum1 = 0;
			sum1+=parameters.getInterceptForValue(mB.getHead().getValue());
			int ft_index=0;
			for (Feature ft : mB.getFeatureValues().keySet()) {
				sum1+=applyRegression(mB,ft,parameters,ft_index,mB.getHead().getValue());
				ft_index++;
			}
			return Math.exp(sum1);
		}
	}

	/**
	 * Get nominator of logistic regression equation
	 * @param mB
	 * @param parameters
	 * @param head_val
	 * @return
	 */
	protected double getNominator(MarkovBlanket mB, LogRegregressors parameters,Value head_val) {
		if(parameters.isNthCoefficient(head_val)){
			return 1.0;
		}

		else{
			double sum1 = 0;
			sum1+=parameters.getInterceptForValue(head_val);
			int ft_index=0;
			for (Feature ft : mB.getDep().getFeatures()) {
				sum1+=applyRegression(mB,ft,parameters,ft_index,head_val);
				ft_index++;
			}
			return Math.exp(sum1);
		}
	}
	/**
	 * Given a markov blanket, a specific feature from the dependency, logistic regression parameters, the index of the 
	 * feature in the dependency (and thus in weka instances) and head atom value, apply the regression on this markov blanket.
	 * @param mB
	 * @param ft
	 * @param parameters
	 * @param feature_index
	 * @param head_value
	 * @return
	 */
	private double applyRegression(MarkovBlanket mB,Feature ft, LogRegregressors parameters,int feature_index,Value head_value) {
		Instances instSceleton= initializeWekaInstanceSceleton(mB.getDep());
		NominalToBinary filter=new NominalToBinary();
		try {
			filter.setInputFormat(instSceleton);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		double tmp = Double.NaN;
		if(mB.getFeatureValues().get(ft) instanceof NumberValue){
			tmp= ((NumberValue)mB.getFeatureValues().get(ft)).getNumber() * parameters.getCoefficientForValueAndFeature(head_value, ft);
		}
		else{
			Instance inst=this.createInstance(instSceleton, mB);
			if(filter.input(inst)){
				Instance inst1=filter.output();
				tmp= inst1.value(feature_index) * parameters.getCoefficientForValueAndFeature(head_value, ft);
				try {
					filter.batchFinished();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return tmp;
	}


	/**
	 * Transform instance such that it turns nominal
	 * attributes into binary
	 * @param inst
	 * @return
	 * @throws Exception
	 */
	public NominalToBinary getFilter(Instances inst) throws Exception{
		NominalToBinary filter=new NominalToBinary();
		filter.setInputFormat(inst);
		return filter;
	}

	/**
	 * Given a trained nominal to binary filter, return a numerical representation of an attribute at in an instance
	 * @param at
	 * @param in
	 * @param filter
	 * @return 
	 */
	public double getNominalAttributeToNumeric(Attribute at,Instance in,NominalToBinary filter){
		if(filter.input(in)){
			return filter.output().value(at);
		}
		return Double.NaN;
	}

	/**
	 * Create instace from markov blanket for a specific instances sceleton
	 * @param data
	 * @param mb
	 * @return numerical representation of an attribute at in an instance
	 */
	public Instance createInstance(Instances data,MarkovBlanket mb){
		Instance in=new Instance(mb.getDep().getFeatures().size()+1);
		in.setDataset(data);
		//difference between categorical and boolean value
		if(mb.getHead().getValue() instanceof BoolValue){
			in.setValue(data.classAttribute(), ((BoolValue)mb.getHead().getValue()).getStringValue());
		}
		else{
			in.setValue(data.classAttribute(), ((StringValue)mb.getHead().getValue()).toString());
		}
		int j=0;
		for(Feature f:mb.getDep().getFeatures()){
			if(f.isContinuousOutput()){
				try{
					in.setValue(j, ((NumberValue)mb.getFeatureValues().get(mb.getDep().getFeatures().get(j))).getNumber());
				}
				catch(ClassCastException e){
					in.setMissing(j);
				}
			}
			else{
				if(mb.getFeatureValues().get(mb.getDep().getFeatures().get(j)) instanceof BoolValue){
					in.setValue(j,((BoolValue)mb.getFeatureValues().get(mb.getDep().getFeatures().get(j))).getStringValue());
				}
				else if (mb.getFeatureValues().get(mb.getDep().getFeatures().get(j)) instanceof StringValue){
					try{
						//System.out.println(data.checkForAttributeType(j));
						//System.out.println(((StringValue)mb.getFeatureValues().get(mb.getDep().getFeatures().get(j))).getValue().toString());
						in.setValue(j,((StringValue)mb.getFeatureValues().get(mb.getDep().getFeatures().get(j))).getValue().toString());
					}
					catch(ClassCastException e){
						in.setMissing(j);
					}
				}
				//undefined value
				else{
					in.setMissing(j);
				}
			}
			j++;
		}
		return in;
	}
	/**
	 * Get a normalizing constant for the markov blanket for specific logistic regression parameters
	 * @param mB
	 * @param parameters
	 * @return
	 * @throws LogisticRegressionParameterNotExisting
	 */
	protected double getNormalizingConstant(MarkovBlanket mB,LogRegregressors parameters) throws LogisticRegressionParameterNotExisting {
		double sum = 0;
		List<Value> range=((RangeDiscrete)mB.getHead().getAtom().getPredicate().getRange()).getValues();
		for (int i = 0; i < range.size(); i++) {
			double sum1 = 0;
			try{
				sum1+=parameters.getInterceptForValue(range.get(i));
				int ft_index=0;
				for (Feature ft : mB.getDep().getFeatures()) {
					sum1 += this.applyRegression(mB, ft, parameters, ft_index, range.get(i));
					ft_index++;
				}
			}
			//this means that parameters are not specified for this value. Which is not considered an error, but we estimate one parameter less
			catch(NullPointerException e){
				//throw new LogisticRegressionParameterNotExisting();
			}
			sum += Math.exp(sum1);
		}
		return sum+1;
	}



	@Override
	public LogRegregressors trainClassifier(QueryData data, Instances trainingInstances) {
		Logistic log=trainClassifier(trainingInstances);
		this.learned_estimator=log;
		double[][] wekaCoefficients= transposeMatrix(log.coefficients());
		LogRegregressors pars=null;
		try {
			pars= extractParameters(wekaCoefficients,data.getDep());
		} catch (WrongParameterNumber e) {
			e.printStackTrace();
		} catch (WrongValueType e) {
			e.printStackTrace();
		} catch (WrongValueSpecification e) {
			e.printStackTrace();
		}
		return pars;
	}

	
	/**
	 * Train classifier for instances
	 * @param trainingInstances
	 * @return
	 */
	protected Logistic trainClassifier(Instances trainingInstances){
		Logistic log=new Logistic();
		try {
			log.setRidge(0.005);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			log.buildClassifier(trainingInstances);
		} catch (Exception e) {
			//problem with building the classifier
			//try to log somehow
			//answer: cannot do unary classes!
			//log this error just in case it's something else
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			hybrid.experimenter.AlgorithmParameters.log_errors.info(errors.toString()+"******************************************\n");
			
		}
		return log;
	}

	/**
	 * given a matrix returned by weka estimation, turn these parameters into
	 * LogRegressor representation
	 * @param wekaCoefficients
	 * @param dep
	 * @return
	 * @throws WrongParameterNumber
	 * @throws WrongValueType
	 * @throws WrongValueSpecification
	 */
	protected LogRegregressors extractParameters(double[][] wekaCoefficients,Dependency dep) throws WrongParameterNumber, WrongValueType, WrongValueSpecification {
		LogRegregressors logReg=new LogRegregressors(dep);
		for (int i = 0; i <((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().size() - 1; i++) {
			String value =(String) ((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().toArray()[i].toString();
			double[] coeff = wekaCoefficients[i];
			double intercept=coeff[0];
			HashMap<Feature, Double> tmp = new HashMap<Feature, Double>();
			int index=0;
			for (Feature pr : dep.getFeatures()) {
				try{
					tmp.put(pr, coeff[(index++)+1]);
				}
				catch(ArrayIndexOutOfBoundsException exc){
					tmp.put(pr, (double) 0);
				}

			}
			logReg.addConditionalParameter(new StringValue(value), new Regression(intercept,tmp));
		}
		//add the final parameter which need not to be estimated 
		String s_value=(String)((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().toArray()[((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues().size() - 1].toString();
		StringValue s=new StringValue(s_value);
		logReg.addConditionalParameter(s,new MissingRegression(dep.getFeatures()));
		return logReg;
	}

	/**
	 * Needed for setting the parameters
	 * 
	 * @param m
	 * @return
	 */
	protected static double[][] transposeMatrix(double[][] m) {
		int r = m.length;
		int c = m[0].length;
		double[][] t = new double[c][r];
		for (int i = 0; i < r; ++i) {
			for (int j = 0; j < c; ++j) {
				t[j][i] = m[i][j];
			}
		}
		return t;
	}
	
	@Override
	public Instances fillInTheValueS(QueryData trainingData,Instances instancesSceleton) {
		Instances data=new Instances(instancesSceleton);
		for(Interpretation i:trainingData.getQuery_results().keySet()){
			for(MarkovBlanket mB:trainingData.getQuery_results().get(i)){	
				Instance inst=createInstance(data,mB);
				data.add(inst);
			}
		}
		return data;
	}

	/**
	 * Given a dependency, initialize weka instance sceleton
	 */
	public Instances initializeWekaInstanceSceleton(Dependency dep) {
		FastVector attributes=new FastVector();
		for(Feature f:dep.getFeatures()){
			if(f.isContinuousOutput()){
				attributes.addElement(new Attribute(f.getFeatureIdentifier()));
			}
			else if(f.isDiscreteOutput()){
				FastVector my_nominal_values = new FastVector(); 
				for(Value v:((RangeDiscrete)f.getRange()).getValues()){
					my_nominal_values.addElement(v.toString());
				}

				attributes.addElement(new Attribute(f.getFeatureIdentifier(),my_nominal_values));
			}
		}
		//adding class attribute
		FastVector class_values = new FastVector();
		for(Value v:((RangeDiscrete)dep.getHead().getPredicate().getRange()).getValues()){
			class_values.addElement(v.toString());
		}
		attributes.addElement(new Attribute(dep.getHead().getAtom(),class_values));

		Instances inst=new Instances(dep.getHead().toString(),attributes,10000);
		inst.setClass((Attribute) attributes.lastElement());
		return inst;
	}

	/**
	 * get probability that a specified class value val has given the markov blanket and estimated parameters
	 */
	@Override
	public Double getProbability(Value val, MarkovBlanket mB,LogRegregressors par) {
		return this.getProbabilityDistributionAllValues(mB, par).get(val);
	}


	@Override
	public Value getPrediction(MarkovBlanket mB, LogRegregressors parameters) {
		HashMap<Value,Double> probabilityDistribution=this.getProbabilityDistributionAllValues(mB, parameters);
		Random ran=new Random();
		double nr=ran.nextDouble();
		double boundary=0.0;
		for(Value v:probabilityDistribution.keySet()){
			if(nr<(probabilityDistribution.get(v)+boundary)){
				return v;
			}
			else{
				boundary+=probabilityDistribution.get(v);
			}
			
		}
		return null;
	}


	@Override
	public Value getPrediction(MarkovBlanket mB, LogRegregressors parameters,Random ran) {
		HashMap<Value,Double> probabilityDistribution=this.getProbabilityDistributionAllValues(mB, parameters);
		double nr=ran.nextDouble();
		double boundary=0.0;
		for(Value v:probabilityDistribution.keySet()){
			if(nr<(probabilityDistribution.get(v)+boundary)){
				return v;
			}
			else{
				boundary+=probabilityDistribution.get(v);
			}
			
		}
		return null;
	}


	@Override
	//return the prediction for which we get the largest probability
	public Value getPrediction_no_noise(MarkovBlanket mB,LogRegregressors parameters) {
		HashMap<Value,Double> probabilityDistribution=this.getProbabilityDistributionAllValues(mB, parameters);
		Value return_value=null;
		double prob_max=0;
		for(Value v:probabilityDistribution.keySet()){
			if(probabilityDistribution.get(v)>prob_max){
				return_value=v;
				prob_max=probabilityDistribution.get(v);
			}
		}
		return return_value;
	}

	@Override
	public double getError(QueryData data, LogRegregressors paramters,boolean print) {
		return Double.NaN;
	}

	//some other way (cannot use as for continuous predicates)!
	@Override
	public double getUnnormalizedError(QueryData data,LogRegregressors paramters, boolean print) {
		//System.out.println("Calculating error for logistic regression!");
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			
			Value d=getPrediction_no_noise(mb,paramters);
			//System.out.println(mb+" "+d+" "+mb.getHead().getValue());
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
