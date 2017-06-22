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
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFeatureFilter {
	/*private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
    private static Atom takes;
	private static Atom difficulty;
	private static Atom friend;
	private static Atom friend1;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static Conjunction testClass;
	
	*//**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 *//*
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
		friend1=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	    
	}
	
	*//**
	 * Testing renameConjunctions of feature generator.
	 * @throws Exception
	 *//*
	@Test(expected=HasDuplicateLiterals.class)
	public void applyFilter() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(new PosLiteral(friend1));
		atoms.add(new PosLiteral(friend1));
		Conjunction conj=new Conjunction(intelligence,atoms);
	}
	
	
	*//**
	 * Testing renameConjunctions of feature generator.
	 * @throws Exception
	 *//*
	@Test(expected=HasDuplicateLiterals.class)
	public void testSameFeatures() throws Exception{
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(new PosLiteral(friend1));
		atoms.add(new PosLiteral(friend1));
		Conjunction conj=new Conjunction(intelligence,atoms);
	}
	
	
	*//**
	 * Testing renameConjunctions of feature generator.
	 * @throws Exception
	 *//*
	@Test
	public void testConnectionFilter() throws Exception{
		hybrid.core.GenerateUniqueIDforAtom.reset();
		Type stud=new Type("student");
		Logvar student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
		Logvar student2=new Logvar("S2",stud);
		Predicate fr=new BooleanPred("friend",2);
		Atom friend1=new Atom(fr,new Logvar[]{student,student1});
		Atom friend2=new Atom(fr,new Logvar[]{student1,student2});
		Atom friend3=new Atom(fr,new Logvar[]{student,student2});
		List<Literal> atoms=new ArrayList<Literal>();
		atoms.add(new PosLiteral(friend1));
		atoms.add(new PosLiteral(friend2));
		atoms.add(new PosLiteral(friend3));

		Conjunction temp=new Conjunction(intelligence,atoms);
		temp.setInputLogvar(Arrays.asList(student,student1));
		FeatureFilter ft=new FeatureFilter();
		Assert.assertEquals(true, ft.succesfulconnectionBasedFilter(temp));
	}*/
	
	
}
