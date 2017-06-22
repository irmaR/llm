package hybrid.network;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNumberValue {

	@Test
	public void testEquals(){
		NumberValue nm1=new NumberValue(105.121);
		NumberValue nm2=new NumberValue(105.1211);
		assertEquals(true,nm1.equals(nm2));
	}
	
}
