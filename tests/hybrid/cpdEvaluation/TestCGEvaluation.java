package hybrid.cpdEvaluation;

import static org.junit.Assert.assertEquals;
import hybrid.cpds.CG;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
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
import hybrid.parameters.AssignmentKey;
import hybrid.parameters.CGParameters;
import hybrid.parameters.FeatureValuePair;
import hybrid.parameters.Gaussian;
import hybrid.parameters.GaussianCoefficients;
import hybrid.parameters.LinearGParameters;
import hybrid.parameters.UndefinedAssignmentKey;
import hybrid.queryMachine.TuPrologQueryMachine;
import hybrid.querydata.QueryData;
import hybrid.penalties.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCGEvaluation {

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
	private static ValueFt diffFt;
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
		friend=new Atom(fr,new Logvar[]{student,student});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	

		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();	
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new UndefinedValue()));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c3")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s4"),new Constant("c4")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s5"),new Constant("c5")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s6"),new Constant("c6")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s7"),new Constant("c7")}),new StringValue("high")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c1")}),new StringValue("easy")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c2")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c3")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c4")}),new StringValue("easy")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c5")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c6")}),new StringValue("hard")));
		asig.addRandVar(difficulty, new GroundAtom(difficulty,new Subst(new Logvar[]{course},new Constant[]{new Constant("c7")}),new StringValue("easy")));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new NumberValue(4)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		gradeFt=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade))));
		diffFt=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(difficulty))));
        Dependency dep=new Dependency(grade,new Feature[]{gradeFt,diffFt});
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


		hM1.put(diffFt, asig.getAssignmentFor(difficulty).get(0).getValue());
		hM2.put(diffFt, asig.getAssignmentFor(difficulty).get(1).getValue());
		hM3.put(diffFt, asig.getAssignmentFor(difficulty).get(2).getValue());
		hM4.put(diffFt, asig.getAssignmentFor(difficulty).get(3).getValue());
		hM5.put(diffFt, asig.getAssignmentFor(difficulty).get(4).getValue());
		hM6.put(diffFt, asig.getAssignmentFor(difficulty).get(5).getValue());
		hM7.put(diffFt, asig.getAssignmentFor(difficulty).get(6).getValue());

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


	@Test
	public void testAssignmentKey() throws ConjunctionConstructionProblem{
		ValueFt ft=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade))));
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(difficulty))));
		CGParameters cGPar=new CGParameters(new Dependency(intelligence,new Feature[]{ft,ft1}));
		CGEvaluator cgEval=new CGEvaluator();
		FeatureValuePair fP11=new FeatureValuePair(ft, new StringValue("low"));
		FeatureValuePair fP12=new FeatureValuePair(ft, new StringValue("mid"));
		FeatureValuePair fP13=new FeatureValuePair(ft, new StringValue("high"));

		FeatureValuePair fP21=new FeatureValuePair(ft1, new StringValue("easy"));
		FeatureValuePair fP22=new FeatureValuePair(ft1, new StringValue("medium"));
		FeatureValuePair fP23=new FeatureValuePair(ft1, new StringValue("hard"));
		List<FeatureValuePair> ft1_list=new ArrayList<FeatureValuePair>();
		List<FeatureValuePair> ft2_list=new ArrayList<FeatureValuePair>();

		List<List<FeatureValuePair>> tmp=new ArrayList<List<FeatureValuePair>>();
		ft1_list.add(fP11);
		ft1_list.add(fP12);
		ft1_list.add(fP13);

		ft2_list.add(fP21);
		ft2_list.add(fP22);
		ft2_list.add(fP23);

	    tmp.add(ft1_list);
	    tmp.add(ft2_list);
	    assertEquals(tmp,cgEval.getAllFeatureValuePairs(new Dependency(intelligence,new Feature[]{ft,ft1})));
	}

	@Test
	public void testCartesianProduct() throws ConjunctionConstructionProblem{
		CGEvaluator cgEval=new CGEvaluator();
		ValueFt ft=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade))));
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(difficulty))));
		CGParameters cGPar=new CGParameters(new Dependency(intelligence,new Feature[]{ft,ft1}));
		List<List<FeatureValuePair>> tmp=cgEval.getAllFeatureValuePairs(new Dependency(intelligence,new Feature[]{ft,ft1}));
		List<List<FeatureValuePair>> cartProd=cgEval.getCartesianProductsOfFeatureValues(tmp);
		assertEquals(9,cartProd.size());

	}

	@Test
	public void estimateParameters() throws ConjunctionConstructionProblem{
		CGEvaluator cgEval=new CGEvaluator();
		ValueFt ft=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade))));
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(difficulty))));
		CGParameters par=(CGParameters) cgEval.estimateParameters(qD);
		//Checking pars for: grade=low, difficulty=easy
		HashMap<Feature,Value> parentValuesLow_Easy=new HashMap<Feature, Value>();
		parentValuesLow_Easy.put(ft, new StringValue("low"));
		parentValuesLow_Easy.put(ft1, new StringValue("easy"));
		GaussianCoefficients par1=par.getParameters(qD.getDep(),parentValuesLow_Easy,par,cgEval);	
		assertEquals(1.0,par1.getMean(),0.01);
		assertEquals(Double.NaN,par1.getSigma(),0.01);

		//Checking pars for: grade=mid, difficulty=easy
		HashMap<Feature,Value> parentValuesMid_Easy=new HashMap<Feature, Value>();
		parentValuesMid_Easy.put(ft, new StringValue("mid"));
		parentValuesMid_Easy.put(ft1, new StringValue("easy"));
		GaussianCoefficients par3=par.getParameters(qD.getDep(),parentValuesMid_Easy,par,cgEval);	
		assertEquals(Double.NaN,par3.getMean(),0.01);
		assertEquals(Double.NaN,par3.getSigma(),0.01);


		//Checking pars for: grade=high, difficulty=easy
		HashMap<Feature,Value> parentValuesHIGHEasY=new HashMap<Feature, Value>();
		parentValuesHIGHEasY.put(ft, new StringValue("high"));
		parentValuesHIGHEasY.put(ft1, new StringValue("easy"));
		GaussianCoefficients par2=par.getParameters(qD.getDep(),parentValuesHIGHEasY,par,cgEval);	
		assertEquals(5.5,par2.getMean(),0.01);
		assertEquals(2.123,par2.getSigma(),0.01);

		//Checking pars for: grade=low, difficulty=medium
		HashMap<Feature,Value> parentValuesLow_Medium=new HashMap<Feature, Value>();
		parentValuesLow_Medium.put(ft, new StringValue("low"));
		parentValuesLow_Medium.put(ft1, new StringValue("medium"));
		GaussianCoefficients par4=par.getParameters(qD.getDep(),parentValuesLow_Medium,par,cgEval);	
		assertEquals(Double.NaN,par4.getMean(),0.01);
		assertEquals(Double.NaN,par4.getSigma(),0.01);
		assertEquals(4.0,par.getMarginalProb().getMean(),0.1);
		assertEquals(2.16,par.getMarginalProb().getStd(),0.1);

	}
	
	@Test
	public void testWithAllinterpretationsDependency1() throws Exception{
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		Logvar student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
		Logvar course=new Logvar("C",c);
		Logvar professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr1=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		Predicate fr=new BooleanPred("friend",2);
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		Atom intelligence=new Atom(intel, new Logvar[]{student});
		Atom grade1=new Atom(gr1, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student});
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		NetworkInfo ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});
		
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		//System.out.println("Data "+d.toDatabaseFormat());
		TuPrologQueryMachine tuPrologQueryMachine=new TuPrologQueryMachine(d, new MDLPenalty());
		Standard_Conjunction con=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(difficulty)));
		Dependency dep=new Dependency(intelligence,new Feature[]{new Mode(con)});
		dep.getCpd().setParameters(dep.getCpd().getCpdEvaluator().estimateParameters(tuPrologQueryMachine.getQueryResults(dep)));
     
		System.out.println(dep.getCpd().getParameters());
		
		AssignmentKey key1=new AssignmentKey(new Feature[]{dep.getFeatures().get(0)}, new Value[]{new StringValue("easy")});
        AssignmentKey key2=new AssignmentKey(new Feature[]{dep.getFeatures().get(0)}, new Value[]{new StringValue("hard")});
        AssignmentKey key3=new AssignmentKey(new Feature[]{dep.getFeatures().get(0)}, new Value[]{new StringValue("medium")});
        UndefinedAssignmentKey key4=new UndefinedAssignmentKey();
        
        assertEquals(105.941,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key1).getMean(),0.01);
        assertEquals(13.032,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key1).getSigma(),0.01);
        
        assertEquals(99.608,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key2).getMean(),0.01);
        assertEquals(23.43,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key2).getSigma(),0.01);
        
        assertEquals(Double.NaN,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key3).getMean(),0.01);
        assertEquals(Double.NaN,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key3).getSigma(),0.01);
        
        assertEquals(104.151,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key4).getMean(),0.01);
        assertEquals(0.430,((CGParameters)((CG)dep.getCpd()).getParameters()).getParameters(key4).getSigma(),0.01);
	}
	
	
	@Test
	public void estimateParametersDependency3() throws Exception{
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
		CGEvaluator cG=new CGEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		Dependency dep=new Dependency(intelligence,new Feature[]{new Mode(new Standard_Conjunction(intelligence,new PosLiteral(grade)))});
	
		cG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
		System.out.println(dep+ " "+dep.getCpd().getParameters());
		System.out.println(cG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CGParameters) dep.getCpd().getParameters(),new NoPenalty()));
	}
	
	
	@Test
	public void getProbabilityMarkovBlankets() throws Exception{
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
		CGEvaluator cG=new CGEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		Dependency dep=new Dependency(intelligence,new Feature[]{new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(difficulty))),new Exist(new Standard_Conjunction(intelligence,new PosLiteral(takes)))});
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
	    cG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
	    
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
		Atom grade=new Atom(gr, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		Atom difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,friend,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	
		CGEvaluator cG=new CGEvaluator();
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tuPrologQueryMachine_training=new TuPrologQueryMachine(d, new MDLPenalty());
		Dependency dep=new Dependency(intelligence,new Feature[]{new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(difficulty))),new Exist(new Standard_Conjunction(intelligence,new PosLiteral(takes)))});
		TuPrologQueryMachine tuPrologQueryMachine_validation=new TuPrologQueryMachine(d, new MDLPenalty());
	    cG.estimateParameters(tuPrologQueryMachine_training.getQueryResults(dep));
	    System.out.println(tuPrologQueryMachine_training.getQueryResults(dep));
		System.out.println(dep.getCpd().getParameters());
	    assertEquals(-62.088,cG.calculatePLL(tuPrologQueryMachine_validation.getQueryResults(dep), (CGParameters) dep.getCpd().getParameters(),new NoPenalty()).getScore(),0.01);
	
	}
	
	


}
