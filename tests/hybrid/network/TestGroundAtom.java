package hybrid.network;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestGroundAtom {
	
	private static Atom grade;
	
	@Test
	public void testEquals() throws SubstitutionException{
		Type stud=new Type("student");
		Type c=new Type("course");
		
		Logvar student=new Logvar("S",stud);
		Logvar course=new Logvar("C",c);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
        Atom grade=new Atom(gr, new Logvar[]{student,course});
		GroundAtom gr1=new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("high"));
		GroundAtom gr2=new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c1")}),new StringValue("high"));
        assertEquals(false,gr1.equals(gr2));
		assertEquals(true,gr1.equals(gr1));
		gr1.setValue(new StringValue("mid"));
		assertEquals(false,gr1.equals(gr2));
	}
	
	@Test
	public void testContinuousAtom() throws SubstitutionException{
		Type stud=new Type("student");
		Type c=new Type("course");
		
		Logvar student=new Logvar("S",stud);
		Logvar course=new Logvar("C",c);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
        Atom grade=new Atom(gr, new Logvar[]{student,course});
		GroundAtom gr1=new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new NumberValue(105.121));
		GroundAtom gr2=new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new NumberValue(105.1211));
        assertEquals(true,gr1.equals(gr2));
	}
	
	
	
}
