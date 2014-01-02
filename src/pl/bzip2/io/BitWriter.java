package pl.bzip2.io;
import java.io.EOFException;
import java.io.IOException;

/**
 * Wrapper na tablicę bajtów pozwalający pisać bity
 */
public class BitWriter {
	
	private byte[] buffer;
	private int bufferPos;

	private static byte[] helper = {(byte)0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
	
	
	public BitWriter(int bufferSize) {
		buffer = new byte[bufferSize];
	}
	
	/**
	 * Zapisuje bit do bufora.
	 * @param bit 0 lub 1
	 * @throws IOException
	 */
	public void write(byte bit) throws IOException {
		if(bufferPos >= buffer.length * 8){
			throw new EOFException();
		}
		byte temp = helper[bufferPos%8];
		if (bit == 0) {
			buffer[bufferPos/8] &= (~temp);
		} else {
			buffer[bufferPos/8] |= temp;
		}
		bufferPos++;
	}
	
	/**
	 * Zapisuje tablicę bitów.
	 * @param bits tablica z wartościami 0 lub 1
	 * @throws IOException
	 * @see {@link #write(byte)}
	 */
	public void write(byte[] bits) throws IOException{
		for(int i = 0;i<bits.length;i++){
			write(bits[i]);
		}
	}
	
	public byte[] array(){
		return buffer;
	}
	
}
