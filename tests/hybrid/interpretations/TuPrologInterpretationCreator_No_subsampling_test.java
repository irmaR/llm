package hybrid.interpretations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hybrid.network.Atom;
import hybrid.network.BoolValue;
import hybrid.network.BooleanPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.Predicate;
import hybrid.network.Subst;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.Prolog;

public class TuPrologInterpretationCreator_No_subsampling_test {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom takes;
	private static Logvar student;
	private static Logvar course;
	private static Type course_type;
	private static Type student_type;
	
	@BeforeClass
	public static void setUp(){
		System.out.println(pathToInterpretations);
		student_type=new Type("student");
		course_type=new Type("course");
		student=new Logvar("S",student_type);
		course=new Logvar("C",new Type("course"));
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		BooleanPred takesP=new BooleanPred("takes",2);
		intelligence=new Atom(intel, new Logvar[]{student});
		takes=new Atom(takesP,new Logvar[]{student,course});
		ntw=new NetworkInfo(new Atom[]{intelligence,takes},new Type[]{student_type,course_type});
		
	}

	@Test
	public void checkInterpretation() throws SubstitutionException{
		TuPrologInterpretationCreator_NoSubsampling tC=new TuPrologInterpretationCreator_NoSubsampling();
		Interpretation i=tC.createInterpretation(ntw, pathToInterpretations+"/interp1.pl","test");
		Constant c0=new Constant("c0");
		Constant c1=new Constant("c1");
		Constant c2=new Constant("c2");
		Constant s0=new Constant("s0");
		Constant s1=new Constant("s1");
		Constant s2=new Constant("s2");
		Constant s3=new Constant("s3");
		System.out.println(i.getDatabaseFormat());
		assertEquals(c0,i.getDomain().getDomainElements().get(course_type).get(0));
		assertEquals(c1,i.getDomain().getDomainElements().get(course_type).get(1));
		assertEquals(c2,i.getDomain().getDomainElements().get(course_type).get(2));
		assertEquals(s0,i.getDomain().getDomainElements().get(student_type).get(0));
		assertEquals(s1,i.getDomain().getDomainElements().get(student_type).get(1));
		assertEquals(s2,i.getDomain().getDomainElements().get(student_type).get(2));
		assertEquals(s3,i.getDomain().getDomainElements().get(student_type).get(3));
		GroundAtom intel1=new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s0")}),new NumberValue(101.35));
		GroundAtom intel2=new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(109.189));
		GroundAtom intel3=new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(116.65));
		GroundAtom intel4=new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(90.227));

		GroundAtom takes1=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s0"),new Constant("c0")}),new BoolValue(true));
		GroundAtom takes2=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s0"),new Constant("c1")}),new BoolValue(true));
		GroundAtom takes3=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s0"),new Constant("c2")}),new BoolValue(true));
		GroundAtom takes4=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c0")}),new BoolValue(true));
		GroundAtom takes5=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new BoolValue(true));
		GroundAtom takes6=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c2")}),new BoolValue(true));
		GroundAtom takes7=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c1")}),new BoolValue(true));
		GroundAtom takes8=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c0")}),new BoolValue(true));
		GroundAtom takes9=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c1")}),new BoolValue(true));
		GroundAtom takes10=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c2")}),new BoolValue(false));
		GroundAtom takes11=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c0")}),new BoolValue(false));
		GroundAtom takes12=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s3"),new Constant("c2")}),new BoolValue(false));

		
		ArrayList<GroundAtom> trueAtoms=new ArrayList<GroundAtom>();
		trueAtoms.add(takes1);
		trueAtoms.add(takes2);
		trueAtoms.add(takes3);
		trueAtoms.add(takes4);
		trueAtoms.add(takes5);
		trueAtoms.add(takes6);
		trueAtoms.add(takes7);
		trueAtoms.add(takes8);
		trueAtoms.add(takes9);
		trueAtoms.add(takes10);
		trueAtoms.add(takes11);
		trueAtoms.add(takes12);


		for(GroundAtom g:trueAtoms){
			if(i.getGroundAtoms(takes).contains(g)){
				assertTrue(true);
			}
			else{
				assertTrue(false);
			}
		}
		
		assertEquals(12, i.getGroundAtoms().getAssignmentFor(takes).size(),0.1);
		
	}
	
}
