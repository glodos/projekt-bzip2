package pl.bzip2.io;
import java.io.EOFException;
import java.io.IOException;


/**
 * Wrapper na tablicę bajtów, który pozwala na czytanie jej w postaci bitów
 */
public class BitReader {
	private byte[] buffer;
	private int bufferPos;
	
	private static byte[] helper = {(byte)0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
	
	public BitReader(byte[] buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * Czyta kolejny bit z wejścia.
	 * @return przeczytany bit, 0 lub 1
	 * @throws IOException jeśli wszystko zostało przeczytane
	 */
	public byte read() throws IOException {
		if (bufferPos/8 == buffer.length) {
			throw new EOFException();
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

    /**
     * Zwraca liczbę pozostałych bitów
     * @return 
     */
    public int available(){
    	return buffer.length*8 - bufferPos;
    }
	
}
