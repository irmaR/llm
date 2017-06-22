package hybrid.dependencies;

import static org.junit.Assert.*;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.Mode;
import hybrid.features.Proportion;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestDepenendency {

	
	private static NetworkInfo ntw;
	private static Atom intelligence;
	private static Atom grade;
	private static Atom teaches;
	private static Atom ability;
	private static Atom takes;
	private static Atom difficulty;
	private static Logvar student;
	private static Logvar course;
	private static Logvar professor;
	
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
		ability=new Atom(ab,new Logvar[]{student});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
	}
	
	@Test
	public void testDependencyExtensions_not_succeed() throws FeatureTypeException, ConjunctionConstructionProblem{
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,new PosLiteral(ability)));
		Mode ft2=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(grade)));
		Proportion ft3=new Proportion(new Standard_Conjunction(intelligence,new PosLiteral(takes)));
        Dependency dep=new Dependency(intelligence,new Feature[]{ft1,ft2,ft3});
        try{
        	dep.extend(ft1);
        }
        catch(FeatureAlreadyExists e){
        	
        }
	}
	
	@Test
	public void testDependencyExtension1() throws FeatureTypeException, FeatureAlreadyExists, ConjunctionConstructionProblem{
		ValueFt ft1=new ValueFt(new Standard_Conjunction(intelligence,new PosLiteral(ability)));
		Mode ft2=new Mode(new Standard_Conjunction(intelligence,new PosLiteral(takes),new PosLiteral(grade)));
		Proportion ft3=new Proportion(new Standard_Conjunction(intelligence,new PosLiteral(takes)));
        Dependency dep=new Dependency(intelligence,new Feature[]{ft1,ft2});
        Dependency dep_test=new Dependency(intelligence,new Feature[]{ft1,ft2,ft3});
        Dependency new_dep=dep.extend(ft3);
        assertEquals(dep_test,new_dep);
	}
	
}
