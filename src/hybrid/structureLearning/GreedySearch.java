package hybrid.structureLearning;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.FeatureAlreadyExists;
import hybrid.experimenter.CVIteration;
import hybrid.experimenter.LearnedDependencyStatistics;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import hybrid.features.Feature;
import hybrid.network.Atom;
import hybrid.queryMachine.QueryMachine;
import hybrid.querydata.QueryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.ComparisonFailure;

public class GreedySearch extends StructureSearch{
    	
	private CVIteration iteration;
	
	
	@Override
	public LearnedDependency performSearchForAtom(Atom a, List<Feature> features,QueryMachine query_machine_training,QueryMachine query_machine_validation,QueryMachine query_machine_test) {
		//Log feature space
		long time_begin=System.nanoTime();
		//track some statistics
		LearnedDependencyStatistics statistics=new LearnedDependencyStatistics(a);
		//first determine score with empty parent set
		Dependency dep_independent=new Dependency(a,new Feature[]{});
	
		//setting current score to -Infinity
		//estimate parameters of the independent model
		//setting current best score to the independent model one
		LearnedDependency bestScore=getParsAndScoreMarginal(a,query_machine_training,query_machine_validation,dep_independent);
		hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal score: "+bestScore);
		LearnedDependency currentScore=new LearnedDependency(null,Double.NEGATIVE_INFINITY);
		List<LearnedDependency> extension_scores=null;
	
		while (bestScore.betterThan(currentScore) && (bestScore.getDep().getFeatures().size()<20)) {
			hybrid.loggers.DetailedStructureLearningForAtom.println(" Extensions ... " );
			currentScore=bestScore;
			extension_scores=new ArrayList<LearnedDependency>();
			extension_scores.add(currentScore);
			int i=0;
			
			for(Feature ft:features){
				System.out.println("Adding"+i +"th feature: "+ ft);
				i++;
				Dependency dep_tmp;
				try {
					dep_tmp = bestScore.getDep().extend(ft);
				} catch (FeatureAlreadyExists e1) { //feature already exists catch
					System.out.println(" Feature already exists in the parent set");
					continue;
				}
				System.out.println("---------- ESTIMATING PARAMETERS: --------------");
				QueryData queryDataextensions_training=query_machine_training.getQueryResults(dep_tmp);
				dep_tmp.getCpd().setParameters(dep_tmp.getCpd().getCpdEvaluator().estimateParameters(queryDataextensions_training));
				System.out.println("---------- PARAMETER ESTIMATION DONE --------------");
                
				//calculate pll
				try{
				 System.out.println("---------- CALCULATING THE SCORE PARAMETERS: --------------");
				 QueryData queryDataextensions_validation=query_machine_validation.getQueryResults(dep_tmp);
				 double pll=Double.NaN;
				 pll=dep_tmp.getCpd().getCpdEvaluator().calculatePLL(queryDataextensions_validation,dep_tmp.getCpd().getParameters(),query_machine_validation.getPenalty()).getScore();
				 System.out.println("---------- SCORING COMPLETED: --------------");
				 LearnedDependency extendedFeature=new LearnedDependency(dep_tmp,pll);
				 if(!new Double(extendedFeature.getScore()).isNaN()){
					 extension_scores.add(extendedFeature);
				 }
				 hybrid.loggers.DetailedStructureLearningForAtom.println("SCORE: "+dep_tmp+" = "+pll); 
				}
				catch(Exception e){
					System.out.println(" Problem with feature: "+ft+ " index: "+i+ " for dependency: "+dep_tmp);
					System.out.println(" Problematic iteration : "+this.iteration);
					e.printStackTrace();
				    System.exit(1);
				}	
				 hybrid.loggers.DetailedStructureLearningForAtom.flush();
			}
			//in case any better extensions are possible, return the current best score
			if(extension_scores.size()==0){
				return bestScore;
			}	
			//try{
			/*System.out.println("Unsorted scores: ");
			for(LearnedDependency l:extension_scores){
				System.out.println(l);
			}*/
			Collections.sort(extension_scores);
			/*System.out.println("Sorted scores: ");
			for(LearnedDependency l:extension_scores){
				System.out.println(l);
			}*/
		//	}
		/*	catch(IllegalArgumentException e){
				List<LearnedDependency> failureFts=new ArrayList<LearnedDependency>();
				for(LearnedDependency s:extension_scores){
					if(Double.isNaN(s.getScore()) || Double.isInfinite(s.getScore())){
						failureFts.add(s);
					}
				}
				System.out.println("-------- The list of undefined scores: -------");
				for(LearnedDependency sc:failureFts){
					System.out.println(sc);
				}
			}*/
			//take the first element of the sorted list ( the one with the highest score)
			
			bestScore=extension_scores.get(0);
			hybrid.loggers.DetailedStructureLearningForAtom.println("Best score: "+bestScore);
			hybrid.loggers.DetailedStructureLearningForAtom.println("--------------------------------------------------------");
			hybrid.loggers.DetailedStructureLearningForAtom.println(bestScore+" > "+currentScore+"?"+ "  "+(bestScore.getScore()>currentScore.getScore())+" ");
		}
		//some statistics
		hybrid.loggers.DetailedStructureLearningForAtom.println("TIME: (nanoseconds): "+ (System.nanoTime()-time_begin));
		hybrid.loggers.DetailedStructureLearningForAtom.println("TIME: (seconds): "+ ((double)(System.nanoTime()-time_begin))/1000000000.0);
		statistics.setLearningTime(((double)(System.nanoTime()-time_begin))/1000000000.0);
		statistics.setNr_validation_instances(query_machine_validation.getData().getNrGroundingsInData(a));
		statistics.setNr_training_instances(query_machine_training.getData().getNrGroundingsInData(a));
		statistics.setWPLL_score_validation(bestScore.getScore()/statistics.getNr_validation_instances());
		statistics.setLearned_dependency(bestScore.getDep());
        //set statistics to the learned dependency
		bestScore.setStatistics(statistics);
		hybrid.loggers.DetailedStructureLearningForAtom.println("Time needed for structure learning: "+TimeUnit.SECONDS.convert((System.nanoTime()-time_begin), TimeUnit.NANOSECONDS)+" secs. -----------------");
		hybrid.loggers.DetailedStructureLearningForAtom.println("Top five features: \n");
		//reporting five best dependencies for the overview
		if(extension_scores.size()>5){
			hybrid.loggers.DetailedStructureLearningForAtom.println("FIVE BEST DEPENDENCIES: ");
		      for(int i=0;i<6;i++){
			     hybrid.loggers.DetailedStructureLearningForAtom.println(i+": "+extension_scores.get(i));
		      }
		 }
		hybrid.loggers.DetailedStructureLearningForAtom.println("--------------------------------------------------");
		hybrid.loggers.DetailedStructureLearningForAtom.println("Learned dep: "+bestScore);
		return bestScore;
	}

/**
 * Estimate parameters and getvscore for marginal
 * @param a
 * @param query_machine_training
 * @param query_machine_validation
 * @param dep_independent
 * @return
 */
	private LearnedDependency getParsAndScoreMarginal(Atom a,QueryMachine query_machine_training, QueryMachine query_machine_validation, Dependency dep_independent) {
		System.out.println("---------- ESTIMATING PARAMETERS MARGINAL: -------------- FOR "+a);
		QueryData data_marginal=query_machine_training.getQueryResults(dep_independent);
	    
		dep_independent.getCpd().setParameters(dep_independent.getCpd().getCpdEvaluator().estimateParameters(data_marginal));
		System.out.println("---------- PARAMETER ESTIMATION DONE -------------- FOR "+a);

		//getting score of the independent model
		 hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency parameters " +dep_independent.getCpd().getParameters());
		 System.out.println("---------- CALCULATING THE SCORE PARAMETERS MARGINAL: --------------");
		 double score=0;
		 
		 score=dep_independent.getCpd().getCpdEvaluator().calculatePLL(query_machine_validation.getQueryResults(dep_independent), dep_independent.getCpd().getParameters(),query_machine_validation.getPenalty()).getScore();
		 
		 System.out.println("---------- SCORING COMPLETED: -------------- "+a);
		 hybrid.loggers.DetailedStructureLearningForAtom.println("Marginal dependency " +dep_independent);
		 return new LearnedDependency(dep_independent, score);
	}


	public void setIteration(CVIteration iteration) {
		this.iteration = iteration;
	}


	public CVIteration getIteration() {
		return iteration;
	}

	
	
}
