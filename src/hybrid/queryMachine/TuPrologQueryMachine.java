package hybrid.queryMachine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;

import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.DistributeFeaturesInBlocks;
import hybrid.featureGenerator.FeatureBlock;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Library;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;
import hybrid.cpds.CPD;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.interpretations.Data;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.penalties.Penalty;
import hybrid.querydata.QueryData;


/**
 * A class representing a query machine for finding Markov Blanket and values of features for
 * CPD cpd. This class implements interface QueryMachine and uses TuProlog to find the Markov blanket values.
 * @author irma
 *
 */
public class TuPrologQueryMachine extends QueryMachine {

	private Prolog engine;
	private Theory th;
	private Library lib;
	private HashMap<Atom,Double> inference_time_per_grounding;
	private HashMap<Interpretation,Prolog> engine_per_interpretation;
   
	/**
	 * Create object of TuPrologQueryMachine by giving the interpretations for grounded atoms.
	 * @param interpretations
	 * @param penaltyType TODO
	 * @throws IOException 
	 */
	public TuPrologQueryMachine(Data interpretations, Penalty penaltyType) throws IOException{
		super(interpretations,penaltyType);
		engine_per_interpretation=new HashMap<Interpretation, Prolog>();
		inference_time_per_grounding=new HashMap<Atom, Double>();
		//instantiate interpretations from Data


		for(Interpretation i:interpretations.getInterpretations()){
			//System.out.println(" INTERPRETATION: "+i.getPath_to_interpretation());
			InputStream is = this.getClass().getResourceAsStream("Queries.pl");
			//System.out.println("------------ Initializing interpretation for query machine ---------------------");
			Theory th_interp=null;
			Theory query_theory=null;
			try {
				long time_begin_theory_creation=System.nanoTime();
				th_interp = new Theory(i.getPrologFormat());
				double time_end=((double)(System.nanoTime()-time_begin_theory_creation))/1000000000.0;
				//System.out.println(" Time needed to obtain it: "+time_end+ "seconds");
				query_theory=new Theory(is);
			} catch (InvalidTheoryException e) {
				e.printStackTrace();
			}
			//creating prolog engine
			Prolog engine=new Prolog();
			try {
				//adding theory (made of available queries in Queries.pl)
				engine.addTheory(th_interp);
				engine.addTheory(query_theory);
			} catch (InvalidTheoryException e1) {
				e1.printStackTrace();
			}
			try {
				//load standard prolog library
				lib = engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			} catch (InvalidLibraryException e) {
				e.printStackTrace();
			}
			this.engine_per_interpretation.put(i, engine);
		}
	}


	

