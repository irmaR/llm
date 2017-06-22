package hybrid.queryMachine;

import static org.junit.Assert.assertEquals;
import hybrid.comparators.Bigger;
import hybrid.comparators.Equals;
import hybrid.comparators.Smaller;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.ComplexConjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.features.Average;
import hybrid.features.Operator_Feature;
import hybrid.features.Feature;
import hybrid.interpretations.Data;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Argument;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.DiscreteValue;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.network.LogvarRestrictionLiteral;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.operators.Addition;
import hybrid.operators.Multiplication;
import hybrid.operators.Subtraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.Library;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

public class TestComplexQuery {

	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom difficulty;
	private static Atom intelligence1;
	private static Atom grade;
	private static TestRandvarValue difficulty_low;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom takes1;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static Dependency dep3;
	private static Dependency dep4;
	private static Dependency dep5;
	private static Dependency dep6;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws ConjunctionConstructionProblem 
	 */
	@BeforeClass
	public static void setUp() throws ConjunctionConstructionProblem{
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
		Predicate diff=new CategoricalPred("difficulty",1);
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		intelligence=new Atom(intel, new Logvar[]{student});
		intelligence1=new Atom(intel, new Logvar[]{student1});
		grade=new Atom(gr, new Logvar[]{student,course});
		difficulty=new Atom(diff, new Logvar[]{course});
		takes=new Atom(tk, new Logvar[]{student,course});
		takes1=new Atom(tk, new Logvar[]{student1,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty_low=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(),new StringValue("medium"));
		ntw=new NetworkInfo(new Atom[]{difficulty,intelligence,grade,takes,teaches,ability},new Type[]{stud,c,p});
		ComplexConjunction conj_context=new ComplexConjunction(grade,new PosLiteral(intelligence),new PosLiteral(intelligence1),new PosLiteral[]{new PosLiteral(takes),new PosLiteral(takes1)},new Addition());
		ComplexConjunction conj=new ComplexConjunction(grade,new PosLiteral(intelligence),new PosLiteral(intelligence1),new Addition());
		ComplexConjunction conj1=new ComplexConjunction(grade,new PosLiteral(intelligence),new PosLiteral(intelligence1),new Multiplication());
		ComplexConjunction conj2_context_grounded=new ComplexConjunction(grade,new PosLiteral(intelligence),new PosLiteral(intelligence1),new PosLiteral[]{new PosLiteral(difficulty_low)},new Addition());
		ComplexConjunction conj2_context_grounded_logvar_restriction=new ComplexConjunction(grade,new PosLiteral(intelligence),new PosLiteral(intelligence1),new PosLiteral[]{new PosLiteral(difficulty_low)},new Addition(),new LogvarRestrictionLiteral[]{new LogvarRestrictionLiteral("\\==", student1, student)});
		dep1=new Dependency(grade, new Feature[]{new Operator_Feature(conj,new Smaller(100.0),new Average())});
		dep2=new Dependency(grade, new Feature[]{new Operator_Feature(conj,new Equals(213.54453087831934),new Average())});
		dep3=new Dependency(grade, new Feature[]{new Operator_Feature(conj_context,new Equals(214.4605391543),new Average())});
		dep4=new Dependency(grade, new Feature[]{new Operator_Feature(conj1,new Bigger(100.0),new Average())});
		dep5=new Dependency(grade, new Feature[]{new Operator_Feature(conj2_context_grounded,new Bigger(100.0),new Average())});
		dep6=new Dependency(grade, new Feature[]{new Operator_Feature(conj2_context_grounded_logvar_restriction,new Bigger(100.0),new Average())});
	}
	
	@Test
	public void testComplexFeature() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c1"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep1.getFeatures().get(0),null);		
		System.out.println(v);
		assertEquals(new BoolValue(false),v);
	}
	
	@Test
	public void testComplexFeature_1() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c1"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep4.getFeatures().get(0),null);		
		assertEquals(new BoolValue(true),v);
	}
	
	@Test
	public void testComplexFeatureAggregation() throws Exception{	
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c1"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep2.getFeatures().get(0),null);		    
		assertEquals(new BoolValue(false),v);
	}
	
	@Test
	public void testComplexFeatureAggregationwithContext() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c2"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep3.getFeatures().get(0),null);		    
		assertEquals(new BoolValue(true),v);
	}
	
	@Test
	public void testComplexFeatureAggregationwithContext_randvarValue_test() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c2"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep5.getFeatures().get(0),null);		    
		assertEquals(new BoolValue(true),v);
	}
	
	@Test
	public void testComplexFeatureAggregationwithContext_and_logvar_restrictions_randvarValue_test() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		Prolog engine=new Prolog();
		InputStream is = this.getClass().getResourceAsStream("Queries.pl");
		try {
			engine.addTheory(new Theory(is));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		System.out.println(engine.getTheory());
		Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		TuPrologQueries tQrs=new TuPrologQueries(lib);
		HashMap<Logvar,Argument> subst=new HashMap<Logvar, Argument>();
		subst.put(student, new Constant("s1"));
		subst.put(course, new Constant("c2"));
		Subst s=new Subst(subst);
		GroundAtom grades1c1=new GroundAtom(grade,s,null);
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,(Operator_Feature)dep6.getFeatures().get(0),null);		    
		assertEquals(new BoolValue(true),v);
	}

}
