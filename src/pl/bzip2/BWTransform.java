package pl.bzip2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BWTransform {
	
	private byte[] vector;
	private int start;
	
	BWTransform(byte[] vector, int start){
		this.vector = vector;
		this.start = start;
	}
	
	public byte[] getVector() {
		return vector;
	}

	public int getStart() {
		return start;
	}
	
	public static BWTransform encode(byte[] bytes){
		int n = bytes.length;
		List<BWRotation> rotations = new ArrayList<>(n);
		for(int i = 0;i<n;i++){
			rotations.add(new BWRotation(bytes, i));
		}
		Collections.sort(rotations);
		int firstIndex = 0;
		byte[] result = new byte[n];
		BWRotation r;
		for(int i = 0;i<n;i++){
			r = rotations.get(i);
			result[i] = r.getLast();
			if (r.getStart()==1) {
				firstIndex = i;
			}
		}
		return new BWTransform(result, firstIndex);
	}
	
    /**
     * The standard number of bits per chunk/word when huffing.
     */
    public static final int BITS_PER_WORD = 8;
    
    /**
     * The size of the alphabet given the number of bits per chunk, this
     * should be 2^BITS_PER_WORD.
     */
    public static final int ALPH_SIZE = (1 << BITS_PER_WORD);
	
	public static byte[] decode(byte[] data, int start){
		byte[] F = Arrays.copyOf(data, data.length);
		Arrays.sort(F);
		int[] T = generateT(data, F);
		
		byte[] result = new byte[data.length];
		int iT = start;
		for(int i = 0;i<result.length;i++){
			result[i] = data[iT];
			iT = T[iT];
		}
		return result;
	}
	
	private static int[] generateT(byte[] L, byte[] F){
		int[] T = new int[L.length];
		boolean [] usedL = new boolean[L.length];
		for(int i = 0;i<F.length;i++){
			for(int j = 0;j<L.length;j++){
				if(!usedL[j] && L[j] == F[i]){
					usedL[j]=true;
					T[i] = j; 
					break;
				}
			}
		}
		return T;
	}

	static class BWRotation implements Comparable<BWRotation>{
		byte[] bytes;
		int start;
		public BWRotation(byte[] bytes, int start) {
			this.bytes = bytes;
			this.start = start;
		}
		
		public byte getLast(){
			return bytes[(start+bytes.length-1) % bytes.length];
		}
		public int getStart(){
			return start;
		}
		
		public byte byteAt(int index){
			return bytes[(start+index+bytes.length) % bytes.length];
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<bytes.length;i++){
				sb.append(byteAt(i));
			}
			return sb.toString();
		}
		
		@Override
		public int compareTo(BWRotation o) {
	        int len = bytes.length;

	        int k = 0;
	        while (k < len) {
	            byte c1 = byteAt(k);
	            byte c2 = o.byteAt(k);
	            if (c1 != c2) {
	                return c1 - c2;
	            }
	            k++;
	        }
	        return 0;
		}
		
	}
}
