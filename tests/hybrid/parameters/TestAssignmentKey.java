package hybrid.parameters;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import hybrid.cpdEvaluation.CGEvaluator;
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
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Type;
import hybrid.network.Value;

import org.junit.Before;
import org.junit.Test;

import weka.classifiers.trees.m5.Values;

public class TestAssignmentKey {
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
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@Before
	public void setUp() throws FeatureTypeException{
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
		}
	
	@Test
	public void testAssignmentKey() throws ConjunctionConstructionProblem{
		ValueFt ft=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade))));
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes))));
		AssignmentKey assigKey=new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new NumberValue(5.0),new NumberValue(2.5)});
		AssignmentKey assigKey1=new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new NumberValue(5.0),new NumberValue(2.5)});
	    assertEquals(true,assigKey.equals(assigKey1));
	}
	
	@Test
	public void testAssignmentKey1() throws FeatureTypeException, ConjunctionConstructionProblem{
		Mode ft=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(difficulty)));
		Exist ft1=new Exist(new Standard_Conjunction(intelligence,new PosLiteral(friend)));
		
		Dependency dep=new Dependency(intelligence,new Feature[]{ft,ft1});
		
		List<AssignmentKey> keys=new ArrayList<AssignmentKey>();
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("easy"),new BoolValue("true")}));
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("medium"),new BoolValue("true")}));
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("hard"),new BoolValue("true")}));
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("easy"),new BoolValue("false")}));
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("medium"),new BoolValue("false")}));
		keys.add(new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("hard"),new BoolValue("false")}));
		
		HashMap<AssignmentKey,MarkovBlanket> mb=new HashMap<AssignmentKey, MarkovBlanket>();
		for(AssignmentKey k:keys){
			mb.put(k, null);
		}
		
		HashMap<Feature,Value> testVals=new HashMap<Feature, Value>();
		testVals.put(ft, new StringValue("hard"));
		testVals.put(ft1, new BoolValue("true"));
		CGEvaluator cgEval=new CGEvaluator();
		AssignmentKey key=cgEval.extractAssignmentKey(dep, testVals);
		AssignmentKey key1=new AssignmentKey(new Feature[]{ft,ft1},new Value[]{new StringValue("hard"),new BoolValue("true")});
	    System.out.println(key.hashCode());
	    System.out.println(key1.hashCode());
	    System.out.println(key+ " vs "+key1);
	    System.out.println(key.getKey().get(0).getVal()+ " vs "+key1.getKey().get(0).getVal());
	    System.out.println(key.getKey().get(0).getVal().equals(key1.getKey().get(0).getVal()));
		//assertEquals(true,key.equals(key1));
	}
	
	
	
}


