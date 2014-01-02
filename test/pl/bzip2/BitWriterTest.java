package pl.bzip2;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import pl.bzip2.io.BitWriter;

public class BitWriterTest {

	@Test
	public void testFlush() {
	}

	@Test
	public void testWrite() throws IOException {
		BitWriter w = new BitWriter(4);
		//2116456356
		byte[] num = new byte[]{0,1,1,1,1,1,1,0,0,0,1,0,0,1,1,0,1,0,0,0,1,1,1,1,1,0,1,0,0,1,0,0};
		w.write(num);
		byte[] input = w.array();
		//input should be [126, 38, -113, -92]
		assertTrue(decode(input)==2116456356);
	}
	
	private static int decode(byte[] bi) {
		  return bi[3] & 0xFF | (bi[2] & 0xFF) << 8 |
		         (bi[1] & 0xFF) << 16 | (bi[0] & 0xFF) << 24;
		}

}
