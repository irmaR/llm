package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import junit.framework.Assert;

import hybrid.core.GenerateUniqueIDforAtom;
import hybrid.dependencies.Dependency;
import hybrid.features.FeatureTypeException;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGetAllPossibleAtomCombinations {
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
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@Before
	public void setUp() throws FeatureTypeException{
		GenerateUniqueIDforAtom.reset();
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
	public void testGettingAllUnaryCombinations() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		List<ProposedConjunction> prop=testClass.getAllUnaryProposedConjunctions(new Literal[]{new PosLiteral(friend),new PosLiteral(intelligence),new PosLiteral(grade),new PosLiteral(takes)},intelligence,5);
		System.out.println(prop);
		/*Assert.assertEquals(3,prop.size());
		Assert.assertEquals(intelligence, prop.get(0).getAtomList().get(0));
		Assert.assertEquals(grade, prop.get(1).getAtomList().get(0));
		Assert.assertEquals(takes, prop.get(2).getAtomList().get(0));
		System.out.println(prop);*/
	}
	
	/*@Test
	public void testGettingAllCombinationUpToLength2() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		List<ProposedConjunction> prop=testClass.getallPossibleCombinationUpToLength(new Atom[]{intelligence,grade,takes}, 2, difficulty);
		System.out.println(prop.size());
		System.out.println(prop);
		assertEquals(6,prop.size());
		
	}*/
	
	@Test
	public void testGettingAllCombinationUpToLength3() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		List<ProposedConjunction> prop=testClass.getallPossibleCombinationUpToLength(new PosLiteral[]{new PosLiteral(intelligence),new PosLiteral(grade),new PosLiteral(takes),new PosLiteral(friend)}, 3, difficulty, 3,3);
		assertEquals(17,prop.size());
	}
	
	@Test
	public void testGettingAllCombinationUpToLength5() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		List<ProposedConjunction> prop=testClass.getallPossibleCombinationUpToLength(new PosLiteral[]{new PosLiteral(intelligence),new PosLiteral(grade),new PosLiteral(takes), new PosLiteral(teaches),new PosLiteral(ability),new PosLiteral(difficulty)}, 5,friend, 3,3);
		System.out.println(prop.size());
		assertEquals(100,prop.size());
		
	}
	
	@Test
	public void testAddingRandVarValueTests() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		List<ProposedConjunction> prop=testClass.getallPossibleCombinationUpToLength(ntw.getRandvarValueTestsAsPositiveLit(), 5,intelligence, 3,3);
		System.out.println(prop);
		assertEquals(3,prop.size());
		
	}

	/*@Test
	public void tesAddingTwoRandvarValueTestsTogether() throws Exception{
		AtomCombinationCreator testClass=new AtomCombinationCreator();

		TestRandvarValue rv1=new TestRandvarValue(grade.getP(),grade.getArguments(),new StringValue("high"));
		TestRandvarValue rv2=new TestRandvarValue(difficulty.getP(),difficulty.getArguments(),new StringValue("easy"));
		ProposedConjunction newProp=new ProposedConjunction(rv1);
		newProp.addToAtomList(rv2);
		System.out.println(newProp);
	//	Assert.assertEquals(74,prop.size());
		
	}*/
	
	
}