	@Override
	public QueryData getQueryResults(Dependency dep) {
		QueryData queryData = new QueryData(dep);
		//if the features were not pre-computed for this atom, compute them
		double interp_querying_total_time=0;
		double interp_querying_total_time_prolog=0;

		if(cache==null || cache.isEmpty() || !this.useCache){
			//System.out.println("No cache...");
			for (int i = 0; i < data.getInterpretations().size(); i++) {
				if(data.getInterpretations().get(i).getGroundAtoms(dep.getHead())==null){
					throw new NullPointerException("The number of ground atoms for: "+dep.getHead()+ "in this interpretation is zero. Check that input data contains ground atoms for this predicate!");
				}
				//System.out.println("Getting data from interpretations with "+data.getInterpretations().get(i).getGroundAtoms(dep.getHead()).size()+" groundings for :"+dep.getHead());
				long interpretation_loading_time=System.nanoTime();
				Interpretation inter = data.getInterpretations().get(i);
				Prolog engine=new Prolog();
				engine=this.engine_per_interpretation.get(data.getInterpretations().get(i));
				try {
					Library lib = engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
				} catch (InvalidLibraryException e1) {
					e1.printStackTrace();
				}
				//System.out.println(" Interpretation loaded for:"+dep+" loaded  .... in "+(((double)(System.nanoTime()-interpretation_loading_time))/1000000000.0)+ "seconds");			
				long interpretation_querrying_begin_time=System.nanoTime();
				try {
					queryData.addMarkovBlankets(inter,getMarkovBlanketResults(lib,inter.getGroundAtoms(dep.getHead()),engine,dep, inter).get(inter));
					queryData.add_inference_time_per_groundAtom(dep.getHead(), this.inference_time_per_grounding.get(dep.getHead()));
					queryData.incrementNrGroundingsForHead(inter.getGroundAtoms(dep.getHead()).size());
					//hybrid.loggers.DetailedStructureLearningForAtom.println("Evaluated on: "+inter.getGroundAtoms(dep.getHead()).size()+" ground atoms");
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				double time_end=((double)(System.nanoTime()-interpretation_querrying_begin_time))/1000000000.0;
				//hybrid.loggers.DetailedStructureLearningForAtom.println("Inference time: "+time_end+" seconds");
				//System.out.println(" Interpretation queried for:"+dep+" queried  .... in "+time_end+ "seconds");
				interp_querying_total_time+=time_end;
				//System.out.println("Interpretation "+i+"th queried out of:" +data.getInterpretations().size());
			}
			//System.out.println(" TOTAL TIME FOR INTERPRETATION QUERYING: "+interp_querying_total_time);
			//hybrid.loggers.DetailedStructureLearningForAtom.println(" TOTAL TIME FOR INTERPRETATION QUERYING: "+interp_querying_total_time);
		}
		//cache was set
		else{
			//compute markov blankets from cache
			//System.out.println(" CACHE EXISTS FOR: "+dep);
			//System.out.println(cache);
			//hybrid.loggers.DetailedStructureLearningForAtom.println("----------------- CACHE EXISTS!!! ----------------------------");
			//hybrid.loggers.DetailedStructureLearningForAtom.println("CACHE HAS: "+cache.keySet().size()+ " entries");
			for (int i = 0; i < data.getInterpretations().size(); i++) {
				Interpretation inter = data.getInterpretations().get(i);
				for(GroundAtom g:inter.getGroundAtoms(dep.getHead())){
					//System.out.println(g);
					//System.out.println(cache.get(dep.getHead()));
					queryData.addMarkovBlanket(inter, cache.get(dep.getHead()).getMarkovBlanket(inter, g, dep));
	
				}
			}			
		}
		return queryData;
	}

	/**
	 * To save computation time, the feature values should be pre-computed for an atom for which we defined
	 * a feature space.
	 * @param atom - atom for which we want to pre-compute the feature space
	 * @param featureSpace - the feature space
	 * @return
	 */
	@Override
	public ComputedFeatureSpace calculateCache(Atom atom,List<Feature> featureSpace,String procedure){
		ComputedFeatureSpace ftValues=new ComputedFeatureSpace(atom,featureSpace,data);
		double total_cache_time=0;   
        int counter=0;
		for(Feature ft:featureSpace){
			counter++;
			//System.out.println(ft);
			hybrid.loggers.DetailedStructureLearningForAtom.println(procedure+" Interp nr: "+data.getInterpretations().size()+" Calculating CACHE FOR: "+ft);
			System.out.println(counter+"th feature out of: "+featureSpace.size()+"\nTarget PRED: "+atom+"Calculating cache for: "+ft);
			long interpretation_querrying_begin_time=System.nanoTime();
			double perGroundAtomTime=0;

			if(AlgorithmParameters.isTrackRunningTimesFlag()){
				AlgorithmParameters.writeToTrackWriter("---------   Track time Cache  ---- FEATURE: "+ft+" ------------------");
			}

			for (int i = 0; i < data.getInterpretations().size(); i++) {

				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter("****** Interp: *********"+data.getInterpretations().get(i).getPath_to_interpretation()+" ***************");
				}

				long interpretation_loading_time=System.nanoTime();
				Interpretation inter = data.getInterpretations().get(i);
				Prolog engine=new Prolog();

				//load engine
				long engine_load_begin=System.nanoTime();
				engine=this.engine_per_interpretation.get(data.getInterpretations().get(i));
				try {
					Library lib = engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
				} catch (InvalidLibraryException e1) {
					e1.printStackTrace();
				}
				double time_end=((double)(System.nanoTime()-engine_load_begin))/1000000000.0;

				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter(" Time needed to load the engine: "+time_end);
				}

				//calculate markov blanket
				long feature_cache_load_begin=System.nanoTime();
				FeatureCache fCache=this.getFeatureValues(lib,engine, ft, inter, atom);
				double time_end_feature_cache=((double)(System.nanoTime()-feature_cache_load_begin))/1000000000.0;

				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter(" Time needed to calculate MB: "+time_end_feature_cache);
					AlgorithmParameters.writeToTrackWriter(" Average per ground atom: "+time_end_feature_cache/data.getInterpretations().get(i).getGroundAtoms(atom).size());
				}

				perGroundAtomTime+=(time_end_feature_cache/data.getInterpretations().get(i).getGroundAtoms(atom).size());

				//add markov blankets to cache
				long add_cache_begin=System.nanoTime();
				ftValues.addValueForFeatureAndInterpretation(inter,fCache);
				double time_end_add_cache=((double)(System.nanoTime()-add_cache_begin))/1000000000.0;

				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter(" Time needed to add MBs: "+time_end_add_cache);
				}
				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter("**********************************************************************");
				}

			}
			double time_end=((double)(System.nanoTime()-interpretation_querrying_begin_time))/1000000000.0;
			total_cache_time+=time_end;
			hybrid.loggers.DetailedStructureLearningForAtom.println("Cache for this feature calculated in: "+time_end+" seconds \n");
			//System.out.println("Cache for this feature calculated in: "+time_end+" seconds \n");
			hybrid.loggers.DetailedStructureLearningForAtom.println();
			if(AlgorithmParameters.isTrackRunningTimesFlag()){
				AlgorithmParameters.writeToTrackWriter("\n-------------------- cache time (per ground atom for all interps): "+perGroundAtomTime*data.getInterpretations().size()+" seconds -------------\n");	        	
			}
		}

		hybrid.loggers.DetailedStructureLearningForAtom.println("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
		hybrid.loggers.DetailedStructureLearningForAtom.println("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		if(AlgorithmParameters.isTrackRunningTimesFlag()){
			AlgorithmParameters.writeToTrackWriter("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
			AlgorithmParameters.writeToTrackWriter("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		}
		//System.out.println("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
		//System.out.println("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		return ftValues;

	}



	/**
	 * Get feature ft values in engine and interpretation inter for head atom. 
	 * @param engine
	 * @param ft
	 * @param inter
	 * @param head_atom
	 * @return
	 */
	protected FeatureCache getFeatureValues(Library lib,Prolog engine,Feature ft, Interpretation inter,Atom head_atom){
		long querrying_begin_time=System.nanoTime();
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		FeatureCache tmp=new FeatureCache(ft);
		/*System.out.println(head_atom);
		System.out.println(inter);
		for(GroundAtom g:inter.getGroundAtoms(head_atom)){
			System.out.println(g);
		}
		*/
		for(GroundAtom g:inter.getGroundAtoms(head_atom)){
			try {
				
				tmp.addValue(g, tQrs.getFeatureValue(g, engine, ft, inter));
			} catch (NoSolutionException e) {
				e.printStackTrace();
			}
		}	

		return tmp;

	}

	/**
	 * Getting Markov blanket values for all ground atoms of a head predicate in dependency in an interpretation. 
	 * @param lib 
	 * @param groundAtomList
	 * @param engine
	 * @param currentInterpretation TODO
	 * @param cpd
	 * @return
	 * @throws NoSolutionException 
	 */
	private HashMap<Interpretation,List<MarkovBlanket>> getMarkovBlanketResults(Library lib, List<GroundAtom> groundAtomList,Prolog engine, Dependency dep, Interpretation currentInterpretation) throws NoSolutionException {
		Runtime rt = Runtime.getRuntime();
		HashMap<Interpretation,List<MarkovBlanket>> tmp=new HashMap<Interpretation, List<MarkovBlanket>>();
		List<MarkovBlanket> tmp1=new ArrayList<MarkovBlanket>();
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		QueryData qData=new QueryData(dep);
		int nr_groundings=0;

		if(groundAtomList==null){
			throw new NullPointerException("Check if all the atoms were specified in ntw. There are no ground atoms for "+qData.getDep().getHead()+ " in the database");
		}
		long time_begin=System.nanoTime();
		long time_begin_grAtoms=System.nanoTime();
		for(GroundAtom g:groundAtomList){
			nr_groundings++;
			HashMap<Feature,Value> ftValue=tQrs.getMBValues(g,engine,dep, currentInterpretation);		
			//System.out.println("ADDING MARKOV BLANKET: "+new MarkovBlanket(g, dep,ftValue));
			tmp1.add(new MarkovBlanket(g, dep,ftValue));
		}
		//System.out.println(" INFERENCE TIME ALL GROUND ATOMS "+dep+" = "+((double)(System.nanoTime()-time_begin_grAtoms))/1000000000.0);
		double time_end=((double)(System.nanoTime()-time_begin))/1000000000.0;
		this.inference_time_per_grounding.put(dep.getHead(), time_end/nr_groundings);
		//System.out.println(" INFERENCE TIME PER GROUND ATOM OF "+dep+" = "+this.inference_time_per_grounding);
		tmp.put(currentInterpretation, tmp1);
		return tmp;
	}


	public double getStandardDeviationOfTargetPredicate(Dependency dep) throws WrongValueType {
		QueryData markov_blankets=this.getQueryResults(dep);
		double sum1=0;
		double sum2=0;
		int n=0;

		for(Interpretation i:markov_blankets.getQuery_results().keySet()){
			for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
				n++;
				sum1=sum1+mB.getHead().getValue().toNumber();
			}
		}

		double mean = sum1 / n;

		for(Interpretation i:markov_blankets.getQuery_results().keySet()){
			for(MarkovBlanket mB:markov_blankets.getQuery_results().get(i)){
				sum2 = sum2 + (mB.getHead().getValue().toNumber() - mean)*(mB.getHead().getValue().toNumber() - mean);
			}
		}

	    double variance = sum2 / (n - 1);
			return Math.sqrt(variance);
	}








}
