package hybrid.queryMachine;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.Feature;
import hybrid.interpretations.Interpretation;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.NumberValue;
import hybrid.network.RangeDiscrete;
import hybrid.network.StringValue;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import alice.tuprolog.Library;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

public class TuPrologFeatureValuesExtractor {
}
/*
	private Library lib;

	public TuPrologFeatureValuesExtractor(Library lib) {
		this.lib=lib;
	}

	*//**
	 * Main method for getting Markov blanket values for ground atom g for dependency encoded with cpd.
	 * @param g
	 * @param engine
	 * @param currentInterpretation TODO
	 * @param cpd
	 * @return
	 * @throws NoSolutionException 
	 *//*
	public HashMap<Feature, FeatureValue> getMBValues(GroundAtom g, Prolog engine,Dependency dep, Interpretation currentInterpretation) throws NoSolutionException {
		HashMap<Feature,FeatureValue> tmp=new HashMap<Feature, FeatureValue>();

		for(Feature f:dep.getFeatures()){
			FeatureValue tmpValue=null;
			//case 1: deterministic?
			if(f.isDeterministic()){
				tmpValue=queryforDeterministicFeature(g,engine,(DeterministicFeature)f, currentInterpretation);
				tmp.put(f, tmpValue);
			}
			//case 2: non-deterministic?
			else{
				tmpValue=queryforNonDeterministicFeature(g,engine,(NonDeterministicFeature)f, currentInterpretation);
				tmp.put(f, tmpValue);
			}	
		}
		return tmp;
	}

	*//**
	 * Get Feature value for ground atom g in interpretation and for prolog engine
	 * @param g
	 * @param engine
	 * @param currentInterpretation TODO
	 * @param cpd
	 * @return
	 * @throws NoSolutionException 
	 *//*
	public FeatureValue getFeatureValue(GroundAtom g, Prolog engine,Feature f,Interpretation currentInterpretation) throws NoSolutionException {   
		FeatureValue tmpValue=null;
		//case 1: deterministic?
		if(f.isDeterministic()){
			tmpValue=queryforDeterministicFeature(g,engine,(DeterministicFeature)f, currentInterpretation);
		}
		//case 2: non-deterministic?
		else{
			tmpValue=queryforNonDeterministicFeature(g,engine,(NonDeterministicFeature)f, currentInterpretation);

		}	
		return tmpValue;
	}


	*//**
	 * Get value for deterministic query for current state of the engine
	 * @param g - ground atom in head
	 * @param engine - current prolog engine
	 * @param ft - query
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 *//*
	protected FeatureValue queryforDeterministicFeature(GroundAtom g, Prolog engine, DeterministicFeature ft, Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		//In case this deterministic feature doesn't have a selector: e.g., grade(S,C) | Value[intelligence(S)]
		Struct query=null;
		if(ft.getSelector().size()==0){
			query=new Struct("getDiscreteValueDeterm",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),new Var("Value"));
			long solve_engine=System.nanoTime();
			res= engine.solve(query);
		}
		//In case this deterministic feature has a selector: e.g., grade(S,C) | Value[takes(S,C),intelligence(S)]
		else{
			long solve_engine=System.nanoTime();
			Term selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
			query=new Struct("getDiscreteValueDeterm",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),new Var("Value"));
			res= engine.solve(query);
		}
		//The feature is processing the value
		((alice.tuprolog.lib.JavaLibrary)lib).dismissAll();
		try{
			if(res.getVarValue("Value") instanceof alice.tuprolog.Double || res.getVarValue("Value") instanceof alice.tuprolog.Int){
				if(ft.isDiscreteOutput()){
					return new FeatureStringValue(res.getVarValue("Value").toString());
				}
				return new DoubleFeatureValue(((alice.tuprolog.Number)res.getVarValue("Value")).doubleValue());
			}
			else{
				return new FeatureStringValue(res.getVarValue("Value").toString());
			}
		}

		catch(NoSolutionException e){
			return new UndefinedValueFeature();
		}
	}





	*//**
	 * Getting value for non-deterministic features such as Average, Min, Max and so on.
	 * @param g
	 * @param engine
	 * @param interpretation TODO
	 * @param f
	 * @return
	 * @throws NoSolutionException 
	 *//*
	protected FeatureValue queryforNonDeterministicFeature(GroundAtom g, Prolog engine,Feature ft, Interpretation interpretation) throws NoSolutionException {
		if(ft.isContinuousInput()){
			return getValueForQuery(g,engine,(NonDeterministicContinuous)ft,interpretation);
		}
		//Output to ft is 
		else if(ft instanceof NonDetermRangeBound){
			return getValueForQuery(g,engine,(NonDetermRangeBound)ft, interpretation);
		}
		//Proportion and Count
		else if(ft instanceof NonDetermInputBound){
			return getValueForQuery(g,engine,(NonDetermInputBound)ft, interpretation);
		}
		return null;
	}

	*//**
	 * Finding values for features with bounded input i.e., the domain of the feature is 
	 * finite, while the output is not necessary finite. For example features Proportion and Count belong to this category as
	 * the Proportion finds the number of TRUE groundings out of the number of possible groundings.
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation TODO
	 * @return
	 *//*
	protected NumberFeatureValue getValueForQuery(GroundAtom g, Prolog engine,NonDetermInputBound ft, Interpretation currentInterpretation) {
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
		query=new Struct("getDiscreteCountsOnlySelector",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,resultCount);
		engine.solve(query);
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		return new NumberFeatureValue(results.getCount(),currentInterpretation.getNrPossibleGroundings(ft.getUnboundArguments()));
	}

	*//**
	 * Get values for feature with bounded range: that is their output range is bounded and not continuous
	 * @param g
	 * @param engine
	 * @param ft
	 * @param currentInterpretation TODO
	 * @return
	 * @throws NoSolutionException
	 *//*
	protected HashMapFeatureValue getValueForQuery(GroundAtom g,Prolog engine, NonDetermRangeBound ft, Interpretation currentInterpretation) throws NoSolutionException {
		//Non deterministic discrete feature
		HashMap<String,Integer> results_count=new HashMap<String,Integer>();

		for(Value v:((RangeDiscrete)ft.getRange()).getValues()){
			results_count.put(((StringValue)v).getValue(), new Integer(0));
		}
		Term selector = null;
		Struct query=null;
		long time_begin=System.nanoTime();
		Struct resultMap=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results_count);

		if(ft.getSelector().size()!=0){
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
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		return new HashMapFeatureValue(results_count);
	}


	protected ArrayFeatureValues getValueForQuery(GroundAtom g,Prolog engine, NonDeterministicContinuous ft,Interpretation currentInterpretation) throws NoSolutionException {
		SolveInfo res=null; 
		List<Double> results=new ArrayList<Double>();
		Struct resStruct=((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).register(results);
		Term selector = null;
		Struct query=null;
		//Non deterministic numeric feature
		//no selector
		if(ft.getSelector().size()==0){
			long solve_engine=System.nanoTime();
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),new Struct(),Term.createTerm(ft.getAtom().toString()),new Var("Value"));
			res= engine.solve(query);
		}
		//with selector
		else{
			long solve_engine=System.nanoTime();
			selector=initializeSelectors(engine, ft.getSelector(),(AbstractConjunction)ft.getConjunction());
			query=new Struct("getContinuousValue",Term.createTerm(g.getAtom().toString()),Term.createTerm(g.createTermWithoutValue()),selector,Term.createTerm(ft.getAtom().toString()),resStruct);
			res= engine.solve(query);
		}
		((alice.tuprolog.lib.JavaLibrary)engine.getLibrary("alice.tuprolog.lib.JavaLibrary")).dismissAll();
		//System.out.println(g+" RESULTS: "+results);
		return new ArrayFeatureValues(results);
	}


	*//**
	 * Make a term representing the selectors
	 * @param engine
	 * @param booleanAtoms
	 * @param conjunction 
	 * @return
	 * @throws NoSolutionException
	 *//*
	private Term initializeSelectors(Prolog engine,List<Literal> booleanAtoms, AbstractConjunction conjunction) throws NoSolutionException {
		Term List=null;
		List = new Var("List");
		List<Literal> sorted=new ArrayList<Literal>();
		sorted.addAll(sortBooleans(booleanAtoms)); 

		if(conjunction.getLogvarRestrictions().size()!=0){
			sorted.addAll(conjunction.getLogvarRestrictions());
		}
		for (Literal lp : sorted) {
			String term=lp.createFOLTerm();
			List = engine.solve(new Struct("append", List, new Struct((alice.tuprolog.Term) Term.createTerm(term), new Struct()), new Var("List1"))).getVarValue("List1");
		}
		return List;

	}

	*//**
	 * Sort booleans according to internal predicates
	 * @param booleanPreds
	 * @return
	 *//*
	private List<Literal> sortBooleans(List<Literal> booleanPreds) {
		ArrayList<Literal> pr = new ArrayList<Literal>();
		Collections.sort(booleanPreds, new InternalComparator());
		return booleanPreds;
	}





}*/
