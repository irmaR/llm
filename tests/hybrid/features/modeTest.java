package hybrid.features;
import static org.junit.Assert.*;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Mode;
import hybrid.network.Atom;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NumberValue;
import hybrid.network.PosLiteral;
import hybrid.network.StringValue;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.queryMachine.HashMapFeatureValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;


public class modeTest {
	static ArrayList<Integer> values;
	static ArrayList<Value> Stringvalues;
	static HashMap<String,Integer> counts;
	
	@BeforeClass
	public static void setUp(){
		counts=new HashMap<String,Integer>();
		counts.put("low", 2);
		counts.put("high", 3);
		
		
		
		
		values=new ArrayList<Integer>();
	    values.add(new Integer(1));
	    values.add(new Integer(2));
	    values.add(new Integer(3));
	    values.add(new Integer(2));
	    values.add(new Integer(2));
	    values.add(new Integer(4));
	    
	    Stringvalues=new ArrayList<Value>();
	    Stringvalues.add(new StringValue("low"));
	    Stringvalues.add(new StringValue("low"));
	    Stringvalues.add(new StringValue("high"));
	    Stringvalues.add(new StringValue("high"));
	    Stringvalues.add(new StringValue("high"));

	}

	
	@Test
	public void testModeStringValues() throws FeatureTypeException, ConjunctionConstructionProblem{
		PosLiteral p=new PosLiteral(new Atom(new GaussianPred("dummy",1,1.0,2.0),Arrays.asList(new Logvar("P",new Type("puppy")))));
		Atom head=new Atom(new GaussianPred("dummy_head",1,1.0,2.0),Arrays.asList(new Logvar("P",new Type("puppy"))));
		Standard_Conjunction c=new Standard_Conjunction(head,p);		Mode mode=new Mode(c);
		HashMapFeatureValue fv=new HashMapFeatureValue(counts);
		assertEquals(new StringValue("high"), mode.processValue(fv));
	}
}
