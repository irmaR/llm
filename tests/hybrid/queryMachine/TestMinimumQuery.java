package hybrid.queryMachine;

import static org.junit.Assert.*;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Argument;
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
import hybrid.network.Subst;
import hybrid.network.Type;
import hybrid.network.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;


import alice.tuprolog.Library;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

public class TestMinimumQuery {
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
		Standard_Conjunction conj=new Standard_Conjunction(grade,Arrays.asList(new PosLiteral(teaches), new Literal(ability)));
		dep1=new Dependency(grade, new Feature[]{new Min(conj)});
	}
	
	@Test
	public void testDataLoaderNoSelector() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
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
		Value v=tQrs.getFeatureValue(new GroundAtom(grade,s,null), engine,dep1.getFeatures().get(0),d.getInterpretations().get(0));
		assertEquals(75.53,((NumberValue)v).getNumber(),0.2);
	}
}
