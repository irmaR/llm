package hybrid.cpdEvaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import hybrid.cpds.LinearGaussian;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.interpretations.Assignment;
import hybrid.interpretations.Data;
import hybrid.interpretations.Domain;
import hybrid.interpretations.Interpretation;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.parameters.Gaussian;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.LinearGaussianCoeff;
import hybrid.parameters.Regression;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;

public class TestLinearGaussianEvaluator {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
    private static Atom takes;
	private static Atom difficulty;
	private static Atom friend;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static AbstractConjunction testClass;
	private static QueryData qD;
	private static ValueFt intell;
	
	@Before
	public void setUp() throws FeatureTypeException, SubstitutionException, ConjunctionConstructionProblem{
		System.out.println(pathToInterpretations);
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		student=new Logvar("S",stud);
		student1=new Logvar("S1",stud);
		course=new Logvar("C",c);
		professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr=new GaussianPred("grade",2,5,10);

		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		intelligence=new Atom(intel, new Logvar[]{student});
		grade=new Atom(gr, new Logvar[]{student,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,difficulty},new Type[]{stud,c,p});   
		
		
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();	
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new NumberValue(2)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new NumberValue(4)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c3")}),new NumberValue(6)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s4"),new Constant("c4")}),new NumberValue(8)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s5"),new Constant("c5")}),new NumberValue(10)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s6"),new Constant("c6")}),new NumberValue(5)));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s7"),new Constant("c7")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new UndefinedValue()));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
		Dependency dep=new Dependency(grade,new Feature[]{intell});
		
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM2=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM3=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM4=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM5=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM6=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM7=new HashMap<Feature,Value>();
		hM1.put(intell, asig.getAssignmentFor(intelligence).get(0).getValue());
		hM2.put(intell, asig.getAssignmentFor(intelligence).get(1).getValue());
		hM3.put(intell, asig.getAssignmentFor(intelligence).get(2).getValue());
		hM4.put(intell, asig.getAssignmentFor(intelligence).get(3).getValue());
		hM5.put(intell, asig.getAssignmentFor(intelligence).get(4).getValue());
		hM6.put(intell, asig.getAssignmentFor(intelligence).get(5).getValue());
		hM7.put(intell, asig.getAssignmentFor(intelligence).get(6).getValue());
		
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),dep,hM1);
		MarkovBlanket mB2=new MarkovBlanket(asig.getAssignmentFor(grade).get(1),dep,hM2);
		MarkovBlanket mB3=new MarkovBlanket(asig.getAssignmentFor(grade).get(2),dep,hM3);
		MarkovBlanket mB4=new MarkovBlanket(asig.getAssignmentFor(grade).get(3),dep,hM4);
		MarkovBlanket mB5=new MarkovBlanket(asig.getAssignmentFor(grade).get(4),dep,hM5);
		MarkovBlanket mB6=new MarkovBlanket(asig.getAssignmentFor(grade).get(5),dep,hM6);
		MarkovBlanket mB7=new MarkovBlanket(asig.getAssignmentFor(grade).get(6),dep,hM7);
		qD=new QueryData(dep);
		qD.addMarkovBlanket(i, mB1);
		qD.addMarkovBlanket(i, mB2);
		qD.addMarkovBlanket(i, mB3);
		qD.addMarkovBlanket(i, mB4);
		qD.addMarkovBlanket(i, mB5);
		qD.addMarkovBlanket(i, mB6);
		qD.addMarkovBlanket(i, mB7);
	}

	@Test
	public void estimateParameters() throws SubstitutionException{
		LinearGaussianEvaluator lG=new LinearGaussianEvaluator();
		double intercept=3.285;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
        coeffs.put(intell,0.6428);
		Regression reg=new Regression(intercept, coeffs);	
		LinearGParameters par=(LinearGParameters) lG.estimateParameters(qD);
		System.out.println("Parameters; "+par);
		assertEquals(reg.getIntercept(),par.getPars().getReg_coeff().getIntercept(),0.01);
		assertEquals(reg.getWeights().get(intell),par.getPars().getReg_coeff().getWeights().get(intell),0.01);
		assertEquals(2.796,par.getPars().getStd(),0.01);
	}
	
/*	@Test 
	public void getAverageValuesForAttributes() throws BadParentSpecification{
		LinearGaussianEvaluator lG=new LinearGaussianEvaluator();
		Instances sceleton=lG.initializeWekaInstanceSceleton(qD.getDep());
		lG.fillInTheValueS(qD, sceleton);
		HashMap<Feature,Double> averageValues=lG.getAverageValueForAttribute();
		assertEquals(4.0,averageValues.get(intell),0.01);
	}
	
	@Test
	public void getStandardDeviation() throws SubstitutionException{
		double intercept=3;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
        coeffs.put(intell,0.5);
		Regression reg=new Regression(intercept, coeffs);	
		LinearGaussianEvaluator lG=new LinearGaussianEvaluator();
		HashMap<Feature,Double> averageValues=new HashMap<Feature, Double>();
		averageValues.put(intell, new Double(4.0));
		lG.setAverageValueForAttribute(averageValues);
		List<MarkovBlanket> data=new ArrayList<MarkovBlanket>();
		for(Interpretation i:qD.getQuery_results().keySet()){
			for(MarkovBlanket m:qD.getQuery_results().get(i)){
				data.add(m);
			}
		}		
		double std=lG.estimateStandardDeviation(data,reg);
		assertEquals(2.91,std,0.01);
	}
	
	@Test
	public void estimateParametersDependency1() throws SubstitutionException{
		LinearGaussianEvaluator lG=new LinearGaussianEvaluator();
		double intercept=3.285;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
        coeffs.put(intell,0.6428);
		Regression reg=new Regression(intercept, coeffs);	
		LinearGParameters par=(LinearGParameters) lG.estimateParameters(qD);
		System.out.println("Parameters; "+par);
		assertEquals(reg.getIntercept(),par.getPars().getReg_coeff().getIntercept(),0.01);
		assertEquals(reg.getWeights().get(intell),par.getPars().getReg_coeff().getWeights().get(intell),0.01);
		assertEquals(2.79,par.getPars().getStd(),0.01);
	}
	
	@Test
	public void getProbabilities() throws SubstitutionException{
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		hM1.put(intell, new NumberValue(3));
		MarkovBlanket mB1=new MarkovBlanket(new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new NumberValue(4)),qD.getDep(),hM1);
		double intercept=3.285;
		HashMap<Feature,Double> coeffs=new HashMap<Feature,Double>();
        coeffs.put(intell,0.6428);
		Regression reg=new Regression(intercept, coeffs);	
		LinearGParameters par=new LinearGParameters(qD.getDep());
		par.setPars(new LinearGaussianCoeff(reg,2.209));
		LinearGaussianEvaluator lgEval=new LinearGaussianEvaluator();
		assertEquals(0.1553,lgEval.getProbability(mB1, par),0.01);
	}
	
	/*@Test
	public void estimateParametersDependency2() throws Exception{
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		Logvar student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
		Logvar course=new Logvar("C",c);
		Logvar professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		Predicate fr=new BooleanPred("friend",2);
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		Atom intelligence=new Atom(intel, new Logvar[]{student});
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	
		LinearGaussianEvaluator lG=new LinearGaussianEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader();
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		Dependency dep=new Dependency(intelligence,new Feature[]{new Proportion(new Conjunction(intelligence,friend))});
		lG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
		assertEquals(-73.36,lG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (LinearGParameters) dep.getCpd().getParameters()),0.01);
	}
	*/
	
	
	
	
	
}
