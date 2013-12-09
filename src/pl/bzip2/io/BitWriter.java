package pl.bzip2.io;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Wrapper na OutputStream pozwalający pisać bity
 */
public class BitWriter {
	
	public static final int BUFFER_LENGTH = 1024*8;
	private OutputStream out;
	private byte[] buffer;
	private int bufferPos;

	private static byte[] helper = {(byte)0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
	
	
	public BitWriter(OutputStream output) {
		out = output;
		buffer = new byte[BUFFER_LENGTH];
	}
	
	/**
	 * Zapisuje zawartość bufora do strumienia o ustawia pozycję bufora na 0.
	 * @throws IOException
	 */
	public void flush() throws IOException {
		int length = bufferPos/8;
		if (bufferPos%8 != 0) {
			length++;
		}
		out.write(buffer, 0, length);
		bufferPos = 0;
	}
	
	/**
	 * Zapisuje bit do bufora. Jeśli bufor jest pełny, dane są zapisywane do strumienia.<br>
	 * Jeśli konieczne jest wymuszenie zapisu bufora, należy użyć {@link #flush()}
	 * @param bit 0 lub 1
	 * @throws IOException
	 */
	public void write(byte bit) throws IOException {
		byte temp = helper[bufferPos%8];
		if (bit == 0) {
			buffer[bufferPos/8] &= (~temp);
		} else {
			buffer[bufferPos/8] |= temp;
		}
		bufferPos++;
		if (bufferPos/8 == buffer.length) {
			flush();
		}
	}
	
	public void writeInt(int integer) throws IOException{
		out.write(ByteBuffer.allocate(4).putInt(integer).array());
	}
	
	/**
	 * Zamyka strumień pod spodem
	 * @throws IOException
	 */
	public void close() throws IOException{
		out.close();
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
	
	/**
	 * Zwraca strumień pod spodem
	 * @return
	 */
	public OutputStream getOutputStream(){
		return out;
	}
	
}
