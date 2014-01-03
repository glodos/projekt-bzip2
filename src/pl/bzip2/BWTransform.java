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
	
//	public static BWTransform encode2(final byte[] bytes){
//		final int n = bytes.length;
//		Integer[] rotations = new Integer[n];
//		for(int i = 0;i<n;i++){
//			rotations[i] = i;
//		}
//		Arrays.sort(rotations, new Comparator<Integer>() {
//
//			@Override
//			public int compare(Integer o1, Integer o2) {
//		        int k = 0;
//		        while (k < n) {
//		            int c1 = byteAt(o1, k, bytes);
//		            int c2 = byteAt(o2, k, bytes);
//		            if (c1 != c2) {
//		                return c1 - c2;
//		            }
//		            k++;
//		        }
//		        return 0;
//			}
//		});
//		int firstIndex = 0;
//		byte[] result = new byte[n];
//		//BWRotation r;
//		for(int i = 0;i<n;i++){
//			result[i] = getLast(rotations[i], bytes);
//			if (rotations[i]==1) {
//				firstIndex = i;
//			}
//		}
//		return new BWTransform(result, firstIndex);
//	}
//	
//	private static byte byteAt(int start, int index, byte[] bytes){
//		return bytes[(start+index+bytes.length) % bytes.length];
//	}
//	
//	private static byte getLast(int start, byte[] bytes){
//		return bytes[(start+bytes.length-1) % bytes.length];
//	}
	
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
		int[]freqs = new int[256];
		for(int i = 0;i<L.length;i++){
			freqs[L[i]&0xFF] ++;
		}
		int[][] indices = new int[256][];
		for(int i = 0;i<indices.length;i++){
			indices[i] = new int[freqs[i]];
		}
		for(int i = 0;i<L.length;i++){
			int[] positions = indices[L[i]&0xFF];
			positions[positions.length - freqs[L[i]&0xFF]--] = i;
		}
		for(int i = 0;i<F.length;i++){
			int[] positions = indices[F[i]&0xFF];
			T[i] = positions[(positions.length+freqs[F[i]&0xFF]++)%positions.length]; 
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
	            int c1 = byteAt(k);
	            int c2 = o.byteAt(k);
	            if (c1 != c2) {
	                return c1 - c2;
	            }
	            k++;
	        }
	        return 0;
		}
		
	}
}
