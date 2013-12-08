package pl.bzip2;

import static org.junit.Assert.*;

import org.junit.Test;

public class MoveToFrontTest {

	@Test
	public void testEncodeDecode1() {
		String test = "dupa dupa dupa heheszki";
		byte[] encoded = MoveToFront.encode(test.getBytes());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

	@Test
	public void testEncodeDecode2() {
		String test = "Fajny projekt";
		byte[] encoded = MoveToFront.encode(test.getBytes());
		String decoded = new String(MoveToFront.decode(encoded));
		assertEquals(test, decoded);
	}

}
