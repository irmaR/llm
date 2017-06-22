package hybrid.network;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hybrid.featureGenerator.Renaming;
import hybrid.features.FeatureTypeException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestAtom {
	private static Atom intelligence;
	private static Atom intelligence1;

	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar professor;
	private static Type stud;
	private static Atom friend;
	private static Type c;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@BeforeClass
	public static void setUp() throws FeatureTypeException{
		stud=new Type("student");
		c=new Type("course");
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
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		Predicate fr=new BooleanPred("friend",2);
		intelligence=new Atom(intel, new Logvar[]{student});
		intelligence1=new Atom(intel, new Logvar[]{student1});
		grade=new Atom(gr, new Logvar[]{student,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		friend=new Atom(fr,new Logvar[]{student,student});
		difficulty=new Atom(diff,new Logvar[]{course});
	}
	
	@Test
	public void testCreateFOLTerm(){
		assertEquals("intelligence(S)",intelligence.createFOLTerm());
		assertEquals("friend(S,S)",friend.createFOLTerm());
	}
	
	
	@Test
	public void testSameArguments(){
		assertEquals(true,intelligence.hasSameArgumentNames(intelligence));
		assertEquals(false,intelligence.hasSameArgumentNames(intelligence1));

	}
	
	/**
	 * Test obtaining a list of atoms from an atom and a list of possible renamings - the atom has one arguments
	 */
	@Test
	public void renameINTERNAL_Atom(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=intelligence.renameINTERNAL_Atom(ren);
		assertEquals(new Atom(new GaussianPred("intelligence",1,1.0,2.0),new Logvar[]{new Logvar("S2",stud)}),result.get(0));
		assertEquals(new Atom(new GaussianPred("intelligence",1,1.0,2.0),new Logvar[]{new Logvar("S1",stud)}),result.get(1));
		assertEquals(false, result.get(0).equals(intelligence));
		assertEquals(false, result.get(1).equals(intelligence));
		assertEquals(false, result.get(1).equals(result.get(0)));
	}
	

	
	@Test
	public void renameINTERNALAtom_test_1(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=intelligence.renameINTERNAL_Atom(ren);
		System.out.println(" Results: "+result);
		assertEquals(intelligence1,result.get(0));	
	}
	
	/**
	 * Test obtaining a list of atoms from an atom and a list of possible renamings - the atom has two arguments
	 */
	@Test
	public void testRenameTwoLogvarsOfExternalAtom(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		renaming.put(c, new Logvar[]{new Logvar("C1",c),new Logvar("C2",c)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=grade.renameExternalAtom(ren);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C2",c)}),result.get(0));
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C1",c)}),result.get(1));
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C2",c)}),result.get(2));
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C1",c)}),result.get(3));

		assertEquals(false, result.get(0).equals(grade));
		assertEquals(false, result.get(1).equals(grade));
		assertEquals(false, result.get(2).equals(grade));
		assertEquals(false, result.get(3).equals(grade));
		assertEquals(false, result.get(0).equals(result.get(1)));
		assertEquals(false, result.get(0).equals(result.get(2)));
		assertEquals(false, result.get(0).equals(result.get(3)));
	}
	
	@Test
	public void testRenameExternal_1(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=grade.renameExternalAtom(ren);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		System.out.println(result);
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C",c)}),result.get(0));
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C",c)}),result.get(1));

		assertEquals(false, result.get(0).equals(grade));
		assertEquals(false, result.get(1).equals(grade));
		assertEquals(false, result.get(0).equals(result.get(1)));
		assertEquals(false, result.get(1).equals(result.get(0)));
	}
	
	@Test
	public void testRenameExternal_takes(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S",stud),new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=takes.renameExternalAtom(ren);
		System.out.println(result);
	}
	
	@Test
	public void testRenameExternal_2(){
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(c, new Logvar[]{new Logvar("C1",c),new Logvar("C2",c)});
		Renaming ren=new Renaming(renaming);
		List<Atom> result=grade.renameExternalAtom(ren);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		System.out.println(result);
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S",stud),new Logvar("C2",c)}),result.get(0));
		assertEquals(new Atom(gr,new Logvar[]{new Logvar("S",stud),new Logvar("C1",c)}),result.get(1));

		assertEquals(false, result.get(0).equals(grade));
		assertEquals(false, result.get(1).equals(grade));
		assertEquals(false, result.get(0).equals(result.get(1)));
		assertEquals(false, result.get(1).equals(result.get(0)));
	}
	
	
	
	
}
