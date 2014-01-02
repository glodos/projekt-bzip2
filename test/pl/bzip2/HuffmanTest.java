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
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteFreqs() throws IOException {
		Huffman huff = new Huffman();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int[]freqs = new int[]{1, 2, 45678, 13232534, 2119999999, 0, 0, 0}; 
		huff.writeFreqs(out, freqs);
		byte[] output = out.toByteArray();
		assertEquals(freqs.length, decode(output, 0));
		int[] result = new int[freqs.length];
		for(int i = 0;i<output.length-4;i+=4){
			result[i/4] = decode(output, i+4);
		}
		assertArrayEquals(result, freqs);
	}
	
	@Test
	public void testReadFreqs() throws IOException{
		Huffman huff = new Huffman();
		byte [] input = new byte[]{0, 0, 0, 8, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, -78, 110, 
				0, -55, -23, -106, 126, 92, -95, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		ByteArrayInputStream in = new ByteArrayInputStream(input);
		int[] freqs = huff.readFreqs(in);
		int[] originalfreqs = new int[]{1, 2, 45678, 13232534, 2119999999, 0, 0, 0}; 
		assertArrayEquals(originalfreqs, freqs);
	}
	
	@Test
	public void testEncodeDecode() throws IOException{
		Huffman huff = new Huffman();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[]bytes = "ehehehehehe łąąććźżżżśś90.,|]~~~]';d".getBytes(); 
		huff.encode(bytes, output);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
		byte[] decoded = huff.decode(inputStream);
		assertArrayEquals(bytes, decoded);
	}
	
	@Test
	public void testEncodeDecode2() throws IOException{
		Huffman huff = new Huffman();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] block1 = "łąąććźżżżśś90.,|]~~~]';d".getBytes(); 
		byte[] block2 = "ehehehehehe".getBytes();
		huff.encode(block1, output);
		huff.encode(block2, output);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
		byte[] decoded1 = huff.decode(inputStream);
		byte[] decoded2 = huff.decode(inputStream);
		assertArrayEquals(block1, decoded1);
		assertArrayEquals(block2, decoded2);
	}


	private static int decode(byte[] bi, int position) {
		  return bi[position+3] & 0xFF | (bi[position+2] & 0xFF) << 8 |
		         (bi[position+1] & 0xFF) << 16 | (bi[position] & 0xFF) << 24;
		}
}
