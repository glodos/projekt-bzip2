package pl.bzip2.io;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;

import org.omg.CORBA.PRIVATE_MEMBER;


/**
 * Wrapper na InputStream, który pozwala na czytanie wejścia w postaci bitów
 */
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
	
	/**
	 * Czyta kolejny bit z wejścia. Dane ze strumienia są przechowywane w buforze, 
	 * więc kolejny odczyt ze strumienia nastąpi po przetworzeniu całego bufora.
	 * @param bufferSize rozmiar danych jaki metoda ma wczytać w przypadku, gdy bufor jest pusty
	 * @return przeczytany bit, 0 lub 1
	 * @throws IOException
	 */
	public byte read(int bufferSize) throws IOException {
		if (bufferPos/8 == bufferLength) {
			bufferLength = input.read(buffer, 0, bufferSize);
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
	/**
	 * Zamyka strumień pod spodem
	 * @throws IOException
	 */
	public void close() throws IOException{
		input.close();
	}
	
	/**
	 * Zwraca strumień, na którym operuje BitReader.
	 * @return
	 */
	public InputStream getInputStream(){
		return input;
	}
	
    /**
     * Pomocnicza funkcja do czytania liczby int ze strumienia wejściowego.
     * @param r strumień wejściowy
     * @return następne 4 bajty z wejścia zamienione na int
     * @throws IOException
     */
    public int readInt() throws IOException{
    	ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    	byte[] intBuf = byteBuffer.array();
    	int read = input.read(intBuf);
    	if(read!=4)
    		throw new IOException("Unable to read the int");
    	return byteBuffer.getInt(0);
    }
    
    public boolean eof(){
    	try {
			return input.available() == 0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
	
}
