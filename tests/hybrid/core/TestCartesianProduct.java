package hybrid.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import hybrid.features.FeatureTypeException;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.Predicate;
import hybrid.network.Type;
import hybrid.utils.CartesianProduct;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCartesianProduct {

	
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
	/*@Test
	public void testCartesianProduct() throws Exception{
	List<List<Atom>> testList=new ArrayList<List<Atom>>();
	List<Atom> list1=new ArrayList<Atom>();
	List<Atom> list2=new ArrayList<Atom>();
	//One list of objects
	Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
	Predicate fr=new BooleanPred("friend",2);
	Atom grade1=new Atom(gr,new Logvar[]{new Logvar("S",stud),new Logvar("C",c)});
	Atom grade2=new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C",c)});
	Atom grade3=new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C",c)});
	list1.addAll(Arrays.asList(grade1,grade2,grade3));
	
	//Second List of Objects
	Atom friend1=new Atom(fr,new Logvar[]{new Logvar("S1",stud),new Logvar("S2",stud)});
	Atom friend2=new Atom(fr,new Logvar[]{new Logvar("S2",stud),new Logvar("S1",stud)}); 
	list2.addAll(Arrays.asList(friend1,friend2));
	testList.add((ArrayList<Atom>) list1);
	testList.add((ArrayList<Atom>) list2);
	CartesianProduct<Atom> cP=new CartesianProduct();
	Assert.assertEquals(6, cP.cartesianProduct(testList).size());
	}*/
	
	@Test
	public void testCartesianProduct1() throws Exception{
	List<List<Atom>> testList=new ArrayList<List<Atom>>();
	List<Atom> list1=new ArrayList<Atom>();
	List<Atom> list2=new ArrayList<Atom>();
	List<Atom> list3=new ArrayList<Atom>();
	List<Atom> list4=new ArrayList<Atom>();
	//One list of objects
	Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
	Predicate fr=new BooleanPred("friend",2);
	Atom grade1=new Atom(gr,new Logvar[]{new Logvar("S",stud),new Logvar("C",c)});
	Atom grade2=new Atom(gr,new Logvar[]{new Logvar("S1",stud),new Logvar("C",c)});
	Atom grade3=new Atom(gr,new Logvar[]{new Logvar("S2",stud),new Logvar("C",c)});
	Atom grade4=new Atom(gr,new Logvar[]{new Logvar("S3",stud),new Logvar("C",c)});
	Atom grade5=new Atom(gr,new Logvar[]{new Logvar("S4",stud),new Logvar("C",c)});
	list1.addAll(Arrays.asList(grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5));
	list2.addAll(Arrays.asList(grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5));
	list3.addAll(Arrays.asList(grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5));
	list4.addAll(Arrays.asList(grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5,grade1,grade2,grade3,grade4,grade5));
	
	testList.add((ArrayList<Atom>) list1);
	testList.add((ArrayList<Atom>) list2);
	testList.add((ArrayList<Atom>) list3);
	testList.add((ArrayList<Atom>) list4);
	CartesianProduct<Atom> cP=new CartesianProduct();
	System.out.println(cP.cartesianProduct(testList).size());
	}
	
	
	
	
}
