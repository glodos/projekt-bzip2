package pl.bzip2;

import java.io.IOException;
import java.util.Arrays;

import pl.bzip2.io.BitReader;

public class Main {
	
	public static void main(String[] args) throws IOException {
		int a = 2116456356;
		System.out.println(Integer.toBinaryString(a));
		System.out.println(Integer.toBinaryString(a&0xFF));
		System.out.println(Integer.toBinaryString((a>>8)&0xFF));
		System.out.println(Integer.toBinaryString((a>>16)&0xFF));
		System.out.println(Integer.toBinaryString((a>>24)&0xFF));
		byte b1 = (byte) (a);
		byte b2 =  (byte) ((a>>>8));
		byte b3 =  (byte) ((a>>>16));
		byte b4 =  (byte) ((a>>>24));
		
		System.out.println(Integer.toBinaryString(((int)b4) << 24));
		System.out.println(Integer.toBinaryString(((int)b3) << 16));
		System.out.println(Integer.toBinaryString( ((int)b2) << 8));
		System.out.println(Integer.toBinaryString( (int)b1));
		System.out.println(Integer.toBinaryString((b4 << 24) | (b3 << 16) | (b2 << 8) | (b1)));
		
		System.out.println(a == (b1 & 0xFF | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24));
		
		int[] tab = new int[]{1, 2, 3, 4, 5, -13, 2116456356};
		
    	byte[] buf = new byte[tab.length*4];
    	for(int i = 0;i<tab.length*4;i+=4){
    		buf[i]= (byte) ((tab[i/4] >> 24)); 
    		buf[i+1]= (byte) ((tab[i/4] >> 16)); 
    		buf[i+2]= (byte) ((tab[i/4] >> 8)); 
    		buf[i+3]  = (byte) (tab[i/4]); 
    	}
		
		
    	int[] tab2 = new int[buf.length/4];
    	for(int i = 0;i<buf.length;i+=4){
    		tab2[i/4] = (buf[i+3] & 0xFF | (buf[i+2] & 0xFF) << 8 | (buf[i+1] & 0xFF) << 16 | (buf[i] & 0xFF) << 24);
    	}
    	
    	System.out.println(Arrays.equals(tab, tab2));
    	
    	byte[] b = encode(a);
    	System.out.println(Arrays.toString(b));
    	System.out.println(decode(b));
		
//		BitReader r = new BitReader(System.in);
//		while(true){
//			byte b = r.read(10);
//			System.out.println(b);
//		}
	}
	
	private static int decode(byte[] bi) {
		  return bi[3] & 0xFF | (bi[2] & 0xFF) << 8 |
		         (bi[1] & 0xFF) << 16 | (bi[0] & 0xFF) << 24;
		}
		private static byte[] encode(int i) {
		  return new byte[] { (byte) (i >>> 24), (byte) ((i << 8) >>> 24),
		                      (byte) ((i << 16) >>> 24), (byte) ((i << 24) >>> 24)
		  };
		}

}
