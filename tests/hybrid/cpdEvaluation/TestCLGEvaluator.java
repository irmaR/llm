package hybrid.cpdEvaluation;

import static org.junit.Assert.assertEquals;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.experimenter.CVIteration;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.interpretations.Assignment;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.Domain;
import hybrid.interpretations.Interpretation;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
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
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;
import hybrid.network.UndefinedValue;
import hybrid.network.Value;
import hybrid.parameters.CGParameters;
import hybrid.parameters.CLGParameters;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.Gaussian;
import hybrid.parameters.LinearGParameters;
import hybrid.penalties.*;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCLGEvaluator {
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
	private static ValueFt gradeFt;
	private static ValueFt abilityFT;
	private static QueryData qD;

	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 * @throws SubstitutionException 
	 * @throws ConjunctionConstructionProblem 
	 */
	@Before
	public void setUp() throws FeatureTypeException, SubstitutionException, ConjunctionConstructionProblem{
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		student=new Logvar("S",stud);
		student1=new Logvar("S1",stud);
		course=new Logvar("C",c);
		professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		Predicate fr=new BooleanPred("friend",2);
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		intelligence=new Atom(intel, new Logvar[]{student});
		grade=new Atom(gr, new Logvar[]{student,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	

		//Example for CLG:
		//intelligence(S) | value(grade(S,C)), value(ability(S)
		
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();	
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new UndefinedValue()));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c3")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s4"),new Constant("c4")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s5"),new Constant("c5")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s6"),new Constant("c6")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s7"),new Constant("c7")}),new StringValue("high")));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s1")}),new NumberValue(3.0)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s2")}),new NumberValue(3.5)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s3")}),new NumberValue(4.0)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s4")}),new NumberValue(4.5)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s5")}),new NumberValue(5.0)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s6")}),new NumberValue(5.5)));
		asig.addRandVar(ability, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("s7")}),new NumberValue(6.0)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new NumberValue(4)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		gradeFt=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade),new PosLiteral(takes))));
		abilityFT=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(teaches),new PosLiteral(ability))));
        Dependency dep=new Dependency(intelligence,new Feature[]{gradeFt,abilityFT});
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM2=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM3=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM4=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM5=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM6=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM7=new HashMap<Feature,Value>();
		hM1.put(gradeFt, asig.getAssignmentFor(grade).get(0).getValue());
		hM2.put(gradeFt, asig.getAssignmentFor(grade).get(1).getValue());
		hM3.put(gradeFt, asig.getAssignmentFor(grade).get(2).getValue());
		hM4.put(gradeFt, asig.getAssignmentFor(grade).get(3).getValue());
		hM5.put(gradeFt, asig.getAssignmentFor(grade).get(4).getValue());
		hM6.put(gradeFt, asig.getAssignmentFor(grade).get(5).getValue());
		hM7.put(gradeFt, asig.getAssignmentFor(grade).get(6).getValue());


		hM1.put(abilityFT, asig.getAssignmentFor(ability).get(0).getValue());
		hM2.put(abilityFT, asig.getAssignmentFor(ability).get(1).getValue());
		hM3.put(abilityFT, asig.getAssignmentFor(ability).get(2).getValue());
		hM4.put(abilityFT, asig.getAssignmentFor(ability).get(3).getValue());
		hM5.put(abilityFT, asig.getAssignmentFor(ability).get(4).getValue());
		hM6.put(abilityFT, asig.getAssignmentFor(ability).get(5).getValue());
		hM7.put(abilityFT, asig.getAssignmentFor(ability).get(6).getValue());

		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(0),dep,hM1);
		MarkovBlanket mB2=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(1),dep,hM2);
		MarkovBlanket mB3=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(2),dep,hM3);
		MarkovBlanket mB4=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(3),dep,hM4);
		MarkovBlanket mB5=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(4),dep,hM5);
		MarkovBlanket mB6=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(5),dep,hM6);
		MarkovBlanket mB7=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(6),dep,hM7);
		qD=new QueryData(dep);
		qD.addMarkovBlanket(i, mB1);
		qD.addMarkovBlanket(i, mB2);
		qD.addMarkovBlanket(i, mB3);
		qD.addMarkovBlanket(i, mB4);
		qD.addMarkovBlanket(i, mB5);
		qD.addMarkovBlanket(i, mB6);
		qD.addMarkovBlanket(i, mB7);
		System.out.println(qD);
	}
	
	/*@Test
	public void testAssignmentKey(){
		CLGParameters cGPar=new CLGParameters(new Dependency(intelligence,new Feature[]{gradeFt,abilityFT}));
		CLGEvaluator cgEval=new CLGEvaluator();
		FeatureValuePair fP11=new FeatureValuePair(gradeFt, new StringValue("low"));
		FeatureValuePair fP12=new FeatureValuePair(gradeFt, new StringValue("mid"));
		FeatureValuePair fP13=new FeatureValuePair(gradeFt, new StringValue("high"));

		FeatureValuePair fP21=new FeatureValuePair(abilityFT, new NumberValue(3.0));
		FeatureValuePair fP22=new FeatureValuePair(abilityFT, new NumberValue(3.5));
		FeatureValuePair fP23=new FeatureValuePair(abilityFT, new NumberValue(4.0));
		List<FeatureValuePair> ft1_list=new ArrayList<FeatureValuePair>();
		List<FeatureValuePair> ft2_list=new ArrayList<FeatureValuePair>();

		List<List<FeatureValuePair>> tmp=new ArrayList<List<FeatureValuePair>>();
		ft1_list.add(fP11);
		ft1_list.add(fP12);
		ft1_list.add(fP13);
	    tmp.add(ft1_list);
	    assertEquals(tmp,cgEval.getAllFeatureValuePairs(new Dependency(intelligence,new Feature[]{gradeFt,abilityFT})));
	}
	
	@Test
	public void estimateParameters(){
		CLGEvaluator cgEval=new CLGEvaluator();

		CLGParameters par=(CLGParameters) cgEval.estimateParameters(qD);

		//Checking pars for: grade=low// TODO Auto-generated catch block
		HashMap<Feature,Value> parentValuesLow=new HashMap<Feature, Value>();
		parentValuesLow.put(gradeFt, new StringValue("low"));
		parentValuesLow.put(abilityFT, new NumberValue(3));
		LinearGParameters par1=par.getParameters(qD.getDep(), parentValuesLow,cgEval);
		assertEquals(1.999,par1.getPars().getReg_coeff().getWeights().get(abilityFT),0.01);
		assertEquals(-4.999,par1.getPars().getReg_coeff().getIntercept(),0.01);
		assertEquals(1.4142135537782532E-8,par1.getPars().getStd(),0.01);

		//Checking pars for: grade=mid
		HashMap<Feature,Value> parentValuesMid=new HashMap<Feature, Value>();
		parentValuesMid.put(gradeFt, new StringValue("mid"));
		parentValuesMid.put(abilityFT, new NumberValue(3));
		LinearGParameters par2=par.getParameters(qD.getDep(), parentValuesMid,cgEval);
		assertEquals(null,par2);
		
		//Checking pars for: grade=high
		HashMap<Feature,Value> parentValuesHigh=new HashMap<Feature, Value>();
		parentValuesHigh.put(gradeFt, new StringValue("high"));
		parentValuesHigh.put(abilityFT, new NumberValue(3));
		LinearGParameters par3=par.getParameters(qD.getDep(), parentValuesHigh,cgEval);
		assertEquals(1.999,par3.getPars().getReg_coeff().getWeights().get(abilityFT),0.01);
		assertEquals(-4.999,par3.getPars().getReg_coeff().getIntercept(),0.01);
		assertEquals(4.303315185177217E-9,par3.getPars().getStd(),0.01);
		
		//Checking pars for: grade=undefinedAssignmentKey
		HashMap<Feature,Value> parentValuesUndefined=new HashMap<Feature, Value>();
		parentValuesUndefined.put(gradeFt, new UndefinedValue());
		parentValuesUndefined.put(abilityFT, new NumberValue(3));
		LinearGParameters par4=par.getParameters(qD.getDep(), parentValuesUndefined,cgEval);
		assertEquals(0.0,par4.getPars().getReg_coeff().getWeights().get(abilityFT),0.01);
		assertEquals(2.0,par4.getPars().getReg_coeff().getIntercept(),0.01);
		assertEquals(Double.NaN,par4.getPars().getStd(),0.01);

	}
	
	@Test
	public void estimateParametersDependency4() throws Exception{
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
		Atom intelligence1=new Atom(intel, new Logvar[]{student1});
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	
		CLGEvaluator clG=new CLGEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader();
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		Dependency dep=new Dependency(intelligence,new Feature[]{new Mode(new Conjunction(intelligence,takes,difficulty)),new Max(new Conjunction(intelligence,friend,intelligence1))});
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
		System.out.println(" ******************** Estimate parameters ***************************");
		clG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		System.out.println(" ******************** Calculating score ******************************");
		System.out.println(" PARAMETERS : "+dep.getCpd().getParameters());
		assertEquals(-92.85,clG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CLGParameters) dep.getCpd().getParameters()),0.01);
	
	}*/
	
	@Test
	public void estimateParametersDependency4() throws Exception{
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
		Atom intelligence1=new Atom(intel, new Logvar[]{student1});
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom takes1=new Atom(tk, new Logvar[]{student1,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	
		CLGEvaluator clG=new CLGEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		//features
		Mode ft1=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(difficulty)));
		Proportion ft2=new Proportion(new Standard_Conjunction(intelligence,new PosLiteral(friend)));
		Proportion ft3=new Proportion(new Standard_Conjunction(intelligence,new PosLiteral(takes)));
		Mode ft4=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(grade)));
		Dependency dep=new Dependency(intelligence,new Feature[]{ft1,ft2,ft3,ft4});
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
		System.out.println(" ******************** Estimate parameters ***************************");
		clG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		System.out.println(" ******************** Calculating score ******************************");
		System.out.println(" PARAMETERS : "+dep.getCpd().getParameters());
		//assertEquals(10.78,clG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CLGParameters) dep.getCpd().getParameters(),new NoPenalty()),0.01);
	
	}
	
	/*@Test
	public void debug() throws Exception{
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
		Atom intelligence1=new Atom(intel, new Logvar[]{student1});
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom grade1=new Atom(gr, new Logvar[]{student1,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom takes1=new Atom(tk, new Logvar[]{student1,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	
		
		TuPrologInterpretationCreator_Subsampling dataLoader= new TuPrologInterpretationCreator_Subsampling(1);
		
		Interpretation interTest=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/medium_university/interp1.pl","test");
		Interpretation interTraining1=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/medium_university/interp4.pl","test");
		Interpretation interTraining2=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/medium_university/interp3.pl","test");
		Interpretation interValidation1=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/medium_university/interp5.pl","test");
		Interpretation interValidation2=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/medium_university/interp2.pl","test");

		List<Interpretation> training=new ArrayList<Interpretation>();
		List<Interpretation> validation=new ArrayList<Interpretation>();
		training.add(interTraining2);
		training.add(interTraining1);
		validation.add(interValidation1);
		validation.add(interValidation2);
		
		CVIteration cvI=new CVIteration(interTest, training, validation);
		
		Min ft1=new Min(new Standard_Conjunction(intelligence,new PosLiteral(friend),new Literal(intelligence1)));
		Mode ft2=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(friend),new Literal(grade1)));
		
		Dependency dep=new Dependency(intelligence,new Feature[]{ft1,ft2});
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(new Data(training), new MDLPenalty());
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(new Data(validation), new MDLPenalty());

		((CLGEvaluator)dep.getCpd().getCpdEvaluator()).estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		System.out.println(" PARAMETERS : "+dep.getCpd().getParameters());
		System.out.println(((CLGEvaluator)dep.getCpd().getCpdEvaluator()).calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CLGParameters) dep.getCpd().getParameters(),new NoPenalty()));
		/*
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		//features
		Mode ft1=new Mode(new Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(difficulty)));
		Proportion ft2=new Proportion(new Conjunction(intelligence,new PosLiteral(friend),new PosLiteral(takes1)));
		Proportion ft3=new Proportion(new Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(teaches)));
		Mode ft4=new Mode(new Conjunction(intelligence,new PosLiteral(grade)));
		Dependency dep=new Dependency(intelligence,new Feature[]{ft1,ft2,ft3,ft4});
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
		System.out.println(" ******************** Estimate parameters ***************************");
		clG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		System.out.println(" ******************** Calculating score ******************************");
		System.out.println(" PARAMETERS : "+dep.getCpd().getParameters());
		assertEquals(147.81,clG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CLGParameters) dep.getCpd().getParameters()),0.01);
	
	}*/
	
	

}
