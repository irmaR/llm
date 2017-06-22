package hybrid.network;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.AtomCombinationCreator;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ProposedConjunction;
import hybrid.featureGenerator.Renaming;
import hybrid.features.FeatureTypeException;

import org.junit.Before;
import org.junit.Test;

public class TestRandvarValueTest {
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
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static Type stud;
	
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
		stud=new Type("student");
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
	public void testSameArguments(){
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		assertEquals(true,grade.hasSameArgumentNames(rv1));

	}
	
	@Test
	public void testEquality_of_two_randvalValue_tests() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		TestRandvarValue rv2=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(),new StringValue("easy"));
	    assertEquals(false,rv1.equals(rv2));
	}
	
	@Test
	public void testEquality_of_two_randvalValue_tests1() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		TestRandvarValue rv2=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
	    assertEquals(true,rv1.equals(rv2));		
	}
	
	@Test
	public void tesAddingTwoRandvarValueTestsTogether() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		TestRandvarValue rv2=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(),new StringValue("easy"));
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(rv1), 3);
		ProposedConjunction newProp=init.extend(new PosLiteral(rv2));
		System.out.println(newProp);
	//	Assert.assertEquals(74,prop.size());
		
	}
	
	@Test
	public void testAtomCreator() throws Exception{
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		PosLiteral posLit=new PosLiteral(rv1);
		NegLiteral negLit=new NegLiteral(rv1);
		assertEquals("grade(S,C,high)",posLit.createFOLTerm());
		assertEquals("not(grade(S,C,high))",negLit.createFOLTerm());
	}
	
	@Test
	public void testRenaming() throws Exception{
		TestRandvarValue rv1=new TestRandvarValue(grade.getPredicate(),grade.getArguments(),new StringValue("high"));
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		PosLiteral posLit=new PosLiteral(rv1);
		NegLiteral negLit=new NegLiteral(rv1);
		List<Atom> atoms=rv1.applyRenaming(ren);
		System.out.println("atoms : "+atoms);
	}
	
	
}
