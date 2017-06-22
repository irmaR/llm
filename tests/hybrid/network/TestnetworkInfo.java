package hybrid.network;

import static org.junit.Assert.*;
import junit.framework.Assert;
import hybrid.features.FeatureTypeException;

import org.junit.Before;
import org.junit.Test;

public class TestnetworkInfo {
	
	private static  NetworkInfo ntw;
	private static Atom grade;
	private static Atom difficulty;
	
	@Before
	public void setUp() throws FeatureTypeException{
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		Logvar student=new Logvar("S",stud);
		Logvar student1=new Logvar("S1",stud);
		Logvar course=new Logvar("C",c);
		Logvar professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		Predicate fr=new BooleanPred("friend",2);
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		Atom intelligence=new Atom(intel, new Logvar[]{student});
		grade=new Atom(gr, new Logvar[]{student,course});
		Atom takes=new Atom(tk, new Logvar[]{student,course});
		Atom ability=new Atom(ab,new Logvar[]{professor});
		Atom teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		Atom friend=new Atom(fr,new Logvar[]{student,student});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	}
	
	@Test
	public void testRandVarValueTest(){
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		TestRandvarValue t1=new TestRandvarValue(grade.getPredicate(),grade.getArguments() ,new StringValue("low"));
		TestRandvarValue t2=new TestRandvarValue(grade.getPredicate(),grade.getArguments() , new StringValue("mid"));
		TestRandvarValue t3=new TestRandvarValue(grade.getPredicate(),grade.getArguments() , new StringValue("high"));
		TestRandvarValue t4=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(), new StringValue("easy"));
		TestRandvarValue t5=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(), new StringValue("medium"));
		TestRandvarValue t6=new TestRandvarValue(difficulty.getPredicate(),difficulty.getArguments(), new StringValue("hard"));
		System.out.println(ntw.getRandvarValueTests());
       /* assertEquals(t1,ntw.getRandvarValueTests().get(1));
        assertEquals(t2,ntw.getRandvarValueTests().get(0));
        assertEquals(t3,ntw.getRandvarValueTests().get(2));
        assertEquals(t4,ntw.getRandvarValueTests().get(5));
        assertEquals(t5,ntw.getRandvarValueTests().get(3));
        assertEquals(t6,ntw.getRandvarValueTests().get(4));*/
	}
	
}
