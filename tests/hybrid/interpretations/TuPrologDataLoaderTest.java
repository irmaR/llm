package hybrid.interpretations;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.FeatureTypeException;
import hybrid.network.Argument;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Subst;
import hybrid.network.Type;
import hybrid.network.GroundAtom;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TuPrologDataLoaderTest {	
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
    private static Atom takes;
	private static Atom difficulty;
	private static Atom friend;
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	private static AbstractConjunction testClass;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
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
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});   
	}
	
	@Test
	public void testDataLoader(){
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1.0));
		try {
			Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
			List<GroundAtom> g=d.getInterpretations().get(0).getGroundAtoms(intelligence);
			
			GroundAtom g1=g.get(0);
			GroundAtom g2=g.get(1);
			
			HashMap<Logvar,Argument> subst1=new HashMap<Logvar,Argument>();
			subst1.put(student, new Constant("s0"));
			Subst sub=new Subst(subst1);
			assertEquals(sub, g1.getSubst());		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testNumberOfGroundings(){
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1.0));
		try {
			Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
			assertEquals(12, d.getNrGroundingsInData(intelligence));
			assertEquals(9, d.getNrGroundingsInData(difficulty));
			assertEquals(7, d.getNrGroundingsInData(ability));
			assertEquals(19, d.getNrGroundingsInData(grade));
			assertEquals(9, d.getInterpretations().get(0).getGroundAtoms(grade).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testNumberOfGroundingsOneInterpretation(){
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1.0));
		try {
			Data d=dataLoader.loadData(pathToInterpretations, new String[]{"interp1.pl"}, ntw);
			assertEquals(4, d.getNrGroundingsInData(intelligence));
			assertEquals(3, d.getNrGroundingsInData(difficulty));
			assertEquals(3, d.getNrGroundingsInData(ability));
			assertEquals(9, d.getNrGroundingsInData(grade));
			assertEquals(12, d.getNrGroundingsInData(takes));
			assertEquals(9, d.getInterpretations().get(0).getGroundAtoms(grade).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testOneInterpretationLoader(){
		TuPrologInterpretationCreator_Subsampling dataLoader=new TuPrologInterpretationCreator_Subsampling(1.0);
		try {
			Interpretation interTest=dataLoader.createInterpretation(ntw,System.getProperty("user.dir")+"/tests/test_data/small_university/interp1.pl","test");
			assertEquals(4,interTest.getGroundAtoms(intelligence).size());
			assertEquals(9,interTest.getGroundAtoms(grade).size());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testSubsampling(){
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1.0));
		try {
			Data d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw,DataType.training);
		    for(Interpretation i:d.getInterpretations()){
		    	System.out.println(i.getDatabaseFormat());
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}



