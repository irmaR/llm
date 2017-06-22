package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hybrid.core.GenerateUniqueIDforAtom;
import hybrid.dependencies.Dependency;
import hybrid.features.FeatureTypeException;
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

public class TestProposedConjunction {
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
	@Before
	public void setUp() throws FeatureTypeException{
		GenerateUniqueIDforAtom.reset();
		System.out.println(pathToInterpretations);
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
	public void testProposedConjunctionConstructor() throws Exception{
		List<Literal> literals=new ArrayList<Literal>();
		literals.add(new PosLiteral(takes));
		literals.add(new PosLiteral(friend));
		ProposedConjunction p=new ProposedConjunction(literals, 3);
		System.out.println(p);
	}
	
	
	@Test
	public void testhasDuplicateBooleanPredicates() throws Exception{
		ProposedConjunction p=new ProposedConjunction(new PosLiteral(takes), 3);
		assertEquals(true,p.setDuplicateBoolean(new PosLiteral(takes)));
	}
	
	@Test
	public void testNoRenaming() throws Exception{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(takes), 3);
		ProposedConjunction p=init.extend(new PosLiteral(grade));
		assertEquals(true,p.getRenaming().getRenaming().size()==0);
	}
	
	@Test
	public void testNoRenaming1() throws Exception{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(takes), 3);
		ProposedConjunction p=init.extend(new PosLiteral(friend));
		assertEquals(false,p.getRenaming()==null);
	}
	
	@Test
	public void testRenaming1() throws Exception{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(friend), 5);
		ProposedConjunction p=init.extend(new PosLiteral(friend));
		ProposedConjunction p1=p.extend(new PosLiteral(friend));
		System.out.println(p1);
		assertEquals(5,p1.getRenaming().getRenaming().get(friend.getArgument(0).getType()).size());
	}
	
	@Test 
	public void testGettingBindingLogvars() throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(takes), 3);
		ProposedConjunction p=init.extend(new PosLiteral(friend));
		Set<Type> bindingVars=new HashSet<Type>();
		bindingVars.add(friend.getArguments().get(0).getType());
		bindingVars.add(friend.getArguments().get(1).getType());
		bindingVars.add(takes.getArguments().get(0).getType());
		bindingVars.add(takes.getArguments().get(1).getType());
		assertEquals(bindingVars,p.getBindingTypes());
	}
	
	@Test
	public void testhasCommonTypes(){
		ProposedConjunction init =new ProposedConjunction(new PosLiteral(teaches), 3);
		assertEquals(false,init.hasCommonTypes(intelligence));
	}
	
	@Test
	public void testhasCommonTypesTrue(){
		ProposedConjunction init = new ProposedConjunction(new PosLiteral(teaches), 3);
		assertEquals(true,init.hasCommonTypes(difficulty));
	}
	
	
	@Test
	public void testExtenstion2() throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{
		Type stud=new Type("student");
		Type c=new Type("course");
		Type p=new Type("prof");
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(friend), 3);
		AtomCombinationCreator testClass=new AtomCombinationCreator();
		ProposedConjunction proba=init.extend(new PosLiteral(friend)).extend(new PosLiteral(friend));	
		HashMap<Type,HashSet<Logvar>> testResult=new HashMap<Type, HashSet<Logvar>>();
		HashSet<Logvar> set=new HashSet<Logvar>();
		set.add(new Logvar("S",stud));
		set.add(new Logvar("S2",stud));
		set.add(new Logvar("S1",stud));
		testResult.put(stud, set);
		assertEquals(testResult, proba.getRenaming().getRenaming());
	}
	
	@Test(expected=NoCommonTypeWithCurrentProposedConjunction.class)
	public void testExtenstion3() throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(teaches), 3);
        init.extend(new PosLiteral(intelligence));		
	}
	
	@Test(expected=DuplicateAtom.class)
	public void testExtenstion4() throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(intelligence), 3);
		init.extend(new PosLiteral(intelligence));
	}
	
	@Test(expected=NoCommonTypeWithCurrentProposedConjunction.class)
	public void testExtenstion5() throws NoCommonTypeWithCurrentProposedConjunction, DuplicateAtom{
		ProposedConjunction init=new ProposedConjunction(new PosLiteral(intelligence), 3);
		init.extend(new PosLiteral(ability));
	}
}
