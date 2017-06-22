package hybrid.network;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Renaming;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Proportion;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestAtomRenaming {
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Logvar student;
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
		grade=new Atom(gr, new Logvar[]{student,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		friend=new Atom(fr,new Logvar[]{student,student});
		difficulty=new Atom(diff,new Logvar[]{course});
	}
	
	@Test
	public void testAtomRenaming() throws Exception{
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S",stud),new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		friend.applyRenaming(ren);
	}
	
	@Test
	public void testAtomRenamingOneLogvar() throws Exception{
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S",stud),new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);

		List<Atom> createdAtoms=grade.applyRenaming(ren);
		System.out.println(createdAtoms);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Atom grade2=new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C",c)});
		Atom grade3=new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C",c)});
		assertEquals(grade, createdAtoms.get(0));
		assertEquals(grade2, createdAtoms.get(1));
		assertEquals(grade3, createdAtoms.get(2));	
	}
	
	@Test
	public void testAtomRenamingTwoLogvars() throws Exception{
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		renaming.put(c, new Logvar[]{new Logvar("C1",c),new Logvar("C2",c)});
		Renaming ren=new Renaming(renaming);
		List<Atom> createdAtoms=grade.applyRenaming(ren);
		System.out.println(createdAtoms);

		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Atom grade1=new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C1",c)});
		Atom grade2=new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C2",c)});
		Atom grade3=new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C1",c)});
		Atom grade4=new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C2",c)});
		assertEquals(grade1, createdAtoms.get(3));
		assertEquals(grade2, createdAtoms.get(2));
		assertEquals(grade3, createdAtoms.get(1));
		assertEquals(grade4, createdAtoms.get(0));
	}
	
	@Test
	public void testRenamingINTERNALAtom() throws Exception{
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> createdAtoms=friend.applyRenaming(ren);
		Predicate fr=new BooleanPred("friend",2);
		Atom friend1=new Atom(fr,new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Atom friend2=new Atom(fr,new Logvar[]{new Logvar("S2",stud),new Logvar("S1",stud)}); 
		System.out.println(createdAtoms);
		assertEquals(friend2, createdAtoms.get(0));
		assertEquals(friend1, createdAtoms.get(1));

	}
	
	@Test
	public void testRenamingINTERNALAtomThreeRenamings() throws Exception{
		HashMap<Type,Logvar[]> renaming=new HashMap<Type, Logvar[]>();
		renaming.put(stud, new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud),new Logvar("S3",stud)});
		Renaming ren=new Renaming(renaming);
		List<Atom> createdAtoms=friend.applyRenaming(ren);
		Predicate fr=new BooleanPred("friend",2);
		Atom friend1=new Atom(fr,new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
		Atom friend2=new Atom(fr,new Logvar[]{new Logvar("S1",stud),new Logvar("S3",stud)}); 
		Atom friend3=new Atom(fr,new Logvar[]{new Logvar("S2",stud),new Logvar("S1",stud)}); 
		Atom friend4=new Atom(fr,new Logvar[]{new Logvar("S2",stud),new Logvar("S3",stud)}); 
		Atom friend5=new Atom(fr,new Logvar[]{new Logvar("S3",stud),new Logvar("S1",stud)}); 
		Atom friend6=new Atom(fr,new Logvar[]{new Logvar("S3",stud),new Logvar("S2",stud)}); 
		assertEquals(friend1, createdAtoms.get(5));
		assertEquals(friend2, createdAtoms.get(4));
		assertEquals(friend3, createdAtoms.get(3));
		assertEquals(friend4, createdAtoms.get(2));
		assertEquals(friend5, createdAtoms.get(1));
		assertEquals(friend6, createdAtoms.get(0));

	}
	
	
}
