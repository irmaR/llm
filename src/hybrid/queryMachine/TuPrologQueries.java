package hybrid.queryMachine;
import hybrid.cpds.CPD;
import hybrid.dependencies.Dependency;
import hybrid.experimenter.AlgorithmParameters;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.Average;
import hybrid.features.ComparisonFeature;
import hybrid.features.ComparisonFeatureContinuousOutput;
import hybrid.features.ContinuousOutputAggregate;
import hybrid.features.DiscreteInputContinuousOutput;
import hybrid.features.DiscretizedProportion;
import hybrid.features.Exist;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.OperatorFeature;
import hybrid.features.Feature;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.SelectorTermTuProlog;
import hybrid.features.ValueFt;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.NumberValue;
import hybrid.network.Range;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.operators.Operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.Library;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import alice.tuprolog.lib.InvalidObjectIdException;

/**
 * This class contains methods for handling tuprolog related queries. 
 * @author irma
 *
 */
public class TuPrologQueries {
	private Library lib;

	public TuPrologQueries(Library lib) {
		this.lib=lib;
	}

	/**
	 * Main method for getting Markov blanket values for ground atom g for dependency encoded with cpd.
	 * @param g - ground atom
	 * @param engine - engine with all necessary querrying theories and interpretation
	 * @param dep - dependency
	 * @param currentInterpretation current interpretation we're querying
	 * @return values for features. Each feature in dependency is mapped to its value
	 * @throws NoSolutionException 
	 */
	public HashMap<Feature, Value> getMBValues(GroundAtom g, Prolog engine,Dependency dep, Interpretation currentInterpretation) throws NoSolutionException {
		HashMap<Feature,Value> tmp=new HashMap<Feature, Value>();
		for(Feature f:dep.getFeatures()){
			tmp.put(f,getFeatureValue(g, engine,f,currentInterpretation));
		}
		return tmp;
	}



