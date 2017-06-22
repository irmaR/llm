package hybrid.evaluation;

import java.util.ArrayList;
import java.util.List;

import hybrid.dependencies.*;
import hybrid.features.*;
import hybrid.network.Literal;

public class CalculateEditMetric {
	
		/**
		 * Find the edit metric between two dependencies
		 * @param correct - correct(true) dependency
		 * @param learned - learned dependency
		 */
		public double findDistance(Dependency correct, Dependency learned){
			double distance=0;
			List<Feature> correctFeatures=correct.getFeatures();
			List<Feature> learnedFeature=learned.getFeatures();

			//find the best matching
			//double bestDistance=Double.POSITIVE_INFINITY;
			BestMatch tmp=null;

			if(learnedFeature.size()==0){
				distance=0;
				for(Feature f:correctFeatures){
					//1 for aggregation 
					distance+=(1+f.getConjunction().getLiteralList().size());
				}
				return distance;
			}

			if(correctFeatures.size()==0){
				distance=0;
				for(Feature f:learnedFeature){
					//1 for aggregation 
					distance+=(1+f.getConjunction().getLiteralList().size());
				}
				return distance;
			}

			while(true){
				if(learnedFeature.size()==0 || correctFeatures.size()==0){
					break;
				}
				double bestDistance=Double.POSITIVE_INFINITY;
				
				for(Feature f:correctFeatures){
					BestMatch f1=findBestMatch(f,learnedFeature);

					if(f1.getDistance()<=bestDistance){
						bestDistance=f1.getDistance();
						tmp=f1;
					}	 
				}
				learnedFeature.remove(tmp.getBest_match_feature());
				correctFeatures.remove(tmp.getCorrect_ft());
				distance+=tmp.getDistance();
			}
			//In case we learn more features than needed
			//System.out.println("Distance so far: "+distance);
			System.out.println(" Leftover fts: "+learnedFeature);
			for(Feature f:learnedFeature){
				distance+=f.getConjunction().getLiteralList().size()+1;
			}
			for(Feature f:correctFeatures){
				distance+=f.getConjunction().getLiteralList().size()+1;
			}
			return distance;

		}


		private BestMatch findBestMatch(Feature f, List<Feature> learnedFeature) {
			double distance=Double.POSITIVE_INFINITY;
			Feature bestFeature=null;

			for(Feature f1:learnedFeature){
				double distance1=calculateDistance(f,f1);
				//distance for different number of literals
				//distance1+=(Math.abs(f.getAllLiterals().size()-f1.getAllLiterals().size()));
				if(distance1<distance){
					bestFeature=f1;
					distance=distance1;
				}
			}

			return new BestMatch(f,bestFeature,distance);
		}


		/**
		 * Calculate the distence between two features
		 * @param f
		 * @param f1
		 * @return
		 */
		private double calculateDistance(Feature f, Feature f1) {
			double distance=0;

			ArrayList<Literal> probPreds=new ArrayList<Literal>();
			probPreds.addAll(f1.getConjunction().getLiteralList());
			
			double diff1=-1;
			if(f.getConjunction().getLiteralList().size()>=f1.getConjunction().getLiteralList().size()){
			  diff1=difference(f,f1);
			}
			if(f1.getConjunction().getLiteralList().size()>f.getConjunction().getLiteralList().size()){
				diff1=difference(f1,f);
			}
			distance=diff1;
			//if aggregates don't match
			if(!f.getClass().equals(f1.getClass())){
				distance++;
			}
			return distance;
		}


		/**
		 * Find the difference between two features
		 * @param f
		 * @param f1
		 * @return
		 */
		private double difference(Feature f, Feature f1) {
			double dist=0;
			ArrayList<Literal> probPreds=new ArrayList<Literal>();
			probPreds.addAll(f.getConjunction().getLiteralList());
			for(Literal tmp:probPreds){
				boolean found_match=false;
				if(!f1.getConjunction().getLiteralList().contains(tmp)){
					dist++;
				}
			}
			return dist;
		}


	}

/**
 * Inner class representing a pair of features representing the best match
 * @author irma
 *
 */
 class BestMatch {

	private Feature correct_ft;
	private Feature best_match_feature;
	private double distance;



	public BestMatch(Feature correct_ft, Feature best_match_feature, double distance) {
		super();
		this.correct_ft = correct_ft;
		this.best_match_feature = best_match_feature;
		this.distance = distance;
	}



	public Feature getCorrect_ft() {
		return correct_ft;
	}



	public void setCorrect_ft(Feature correct_ft) {
		this.correct_ft = correct_ft;
	}



	public Feature getBest_match_feature() {
		return best_match_feature;
	}



	public void setBest_match_feature(Feature best_match_feature) {
		this.best_match_feature = best_match_feature;
	}



	public double getDistance() {
		return distance;
	}



	public void setDistance(double distance) {
		this.distance = distance;
	}



	@Override
	public String toString() {
		return "BestMatch [correct_ft=" + correct_ft + ", best_match_feature="
				+ best_match_feature + ", distance=" + distance + "]";
	}

}

