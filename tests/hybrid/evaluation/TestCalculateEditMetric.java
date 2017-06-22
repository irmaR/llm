package hybrid.evaluation;


import static org.junit.Assert.assertEquals;
import hybrid.dependencies.Dependency;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Exist;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.features.ValueFt;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.Test;

public class TestCalculateEditMetric {
	private static NetworkInfo ntw;
	private static String pathToInterpretations=System.getProperty("user.dir")+"/tests/test_data/small_university";
	private static Atom intelligence;
	private static Atom intelligence1;
	private static Atom grade;
	private static Atom grade1;
	private static TestRandvarValue grade1_randvar;
	private static TestRandvarValue satisfaction_low;
	private static Atom teaches;
	private static Atom ability;
	private static Atom nrhours;
	private static Atom nrhours1;
    private static Atom takes;
    private static Atom satisfaction;
	private static Atom difficulty;
	private static Atom friend;
	
	private static PosLiteral intelligenceP;
	private static PosLiteral intelligenceP1;
	private static PosLiteral gradeP;
	private static PosLiteral nrhoursP;
	private static PosLiteral gradeP1;
	private static PosLiteral gradeP1randvar;
	private static PosLiteral teachesP;
	private static PosLiteral abilityP;
    private static PosLiteral takesP;
    private static PosLiteral takesP1;
	private static PosLiteral difficultyP;
	private static PosLiteral friendP;
	
	private static Logvar student;
	private static Logvar student1;
	private static Logvar course;
	private static Logvar course1;
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
		course1=new Logvar("C1",c);
		professor=new Logvar("P",p);
		Predicate intel=new GaussianPred("intelligence",1,1.0,2.0);
		Predicate gr=new CategoricalPred("grade",2,new String[]{"low","mid","high"});
		Predicate sat=new CategoricalPred("satisfaction",2,new String[]{"low","mid","high"});
		Predicate tk=new BooleanPred("takes",2);
		Predicate ab=new GaussianPred("ability",1,1.0,2.0);
		Predicate nrh=new GaussianPred("nrhours",1,1.0,2.0);
		Predicate tch=new BooleanPred("teaches",2);
		Predicate fr=new BooleanPred("friend",2);
		Predicate diff=new CategoricalPred("difficulty",1,new String[]{"easy","medium","hard"});
		intelligence=new Atom(intel, new Logvar[]{student});
		intelligence1=new Atom(intel, new Logvar[]{student1});
		grade=new Atom(gr, new Logvar[]{student,course});
		satisfaction=new Atom(sat, new Logvar[]{student,course});
		grade1=new Atom(gr, new Logvar[]{student1,course});
		takes=new Atom(tk, new Logvar[]{student,course});
		Atom takes1 = new Atom(tk, new Logvar[]{student1,course});
		ability=new Atom(ab,new Logvar[]{professor});
		nrhours=new Atom(nrh,new Logvar[]{course});
		nrhours1=new Atom(nrh,new Logvar[]{course1});
		teaches=new Atom(tch,new Logvar[]{professor,course});
		difficulty=new Atom(diff,new Logvar[]{course});
		friend=new Atom(fr,new Logvar[]{student,student1});
		grade1_randvar=new TestRandvarValue(gr, grade1.getArguments(), new StringValue("low"));
		satisfaction_low=new TestRandvarValue(sat, satisfaction.getArguments(), new StringValue("low"));
		
		intelligenceP1=new PosLiteral(intelligence1);
		intelligenceP=new PosLiteral(intelligence);
		gradeP=new PosLiteral(grade);
		gradeP1=new PosLiteral(grade1);
		gradeP1randvar=new PosLiteral(grade1);
		takesP=new PosLiteral(takes);
		takesP1=new PosLiteral(takes1);
		abilityP=new PosLiteral(ability);
		teachesP=new PosLiteral(teaches);
		difficultyP=new PosLiteral(difficulty);
		friendP=new PosLiteral(friend);
		nrhoursP=new PosLiteral(nrhours);
		
