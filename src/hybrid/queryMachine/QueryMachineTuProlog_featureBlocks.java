package hybrid.queryMachine;

import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.DistributeFeaturesInBlocks;
import hybrid.featureGenerator.FeatureBlock;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Value;
import hybrid.penalties.Penalty;
import hybrid.querydata.QueryData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Library;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

public class QueryMachineTuProlog_featureBlocks extends QueryMachine {

	public QueryMachineTuProlog_featureBlocks(Data data, Penalty penalty) {
		super(data, penalty);
		// TODO Auto-generated constructor stub
	}

	@Override
	public QueryData getQueryResults(Dependency depend) {
		// TODO Auto-generated method stub
		return null;
	}
}
/*
	private Prolog engine;
	private Theory th;
	private Library lib;
	private HashMap<Atom,Double> inference_time_per_grounding;
	private HashMap<Interpretation,Prolog> engine_per_interpretation;

	*//**
	 * Create object of TuPrologQueryMachine by giving the interpretations for grounded atoms.
	 * @param interpretations
	 * @param penaltyType TODO
	 * @throws IOException 
	 *//*
	public QueryMachineTuProlog_featureBlocks(Data interpretations, Penalty penaltyType) throws IOException{
		super(interpretations,penaltyType);
		engine_per_interpretation=new HashMap<Interpretation, Prolog>();
		inference_time_per_grounding=new HashMap<Atom, Double>();
		//instantiate interpretations from Data


		for(Interpretation i:interpretations.getInterpretations()){
			System.out.println(" INTERPRETATION: "+i.getPath_to_interpretation());
			InputStream is = this.getClass().getResourceAsStream("Queries.pl");
			System.out.println("------------ Initializing interpretation for query machine ---------------------");
			Theory th_interp=null;
			Theory query_theory=null;
			try {
				long time_begin_theory_creation=System.nanoTime();
				th_interp = new Theory(i.getPrologFormat());
				double time_end=((double)(System.nanoTime()-time_begin_theory_creation))/1000000000.0;
				System.out.println(" Time needed to obtain it: "+time_end+ "seconds");
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
	
	*//**
	 * To save computation time, the feature values should be pre-computed for an atom for which we defined
	 * a feature space.
	 * @param atom - atom for which we want to pre-compute the feature space
	 * @param featureSpace - the feature space
	 * @return
	 *//*
	@Override
	public ComputedFeatureSpace calculateCache(Atom atom,List<Feature> featureSpace,String procedure){
		ComputedFeatureSpace ftValues=new ComputedFeatureSpace(atom,featureSpace,data);
		double total_cache_time=0;   

		DistributeFeaturesInBlocks dF=new DistributeFeaturesInBlocks();
		List<FeatureBlock> featureBlocks=dF.distributFeaturesToBlocks(featureSpace);

	
		for(FeatureBlock fBlock:featureBlocks){
			HashMap<GroundAtom, FeatureValue> ftValuesBlock=null;
			
			for(Feature ft:fBlock.getFeaturesInThisBlock()){
				hybrid.loggers.DetailedStructureLearningForAtom.println(procedure+" Interp nr: "+data.getInterpretations().size()+" Calculating CACHE FOR: "+ft);
				System.out.println(procedure+" "+featureSpace.size()+" Target PRED: "+atom+"Calculating cache for: "+ft);
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

					
					
					long feature_cache_load_begin=System.nanoTime();
					FeatureCache fCache=null;
					
					//if there is only one feature in this block just calculate the markov blanket
					if(fBlock.getFeaturesInThisBlock().size()==1){
					    fCache=this.getMarkovBlankets(lib,engine, ft, inter, atom);
					}
					//else, if the groundings are not calculated, calculate them, and they are gonna be shared by other features
					else{
						System.out.println("BLOCK OF FEATURES");
						if(ftValuesBlock==null){
						   ftValuesBlock=this.getValuesOfFeatures(lib,engine, ft, inter, atom);
						}
						fCache=this.getMarkovBlankets(ftValuesBlock,ft, inter, atom);
					}
					
					
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
				System.out.println("Cache for this feature calculated in: "+time_end+" seconds \n");
				hybrid.loggers.DetailedStructureLearningForAtom.println();
				if(AlgorithmParameters.isTrackRunningTimesFlag()){
					AlgorithmParameters.writeToTrackWriter("\n-------------------- cache time (per ground atom for all interps): "+perGroundAtomTime*data.getInterpretations().size()+" seconds -------------\n");	        	
				}
			}
			hybrid.loggers.DetailedStructureLearningForAtom.flush();
		}
		hybrid.loggers.DetailedStructureLearningForAtom.println("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
		hybrid.loggers.DetailedStructureLearningForAtom.println("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		if(AlgorithmParameters.isTrackRunningTimesFlag()){
			AlgorithmParameters.writeToTrackWriter("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
			AlgorithmParameters.writeToTrackWriter("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		}
		System.out.println("\n-------------------- Average cache time (per interpretation): "+(total_cache_time)/data.getInterpretations().size()+" -------------\n");
		System.out.println("\n-------------------- Average cache time (per feature per interpretation): "+(total_cache_time/featureSpace.size())/data.getInterpretations().size()+" -------------\n");

		return ftValues;

	}

	*//**
	 * Get feature ft values in engine and interrpetation inter for head atom. 
	 * @param engine
	 * @param ft
	 * @param inter
	 * @param head_atom
	 * @return
	 *//*
	protected FeatureCache getMarkovBlankets(Library lib,Prolog engine,Feature ft, Interpretation inter,Atom head_atom){
		long querrying_begin_time=System.nanoTime();
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		FeatureCache tmp=new FeatureCache(ft);
		for(GroundAtom g:inter.getGroundAtoms(head_atom)){
			try {
				tmp.addValue(g, tQrs.getFeatureValue(g, engine, ft, inter));
			} catch (NoSolutionException e) {
				e.printStackTrace();
			}
		}	

		return tmp;

	}
	
	
	*//**
	 * Get feature ft values in engine and interrpetation inter for head atom. 
	 * @param engine
	 * @param ft
	 * @param inter
	 * @param head_atom
	 * @return
	 *//*
	protected FeatureCache getMarkovBlankets(HashMap<GroundAtom,FeatureValue> feature_values,Feature ft, Interpretation inter, Atom head_atom){
		long querrying_begin_time=System.nanoTime();
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		FeatureCache tmp=new FeatureCache(ft);
		for(GroundAtom g:inter.getGroundAtoms(head_atom)){
			tmp.addValue(g, ft.processValue(feature_values.get(g)));
		}	

		return tmp;

	}
	
	*//**
	 * Getting Markov blanket values for all ground atoms of a head predicate in dependency in an interpretation. 
	 * @param lib 
	 * @param groundAtomList
	 * @param engine
	 * @param currentInterpretation TODO
	 * @param cpd
	 * @return
	 * @throws NoSolutionException 
	 *//*
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
			tmp1.add(new MarkovBlanket(g, dep,ftValue));

		}
		System.out.println(" INFERENCE TIME ALL GROUND ATOMS "+dep+" = "+((double)(System.nanoTime()-time_begin_grAtoms))/1000000000.0);

		double time_end=((double)(System.nanoTime()-time_begin))/1000000000.0;
		this.inference_time_per_grounding.put(dep.getHead(), time_end/nr_groundings);
		System.out.println(" INFERENCE TIME PER GROUND ATOM OF "+dep+" = "+this.inference_time_per_grounding);
		tmp.put(currentInterpretation, tmp1);
		return tmp;
	}
	
	
	
	*//**
	 * Get feature ft values in engine and interrpetation inter for head atom. 
	 * @param engine
	 * @param ft
	 * @param inter
	 * @param head_atom
	 * @return
	 *//*
	protected HashMap<GroundAtom,FeatureValue> getValuesOfFeatures(Library lib,Prolog engine,Feature ft, Interpretation inter,Atom head_atom){
		long querrying_begin_time=System.nanoTime();
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		FeatureCache tmp=new FeatureCache(ft);
		FeatureValue fts=null;
		HashMap<GroundAtom,FeatureValue> featureValues=new HashMap<GroundAtom, FeatureValue>();
		
		for(GroundAtom g:inter.getGroundAtoms(head_atom)){
			try {
				featureValues.put(g,tQrs.getFeatureValue(g, engine, ft, inter));
			} catch (NoSolutionException e) {
				e.printStackTrace();
			}
		}	
		return featureValues;

	}

	
	
	@Override
	public QueryData getQueryResults(Dependency dep) {
		QueryData queryData = new QueryData(dep);
		//if the features were not pre-computed for this atom, compute them
		double interp_querying_total_time=0;
		double interp_querying_total_time_prolog=0;
		if(cache==null){
			for (int i = 0; i < data.getInterpretations().size(); i++) {
				System.out.println("Getting data from interpretations with "+data.getInterpretations().get(i).getGroundAtoms(dep.getHead()).size()+" groundings for :"+dep.getHead());
				long interpretation_loading_time=System.nanoTime();
				Interpretation inter = data.getInterpretations().get(i);

				Prolog engine=new Prolog();
				engine=this.engine_per_interpretation.get(data.getInterpretations().get(i));
				try {
					Library lib = engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
				} catch (InvalidLibraryException e1) {
					e1.printStackTrace();
				}
				
				System.out.println(" Interpretation loaded for:"+dep+" loaded  .... in "+(((double)(System.nanoTime()-interpretation_loading_time))/1000000000.0)+ "seconds");			
				long interpretation_querrying_begin_time=System.nanoTime();
				try {
					queryData.addMarkovBlankets(inter,getMarkovBlanketResults(lib,inter.getGroundAtoms(dep.getHead()),engine,dep, inter).get(inter));
					queryData.add_inference_time_per_groundAtom(dep.getHead(), this.inference_time_per_grounding.get(dep.getHead()));
					queryData.incrementNrGroundingsForHead(inter.getGroundAtoms(dep.getHead()).size());
					//hybrid.Loggers.DetailedStructureLearningForAtom.println("Evaluated on: "+inter.getGroundAtoms(dep.getHead()).size()+" ground atoms");
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				double time_end=((double)(System.nanoTime()-interpretation_querrying_begin_time))/1000000000.0;
				//hybrid.Loggers.DetailedStructureLearningForAtom.println("Inference time: "+time_end+" seconds");
				System.out.println(" Interpretation queried for:"+dep+" queried  .... in "+time_end+ "seconds");
				interp_querying_total_time+=time_end;
			}
			System.out.println(" TOTAL TIME FOR INTERPRETATION QUERYING: "+interp_querying_total_time);
			//System.out.println(" TOTAL TIME FOR INTERPRETATION QUERYING (PROLOG): "+interp_querying_total_time_prolog);

			//hybrid.Loggers.DetailedStructureLearningForAtom.println(" TOTAL TIME FOR INTERPRETATION QUERYING: "+interp_querying_total_time);
		}
		//cache was set
		else{
			//compute markov blankets from cache
			hybrid.loggers.DetailedStructureLearningForAtom.println("----------------- CACHE EXISTS!!! ----------------------------");
			hybrid.loggers.DetailedStructureLearningForAtom.println("CACHE HAS: "+cache.keySet().size()+ " entries");

			for (int i = 0; i < data.getInterpretations().size(); i++) {
				Interpretation inter = data.getInterpretations().get(i);
				for(GroundAtom g:inter.getGroundAtoms(dep.getHead())){
					queryData.addMarkovBlanket(inter, cache.get(dep.getHead()).getMarkovBlanket(inter, g, dep));
				}
			}			
		}
		return queryData;
	}



	
}
*/