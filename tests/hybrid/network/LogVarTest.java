package hybrid.network;
import static org.junit.Assert.*;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Type;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;


public class LogVarTest {

	static Logvar student;
	static Logvar student1;
	static Logvar course;
	static Logvar course1;
	static Type stud;
	
	@BeforeClass
	public static void setUp(){
		 stud=new Type("student");
		 student=new Logvar("S",stud);
		 student1=new Logvar("S1",stud);
		 course=new Logvar("C",new Type("course"));
		 course1=new Logvar("C1",new Type("course"));	
	}
	
	@Test
	public void testsameLogVar(){
		assertEquals(student, student);
	}
	
	@Test
	public void testdifferentLogVar(){
		assertNotSame(student, student1);
	}
	
	@Test
	public void testRenaming(){
		Logvar renamedLogvar=student.copyAndRename("1");
		Logvar student1=new Logvar("S1",new Type("student"));
		assertEquals(new Logvar("S1",new Type("student")), renamedLogvar);
	}
	
	@Test
	public void testCreateFOLTerm(){
		String term="student(S)";
		assertEquals(student.createFOLTerm(),term);
		assertEquals(student1.createFOLTerm(),"student(S1)");
	}
	
	
	
}
