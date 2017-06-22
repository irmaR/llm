package hybrid.featureGenerator;

import static org.junit.Assert.*;

import java.util.List;

import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.GaussianPred;
import hybrid.network.Literal;
import hybrid.network.Logvar;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.StringValue;
import hybrid.network.TestRandvarValue;
import hybrid.network.Type;

import org.junit.Before;
import org.junit.Test;

public class TestAtomCombinationCreator {

	private static Atom trusts;
	private static Atom knows;
	private static Atom intelligence;
	private static TestRandvarValue intelligence_s1;
	private static Logvar person;
	private static Logvar person1;

	
	@Before
	public void setUp(){
		Type p=new Type("person");
		person=new Logvar("p",p);
		person1=new Logvar("p1",p);
		Predicate tr=new BooleanPred("trusts",2);
		Predicate intelligence_pred=new GaussianPred("intelligence",1,0,180);
		Predicate kn=new BooleanPred("knows",2);
		trusts=new Atom(tr,new Logvar[]{person,person});
		knows=new Atom(kn,new Logvar[]{person,person});
		intelligence=new Atom(intelligence_pred,new Logvar[]{person});
		intelligence_s1=new TestRandvarValue(intelligence_pred, intelligence.getArguments(), new StringValue("int0"));

	}
	
	@Test
	public void test_getallPossibleCombinationUpToLengthOnlyPositiveLiteral(){
		AtomCombinationCreator a=new AtomCombinationCreator();
		List<ProposedConjunction> combs=a.getallPossibleCombinationUpToLength(new Literal[]{new Literal(trusts),new Literal(knows)},3, trusts, 3,2);
		System.out.println(combs);
		//all renamings should be of maximum length 3
		for(ProposedConjunction p:combs){
			if(p.getAtomList().size()>2){
			assertEquals(3,p.getRenaming().getRenaming().get(person.getType()).size());
			}
		}
	}
	
	@Test
	public void test_getallPossibleCombinationUpTo_friend(){
		AtomCombinationCreator a=new AtomCombinationCreator();
		List<ProposedConjunction> combs=a.getallPossibleCombinationUpToLength(new Literal[]{new Literal(trusts),new Literal(knows)},3, trusts, 3,2);
		//all renamings should be of maximum length 3
		for(ProposedConjunction p:combs){
			if(p.getAtomList().size()>2){
			assertEquals(3,p.getRenaming().getRenaming().get(person.getType()).size());
			}
		}
	}
	
	@Test
	public void test_unary_combinator_friend(){
		AtomCombinationCreator a=new AtomCombinationCreator();
		List<ProposedConjunction> list=a.getAllUnaryProposedConjunctions(new PosLiteral[]{new PosLiteral(intelligence_s1),new PosLiteral(intelligence)}, knows, 2);
		assertEquals(true,(list.size()==2));
	}
	
	@Test
	public void test_unary_combinator(){
		AtomCombinationCreator a=new AtomCombinationCreator();
		List<ProposedConjunction> list=a.getAllUnaryProposedConjunctions(new PosLiteral[]{new PosLiteral(intelligence_s1),new PosLiteral(intelligence)}, intelligence, 2);
		System.out.println(list);
		assertEquals(true,(list.size()==0));
	}
	
}
