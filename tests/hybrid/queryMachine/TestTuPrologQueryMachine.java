package hybrid.queryMachine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hybrid.cpds.LogisticRegression;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.ValueFt;
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
import hybrid.querydata.QueryData;
import hybrid.features.*;
import hybrid.interpretations.Data;
import hybrid.interpretations.DataType;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestTuPrologQueryMachine {
    private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom intelligence1;
	private static Atom grade;
	private static Atom grade1;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Atom friend;
	private static Logvar student;
	private static Logvar course;
	private static Dependency dep1;
	private static Dependency dep2;
	private static Logvar professor;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws NotFullyConnectedLogvars 
	 */
	@BeforeClass
	public static void setUp() throws ConjunctionConstructionProblem{
		System.out.println(pathToInterpretations);
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
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
		intelligence1=new Atom(intel, new Logvar[]{student1});
		grade=new Atom(gr, new Logvar[]{student,course});
		grade1=new Atom(gr, new Logvar[]{student1,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,friend,teaches,ability,difficulty},new Type[]{stud,c,p});	
		Standard_Conjunction conj=new Standard_Conjunction(grade,Arrays.asList(new Literal(intelligence)));
		dep1=new Dependency(grade, new Feature[]{new ValueFt(conj)});
	}
	
	@Test
	public void testDataLoader() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tQ=new TuPrologQueryMachine(d, null);
		QueryData q=tQ.getQueryResults(dep1);
		assertEquals(3,q.getQuery_results().size());	
	}
	
	@Test
	public void testCacheCreator() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		TuPrologQueryMachine tQ=new TuPrologQueryMachine(d, null);
		List<Feature> featureSpace=new ArrayList<Feature>();
		featureSpace.add(new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(grade)))));
		featureSpace.add(new Mode(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(friend),new Literal(grade1)))));
		featureSpace.add(new Average(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(friend),new Literal(intelligence1)))));
		ComputedFeatureSpace c=tQ.calculateCache(intelligence, featureSpace,"training");
		System.out.println(c);
		//interpretation 1
		GroundAtom g0=new GroundAtom(intelligence,new Subst(student, new Constant("s0")),new NumberValue(101.354));
		GroundAtom g1=new GroundAtom(intelligence,new Subst(student, new Constant("s1")),new NumberValue(109.189));
		GroundAtom g2=new GroundAtom(intelligence,new Subst(student, new Constant("s2")),new NumberValue(116.651));
		GroundAtom g3=new GroundAtom(intelligence,new Subst(student, new Constant("s3")),new NumberValue(90.227));
   
        assertEquals(3,c.getFeatureValues().size());
        //interpretation 1:
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g0));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g1));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g2));
        assertEquals(new StringValue("mid"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g3));

        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g0));
        assertEquals(new StringValue("mid"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g1));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g2));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g3));
        
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g0));
        assertEquals(new NumberValue(95.790),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g1));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g2));
        assertEquals(new NumberValue(101.354),c.getFeatureValues().get(d.getInterpretations().get(0)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g3));

        //interpretation2:
        g0=new GroundAtom(intelligence,new Subst(student, new Constant("s0")),new NumberValue(90.7612));
        g1=new GroundAtom(intelligence,new Subst(student, new Constant("s1")),new NumberValue(100.903));
        g2=new GroundAtom(intelligence,new Subst(student, new Constant("s2")),new NumberValue(121.0178));
        g3=new GroundAtom(intelligence,new Subst(student, new Constant("s3")),new NumberValue(111.0831));
        assertEquals(new StringValue("mid"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g0));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g1));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g2));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g3));

        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g0));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g1));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g2));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g3));
        
        assertEquals(new NumberValue(111.001),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g0));
        assertEquals(new NumberValue(121.017),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g1));
        assertEquals(new NumberValue(95.832),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g2));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(1)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g3));
        
        //interpretation3:
        g0=new GroundAtom(intelligence,new Subst(student, new Constant("s0")),new NumberValue(122.449));
        g1=new GroundAtom(intelligence,new Subst(student, new Constant("s1")),new NumberValue(104.455));
        g2=new GroundAtom(intelligence,new Subst(student, new Constant("s2")),new NumberValue(103.846));
        g3=new GroundAtom(intelligence,new Subst(student, new Constant("s3")),new NumberValue(57.777));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g0));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g1));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g2));
        assertEquals(new StringValue("low"),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(0)).getCache().get(g3));

        assertEquals(new StringValue("low"),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g0));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g1));
        assertEquals(new StringValue("high"),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g2));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(1)).getCache().get(g3));
        
        assertEquals(new NumberValue(80.811),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g0));
        assertEquals(new UndefinedValue(),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g1));
        assertEquals(new NumberValue(122.449),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g2));
        assertEquals(new NumberValue(103.846),c.getFeatureValues().get(d.getInterpretations().get(2)).getFeatureCaches().get(featureSpace.get(2)).getCache().get(g3));
        
	}
	
	
	
}
