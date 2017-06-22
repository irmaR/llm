package hybrid.interpretations;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import hybrid.network.ValueFactory;
import hybrid.network.WrongValueType;

import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;

public class TuPrologInterpretationCreator_Subsampling_Test {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static String subsampling_test_file=System.getProperty("user.dir")+"/tests/test_data/small_university/"+ "subsampling_test_data.pl";
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
		ntw=new NetworkInfo(new Atom[]{takes},new Type[]{student_type,course_type});

	}

	@Test
	public void testBooleanPreds(){
		assertEquals(takes, ntw.getBooleanAtoms().get(0));
	}



	@Test
	public void testSubSampling(){
		TuPrologInterpretationCreator_Subsampling tC=new TuPrologInterpretationCreator_Subsampling(1);
		Interpretation i=tC.createInterpretation(ntw, pathToInterpretations+"/interp1.pl","test");
		System.out.println(i.getSubSampleInfo());
		assertEquals(3,i.getSubSampleInfo().getAlphas().get(takes),0.01);
		assertEquals(12,i.getSubSampleInfo().getNrGroundings().get(takes),0.01);
	}

	@Test
	public void subsampling_test_data(){
		TuPrologInterpretationCreator_Subsampling tC=new TuPrologInterpretationCreator_Subsampling(1);
		Interpretation i=tC.createInterpretation(ntw, pathToInterpretations+"/interp1.pl","test");
	}


	@Test
	public void checkInterpretation() throws SubstitutionException{
		TuPrologInterpretationCreator_Subsampling tC=new TuPrologInterpretationCreator_Subsampling(1);
		Interpretation i=tC.createInterpretation(ntw,subsampling_test_file,"test");

		List<Constant> course_constants=new ArrayList<Constant>();
		Constant c0=new Constant("c0");
		course_constants.add(c0);
		Constant c1=new Constant("c1");
		course_constants.add(c1);
		Constant c2=new Constant("c2");
		course_constants.add(c2);

		List<Constant> student_constants=new ArrayList<Constant>();
		Constant s0=new Constant("s0");
		student_constants.add(s0);
		Constant s1=new Constant("s1");
		student_constants.add(s1);
		Constant s2=new Constant("s2");
		student_constants.add(s2);

		HashMap<Type,List<Constant>> expected_domain_map=new HashMap<Type, List<Constant>>();
		expected_domain_map.put(student_type, student_constants);
		expected_domain_map.put(course_type, course_constants);

		Domain expected_domain=new Domain(expected_domain_map);

		GroundAtom takes2=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s0"),new Constant("c1")}),new BoolValue(true));
		GroundAtom takes6=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c2")}),new BoolValue(true));
		GroundAtom takes7=new GroundAtom(takes,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s2"),new Constant("c0")}),new BoolValue(true));


		List<GroundAtom> trueAtoms=new ArrayList<GroundAtom>();
		trueAtoms.add(takes2);
		trueAtoms.add(takes6);
		trueAtoms.add(takes7);

		Interpretation expected=new Interpretation(expected_domain,new Assignment(trueAtoms),subsampling_test_file);		
		List<GroundAtom> negatives=new ArrayList<GroundAtom>();
		List<GroundAtom> positives=new ArrayList<GroundAtom>();

		//check only positive examples; negatives are randomly subsampled
		for(GroundAtom g:trueAtoms){
			try {
				if(g.getValue().toNumber()==1.0){
					if(i.getGroundAtoms(takes).contains(g)){
						assertTrue(true);
						positives.add(g);
					}
					else{
						assertTrue(false);
					}

				}
				else{
					negatives.add(g);
				}
			} catch (WrongValueType e) {
				e.printStackTrace();
			}
		}
		
		//check that there is no overlap betweem positives and negatives
		for(GroundAtom g:negatives){
			if(positives.contains(g)){
				assertTrue(false);
			}
		}

		assertEquals(6, i.getSubSampleInfo().getNrGroundings().get(takes),0.1);
	}




}
