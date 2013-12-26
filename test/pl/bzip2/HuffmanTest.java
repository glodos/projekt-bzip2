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
		BitWriter w = new BitWriter(out);
		int[]freqs = new int[]{1, 2, 45678, 13232534, 2119999999, 0, 0, 0}; 
		huff.writeFreqs(w, freqs);
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
		BitReader r = new BitReader(in);
		int[] freqs = huff.readFreqs(r);
		int[] originalfreqs = new int[]{1, 2, 45678, 13232534, 2119999999, 0, 0, 0}; 
		assertArrayEquals(originalfreqs, freqs);
	}
	
	@Test
	public void testEncodeDecode() throws IOException{
		Huffman huff = new Huffman();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		BitWriter w = new BitWriter(output);
		byte[]bytes = "ehehehehehe łąąććźżżżśś90.,|]~~~]';d".getBytes(); 
		huff.encode(bytes, w);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
		BitReader r = new BitReader(inputStream);
		byte[] decoded = huff.decode(r);
		assertArrayEquals(bytes, decoded);
	}


	private static int decode(byte[] bi, int position) {
		  return bi[position+3] & 0xFF | (bi[position+2] & 0xFF) << 8 |
		         (bi[position+1] & 0xFF) << 16 | (bi[position] & 0xFF) << 24;
		}
}
