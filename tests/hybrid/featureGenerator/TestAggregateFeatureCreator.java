package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import hybrid.dependencies.Dependency;
import hybrid.features.Average;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.features.Mode;
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

import org.junit.Before;
import org.junit.Test;

public class TestAggregateFeatureCreator {

	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
    private static Atom takes;
    private static Atom takes1;
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
		takes1=new Atom(tk, new Logvar[]{student1,course});
		ability=new Atom(ab,new Logvar[]{professor});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student1});
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});   
	}
	
	@Test
	public void testExistFeature() throws FeatureTypeException, ConjunctionConstructionProblem{
		AggregateFeatureCreator aFC=new AggregateFeatureCreator();
		Standard_Conjunction con=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(takes1),new PosLiteral(friend)));
		List<Feature> fts= (List<Feature>) aFC.getallAggregateFeatures(con);
		assertEquals(1, fts.size());
		Exist e=new Exist(con);
		assertEquals(true,fts.contains(e));
		
	}
	
	@Test
	public void testContinuousAggregations() throws FeatureTypeException, ConjunctionConstructionProblem{
		AggregateFeatureCreator aFC=new AggregateFeatureCreator();
		Standard_Conjunction con=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new PosLiteral(teaches),new Literal(ability)));
		List<Feature> fts= (List<Feature>) aFC.getallAggregateFeatures(con);
		assertEquals(3, fts.size());
		System.out.println(fts);
		Average avg=new Average(con);
		Max max=new Max(con);
		Min min=new Min(con);
		assertEquals(true, fts.contains(avg));
		assertEquals(true, fts.contains(max));
		assertEquals(true, fts.contains(min));
		
	}
	
	@Test
	public void testMode() throws FeatureTypeException, ConjunctionConstructionProblem{
		AggregateFeatureCreator aFC=new AggregateFeatureCreator();
		Standard_Conjunction con=new Standard_Conjunction(intelligence,Arrays.asList(new PosLiteral(takes),new Literal(grade)));
		List<Feature> fts= (List<Feature>) aFC.getallAggregateFeatures(con);
		assertEquals(1, fts.size());
		System.out.println(fts);
		Mode mode=new Mode(con);
		assertEquals(true, fts.contains(mode));
		
	}
	
}
