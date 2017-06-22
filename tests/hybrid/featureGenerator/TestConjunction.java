package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
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
import org.junit.Test;

public class TestConjunction {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom intelligence1;
	private static Atom grade;
	private static Atom grade1;
	private static TestRandvarValue grade1_randvar;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Atom friend;

	private static PosLiteral intelligenceP;
	private static PosLiteral intelligenceP1;
	private static PosLiteral gradeP;
	private static PosLiteral gradeP1;
	private static PosLiteral gradeP1randvar;
	private static PosLiteral teachesP;
	private static PosLiteral abilityP;
	private static PosLiteral takesP;
	private static PosLiteral takesP1;
	private static PosLiteral difficultyP;
	private static PosLiteral friendP;

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
		intelligence1=new Atom(intel, new Logvar[]{student1});
		grade=new Atom(gr, new Logvar[]{student,course});
		grade1=new Atom(gr, new Logvar[]{student1,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		Atom takes1 = new Atom(tk, new Logvar[]{student1,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student1});
		grade1_randvar=new TestRandvarValue(gr, grade1.getArguments(), new StringValue("low"));

		intelligenceP1=new PosLiteral(intelligence1);
		intelligenceP=new PosLiteral(intelligence);
		gradeP=new PosLiteral(grade);
		gradeP1=new PosLiteral(grade1);
		gradeP1randvar=new PosLiteral(grade1);
		takesP=new PosLiteral(takes);
		takesP1=new PosLiteral(takes1);
		abilityP=new PosLiteral(ability);
		teachesP=new PosLiteral(teaches);
		difficultyP=new PosLiteral(difficulty);
		friendP=new PosLiteral(friend);

		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	    
	}


	@Test
	public void testGetLiteralList() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(atoms, testClass.getLiteralList());
	}

	@Test
	public void testgetBooleanAtoms() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(atoms, testClass.getBooleanAtoms());
	}

	@Test
	public void testgetBooleanAtoms1() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(difficultyP);
		atoms.add(new PosLiteral(grade1_randvar));

		List<Literal> result=new ArrayList<Literal>();
		result.add(friendP);
		result.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(result, testClass.getBooleanAtoms());
	}

	@Test
	public void testgetInternalBooleanAtoms() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(difficultyP);
		atoms.add(new PosLiteral(grade1_randvar));

		List<Literal> result=new ArrayList<Literal>();
		result.add(friendP);

		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(result, testClass.getInternalBooleanAtoms());
	}

	@Test
	public void TestgetNon_boolean_literal() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(difficultyP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(difficultyP, testClass.getNon_boolean_literal());
	}

	@Test
	public void TestgetNr_booleanVars() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(difficultyP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(2, testClass.getNr_booleanVars());
	}

	@Test
	public void TestgetNr_non_booleanPredicates() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(gradeP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(1, testClass.getNr_non_booleanPredicates());
	}

	@Test
	public void TestgetInputLogvars() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(gradeP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(friend,atoms);
		List<Logvar> inputlogvars=friend.getArguments();
		assertEquals(inputlogvars, testClass.getInputLogvars());
	}

	@Test
	public void TestisDeterministic_false() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(gradeP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(friend,atoms);
		assertEquals(false, testClass.isDeterministic());
	}

	@Test
	public void TestisDeterministic_true() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(intelligenceP);
		AbstractConjunction testClass=new Standard_Conjunction(friend,atoms);
		assertEquals(true, testClass.isDeterministic());
	}




	@Test
	public void testInternalAtoms() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(gradeP1);
		List<Literal> expected=new ArrayList<Literal>();
		expected.add(friendP);
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(expected, testClass.getInternalBooleanAtoms());
	}


	@Test
	public void testBooleanAtoms() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(intelligenceP1);
		List<Literal> expected=new ArrayList<Literal>();
		expected.add(friendP);
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(expected, testClass.getBooleanAtoms());
	}

	@Test
	public void testNon_boolean_atom() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(gradeP1);
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(gradeP1, testClass.getNon_boolean_literal());
	}


	@Test
	public void isDeterministicTest() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(gradeP);
		AbstractConjunction testClass=new Standard_Conjunction(takes,atoms);
		assertEquals(true, testClass.isDeterministic());
	}

	@Test(expected=NotFullyConnected.class)
	public void isFullyConnected() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(takesP);
		atoms.add(gradeP);
		atoms.add(friendP);	
	    AbstractConjunction testClass=new Standard_Conjunction(teaches,atoms);
	}

	@Test
	public void testFeature() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(takesP);
		atoms.add(friendP);
		atoms.add(takesP1);
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
	}


	@Test
	public void isFullyConnectedVers2() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(takesP);
		atoms.add(gradeP);
		AbstractConjunction testClass=new Standard_Conjunction(teaches,atoms);
	}


	@Test
	public void isFullyConnectedTrue() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(takesP);
		atoms.add(gradeP);
		Standard_Conjunction testClass=new Standard_Conjunction(teaches,atoms);
		assertEquals(true, testClass.isFullyConnected());
	}
	
	@Test
	public void isFullyConnectedTrue_1() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(friendP);
		atoms.add(new PosLiteral(grade1_randvar));
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
	}

	//testing: intelligence(S) | friend(S,S1),grade(S,C)
	//shouldn't be connected because of friend(S,S1)
	@Test(expected=NotFullyConnected.class)
	public void isFullyConnectedVers_answer_no() throws Exception{
		AbstractConjunction c=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade),friendP));

	}
	
	//testing: intelligence(S) | takes(S,C)
		//this is allowed feature, therefore should be connected
	    //it is an exception: when we have one boolean in the body, we can have a free logvar
		@Test
		public void isFullyConnectedVers_answer_yes_1() throws Exception{
			AbstractConjunction c=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes)));

		}
	

	//testing: intelligence(S) | friend(S,S1),grade(S,C)
	//shouldn't be connected because of friend(S,S1)
	@Test
	public void isFullyConnectedVers_asnwer_yes() throws Exception{
		Standard_Conjunction c=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(grade1),friendP));
		assertEquals(true, c.isFullyConnected());

	}



	@Test
	public void isDeterministicTest1() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(gradeP);
		AbstractConjunction testClass=new Standard_Conjunction(intelligence,atoms);
		assertEquals(false, testClass.isDeterministic());
	}



}
