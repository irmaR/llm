package hybrid.cpds;

import static org.junit.Assert.*;

import java.util.Arrays;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCPDGenerator {

	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Logvar student;
	private static Logvar course;
	private static Logvar professor;
	
	@BeforeClass
	public static void setUp() throws FeatureTypeException{
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
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		intelligence=new Atom(intel, new Logvar[]{student});
		grade=new Atom(gr, new Logvar[]{student,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
	}
	
	
	@Test 
	public void testIFLogisticRegression() throws ConjunctionConstructionProblem{
		CPDGenerator cpdGenerator=new CPDGenerator();
		assertEquals(true,(cpdGenerator.getAppropriateCPD(new Dependency(difficulty, new Feature[]{new Proportion(new Standard_Conjunction(difficulty,Arrays.asList(new PosLiteral(takes))))})) instanceof LogisticRegression));
	}
	
	@Test 
	public void testIFProbabilityMassFunction(){
		CPDGenerator cpdGenerator=new CPDGenerator();
		assertEquals(true,(cpdGenerator.getAppropriateCPD(new Dependency(difficulty,new Feature[]{})) instanceof ProbabilityMassFunction));
	}
	
	@Test 
	public void testIFLinearGaussian() throws ConjunctionConstructionProblem{
		CPDGenerator cpdGenerator=new CPDGenerator();
		assertEquals(true,(cpdGenerator.getAppropriateCPD(new Dependency(intelligence,new Feature[]{new Proportion(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes)))),new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(takes),new Literal(teaches),new Literal(ability))))})) instanceof LinearGaussian));
	}
	
	@Test 
	public void testIFConditionalLinearGaussian() throws ConjunctionConstructionProblem{
		CPDGenerator cpdGenerator=new CPDGenerator();
		assertEquals(true,(cpdGenerator.getAppropriateCPD(new Dependency(intelligence,new Feature[]{new Proportion(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes)))),new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(grade)))) })) instanceof CLG));
	}
	
	@Test 
	public void testIFConditionalGaussian() throws ConjunctionConstructionProblem{
		CPDGenerator cpdGenerator=new CPDGenerator();
		assertEquals(true,(cpdGenerator.getAppropriateCPD(new Dependency(intelligence,new Feature[]{new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes)))),new ValueFt(new Standard_Conjunction(intelligence,Arrays.asList(new Literal(grade)))) })) instanceof CG));
	}
	
	
	
}
