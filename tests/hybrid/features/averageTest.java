package hybrid.features;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;
import hybrid.featureGenerator.Standard_Conjunction;
import hybrid.featureGenerator.ConjunctionConstructionProblem;
import hybrid.features.Average;
import hybrid.features.FeatureTypeException;
import hybrid.network.Atom;
import hybrid.network.BooleanPred;
import hybrid.network.CategoricalPred;
import hybrid.network.GaussianPred;
import hybrid.network.Logvar;
import hybrid.network.NetworkInfo;
import hybrid.network.NumberValue;
import hybrid.network.PosLiteral;
import hybrid.network.Predicate;
import hybrid.network.Type;
import hybrid.network.Value;
import hybrid.queryMachine.ArrayFeatureValues;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;


public class averageTest {
	
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
		Standard_Conjunction c=new Standard_Conjunction(head,p);
		Average avg=new Average(c);
		assertEquals(5.5, ((NumberValue)avg.processValue(new ArrayFeatureValues(values))).getNumber(),0.2);
	}
	
	
}
