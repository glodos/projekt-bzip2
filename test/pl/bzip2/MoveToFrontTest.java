package pl.bzip2;

import static org.junit.Assert.*;

import org.junit.Test;

public class MoveToFrontTest {

	@Test
	public void testEncodeDecode1() {
		String test = "dupa dupa dupa heheszki";
		char[] encoded = MoveToFront.encode(test.toCharArray());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

	@Test
	public void testEncodeDecode2() {
		String test = "Fajny projekt";
		char[] encoded = MoveToFront.encode(test.toCharArray());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

}