	/**
	 * Get Feature value for ground atom g in interpretation and for prolog engine
	 * @param g - ground atom
	 * @param engine- prolog engine with theories and interpretaion
	 * @param f - feature to query
	 * @param currentInterpretation - current interpretation to query
	 * @return value for the feature
	 * @throws NoSolutionException 
	 */
	public Value getFeatureValue(final GroundAtom g, final Prolog engine,Feature f,final Interpretation currentInterpretation) throws NoSolutionException {   
		//		Value tmpValue=null;
		//case 1: deterministic?
		Value tmpValue=f.dispatch(new QueryDispatcher(){
			@Override
			public Value getValue(ValueFt ft)  {
				try {
					return TuPrologQueries.this.getValue(g,engine,ft,currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(ContinuousOutputAggregate ft) {
				try {
					return  TuPrologQueries.this.getValue(g,engine,ft,currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(OperatorFeature ft) {
				try {
					return TuPrologQueries.this.getValue(g, engine, ft, currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(ComparisonFeature ft) {
				try {
					return TuPrologQueries.this.getValue(g, engine, ft, currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(DiscreteInputContinuousOutput ft) {
				try {
					return TuPrologQueries.this.getValue(g, engine, ft, currentInterpretation);
				} catch (NoSolutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(Mode ft) {
				try {
					return TuPrologQueries.this.getValue(g, engine, ft, currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Value getValue(ComparisonFeatureContinuousOutput ft) {
				try {
					return TuPrologQueries.this.getValue(g, engine, ft, currentInterpretation);
				} catch (NoSolutionException e) {
					e.printStackTrace();
				}
				return null;
			}


		});
		return tmpValue;
	}

	protected Value getValue(GroundAtom g, Prolog engine,ComparisonFeatureContinuousOutput ft,Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		List<Double> results=new ArrayList<Double>();
		Struct resStruct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results);
		Term selector = null;
		Struct query=null;
		//Non deterministic numeric feature
		//no selector
		if(g==null){
			if(ft.getSelector().size()==0){
				long solve_engine=System.nanoTime();
				query=new Struct("getContinuousValue",new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
			//with selector
			else{
				long solve_engine=System.nanoTime();
				selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
				query=new Struct("getContinuousValue",selector,Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
		}
		if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
			long solve_engine=System.nanoTime();
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
			res= engine.solve(query);
		}
		//with selector
		else{
			long solve_engine=System.nanoTime();
			selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resStruct);
			res= engine.solve(query);
		}

		List<Value> tmp=new ArrayList<Value>();
		for(Double d:results){
			tmp.add(new NumberValue(d));
		}
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		return ft.processValue(new ArrayFeatureValues(tmp));
	}

	protected Value getValue(GroundAtom g, Prolog engine,ComparisonFeature ft, Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		List<Double> results=new ArrayList<Double>();
		Struct resStruct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results);
		Term selector = null;
		Struct query=null;
		//Non deterministic numeric feature
		//no selector
		if(g==null){
			if(ft.getSelector().size()==0){
				long solve_engine=System.nanoTime();
				query=new Struct("getContinuousValue",new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
			//with selector
			else{
				long solve_engine=System.nanoTime();
				selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
				query=new Struct("getContinuousValue",selector,Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
		}
		if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
			long solve_engine=System.nanoTime();
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
			res= engine.solve(query);
		}
		//with selector
		else{
			long solve_engine=System.nanoTime();
			selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resStruct);
			res= engine.solve(query);
		}
       
		List<Value> tmp=new ArrayList<Value>();
		for(Double d:results){
			tmp.add(new NumberValue(d));
		}
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		return ft.processValue(new ArrayFeatureValues(tmp));
	}

	/**
	 * Get value for discrete/continuous ValueFt query for current state of the engine
	 * @param g - ground atom in head
	 * @param engine - current prolog engine
	 * @param ft - query
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 */
	protected Value getValue(GroundAtom g, Prolog engine, ValueFt ft, Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		//In case this deterministic feature doesn't have a selector: e.g., grade(S,C) | Value[intelligence(S)]
		Struct query=null;
		if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
			query=new Struct("getValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),new Var("Value"));
			long solve_engine=System.nanoTime();
			res= engine.solve(query);
		}
		//In case this deterministic feature has a selector: e.g., grade(S,C) | Value[takes(S,C),intelligence(S)]
		else{
			long solve_engine=System.nanoTime();
			Term selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
			query=new Struct("getValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),new Var("Value"));
			res= engine.solve(query);
		}
		//The feature is processing the value
		((alice.tuprolog.lib.JavaLibrary)lib).dismissAll();
		try{
			if(res.getVarValue("Value") instanceof alice.tuprolog.Double || res.getVarValue("Value") instanceof alice.tuprolog.Int){
				if(ft.isDiscreteOutput()){
					return ft.processValue(new ArrayFeatureValues(new Value[]{new StringValue(res.getVarValue("Value").toString())}));
				}
				return ft.processValue(new ArrayFeatureValues(new Value[]{new NumberValue(((alice.tuprolog.Number)res.getVarValue("Value")).doubleValue())}));
			}
			else{
				return ft.processValue(new ArrayFeatureValues(new Value[]{new StringValue(res.getVarValue("Value").toString())}));
			}
		}
		catch(NoSolutionException e){
			return new UndefinedValue();
		}
	}
	/**
	 * Get value for continuousOutputAggregate featuress 
	 * @param g - ground atom in head
	 * @param engine - current prolog engine
	 * @param ft - query
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 */
	protected Value getValue(GroundAtom g,Prolog engine, ContinuousOutputAggregate ft,Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		List<Double> results=new ArrayList<Double>();
		Struct resStruct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results);
		Term selector = null;
		Struct query=null;
		//Non deterministic numeric feature
		//no selector
		//if no ground atom specified we are only interested in general feature values in the interpretation
		if(g==null){
			if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
				long solve_engine=System.nanoTime();
				query=new Struct("getContinuousValue",new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
			//with selector
			else{
				long solve_engine=System.nanoTime();
				selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
				query=new Struct("getContinuousValue",selector,Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
		}
		else{
			if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
				long solve_engine=System.nanoTime();
				query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
			//with selector
			else{
				long solve_engine=System.nanoTime();
				selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
				query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resStruct);
				res= engine.solve(query);
			}
		}
		
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		List<Value> tmp=new ArrayList<Value>();
		for(Double d:results){
			tmp.add(new NumberValue(d));
		}
		return ft.processValue(new ArrayFeatureValues(tmp));
	}

	/**
	 * Finding values for features with bounded input i.e., the domain of the feature is 
	 * finite, while the output is not necessary finite. For example features Proportion and Count belong to this category as
	 * the Proportion finds the number of TRUE groundings out of the number of possible groundings.
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException 
	 */
	protected Value getValue(GroundAtom g, Prolog engine,DiscreteInputContinuousOutput ft, Interpretation currentInterpretation) throws NoSolutionException {
		CountMB results=new CountMB();
		Struct resultCount=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results);
		Term selector = null;
		Struct query=null;
		long solve_engine=System.nanoTime();

		try {
			selector = initializeSelectors(engine,ft.getSelector(),(AbstractConjunction)ft.getConjunction());	
		} catch (NoSolutionException e) {
			e.printStackTrace();
		}
		if(ft.getA()!=null && ft.getA().getPredicate().isContinuous()){
			SolveInfo res=null; 
			List<Double> resultsList=new ArrayList<Double>();
			Struct resStruct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(resultsList);
			if(g==null){
				if(ft.getSelector().size()==0 || ft.getConjunction().getLogvarRestrictions().size()==0){
					query=new Struct("getContinuousValue",new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
					res= engine.solve(query);
				}
				//with selector
				else{
					selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
					query=new Struct("getContinuousValue",selector,Term.createTerm(ft.getAtom().toString()),resStruct);
					res= engine.solve(query);
				}
			}
			else{
				if(ft.getSelector().size()==0 && ft.getConjunction().getLogvarRestrictions().size()==0){
					query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resStruct);
					res= engine.solve(query);
				}
				//with selector
				else{
					selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
					query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resStruct);
					res= engine.solve(query);
				}
			}
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(ft.getAtom().toString()),resStruct);
			//System.out.println(query);
			res=engine.solve(query);
			//System.out.println(res);
			//System.out.println(resultsList+ " "+resultsList.size());
			if(resultsList.size()!=0){
				results.incrementCount();
			}
		}
		else{
		query=new Struct("getDiscreteCountsOnlySelector",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,resultCount);
		engine.solve(query);
		}
		//System.out.println(results);
		//Turning count into percentages
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		//For proportion the first value in the list is the total count, and the second element is the number of possible groundings
		ArrayFeatureValues f=new ArrayFeatureValues(new Value[]{new NumberValue(results.getCount()),new NumberValue(currentInterpretation.getNrPossibleGroundings(ft.getUnboundArguments()))});
		//System.out.println(f);
		return ft.processValue(f);

	}

	/**
	 * Get values for feature with bounded range: that is their output range is bounded and not continuous
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 */
	protected Value getModeArrayBased(GroundAtom g,Prolog engine, Mode ft, Interpretation currentInterpretation) throws NoSolutionException {
		//Non deterministic discrete feature
		ArrayList<String> values=new ArrayList<String>();
		Term selector = null;
		Struct query=null;
		long time_begin=System.nanoTime();
		Struct resultMap=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(values);

		if(ft.getSelector().size()!=0 && ft.getConjunction().getLogvarRestrictions().size()==0){
			long solve_engine=System.nanoTime();
			selector = initializeSelectors(engine,ft.getSelector(),(AbstractConjunction)ft.getConjunction());	
			query=new Struct("getArrayDiscreteCounts",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resultMap);
			engine.solve(query);
		}
		else{
			long solve_engine=System.nanoTime();
			query=new Struct("getDiscreteCounts",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resultMap);
			engine.solve(query); 
		}
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		long time_begin_calculate_value=System.nanoTime();
		return null;
	}



	/**
	 * Get values for feature with bounded range: that is their output range is bounded and not continuous
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 */
	protected Value getValue(GroundAtom g,Prolog engine, Mode ft, Interpretation currentInterpretation) throws NoSolutionException {
		//Non deterministic discrete feature
		HashMap<String,Integer> results_count=new HashMap<String,Integer>();

		for(Value v:((RangeDiscrete)ft.getRange()).getValues()){
			results_count.put(((StringValue)v).getValue(), new Integer(0));
		}
		Term selector = null;
		Struct query=null;
		long time_begin=System.nanoTime();
		Struct resultMap=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results_count);
        //System.out.println("FT: "+ft+" SELECTOR: "+ft.getSelector());
		if(ft.getSelector().size()!=0 || ft.getConjunction().getLogvarRestrictions().size()!=0){
			long solve_engine=System.nanoTime();
			selector = initializeSelectors(engine,ft.getSelector(),(AbstractConjunction)ft.getConjunction());	
			query=new Struct("getDiscreteCounts",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resultMap);
			engine.solve(query);
		}
		else{
			long solve_engine=System.nanoTime();
			query=new Struct("getDiscreteCounts",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),resultMap);
			engine.solve(query); 
		}
		//System.out.println("QUERY!!!"+query);

		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		long time_begin_calculate_value=System.nanoTime();
		Value v= ft.processValue(new HashMapFeatureValue(results_count));
		//System.out.println(" VALUEs: "+results_count);
		//System.out.println("MODE: Time to calculate value: "+((double)(System.nanoTime()-time_begin_calculate_value))/1000000000.0);
		//System.out.println("---------------------------------------------");
		return v;
	}

	/**
	 * Get value of a complex query
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation
	 * @return
	 * @throws NoSolutionException
	 */
	protected Value getValue(GroundAtom g,Prolog engine, OperatorFeature ft,Interpretation currentInterpretation) throws NoSolutionException {
		//System.out.println("Operator feature!");
		SolveInfo res=null;
		List<Double> results=new ArrayList<Double>();
		Library lib=null;
		try {
			lib = engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		} catch (InvalidLibraryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Helper_class helper=new Helper_class(ft.getConjunction().get_operator());
		Struct helper_struct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(helper);
		Term selector = null;
		Struct query=null;
		//Non deterministic numeric feature
		//no selector
		Term term1=Term.createTerm(ft.getConjunction().getFirstLiteral().createFOLTerm());
		Term term2=Term.createTerm(ft.getConjunction().getSecondLiteral().createFOLTerm());
		Term List=null;
		List = new Var("List");
		//System.out.println("Term no value: "+g.createTermWithoutValue());
		if(ft.getConjunction().getContext()!=null || ft.getConjunction().getLogvarRestrictions().size()!=0){
			//System.out.println("We have context");
			List = initializeSelectors(engine,ft.getSelector(),(AbstractConjunction)ft.getConjunction());	
			query=new Struct("getComplexQueryResultContext",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),term1,term2,List,helper_struct);	
		}
		else{
			query=new Struct("getComplexQueryResult",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),term1,term2,helper_struct);
		}
		res= engine.solve(query);
		//System.out.println(query);
		//System.out.println("Result: "+res);
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		List<Value> tmp=new ArrayList<Value>();
		for(Double d:helper.getList()){
			tmp.add(new NumberValue(d));
		}
		return ft.processValue(new ArrayFeatureValues(tmp));
	}




	/**
	 * Make a term representing the selectors
	 * @param engine
	 * @param booleanAtoms
	 * @param conjunction 
	 * @return
	 * @throws NoSolutionException
	 */
	private Term initializeSelectors(Prolog engine,List<Literal> booleanAtoms, AbstractConjunction conjunction) throws NoSolutionException {
		Term List=null;
		List = new Var("List");
		List<Literal> sorted=new ArrayList<Literal>();
		if(booleanAtoms!=null){
		sorted.addAll(sortBooleans(booleanAtoms));
		}
		if(conjunction.getLogvarRestrictions()!=null && conjunction.getLogvarRestrictions().size()!=0){
			sorted.addAll(conjunction.getLogvarRestrictions());
		}
		for (Literal lp : sorted) {
			String term=lp.createFOLTerm();
			List = engine.solve(new Struct("append", List, new Struct((alice.tuprolog.Term) Term.createTerm(term), new Struct()), new Var("List1"))).getVarValue("List1");
		}

		return List;

	}

	/**
	 * Sort booleans according to internal predicates
	 * @param booleanPreds
	 * @return
	 */
	private List<Literal> sortBooleans(List<Literal> booleanPreds) {
		ArrayList<Literal> pr = new ArrayList<Literal>();
		Collections.sort(booleanPreds, new InternalComparator());
		return booleanPreds;
	}




}
