package pl.bzip2;

import static org.junit.Assert.*;

import org.junit.Test;

public class BWTransformTest {

	@Test
	public void testEncodeAndDecode1() {
		String test = "dupa dupa dupa heheszki";
		BWTransform encoded = BWTransform.encode(test);
		String decoded = BWTransform.decode(encoded.getVector(), encoded.getStart());
		assertEquals(test, decoded);
	}

	@Test
	public void testEncodeAndDecode2() {
		String test = "Fajny projekt";
		BWTransform encoded = BWTransform.encode(test);
		String decoded = BWTransform.decode(encoded.getVector(), encoded.getStart());
		assertEquals(test, decoded);
	}

}
