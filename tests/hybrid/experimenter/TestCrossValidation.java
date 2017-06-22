package hybrid.experimenter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import hybrid.dependencies.Dependency;
import hybrid.experimenter.CrossValidation;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.FeatureTypeException;
import hybrid.interpretations.Data;
import hybrid.interpretations.TuPrologDataLoader;
import hybrid.interpretations.TuPrologInterpretationCreator_Subsampling;
import hybrid.network.Argument;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.Subst;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.Test;

public class TestCrossValidation {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/medium_university";
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
	public void testLeaveOneOutCrossValidation(){
		TuPrologDataLoader dataLoader=new TuPrologDataLoader(new TuPrologInterpretationCreator_Subsampling(1));
		Data d=null;
		try {
			d=dataLoader.loadData(pathToInterpretations, "interp", "pl", ntw);
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
		CrossValidation cV=new CrossValidation(d.getInterpretations());
		List<CVIteration> folds=cV.obtainFolds();
		
		
		assertEquals(false,folds.get(0).getTest().equals(folds.get(0).getTraining().get(0)));
		assertEquals(false,folds.get(0).getTest().equals(folds.get(0).getTraining().get(1)));
		assertEquals(false,folds.get(0).getTest().equals(folds.get(0).getValidation().get(0)));
		assertEquals(false,folds.get(0).getTest().equals(folds.get(0).getValidation().get(1)));
		assertEquals(true,folds.get(0).getTest().equals(d.getInterpretations().get(0)));
		assertEquals(2,folds.get(0).getTraining().size());
		assertEquals(2,folds.get(0).getValidation().size());

		
		assertEquals(false,folds.get(1).getTest().equals(folds.get(1).getTraining().get(0)));
		assertEquals(false,folds.get(1).getTest().equals(folds.get(1).getTraining().get(1)));
		assertEquals(false,folds.get(1).getTest().equals(folds.get(1).getValidation().get(0)));
		assertEquals(false,folds.get(1).getTest().equals(folds.get(1).getValidation().get(1)));
		assertEquals(true,folds.get(1).getTest().equals(d.getInterpretations().get(1)));
		assertEquals(2,folds.get(1).getTraining().size());
		assertEquals(2,folds.get(1).getValidation().size());

		
		assertEquals(false,folds.get(2).getTest().equals(folds.get(2).getTraining().get(0)));
		assertEquals(false,folds.get(2).getTest().equals(folds.get(2).getTraining().get(1)));
		assertEquals(false,folds.get(2).getTest().equals(folds.get(2).getValidation().get(0)));
		assertEquals(false,folds.get(2).getTest().equals(folds.get(2).getValidation().get(1)));
		assertEquals(true,folds.get(2).getTest().equals(d.getInterpretations().get(2)));
		assertEquals(2,folds.get(2).getTraining().size());
		assertEquals(2,folds.get(2).getValidation().size());

		
		assertEquals(false,folds.get(3).getTest().equals(folds.get(3).getTraining().get(0)));
		assertEquals(false,folds.get(3).getTest().equals(folds.get(3).getTraining().get(1)));
		assertEquals(false,folds.get(3).getTest().equals(folds.get(3).getValidation().get(0)));
		assertEquals(false,folds.get(3).getTest().equals(folds.get(3).getValidation().get(1)));
		assertEquals(true,folds.get(3).getTest().equals(d.getInterpretations().get(3)));
		assertEquals(2,folds.get(3).getTraining().size());
		assertEquals(2,folds.get(3).getValidation().size());

		
		assertEquals(false,folds.get(4).getTest().equals(folds.get(4).getTraining().get(0)));
		assertEquals(false,folds.get(4).getTest().equals(folds.get(4).getTraining().get(1)));
		assertEquals(false,folds.get(4).getTest().equals(folds.get(4).getValidation().get(0)));
		assertEquals(false,folds.get(4).getTest().equals(folds.get(4).getValidation().get(1)));
		assertEquals(true,folds.get(4).getTest().equals(d.getInterpretations().get(4)));
		assertEquals(2,folds.get(4).getTraining().size());
		assertEquals(2,folds.get(4).getValidation().size());
	}
	
	
	
	
	
}
