package hybrid.featureGenerator;

import static org.junit.Assert.*;
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
import hybrid.network.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRenamingCreation {

	
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
    private static Atom takesSC;
    private static Atom takesS1C;
	private static Atom difficulty;
	private static Atom friend;
	private static Atom friendss1;
	private static Atom friends1s;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar student2;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static List<Standard_Conjunction> testClassObject;
	private static Type stud;
	private static Type c;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@Before
	public void setUp() throws FeatureTypeException{
		 stud=new Type("student");
		 c=new Type("course");
		Type p=new Type("prof");
		student=new Logvar("S",stud);
		student1=new Logvar("S1",stud);
		student2=new Logvar("S2",stud);
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
		takesSC=new Atom(tk, new Logvar[]{student,course});
		takesS1C=new Atom(tk, new Logvar[]{student1,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student});
		friendss1=new Atom(fr,new Logvar[]{student,student1});
		friends1s=new Atom(fr,new Logvar[]{student1,student});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takesSC,teaches,ability,difficulty},new Type[]{stud,c,p});
	}

	
	@Test
	public void testCreatingRenamingsForProposedConjunctions1() throws Exception{
		RenamedConjunctionsCreator testClass=new RenamedConjunctionsCreator();
		AtomCombinationCreator comb=new AtomCombinationCreator();
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(new PosLiteral(friend));
		atoms.add(new PosLiteral(takesSC));
		
		List<ProposedConjunction> proposed_conj=comb.getallPossibleCombinationUpToLength(atoms, 3, intelligence, 2, 3);		
		for(ProposedConjunction p:proposed_conj){
			System.out.println("prop: "+p);
		}
		
		List<Standard_Conjunction> result= testClass.createAllRenamingCombinations(proposed_conj,intelligence);
		System.out.println(" conjunction created -------------------\n"+result);
		for(AbstractConjunction c:result){
			System.out.println(c);
		}
		AbstractConjunction c1=new Standard_Conjunction(intelligence,new PosLiteral(friendss1));
		AbstractConjunction c2=new Standard_Conjunction(intelligence,new PosLiteral(friends1s));
		AbstractConjunction c3=new Standard_Conjunction(intelligence,new PosLiteral(takesSC));
		AbstractConjunction c4=new Standard_Conjunction(intelligence,new PosLiteral(friendss1),new PosLiteral(friends1s));
		AbstractConjunction c5=new Standard_Conjunction(intelligence,new PosLiteral(friends1s),new PosLiteral(friendss1));
		AbstractConjunction c6=new Standard_Conjunction(intelligence,new PosLiteral(friendss1),new PosLiteral(takesSC),new PosLiteral(takesS1C));
		AbstractConjunction c7=new Standard_Conjunction(intelligence,new PosLiteral(friendss1),new PosLiteral(takesS1C),new PosLiteral(takesSC));
		AbstractConjunction c8=new Standard_Conjunction(intelligence,new PosLiteral(friends1s),new PosLiteral(takesSC),new PosLiteral(takesS1C));
		AbstractConjunction c9=new Standard_Conjunction(intelligence,new PosLiteral(friends1s),new PosLiteral(takesS1C),new PosLiteral(takesSC));
		assertEquals(c1,result.get(0));
		assertEquals(c2,result.get(1));
		assertEquals(c3,result.get(2));
		assertEquals(c4,result.get(3));
		assertEquals(c5,result.get(4));
		assertEquals(c6,result.get(5));
		assertEquals(c7,result.get(6));
		assertEquals(c8,result.get(7));
		assertEquals(c9,result.get(8));
	}
}
