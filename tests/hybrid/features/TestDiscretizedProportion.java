package hybrid.features;

import static org.junit.Assert.*;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Type;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestDiscretizedProportion {
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Logvar student;
	private static Logvar course;
	private static Logvar professor;
	private static Dependency dep1;
	private static Dependency dep2;
	
	/**
	 * Creating very simple network of only grade and intelligence predicate. 
	 * Testing on two dependencies:
	 * intelligence(S) | Mode{grade(S,c)}
	 * grade(S,C) | Value{intelligence(S)}
	 * @throws FeatureTypeException 
	 */
	@BeforeClass
	public static void setUp() throws FeatureTypeException{
		Type stud=new Type("student");
		Type c=new Type("course");
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
		intelligence=new Atom(intel, new Logvar[]{student});
		grade=new Atom(gr, new Logvar[]{student,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
	}
	
	@Test
	public void test_ExtractDiscretizationLevels() throws ConjunctionConstructionProblem{
		DiscretizedProportion prop=new DiscretizedProportion(new Standard_Conjunction(difficulty,new Literal(takes)),2);
		ArrayList<DiscretizedProportion.Level> levels=prop.extractDiscretizationLevels();
		assertEquals(0.0,levels.get(0).getMin(),0.01);
		assertEquals(50.0,levels.get(0).getMax(),0.01);
		assertEquals(51.0,levels.get(1).getMin(),0.01);
		assertEquals(100.0,levels.get(1).getMax(),0.01);
		assertEquals("lev1",levels.get(0).getBin_name());
		assertEquals("lev2",levels.get(1).getBin_name());
	}
	
	@Test
	public void test_getDiscretizedCountProportion() throws ConjunctionConstructionProblem{
		DiscretizedProportion prop=new DiscretizedProportion(new Standard_Conjunction(difficulty,new Literal(takes)),2);
		ArrayList<DiscretizedProportion.Level> levels=prop.extractDiscretizationLevels();
		System.out.println(levels);
		prop.setDiscretization_levels(levels);
		assertEquals(new StringValue("lev1"),prop.getDiscretizedCountProportion(levels, 20, 40,2));
		assertEquals(new StringValue("lev2"),prop.getDiscretizedCountProportion(levels, 35, 40,2));

	}
	
}
