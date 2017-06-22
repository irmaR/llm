package hybrid.cpdEvaluation;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import hybrid.core.Logarithm2;
import hybrid.cpds.GaussianPrior;
import hybrid.dependencies.Dependency;
import hybrid.dependencies.MarkovBlanket;
import hybrid.featureGenerator.AbstractConjunction;
import hybrid.features.Feature;
import hybrid.features.FeatureTypeException;
import hybrid.interpretations.Assignment;
import hybrid.interpretations.Domain;
import hybrid.interpretations.Interpretation;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.Constant;
import hybrid.network.GaussianPred;
import hybrid.network.GroundAtom;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.Subst;
import hybrid.network.SubstitutionException;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.parameters.BadProbabilityDistribution;
import hybrid.parameters.Gaussian;
import hybrid.parameters.PMF;
import hybrid.penalties.*; 
import hybrid.querydata.QueryData;

import org.junit.Before;
import org.junit.Test;

public class TestGaussianPriorEvaluator {
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
	public void calculateMeanandSTD() throws SubstitutionException{
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new NumberValue(4)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(0));
		MarkovBlanket mB2=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(1));
		MarkovBlanket mB3=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(2));
		MarkovBlanket mB4=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(3));
		MarkovBlanket mB5=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(4));
		MarkovBlanket mB6=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(5));
		MarkovBlanket mB7=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(6));
		QueryData qD=new QueryData(new Dependency(intelligence,new Feature[]{}));
		qD.addMarkovBlanket(i, mB1);
		qD.addMarkovBlanket(i, mB2);
		qD.addMarkovBlanket(i, mB3);
		qD.addMarkovBlanket(i, mB4);
		qD.addMarkovBlanket(i, mB5);
		qD.addMarkovBlanket(i, mB6);
		qD.addMarkovBlanket(i, mB7);
		GaussianPriorEvaluator cpd=new GaussianPriorEvaluator();
		Gaussian par=(Gaussian) cpd.estimateParameters(qD);
		assertEquals(4,par.getMean(),0.1);
		assertEquals(2.16,par.getStd(),0.1);
	}
	
	@Test
	public void getPrediction() throws SubstitutionException{
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(0));

		GaussianPriorEvaluator cpd=new GaussianPriorEvaluator();
		Gaussian par=new Gaussian(100,5);
		cpd.getPrediction(mB1, par);
		
		
	}
	
	@Test
	public void calculatePLL() throws SubstitutionException{
		Domain dom=new Domain(ntw.getTypes());
		Assignment asig=new Assignment();
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s1")}),new NumberValue(1)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s2")}),new NumberValue(2)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s3")}),new NumberValue(3)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s4")}),new NumberValue(4)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s5")}),new NumberValue(5)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s6")}),new NumberValue(6)));
		asig.addRandVar(intelligence, new GroundAtom(intelligence,new Subst(new Logvar[]{student},new Constant[]{new Constant("s7")}),new NumberValue(7)));
		Interpretation i=new Interpretation(dom,asig,pathToInterpretations);
		
		MarkovBlanket mB1=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(0));
		MarkovBlanket mB2=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(1));
		MarkovBlanket mB3=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(2));
		MarkovBlanket mB4=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(3));
		MarkovBlanket mB5=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(4));
		MarkovBlanket mB6=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(5));
		MarkovBlanket mB7=new MarkovBlanket(asig.getAssignmentFor(intelligence).get(6));
		QueryData qD=new QueryData(new Dependency(intelligence,new Feature[]{}));
		qD.addMarkovBlanket(i, mB1);
		qD.addMarkovBlanket(i, mB2);
		qD.addMarkovBlanket(i, mB3);
		qD.addMarkovBlanket(i, mB4);
		qD.addMarkovBlanket(i, mB5);
		qD.addMarkovBlanket(i, mB6);
		qD.addMarkovBlanket(i, mB7);
		GaussianPriorEvaluator cpd=new GaussianPriorEvaluator();
		Gaussian par=(Gaussian) cpd.estimateParameters(qD);
		GaussianPrior gP=new GaussianPrior(qD.getDep(),cpd,new Gaussian());
		gP.setParameters(par);
		assertEquals(-21.3865,cpd.calculatePLL(qD, (Gaussian) gP.getParameters(),new NoPenalty()).getScore(),0.001);
	}
	
	
}
