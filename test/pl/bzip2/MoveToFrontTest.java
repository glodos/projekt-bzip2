package pl.bzip2;

import static org.junit.Assert.*;

import org.junit.Test;

public class MoveToFrontTest {

	@Test
	public void testEncodeDecode1() {
		String test = "dupa dupa dupa heheszki łłłłąąąąććć";
		byte[] encoded = MoveToFront.encode(test.getBytes());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

	@Test
	public void testEncodeDecode2() {
		String test = "Fajny projekt łłłłąąąąććć";
		byte[] encoded = MoveToFront.encode(test.getBytes());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}
	
	@Test
	public void testEncodeDecode3() {
		String test = "ehehehehehe łąąććźżżżśś90.,|]~~~]';d";
		byte[] encoded = MoveToFront.encode(test.getBytes());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

}
