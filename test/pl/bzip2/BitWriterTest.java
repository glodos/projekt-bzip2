package pl.bzip2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

public class BitWriterTest {

	@Test
	public void testFlush() {
	}

	@Test
	public void testWrite() throws IOException {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);
		BitWriter w = new BitWriter(out);
		//w.write((byte)1);
		w.write(new byte[]{0, 0, 0, 0, 0, 0, 0, 1});
		w.flush();
		byte[] input = new byte[10];
		int read = in.read(input);
		w.close();
		in.close();
		assertTrue(read==1);
		assertTrue(input[0]==1);
	}

}