		ntw=new NetworkInfo(new Atom[]{intelligence,grade,takes,teaches,ability,difficulty},new Type[]{stud,c,p});	    
	}
	
	@Test
	public void calculateEdtiMetric(){
		CalculateEditMetric ced=new CalculateEditMetric();
		Dependency true_dep=new Dependency(ability,new Feature[]{});
		Dependency learned_dep=new Dependency(ability,new Feature[]{});
        assertEquals(0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric1() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		Dependency true_dep=new Dependency(difficulty,new Feature[]{});
		Dependency learned_dep=new Dependency(difficulty,new Feature[]{new ValueFt(new Standard_Conjunction(difficulty,nrhoursP))});
        assertEquals(2.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric2() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		Dependency true_dep=new Dependency(difficulty,new Feature[]{});
		System.out.println("Dependency "  +new Standard_Conjunction(difficulty,new PosLiteral(nrhours)));

		Dependency learned_dep=new Dependency(difficulty,new Feature[]{new ValueFt(new Standard_Conjunction(difficulty,new PosLiteral(nrhours)))});
        assertEquals(2.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric3() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		ValueFt value_of_intelligence=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(difficulty)));
		ValueFt value_of_satisfactions=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(satisfaction)));
	
		ValueFt value_of_intelligence1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(difficulty)));
		ValueFt value_of_satisfactions1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(takes),new PosLiteral(satisfaction)));
		
		Dependency true_dep=new Dependency(grade,new Feature[]{value_of_intelligence,value_of_difficulty});
		Dependency learned_dep=new Dependency(grade,new Feature[]{value_of_intelligence1,value_of_difficulty1,value_of_satisfactions1});
        assertEquals(3.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric4() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		ValueFt value_of_intelligence=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(nrhours)));
		ValueFt value_of_satisfactions=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(satisfaction)));
	
		ValueFt value_of_intelligence1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(nrhours)));
		ValueFt value_of_satisfactions1=new ValueFt(new Standard_Conjunction(grade,new PosLiteral(takes),new PosLiteral(satisfaction)));
		Dependency true_dep=new Dependency(grade,new Feature[]{value_of_intelligence,value_of_difficulty});
		Dependency learned_dep=new Dependency(grade,new Feature[]{value_of_intelligence1,value_of_difficulty1,value_of_satisfactions1});
		assertEquals(3.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric5() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		ValueFt value_of_intelligence=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(difficulty)));
		ValueFt value_of_satisfactions=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(satisfaction)));
	    Exist exists_satisfaction_low=new Exist(new Standard_Conjunction(takes,new PosLiteral(satisfaction_low)));
		
		Dependency true_dep=new Dependency(takes,new Feature[]{value_of_intelligence,value_of_difficulty});
		Dependency learned_dep=new Dependency(takes,new Feature[]{value_of_satisfactions,exists_satisfaction_low});
        assertEquals(3.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	@Test
	public void calculateEdtiMetric6() throws ConjunctionConstructionProblem{
		CalculateEditMetric ced=new CalculateEditMetric();
		ValueFt value_of_intelligence=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(intelligence)));
		ValueFt value_of_difficulty=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(difficulty)));
		ValueFt value_of_nrhours=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(nrhours)));
		ValueFt value_of_satisfactions=new ValueFt(new Standard_Conjunction(takes,new PosLiteral(satisfaction)));
	    Exist exists_satisfaction_low=new Exist(new Standard_Conjunction(takes,new PosLiteral(satisfaction_low)));
		
		Dependency true_dep=new Dependency(takes,new Feature[]{value_of_intelligence,value_of_difficulty});
		Dependency learned_dep=new Dependency(takes,new Feature[]{value_of_satisfactions,exists_satisfaction_low,value_of_nrhours});
        assertEquals(4.0,ced.findDistance(true_dep, learned_dep),0.00001);
	}
	
	
	
	}
	

