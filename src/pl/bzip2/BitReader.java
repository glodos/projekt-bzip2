package pl.bzip2;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

import org.omg.CORBA.PRIVATE_MEMBER;



public class BitReader {
	

	private InputStream input;
	private byte[] buffer;
	private int bufferLength;
	private int bufferPos;
	
	private static final int BUFFER_LENGTH = 1024*8;
	private static byte[] helper = {(byte)0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
	
	public BitReader(InputStream input) {
		this.input = input;
		buffer = new byte[BUFFER_LENGTH];
	}
	
	
	public byte read() throws IOException {
		if (bufferPos/8 == bufferLength) {
			bufferLength = input.read(buffer);
			if (bufferLength == 0) {
				throw new EOFException();
			}
			bufferPos = 0;
		}
		
		// read the bit
		byte temp = helper[bufferPos%8];
		byte result;
		if ((buffer[bufferPos/8] & temp) != 0) {
			result = 1;
		} else {
			result = 0;
		}
		bufferPos++;
		return result;
	}
	
	public void close() throws IOException{
		input.close();
	}
	
}
