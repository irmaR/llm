package hybrid.queryMachine;

import static org.junit.Assert.*;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Average;
import hybrid.features.Feature;
import hybrid.features.Mode;
import hybrid.features.ValueFt;
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
import hybrid.network.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestComputedFeatureSpace {

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
	public void testMarkovBlanketCreation() throws Exception{
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		List<Feature> featureSpace=new ArrayList<Feature>();
		Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
		featureSpace.add(new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(grade)))));
		featureSpace.add(new Mode(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(friend),new Literal(grade1)))));
		featureSpace.add(new Average(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(friend),new Literal(intelligence1)))));
		ComputedFeatureSpace ftSpace=new ComputedFeatureSpace(intelligence, featureSpace,d);
		GroundAtom g0=new GroundAtom(intelligence,new Subst(student, new Constant("s0")),new NumberValue(101.354));
		GroundAtom g1=new GroundAtom(intelligence,new Subst(student, new Constant("s1")),new NumberValue(109.189));
		GroundAtom g2=new GroundAtom(intelligence,new Subst(student, new Constant("s2")),new NumberValue(116.651));
		
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g0, featureSpace.get(0),new StringValue("low"));
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g0, featureSpace.get(1),new UndefinedValue());
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g0, featureSpace.get(2),new NumberValue(150.00));
	    
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g1, featureSpace.get(0),new StringValue("mid"));
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g1, featureSpace.get(1),new NumberValue(70.00));
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g1, featureSpace.get(2),new NumberValue(90.00));
	    
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g2, featureSpace.get(0),new StringValue("high"));
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g2, featureSpace.get(1),new NumberValue(80.00));
	    ftSpace.addValueForFeatureAndInterpretation(d.getInterpretations().get(0), g2, featureSpace.get(2),new UndefinedValue());
	    
	    HashMap<Feature,Value> test1=new HashMap<Feature, Value>();
	    test1.put(featureSpace.get(0), new StringValue("low"));
	    test1.put(featureSpace.get(2), new NumberValue(150.00));
	    Dependency dep1=new Dependency(intelligence,new Feature[]{featureSpace.get(0),featureSpace.get(2)});
	    MarkovBlanket testMb=new MarkovBlanket(g0,dep1,test1);
	    
	    assertEquals(testMb,ftSpace.getMarkovBlanket(d.getInterpretations().get(0), g0, dep1));
	    

	}
	
}
