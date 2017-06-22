package hybrid.queryMachine;

import static org.junit.Assert.*;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Average;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.interpretations.Data;
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
import hybrid.network.Type;
import hybrid.network.UndefinedValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.Library;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

public class TestTuPrologQueries {

	 private static NetworkInfo ntw;
		private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
		private static Atom intelligence;
		private static Atom grade;
		private static Atom teaches;
		private static Atom ability;
		private static Atom takes;
		private static Logvar student;
		private static Logvar course;
		private static Logvar professor;
		private static Dependency dep1;
		private static Dependency dep2;
		
		/**
		 * Creating very simple network of only grade and intelligence predicate. 
		 * Testing on two dependencies:
		 * intelligence(S) | Mode{grade(S,c)}
		 * grade(S,C) | Value{intelligence(S)}
		 * @throws FeatureTypeException 
		 * @throws NotFullyConnectedLogvars 
		 */
		@BeforeClass
		public static void setUp() throws FeatureTypeException, ConjunctionConstructionProblem{
			System.out.println(pathToInterpretations);
			Type stud=new Type("student");
			Type c=new Type("course");
			Type p=new Type("prof");
			student=new Logvar("S",stud);
			course=new Logvar("C",c);
			professor=new Logvar("P",p);
			Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
			Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
			Predicate tk=new BooleanPred("takes",2);
			Predicate ab=new GaussianPred("ability",1,1.0,2.0);
			Predicate tch=new BooleanPred("teaches",2);
			intelligence=new Atom(intel, new Logvar[]{student});
			grade=new Atom(gr, new Logvar[]{student,course});
			takes=new Atom(tk, new Logvar[]{student,course});
			ability=new Atom(ab,new Logvar[]{professor});
			teaches=new Atom(tch,new Logvar[]{professor,course});
			ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability},new Type[]{stud,c,p});
			AbstractConjunction conj=new Standard_Conjunction(grade,Arrays.asList(new PosLiteral(teaches), new Literal(ability)));
			dep1=new Dependency(intelligence, new Feature[]{new Mode(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(grade))))});
		}
		
		@Test
		public void testModeDependency1Interpretation0() throws Exception{
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
			System.out.println(d.getInterpretations().get(0));
			Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			TuPrologQueries tQrs=new TuPrologQueries(lib);
			Subst sub=new Subst(new Logvar[]{student},new Constant[]{new Constant("s0")});
			Subst sub1=new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")});
			Subst sub2=new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")});
			Subst sub3=new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")});
			//interpretation 0
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub,new NumberValue(101.35405187696512)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(0)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub1,new NumberValue(109.18900881057698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(0)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub2,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(0)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub3,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(0)),new StringValue("mid"));
		
			engine.addTheory(new Theory(d.getInterpretations().get(0).getPrologFormat()));
		    //interpretation 1
			assertEquals(new StringValue("high"),tQrs.getFeatureValue(new GroundAtom(intelligence,sub,new NumberValue(101.35405187696512)), engine,dep1.getFeatures().get(0), d.getInterpretations().get(1)));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub1,new NumberValue(109.18900881057698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub2,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub3,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("mid"));
		
		}
		
		@Test
		public void testModeDependency1Interpretation1() throws Exception{
			TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
			Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
			Prolog engine=new Prolog();
			InputStream is = this.getClass().getResourceAsStream("Queries.pl");
			try {
				engine.addTheory(new Theory(is));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			engine.addTheory(new Theory(d.getInterpretations().get(1).getPrologFormat()));
			Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			TuPrologQueries tQrs=new TuPrologQueries(lib);
			Subst sub=new Subst(new Logvar[]{student},new Constant[]{new Constant("s0")});
			Subst sub1=new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")});
			Subst sub2=new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")});
			Subst sub3=new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")});
			
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub,new NumberValue(101.35405187696512)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("mid"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub1,new NumberValue(109.18900881057698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub2,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub3,new NumberValue(116.65135063380698)), engine,  dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
		
		}
		
		@Test
		public void testModeDependency1Interpretation3() throws Exception{
			TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
			Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
			Prolog engine=new Prolog();
			InputStream is = this.getClass().getResourceAsStream("Queries.pl");
			try {
				engine.addTheory(new Theory(is));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			engine.addTheory(new Theory(d.getInterpretations().get(2).getPrologFormat()));
			Library lib=engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			TuPrologQueries tQrs=new TuPrologQueries(lib);
			Subst sub=new Subst(new Logvar[]{student},new Constant[]{new Constant("s0")});
			Subst sub1=new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")});
			Subst sub2=new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")});
			Subst sub3=new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")});
			
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub,new NumberValue(101.35405187696512)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("high"));
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub1,new NumberValue(109.18900881057698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new UndefinedValue());
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub2,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new UndefinedValue());
			assertEquals(tQrs.getFeatureValue(new GroundAtom(intelligence,sub3,new NumberValue(116.65135063380698)), engine, dep1.getFeatures().get(0), d.getInterpretations().get(1)),new StringValue("low"));
		
		}
		
		
	
	
}
