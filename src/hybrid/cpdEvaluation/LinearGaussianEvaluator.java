package hybrid.cpdEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import hybrid.cpds.CPD;
import hybrid.cpds.LinearGaussian;
import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.NumberValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.Gaussian;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.Parameters;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.Logarithm2;


/**
 * This class is responsible for estimating parameters and evaluating linear
 * Gaussian distribution. 
 * @author irma
 *
 */

public class LinearGaussianEvaluator implements ContinuousEval<LinearGParameters>,WekaClassifiers{

    HashMap<Feature,Double> averageValueForAttribute;
	
	
    @Override
	public Double getProbability(MarkovBlanket mB, LinearGParameters pars) {
		double mean=getMean(mB, pars.getCoefficients().getReg_coeff());
		double std=pars.getCoefficients().getStd();	
		return getProbability(mB, mean, std);
	}

	/**
	 * Estimate the parameters of Linear Gaussian given training data
	 */
	@Override
	public LinearGParameters estimateParameters(QueryData trainingData) {
		this.averageValueForAttribute=new HashMap<Feature, Double>();
		LinearGParameters pars=null;
		Instances instancesSceleton=null;
		Instances trainingInstances=null;
		try {
			//initialize instance sceleton for Weka
			instancesSceleton = initializeWekaInstanceSceleton(trainingData.getDep());
			//fill in training instances
			trainingInstances =fillInTheValueS(trainingData,instancesSceleton);
		} catch (BadParentSpecification e) {
			e.printStackTrace();
		}
		try {
			//train the classifier
			pars=trainClassifier(trainingData,trainingInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//setting the parameters of the dependency
		trainingData.getDep().getCpd().setParameters(pars);
		return pars;
	}
	
	/**
	 * Calculate PLL given Query data and parameters
	 */
	@Override
	public StructureScore calculatePLL(QueryData testData, LinearGParameters pars,Penalty pen) {
		double pll=0;
		int nr_data_points=0;
		for(Interpretation i:testData.getQuery_results().keySet()){
			for(MarkovBlanket mB:testData.getQuery_results().get(i)){
				pll+=Logarithm2.logarithm2(this.getProbability(mB, pars));
				nr_data_points++;
			}
		}
		return new StructureScore(pll, pen.calculatePenalty(testData.getDep(), nr_data_points), pll-pen.calculatePenalty(testData.getDep(), nr_data_points));
	}
	

	
	/**
	 * Estimate parameters of linear gaussian for a list of markov blankets
	 * @param mb
	 * @param dep
	 * @return
	 * @throws BadParentSpecification
	 * @throws NotEnoughDataForLinearRegression 
	 */
	public Parameters estimateParameters(List<MarkovBlanket> mb, Dependency dep) throws BadParentSpecification, NotEnoughDataForLinearRegression {
		Instances instancesSceleton=null;
		Instances trainingInstances=null;
		try {
			instancesSceleton = initializeWekaInstanceSceleton(dep);
			trainingInstances =fillInTheValueS(dep,mb,instancesSceleton);
		} catch (BadParentSpecification e) {
			e.printStackTrace();
		}
		
		try {
			return trainClassifier(mb,dep,trainingInstances);
		} catch (weka.core.WekaException e) {
			throw new NotEnoughDataForLinearRegression();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
/**
 * Fill in the values for the Markov blanket associated with the dependency for an instance sceleton
 * @param dep
 * @param mb
 * @param instancesSceleton
 * @return
 * @throws BadParentSpecification
 */
	private Instances fillInTheValueS(Dependency dep,List<MarkovBlanket> mb,Instances instancesSceleton) throws BadParentSpecification {
		this.averageValueForAttribute=new HashMap<Feature, Double>();
		HashMap<Feature,List<Value>> values=new HashMap<Feature, List<Value>>();
		Instances data=new Instances(instancesSceleton);
		for(MarkovBlanket mB:mb){	
			Instance inst=new Instance(data.numAttributes());
			//filling in the value of head atom first
			inst.setValue(data.classAttribute(), ((NumberValue)mB.getHead().getValue()).getNumber());
			int j=0;
			for(Feature f:dep.getContinuousFeatures()){
				if(f.isContinuousOutput()){
					if(mB.getFeatureValues().get(f) instanceof UndefinedValue){
						inst.setMissing(j);
					}
					else{
						try{
							values.get(f).add(mB.getFeatureValues().get(f));
						}	
						catch(NullPointerException e){
							values.put(f, new ArrayList<Value>());
							values.get(f).add(mB.getFeatureValues().get(f));
						}
						try{
						inst.setValue(j, ((NumberValue)mB.getFeatureValues().get(dep.getContinuousFeatures().get(j++))).getNumber());
						}
						catch(ClassCastException e){
							inst.setMissing(j);
							j++;
						}
					}
				}
				else{
					throw new BadParentSpecification(" Linear Gaussians only take continuous parents! ");
				}
			}
			data.add(inst);
		}
		for(Feature ft:values.keySet()){
			this.averageValueForAttribute.put(ft, getAverage(values.get(ft)));
		}
		return data;
	}

	/**
	 * Given results of querying procedure for the interpretations, create a weka sceleton for estimatin the parameters.
	 */
	public Instances initializeWekaInstanceSceleton(Dependency dep) throws BadParentSpecification {
		FastVector attributes=new FastVector();
		for(Feature f:dep.getContinuousFeatures()){
			//Only continuous features in parent set
			if(f.isContinuousOutput()){
				attributes.addElement(new Attribute(f.getFeatureIdentifier()));
			}
			else{
				throw new BadParentSpecification(" Linear Gaussians only take continuous parents! ");
			}
		}
		//adding class attribute - which is numeric
		attributes.addElement(new Attribute(dep.getHead().getAtom()));
		Instances inst=new Instances(dep.getHead().toString(),attributes,10000);
		inst.setClass((Attribute) attributes.lastElement());
		return inst;
	}
	
	/**
	 * Fill in the instances in the weka format for Linear regression to take place.
	 */
	public Instances fillInTheValueS(QueryData trainingData,Instances instancesSceleton) throws BadParentSpecification {
		this.averageValueForAttribute=new HashMap<Feature, Double>();
		HashMap<Feature,List<Value>> values=new HashMap<Feature, List<Value>>();
		Instances data=new Instances(instancesSceleton);
		for(Interpretation i:trainingData.getQuery_results().keySet()){
			for(MarkovBlanket mB:trainingData.getQuery_results().get(i)){				
				Instance inst=new Instance(data.numAttributes());
				//filling in the value of head atom first
				inst.setValue(data.classAttribute(), ((NumberValue)mB.getHead().getValue()).getNumber());
				int j=0;
				for(Feature f:trainingData.getDep().getContinuousFeatures()){
					if(f.isContinuousOutput()){
						//if input not defined?
						if(mB.getFeatureValues().get(trainingData.getDep().getFeatures().get(j)) instanceof UndefinedValue){
							//set attribute missing
							////MISSING VALUE!
							inst.setMissing(j++);

						}
						else{
							try{
								values.get(f).add(((NumberValue)mB.getFeatureValues().get(trainingData.getDep().getFeatures().get(j))));
							}
							catch(ClassCastException e){
								System.out.println("Problem with type of the feature. Check if CPD correctly specified. E.g., linear gaussian might not be suitable for: \n");
								System.out.println(trainingData.getDep());
								e.printStackTrace();
							}
							catch(NullPointerException e){
								values.put(f, new ArrayList<Value>());
								values.get(f).add(((NumberValue)mB.getFeatureValues().get(trainingData.getDep().getFeatures().get(j))));
							}
							inst.setValue(j, ((NumberValue)mB.getFeatureValues().get(trainingData.getDep().getFeatures().get(j))).getNumber());
							j++;
						}
					}
					else{
						throw new BadParentSpecification(" Linear Gaussians only take continuous parents! ");
					}
				}
				data.add(inst);
			}
		}
		for(Feature ft:values.keySet()){
			this.averageValueForAttribute.put(ft, getAverage(values.get(ft)));
		}
		return data;
	}
	
	
	/**
	 * get average of values in the list
	 * @param list
	 * @return
	 */
	private Double getAverage(List<Value> list) {
		double sum=0;
		for(Value v:list){
			sum+=((NumberValue)v).getNumber();
		}
		return sum/list.size();
	}

	/**
	 * Traing Linear Gaussian. It consists of performing ridge regression via Weka, and then estimating standard deviation
	 * separately.
	 */
	public LinearGParameters trainClassifier(QueryData qD,Instances trainingInstances) throws Exception {
		LinearRegression log = new LinearRegression();
		String[] options = weka.core.Utils.splitOptions("-S 1");
		log.setOptions(options);
		Instances structure = trainingInstances;
		log.setEliminateColinearAttributes(true);
		log.buildClassifier(structure);
		double[] coeff =log.coefficients();
        List<Double> coefficients=new ArrayList<Double>();
		for(Double d:coeff){
			coefficients.add(d);
		}
		coefficients.remove(trainingInstances.classIndex());     
        double intercept=coefficients.get(coefficients.size()-1);
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
		int i=0;
		for(Feature f:qD.getDep().getContinuousFeatures()){
			coeffs.put(f,coefficients.get(i++));
		}
		Regression reg=new Regression(intercept, coeffs);	
		double std=estimateStandardDeviation(qD.getFlatData(),reg);
		return new LinearGParameters(qD.getDep(), reg, std);
	}
	
	/**
	 * Traing Linear Gaussian. It consists of performing ridge regression via Weka, and then estimating standard deviation
	 * separately.
	 */
	public Parameters trainClassifier(List<MarkovBlanket> data,Dependency dep,Instances trainingInstances) throws Exception {
		LinearRegression log = new LinearRegression();
		String[] options = weka.core.Utils.splitOptions("-S 1");
		log.setOptions(options);
		Instances structure = trainingInstances;
		log.setEliminateColinearAttributes(true);
		log.buildClassifier(structure);
		double[] coeff =log.coefficients();
        List<Double> coefficients=new ArrayList<Double>();
		for(Double d:coeff){
			coefficients.add(d);
		}
		coefficients.remove(trainingInstances.classIndex());     
        double intercept=coefficients.get(coefficients.size()-1);
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
		int i=0;
		for(Feature f:dep.getContinuousFeatures()){
			coeffs.put(f,coefficients.get(i++));
		}
		Regression reg=new Regression(intercept, coeffs);	
		double std=estimateStandardDeviation(data,reg);
		return new LinearGParameters(null, reg, std);
	}
	
	/**
	 * Calculating the standard deviation of the Gaussian head atom 
	 * @param trainingData
	 * @param coeffs
	 * @return
	 */
	protected double estimateStandardDeviation(List<MarkovBlanket> data,Regression coeffs) {
		if(data.size()<2){
			return 0;
		}
		double n = 0;
		double sum = 0;
		double sum2 = 0;
			for(MarkovBlanket mB:data){			
				double mean=getMean(mB,coeffs);
				sum2 += ((((NumberValue)mB.getHead().getValue()).getNumber()) - mean) * ((((NumberValue)mB.getHead().getValue()).getNumber()) - mean);
				n++;
			}
		sum2=sum2+0.000000001;
		sum2=sum2/(n-1);
		return Math.sqrt(sum2);
	}
	
	/**
	 * Calculate mean value of the Gaussian for Markov Blanket and regression coefficients
	 * @param mB
	 * @param coeffs
	 * @return
	 */
	private double getMean(MarkovBlanket mB,Regression coeffs){
		double mean=coeffs.getIntercept();
		for(Feature f:mB.getDep().getContinuousFeatures()){
			//continuous feature undefined, then just return the mean
			if(mB.getFeatureValues().get(f) instanceof UndefinedValue){
				return mean;
			}
			try{
			   mean+=((NumberValue)mB.getFeatureValues().get(f)).getNumber()*coeffs.getWeights().get(f);
			}
			//this means that attribute value is undefined, and we will replace it with the
			//average value for this feature
			//MISSING VALUE!
			catch(ClassCastException e){
				//System.out.println(this.averageValueForAttribute);
				mean+=this.averageValueForAttribute.get(f)*coeffs.getWeights().get(f);
			}
		}
		return mean;
	}

	private Logistic trainClassifier(Instances trainingInstances){
		Logistic log=new Logistic();
		try {
			log.buildClassifier(trainingInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return log;
	}
	
	
	public Double getProbability(MarkovBlanket mB, double mean, double std) {
		BigDecimal bd = new BigDecimal(std).setScale(3, RoundingMode.HALF_EVEN);
		double rounded = bd.doubleValue();
		double norm = 1 / (Math.sqrt(2 * Math.PI) * std);
		double expon = Math.pow((((NumberValue)mB.getHead().getValue()).getNumber() - mean), 2) / (2 * Math.pow(std,2));
		double ret=norm * Math.exp(-expon);
		return ret;
	}
	public HashMap<Feature, Double> getAverageValueForAttribute() {
		return averageValueForAttribute;
	}

	public void setAverageValueForAttribute(
			HashMap<Feature, Double> averageValueForAttribute) {
		this.averageValueForAttribute = averageValueForAttribute;
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, LinearGParameters parameters) {
		double mean=getMean(mB, ((LinearGParameters)parameters).getPars().getReg_coeff());
		double std=((LinearGParameters)parameters).getPars().getStd();	
		Random ran=new Random();
		return new NumberValue(ran.nextGaussian()*std+mean);
	}

	@Override
	public Double getProbability(Value val, MarkovBlanket mB,LinearGParameters par) {
		return null;
	}

	@Override
	public Value getPrediction(MarkovBlanket mB, LinearGParameters parameters,Random ran) {
		double mean=getMean(mB, ((LinearGParameters)parameters).getPars().getReg_coeff());
		double std=((LinearGParameters)parameters).getPars().getStd();	
		double random_nr=ran.nextGaussian();
		return new NumberValue(random_nr*std+mean);
	}

	@Override
	public Value getPrediction_no_noise(MarkovBlanket mB,LinearGParameters parameters) {
		double mean=getMean(mB, ((LinearGParameters)parameters).getPars().getReg_coeff());
		double std=((LinearGParameters)parameters).getPars().getStd();	
		return new NumberValue(mean);
	}

	@Override
	public double getError(QueryData data, LinearGParameters paramters,boolean print) {
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
		acc_error=Math.sqrt(acc_error/n);
		double normalized_error=acc_error/(y_max-y_min);
		return normalized_error;
	}

	@Override
	public double getUnnormalizedError(QueryData data,LinearGParameters paramters, boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,paramters);
			try {
				acc_error+=Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2);
				if(print){
					System.out.println(mb.getHead().getValue().toNumber()+"-"+d.toNumber()+","+Math.pow(mb.getHead().getValue().toNumber()-d.toNumber(),2));
				}
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
		if(print){
			System.out.println("ACC: "+acc_error);
		}
		return acc_error;
	}

	@Override
	public LinearGParameters loadParametersFromCSV(String pathToCSVFile,Dependency dep) throws IOException {
		File csvFile=new File(pathToCSVFile);
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
        int lineNumber=0;
        Double stdeviation=null;
        Double[] coeffs=null;
        Double intercept=null;
	    while (line != null) {
	    	lineNumber++;
	    	if(lineNumber==1){ //read regression coefficients
	    		String[] coefficients=line.split(",");
	    		coeffs=new Double[dep.getFeatures().size()];
	    		intercept=Double.valueOf(coefficients[1]);
	    		int counter=0;
	    		for(int i=2;i<coefficients.length;i++){
	    			coeffs[counter++]=Double.valueOf(coefficients[i]);
	    		}
	    	}
	    	if(lineNumber==2){ //read standard deviation
	    		stdeviation=Double.valueOf(line.split(",")[1]);
	    		break;
	    	} 
	        line = br.readLine();
	    }
	    return new LinearGParameters(dep,new Regression(dep,intercept,coeffs), stdeviation);
	}

	

	
}
