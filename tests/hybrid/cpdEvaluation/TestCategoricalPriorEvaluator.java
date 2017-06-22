package hybrid.cpdEvaluation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import hybrid.core.Logarithm2;
import hybrid.cpds.ProbabilityMassFunction;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.interpretations.Assignment;
import hybrid.interpretations.Domain;
import hybrid.interpretations.Interpretation;
import hybrid.network.*;
import hybrid.parameters.BadProbabilityDistribution;
import hybrid.parameters.PMF;
import hybrid.penalties.NoPenalty;
import hybrid.querydata.QueryData;

public class TestCategoricalPriorEvaluator {
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
	public void calculateProbabilities_with_Laplace_correction(){
		HashMap<Value,Long> counts=new HashMap<Value,Long>();
		counts.put(new StringValue("class1"), new Long(0));
		counts.put(new StringValue("class2"), new Long(990));
		counts.put(new StringValue("class3"), new Long(10));
		long nr_samples=1000;
		ProbabilityMassFunctionEvaluator cpd=new ProbabilityMassFunctionEvaluator();
		HashMap<Value,Double> result=cpd.calculate_and_normalize_probs(new ArrayList(counts.keySet()),counts, nr_samples);
		assertEquals(0.0109,result.get(new StringValue("class3")),0.2);
		assertEquals(0.988,result.get(new StringValue("class2")),0.2);
		assertEquals(9.970089730807576E-4,result.get(new StringValue("class1")),0.000001);
	}
	
	@Test
	public void sumToOneProbs(){
		HashMap<Value,Long> counts=new HashMap<Value,Long>();
		counts.put(new StringValue("class1"), new Long(0));
		counts.put(new StringValue("class2"), new Long(990));
		counts.put(new StringValue("class3"), new Long(10));
		int nr_samples=1000;
		System.out.println(counts.size());
		ProbabilityMassFunctionEvaluator cpd=new ProbabilityMassFunctionEvaluator();
		HashMap<Value,Double> result=cpd.calculate_and_normalize_probs(new ArrayList(counts.keySet()),counts, nr_samples);
		double res=0;
		for(Value v:result.keySet()){
			res+=result.get(v);
		}
		assertEquals(1.0,res,0.1);
		}
	
	@Test
	public void testPLLCalculation() throws SubstitutionException, BadProbabilityDistribution{
		HashMap<Value,Double> prob=new HashMap<Value,Double>();
		prob.put(new StringValue("low"), 0.2);
		prob.put(new StringValue("mid"), 0.2);
		prob.put(new StringValue("high"), 0.6);

		ProbabilityMassFunctionEvaluator cpd=new ProbabilityMassFunctionEvaluator();
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c1")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c2")}),new StringValue("low")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c3")}),new StringValue("mid")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c4")}),new StringValue("mid")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c5")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c6")}),new StringValue("high")));
		asig.addRandVar(grade, new GroundAtom(grade,new Subst(new Logvar[]{student,course},new Constant[]{new Constant("s1"),new Constant("c7")}),new StringValue("high")));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(grade).get(0));
		MarkovBlanket mB2=new MarkovBlanket(asig.getAssignmentFor(grade).get(1));
		MarkovBlanket mB3=new MarkovBlanket(asig.getAssignmentFor(grade).get(2));
		MarkovBlanket mB4=new MarkovBlanket(asig.getAssignmentFor(grade).get(3));
		MarkovBlanket mB5=new MarkovBlanket(asig.getAssignmentFor(grade).get(4));
		MarkovBlanket mB6=new MarkovBlanket(asig.getAssignmentFor(grade).get(5));
		MarkovBlanket mB7=new MarkovBlanket(asig.getAssignmentFor(grade).get(6));
		QueryData qD=new QueryData(new Dependency(grade,new Feature[]{}));
		qD.addMarkovBlanket(i, mB1);
		qD.addMarkovBlanket(i, mB2);
		qD.addMarkovBlanket(i, mB3);
		qD.addMarkovBlanket(i, mB4);
		qD.addMarkovBlanket(i, mB5);
		qD.addMarkovBlanket(i, mB6);
		qD.addMarkovBlanket(i, mB7);
		double pll_expected=Logarithm2.logarithm2(0.2)+Logarithm2.logarithm2(0.2)+Logarithm2.logarithm2(0.2)+Logarithm2.logarithm2(0.2)+Logarithm2.logarithm2(0.6)+Logarithm2.logarithm2(0.6)+Logarithm2.logarithm2(0.6);
        ProbabilityMassFunction cp=new ProbabilityMassFunction(qD.getDep(),new ProbabilityMassFunctionEvaluator(),new PMF(qD.getDep()));
        cp.setParameters(new PMF(qD.getDep(),grade, prob,0.1));
        qD.getDep().setCpd(cp);
		assertEquals(pll_expected,cpd.calculatePLL(qD, cp.getParameters(),new NoPenalty()).getScore(),0.001);
        }
	
	@Test
	public void sample(){
		HashMap<Value,Double> probs=new HashMap<Value,Double>();
		probs.put(new StringValue("low"), 0.2);
		probs.put(new StringValue("mid"), 0.2);
		probs.put(new StringValue("high"),0.6);
		
		ProbabilityMassFunctionEvaluator cpd=new ProbabilityMassFunctionEvaluator();
		
		}
	
	
	
}
