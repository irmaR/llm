package hybrid.network;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringValueTest {

	@Test
	public void equalsbooleanValue(){
		StringValue val=new StringValue("true");
		BoolValue boolVal=new BoolValue(true);
		assertEquals(true,val.equals(boolVal));
		assertEquals(true,boolVal.equals(val));
	}
	
}
