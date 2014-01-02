package pl.bzip2;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import pl.bzip2.io.BitReader;

public class BitReaderTest {

	@Test
	public void readTest() throws IOException {
		byte[] testData = new byte[]{1,2, 3, 4,24, 57, 89, 111};
		BitReader r = new BitReader(testData);
		int dataPos = 0;
		try {
			while(dataPos < testData.length){
				byte read = 0;
				for(int i = 0;i<8;i++){
					read |= r.read();
					if(i<7)
						read <<= 1;
				}
				//read >>=1;
//					System.out.println("----------");
//				System.out.println(Integer.toBinaryString(read));
//				System.out.println(Integer.toBinaryString(testData[dataPos]));
				assertTrue(read == testData[dataPos++]);
			}
		} catch (IOException e) {
		}
	}
	
	@Test
	public void readNegativeTest() throws IOException {
		byte[] testData = new byte[]{-1,-2, -3, -4,-24, -57, -89, -111};
		BitReader r = new BitReader(testData);
		int dataPos = 0;
		try {
			while(dataPos < testData.length){
				byte read = 0;
				for(int i = 0;i<8;i++){
					read |= r.read();
					if(i<7)
						read <<= 1;
				}
				//read >>=1;
//					System.out.println("----------");
//				System.out.println(Integer.toBinaryString(read));
//				System.out.println(Integer.toBinaryString(testData[dataPos]));
				assertTrue(read == testData[dataPos++]);
			}
		} catch (IOException e) {
		}
	}

}
