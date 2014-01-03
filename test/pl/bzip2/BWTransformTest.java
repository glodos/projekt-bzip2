package pl.bzip2;

import static org.junit.Assert.*;

import org.junit.Test;

public class BWTransformTest {

	@Test
	public void testEncodeAndDecode1() {
		String test = "dupa dupa dupa heheszki";
		BWTransform encoded = BWTransform.encode(test.getBytes());
		String enc = new String(encoded.getVector());
		System.out.println(enc+", first: "+encoded.getStart());
		String decoded = new String(BWTransform.decode(encoded.getVector(), encoded.getStart()));
		System.out.println(decoded);
		assertEquals(test, decoded);
	}

	@Test
	public void testEncodeAndDecode2() {
		String test = "Fajny projekt ł";
		BWTransform encoded = BWTransform.encode(test.getBytes());
		String enc = new String(encoded.getVector());
		System.out.println(enc);
		String decoded = new String(BWTransform.decode(encoded.getVector(), encoded.getStart()));
		System.out.println(decoded);
		assertEquals(test, decoded);
	}
	
	@Test
	public void testEncodeAndDecode3() {
		String test = "ehehehehehe 90.,|]~~~]';d";
		BWTransform encoded = BWTransform.encode(test.getBytes());
		String enc = new String(encoded.getVector());
		System.out.println(enc);
		String decoded = new String(BWTransform.decode(encoded.getVector(), encoded.getStart()));
		System.out.println(decoded);
		assertEquals(test, decoded);
	}

}
