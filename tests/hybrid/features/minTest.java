package hybrid.features;
import static org.junit.Assert.*;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.FeatureTypeException;
import hybrid.features.Max;
import hybrid.features.Min;
import hybrid.network.Atom;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NumberValue;
import hybrid.network.PosLiteral;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.queryMachine.ArrayFeatureValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;


public class minTest {
static ArrayList<Value> values;
	
	@BeforeClass
	public static void setUp(){
		values=new ArrayList<Value>();
		for(int i=1;i<=10;i++){
			values.add(new NumberValue(i));
		}
	}
	
	@Test
	public void testBooleanPreds() throws FeatureTypeException, ConjunctionConstructionProblem{
		PosLiteral p=new PosLiteral(new Atom(new GaussianPred("dummy",1,1.0,2.0),Arrays.asList(new Logvar("P",new Type("puppy")))));
		Atom head=new Atom(new GaussianPred("dummy_head",1,1.0,2.0),Arrays.asList(new Logvar("P",new Type("puppy"))));
		Standard_Conjunction c=new Standard_Conjunction(head,p);		Min min=new Min(c);
		assertEquals(1.0, ((NumberValue)min.processValue(new ArrayFeatureValues(values))).getNumber(),0.2);
	}
}
