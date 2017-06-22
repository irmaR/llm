package hybrid.cpdEvaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hybrid.cpds.CPD;
import hybrid.cpds.GaussianPrior;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.interpretations.Interpretation;
import hybrid.network.*;
import hybrid.parameters.Gaussian;
import hybrid.parameters.Parameters;
import hybrid.penalties.Penalty;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structureLearning.StructureScore;
import hybrid.utils.Logarithm2;

/**
 * Class for evaluating Gaussian prior CPD - estimating parameters, calculating score given parameters and 
 * markov blanket. 
 * @author irma
 *
 */
public class GaussianPriorEvaluator implements ContinuousEval<Gaussian> {

	@Override
	public Double getProbability(MarkovBlanket mB, Gaussian cpd) {
		double mean=((Gaussian)cpd).getMean();
		double std=((Gaussian)cpd).getStd();
		if(Double.isNaN(mean) || Double.isNaN(std)){
			return Double.MIN_VALUE;
		}
		double norm = 1 / (Math.sqrt(2 * Math.PI) * std);
		double expon = Math.pow((((NumberValue)mB.getHead().getValue()).getNumber() - mean), 2) / (2 * Math.pow(std,2));
		return norm * Math.exp(-expon);
	}

	@Override
	public Gaussian estimateParameters(QueryData trainingData) {
		List<NumberValue> values=new ArrayList<NumberValue>();
		for(Interpretation i:trainingData.getQuery_results().keySet()){
			for(MarkovBlanket mB:trainingData.getQuery_results().get(i)){
				values.add((NumberValue) mB.getHead().getValue());
			}
		}
		double n = values.size();
		double sum = 0;
		double sum2 = 0;
	
		for (NumberValue v : values) {
			sum += v.getNumber();
		}
		double mean = sum / n;

		for (NumberValue v : values) {
			sum2 += ((v.getNumber()) - mean) * ((v.getNumber()) - mean);
		}
		double std = Math.sqrt(sum2 / (n - 1));
		trainingData.getDep().getCpd().setParameters(new Gaussian(mean,std));
		return new Gaussian(mean,std);
		}


	@Override
	public StructureScore calculatePLL(QueryData data, Gaussian pars,Penalty pen) {
		double pll=0;
		int nr_data_points=0;
		for(Interpretation i:data.getQuery_results().keySet()){
			for(MarkovBlanket mB:data.getQuery_results().get(i)){
				pll+=Logarithm2.logarithm2(this.getProbability(mB, pars));
				nr_data_points++;
			}
		}
		return new StructureScore(pll, pen.calculatePenalty(data.getDep(), nr_data_points), pll-pen.calculatePenalty(data.getDep(), nr_data_points));
	}

	@Override
	public Value getPrediction(MarkovBlanket mB,Gaussian cpd) {
		double mean=((Gaussian)cpd).getMean();
		Random ran=new Random();
		double std=((Gaussian)cpd).getStd();
		return new NumberValue(ran.nextGaussian()*std+mean);
	}

	@Override
	public Double getProbability(Value val, MarkovBlanket mB, Gaussian par) {
		return this.getProbability(mB, par);
	}

	@Override
	public Value getPrediction(MarkovBlanket mB,Gaussian cpd, Random ran) {
		Double mean=((Gaussian)cpd).getMean();
		double std=((Gaussian)cpd).getStd();
		double random_nr=ran.nextGaussian();
		return new NumberValue(random_nr*std+mean);
	}

	@Override
	public Value getPrediction_no_noise(MarkovBlanket mB, Gaussian cpd) {
		Double mean=((Gaussian)cpd).getMean();
		return new NumberValue(mean);
	}

	@Override
	public double getError(QueryData data,Gaussian par,boolean print) {
		double acc_error=0;
		int n=0;
		double y_max=0;
		double y_min=Double.POSITIVE_INFINITY;
		for(MarkovBlanket mb:data.getFlatData()){
			n++;
			Value d=getPrediction_no_noise(mb,par);
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
	public double getUnnormalizedError(QueryData data, Gaussian paramters,boolean print) {
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
