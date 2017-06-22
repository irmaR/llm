package hybrid.cpdEvaluation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import hybrid.core.Logarithm2;
import hybrid.cpds.LogisticRegression;
import hybrid.cpds.WrongParameterNumber;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.ValueFt;
import hybrid.interpretations.Assignment;
import hybrid.interpretations.Data;
import hybrid.interpretations.Domain;
import hybrid.interpretations.Interpretation;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.DiscretizedPredicate;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.MinMaxValue;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.network.WrongValueType;
import hybrid.parameters.BadProbabilityDistribution;
import hybrid.parameters.LogRegregressors;
import hybrid.parameters.MissingRegression;
import hybrid.parameters.PMF;
import hybrid.parameters.Parameters;
import hybrid.parameters.Regression;
import hybrid.parameters.WrongValueSpecification;
import hybrid.queryMachine.MDLPenalty;
import hybrid.queryMachine.NoPenalty;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.structure_learning.StructureLearner;

import org.junit.Before;
import org.junit.Test;

import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.NominalToBinary;

public class TestLogisticRegressionWeka {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom intelligence1;
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
	private static QueryData qD1;
	private static QueryData qDBoolean;

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
		System.out.println(pathToInterpretations);
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
		friend=new Atom(fr,new Logvar[]{student,student1});
		intelligence1=new Atom(intel,new Logvar[]{student1});

		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});   
		
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();

		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c3")}),new StringValue("mid")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s4"),new Constant("c4")}),new StringValue("mid")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s5"),new Constant("c5")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s6"),new Constant("c6")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s7"),new Constant("c7")}),new StringValue("high")));

		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("easy")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c2")}),new StringValue("medium")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c3")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c4")}),new StringValue("easy")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c5")}),new StringValue("easy")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c6")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c7")}),new StringValue("hard")));
		
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s1"),new Constant("s2")}),new BoolValue("true")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s1"),new Constant("s3")}),new BoolValue("false")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s2"),new Constant("s3")}),new BoolValue("false")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s2"),new Constant("s4")}),new BoolValue("true")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s1"),new Constant("s4")}),new BoolValue("true")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s1"),new Constant("s5")}),new BoolValue("false")));
		asig.addRandVar(friend, new GroundAtom(friend,new Subst(new Logvar[]{student,student1},new Constant[]{new Constant("s6"),new Constant("s7")}),new BoolValue("false")));

		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new NumberValue(4)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		ValueFt intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
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
		
		//second query data for: grade(S,C) | difficulty(C)
		
		ValueFt diffF=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(difficulty))));
		Dependency dep1=new Dependency(grade,new Feature[]{diffF});
		
		HashMap<Feature,Value> hM11=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM21=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM31=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM41=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM51=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM61=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM71=new HashMap<Feature,Value>();
		hM11.put(diffF, asig.getAssignmentFor(difficulty).get(0).getValue());
		hM21.put(diffF, asig.getAssignmentFor(difficulty).get(1).getValue());
		hM31.put(diffF, asig.getAssignmentFor(difficulty).get(2).getValue());
		hM41.put(diffF, asig.getAssignmentFor(difficulty).get(3).getValue());
		hM51.put(diffF, asig.getAssignmentFor(difficulty).get(4).getValue());
		hM61.put(diffF, asig.getAssignmentFor(difficulty).get(5).getValue());
		hM71.put(diffF, asig.getAssignmentFor(difficulty).get(6).getValue());

		
		
		MarkovBlanket mB11=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),dep1,hM11);
		MarkovBlanket mB21=new MarkovBlanket(asig.getAssignmentFor(grade).get(1),dep1,hM21);
		MarkovBlanket mB31=new MarkovBlanket(asig.getAssignmentFor(grade).get(2),dep1,hM31);
		MarkovBlanket mB41=new MarkovBlanket(asig.getAssignmentFor(grade).get(3),dep1,hM41);
		MarkovBlanket mB51=new MarkovBlanket(asig.getAssignmentFor(grade).get(4),dep1,hM51);
		MarkovBlanket mB61=new MarkovBlanket(asig.getAssignmentFor(grade).get(5),dep1,hM61);
		MarkovBlanket mB71=new MarkovBlanket(asig.getAssignmentFor(grade).get(6),dep1,hM71);
		
		qD1=new QueryData(dep1);
		qD1.addMarkovBlanket(i, mB11);
		qD1.addMarkovBlanket(i, mB21);
		qD1.addMarkovBlanket(i, mB31);
		qD1.addMarkovBlanket(i, mB41);
		qD1.addMarkovBlanket(i, mB51);
		qD1.addMarkovBlanket(i, mB61);
		qD1.addMarkovBlanket(i, mB71);

		//third query data for: friend(S,S1) | intelligence(S)
		
		ValueFt intellFriend=new ValueFt(new Standard_Conjunction(friend,Arrays.asList(new Literal(intelligence))));
		Dependency depFriendIntelligence=new Dependency(friend,new Feature[]{intellFriend});
		HashMap<Feature,Value> hM1F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM2F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM3F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM4F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM5F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM6F=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM7F=new HashMap<Feature,Value>();
		
		hM1F.put(intellFriend, new NumberValue(1));
		hM2F.put(intellFriend,new NumberValue(1));
		hM3F.put(intellFriend, new NumberValue(2));
		hM4F.put(intellFriend, new NumberValue(2));
		hM5F.put(intellFriend, new NumberValue(1));
		hM6F.put(intellFriend,new NumberValue(1));
		hM7F.put(intellFriend, new NumberValue(6));
		
		MarkovBlanket mB1F=new MarkovBlanket(asig.getAssignmentFor(friend).get(0),depFriendIntelligence,hM1F);
		MarkovBlanket mB2F=new MarkovBlanket(asig.getAssignmentFor(friend).get(1),depFriendIntelligence,hM2F);
		MarkovBlanket mB3F=new MarkovBlanket(asig.getAssignmentFor(friend).get(2),depFriendIntelligence,hM3F);
		MarkovBlanket mB4F=new MarkovBlanket(asig.getAssignmentFor(friend).get(3),depFriendIntelligence,hM4F);
		MarkovBlanket mB5F=new MarkovBlanket(asig.getAssignmentFor(friend).get(4),depFriendIntelligence,hM5F);
		MarkovBlanket mB6F=new MarkovBlanket(asig.getAssignmentFor(friend).get(5),depFriendIntelligence,hM6F);
		MarkovBlanket mB7F=new MarkovBlanket(asig.getAssignmentFor(friend).get(6),depFriendIntelligence,hM7F);
		qDBoolean=new QueryData(depFriendIntelligence);
		qDBoolean.addMarkovBlanket(i, mB1F);
		qDBoolean.addMarkovBlanket(i, mB2F);
		qDBoolean.addMarkovBlanket(i, mB3F);
		qDBoolean.addMarkovBlanket(i, mB4F);
		qDBoolean.addMarkovBlanket(i, mB5F);
		qDBoolean.addMarkovBlanket(i, mB6F);
		qDBoolean.addMarkovBlanket(i, mB7F);
		
		
		
	}

	//grade(S,C) | intelligence(S)
	@Test
	public void testInstanceSceleton() throws SubstitutionException, BadProbabilityDistribution{
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances inst=lW.initializeWekaInstanceSceleton(qD.getDep());
		assertEquals(1,inst.classIndex());
		assertEquals(2,inst.numAttributes());
		assertEquals(3,inst.numClasses());
	}

	@Test
	public void testData() throws SubstitutionException, BadProbabilityDistribution{
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances inst=lW.initializeWekaInstanceSceleton(qD.getDep());
		Instances data=lW.fillInTheValueS(qD, inst);
		System.out.println(data);

		assertEquals("low",data.instance(0).stringValue(1));
		assertEquals("low",data.instance(1).stringValue(1));
		assertEquals("mid",data.instance(2).stringValue(1));
		assertEquals("mid",data.instance(3).stringValue(1));
		assertEquals("high",data.instance(4).stringValue(1));
		assertEquals("high",data.instance(5).stringValue(1));
		assertEquals("high",data.instance(6).stringValue(1));

		assertEquals(1.0,data.instance(0).value(0),0.01);
		assertEquals(2.0,data.instance(1).value(0),0.01);
		assertEquals(3.0,data.instance(2).value(0),0.01);
		assertEquals(4.0,data.instance(3).value(0),0.01);
		assertEquals(5.0,data.instance(4).value(0),0.01);
		assertEquals(6.0,data.instance(5).value(0),0.01);
		assertEquals(7.0,data.instance(6).value(0),0.01);

	}

	@Test 
	public void estimateParameters(){
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(qD.getDep());
		Instances trainingInstances=lW.fillInTheValueS(qD,instancesSceleton);
		Logistic log=lW.trainClassifier(trainingInstances);
		double[][] wekaCoefficients= lW.transposeMatrix(log.coefficients());
		try {
			LogRegregressors par=(LogRegregressors) lW.extractParameters(wekaCoefficients, qD.getDep());
			assertEquals(10.139, par.getInterceptForValue(new StringValue("mid")),0.01);
			assertEquals(17.14, par.getInterceptForValue(new StringValue("low")),0.01);
			assertEquals(-2.25, par.getCoefficientForValueAndFeature(new StringValue("mid"),qD.getDep().getFeatures().get(0)),0.01);
			assertEquals(-5.05, par.getCoefficientForValueAndFeature(new StringValue("low"),qD.getDep().getFeatures().get(0)),0.01);				
		} catch (WrongParameterNumber e) {
			e.printStackTrace();
		} catch (WrongValueType e) {
			e.printStackTrace();
		} catch (WrongValueSpecification e) {
			e.printStackTrace();
		}
	}

	@Test 
	public void calculateNormalizingconstant() throws WrongParameterNumber, WrongValueType, WrongValueSpecification, SubstitutionException, ConjunctionConstructionProblem{
		LogRegregressors par=new LogRegregressors(qD.getDep());
		par.addConditionalParameter(new StringValue("low"), new Regression(qD.getDep(),178.613,new Double[]{-50.2254}));
		par.addConditionalParameter(new StringValue("mid"), new Regression(qD.getDep(),115.6215,new Double[]{-25.347}));
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		ValueFt intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
		Assignment asig=new Assignment();
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(4)));
		hM1.put(intell, asig.getAssignmentFor(intelligence).get(0).getValue());
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),qD.getDep(),hM1);
		try {
			assertEquals(1518906.63777,lW.getNormalizingConstant(mB1, par),0.001);
		} catch (LogisticRegressionParameterNotExisting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	@Test 
	public void getProbabilities() throws WrongParameterNumber, WrongValueType, WrongValueSpecification, SubstitutionException, ConjunctionConstructionProblem{
		LogRegregressors par=new LogRegregressors(qD.getDep());
		par.addConditionalParameter(new StringValue("low"), new Regression(qD.getDep(),178.613,new Double[]{-50.2254}));
		par.addConditionalParameter(new StringValue("mid"), new Regression(qD.getDep(),115.6215,new Double[]{-25.347}));
		par.addConditionalParameter(new StringValue("high"), new MissingRegression(qD.getDep().getFeatures()));
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		ValueFt intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
		Assignment asig=new Assignment();
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(4)));
		hM1.put(intell, asig.getAssignmentFor(intelligence).get(0).getValue());
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),qD.getDep(),hM1);
		//finding probability of: grade(s1,c1)=low | intelligence(s1)=1
		LogisticRegression log=new LogisticRegression(qD.getDep(), new LogisticRegressionWeka(),new LogRegregressors(qD.getDep()));
		log.setParameters(par);
		System.out.println(" Markov blanket: "+mB1+log.getParameters() );
		assertEquals(0.999,lW.getProbability(mB1, log.getParameters()),0.001);
	}
	
	@Test 
	public void getProbabilitiesDistribution() throws WrongParameterNumber, WrongValueType, WrongValueSpecification, SubstitutionException, ConjunctionConstructionProblem{
		LogRegregressors par=new LogRegregressors(qD.getDep());
		par.addConditionalParameter(new StringValue("low"), new Regression(qD.getDep(),178.613,new Double[]{-50.2254}));
		par.addConditionalParameter(new StringValue("mid"), new Regression(qD.getDep(),115.6215,new Double[]{-25.347}));
		par.addConditionalParameter(new StringValue("high"), new MissingRegression(qD.getDep().getFeatures()));
		
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		ValueFt intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
		Assignment asig=new Assignment();
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(4)));
		hM1.put(intell, asig.getAssignmentFor(intelligence).get(0).getValue());
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),qD.getDep(),hM1);
		//finding probability of: grade(s1,c1)=low | intelligence(s1)=1
		LogisticRegression log=new LogisticRegression(qD.getDep(), new LogisticRegressionWeka(),new LogRegregressors(qD.getDep()));
		log.setParameters(par);
		HashMap<Value,Double> probs=lW.getProbabilityDistributionAllValues(mB1, par);
		assertEquals(0.999,probs.get(new StringValue("mid")),0.001);
		assertEquals(1.3761102082871124E-16,probs.get(new StringValue("low")),0.001);
		assertEquals(6.583687459767224E-7,probs.get(new StringValue("low")),0.001);

	}


	@Test
	public void createInstance() throws SubstitutionException, ConjunctionConstructionProblem{
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		ValueFt intell=new ValueFt(new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence))));
		Assignment asig=new Assignment();
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(4)));
		hM1.put(intell, asig.getAssignmentFor(intelligence).get(0).getValue());
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0),qD.getDep(),hM1);
		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(qD.getDep());
		Instance instCreated=lW.createInstance(instancesSceleton, mB1);
		assertEquals("mid", instCreated.stringValue(instCreated.classIndex()));
		assertEquals(4, instCreated.value(0),0.1);
	}

	@Test
	public void getFilterTest(){
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(qD.getDep());
		Instances trainingInstances=lW.fillInTheValueS(qD,instancesSceleton);
		try {
			lW.getFilter(trainingInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void filterInstafilterInstancence() throws Exception{
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM2=new HashMap<Feature,Value>();
		HashMap<Feature,Value> hM3=new HashMap<Feature,Value>();

		ValueFt diff=new ValueFt(new Standard_Conjunction(grade,new Literal(difficulty)));
		Assignment asig1=new Assignment();
		asig1.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig1.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("easy")));

		Assignment asig2=new Assignment();
		asig2.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig2.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("medium")));

		Assignment asig3=new Assignment();
		asig3.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig3.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("hard")));

		hM1.put(diff, asig1.getAssignmentFor(difficulty).get(0).getValue());
		hM2.put(diff, asig2.getAssignmentFor(difficulty).get(0).getValue());
		hM3.put(diff, asig3.getAssignmentFor(difficulty).get(0).getValue());

		Dependency dep=new Dependency(grade,new Feature[]{diff});
		MarkovBlanket mB1=new MarkovBlanket(asig1.getAssignmentFor(grade).get(0),dep,hM1);	
		MarkovBlanket mB2=new MarkovBlanket(asig2.getAssignmentFor(grade).get(0),dep,hM2);		
		MarkovBlanket mB3=new MarkovBlanket(asig3.getAssignmentFor(grade).get(0),dep,hM3);		

		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(dep);

		Instance instCreated1=lW.createInstance(instancesSceleton, mB1);
		Instance instCreated2=lW.createInstance(instancesSceleton, mB2);
		Instance instCreated3=lW.createInstance(instancesSceleton, mB3);

	//	Instances trainingInstances=lW.fillInTheValueS(qD,instancesSceleton);
		NominalToBinary filter=null;
		try {
			filter=lW.getFilter(instancesSceleton);

		} catch (Exception e) {
			e.printStackTrace();
		}
		filter.input(instCreated1);
		Instance transformed1=filter.output();
		System.out.println(transformed1);
		assertEquals(1,transformed1.value(0),0.1);
		filter.batchFinished();

		filter.input(instCreated2);
		Instance transformed2=filter.output();
		System.out.println(transformed2);
		assertEquals(0,transformed2.value(0),0.1);
		filter.batchFinished();

		filter.input(instCreated3);
		Instance transformed3=filter.output();
		System.out.println(transformed3);
		assertEquals(0,transformed3.value(0),0.1);
		filter.batchFinished();
	}

	@Test 
	public void getProbabilitiesTest() throws Exception{
		ValueFt diff=new ValueFt(new Standard_Conjunction(grade,new Literal(difficulty)));
		Dependency dep=new Dependency(grade,new Feature[]{diff});
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(dep);

		Instances trainingInstances=lW.fillInTheValueS(qD1,instancesSceleton);
		Logistic log=lW.trainClassifier(trainingInstances);
		double[][] wekaCoefficients= lW.transposeMatrix(log.coefficients());
		LogRegregressors par=null;
		try {
			par=(LogRegregressors) lW.extractParameters(wekaCoefficients, dep);
		} catch (WrongParameterNumber e) {
			e.printStackTrace();
		} catch (WrongValueType e) {
			e.printStackTrace();
		} catch (WrongValueSpecification e) {
			e.printStackTrace();
		}

		Assignment asig1=new Assignment();
		asig1.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("mid")));
		asig1.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("easy")));
		HashMap<Feature,Value> hM1=new HashMap<Feature,Value>();
		hM1.put(diff,asig1.getAssignmentFor(difficulty).get(0).getValue());
		MarkovBlanket mB1=new MarkovBlanket(asig1.getAssignmentFor(grade).get(0),dep,hM1);
		System.out.println(mB1);
		Instance createdInstance=lW.createInstance(instancesSceleton,mB1);
		//finding probability of: grade(s1,c1)=low | intelligence(s1)=1
		List<Double> probs_no_log=lW.getProbabilityDistribution(mB1, par);
		
		double[] probs_log=log.distributionForInstance(createdInstance);
		int i=0;
		for(double d:probs_log){
			assertEquals(d,probs_no_log.get(i++),0.0001);
		}
	}
	
	@Test
	public void testBooleanBasedRegression(){
		LogisticRegressionWeka lW=new LogisticRegressionWeka();
		Instances instancesSceleton=lW.initializeWekaInstanceSceleton(qDBoolean.getDep());
		Instances trainingInstances=lW.fillInTheValueS(qDBoolean,instancesSceleton);
		Logistic log=lW.trainClassifier(trainingInstances);
		double[][] wekaCoefficients= lW.transposeMatrix(log.coefficients());
		try {
			LogRegregressors par=(LogRegregressors) lW.extractParameters(wekaCoefficients, qDBoolean.getDep());
			assertEquals(-0.628,par.getCoefficientForValueAndFeature(new BoolValue(true), qDBoolean.getDep().getFeatures().get(0)),0.01);	
			assertEquals(0.803,par.getInterceptForValue(new BoolValue(true)),0.01);	
			assertEquals(new MissingRegression(qDBoolean.getDep().getFeatures()),par.getRegressionCoefficients(new BoolValue(false)));	

		} catch (WrongParameterNumber e) {
			e.printStackTrace();
		} catch (WrongValueType e) {
			e.printStackTrace();
		} catch (WrongValueSpecification e) {
			e.printStackTrace();
		}
	}
	
	
}

