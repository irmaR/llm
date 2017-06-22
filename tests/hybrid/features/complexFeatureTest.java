package hybrid.features;

import hybrid.network.NumberValue;
import hybrid.network.Value;

import java.util.ArrayList;

import org.junit.BeforeClass;

public class complexFeatureTest {

	
static ArrayList<Value> values;
	
	@BeforeClass
	public static void setUp(){
		values=new ArrayList<Value>();
		for(int i=1;i<=10;i++){
			values.add(new NumberValue(i));
		}
	}
	
}
