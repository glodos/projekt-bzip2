package pl.bzip2;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.bzip2.io.BitReader;
import pl.bzip2.io.BitWriter;

public class HuffmanTest {
	
	byte[] input;
	byte[] expectedOutput;

	@Before
	public void setUp() throws Exception {
		input = new byte[]{1, 1, 1, 1, 1, 1, 2, 3, 4, 4};
		expectedOutput = new byte[]{0, 0, 0, 9, 1, 1, 1, 1, 4, 0, 0, 2, 3, 0, 0, 0, 10, 0, 0, 0, 2, (byte)252, 21};
	}

	@After
	public void tearDown() throws Exception {
		input = null;
	}

	@Test
	public void testEncode() throws IOException {
		Huffman huff = new Huffman();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		huff.encode(input, new BitWriter(out));
		byte[] output = out.toByteArray();
		assertArrayEquals(expectedOutput, output);
	}

	@Test
	public void testDecode() throws IOException {
		Huffman huff = new Huffman();
		ByteArrayInputStream in = new ByteArrayInputStream(expectedOutput);
		byte[] output = huff.decode(new BitReader(in));
		assertArrayEquals(input, output);
	}

}
