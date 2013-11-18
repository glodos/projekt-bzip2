package pl.bzip2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BWTransform {
	
	private char[] vector;
	private int start;
	
	BWTransform(char[] vector, int start){
		this.vector = vector;
		this.start = start;
	}
	
	public char[] getVector() {
		return vector;
	}

	public int getStart() {
		return start;
	}
	
	public static BWTransform encode(String s){
		int n = s.length();
		List<BWRotation> rotations = new ArrayList<>(n);
		for(int i = 0;i<n;i++){
			rotations.add(new BWRotation(s, i));
		}
		Collections.sort(rotations);
		int firstIndex = 0;
		char[] result = new char[n];
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
	
	public static String decode(char[] data, int start){
		char[] F = Arrays.copyOf(data, data.length);
		Arrays.sort(F);
		int[] T = generateT(data, F);
		
		char[] result = new char[data.length];
		int iT = start;
		for(int i = 0;i<result.length;i++){
			result[i] = data[iT];
			iT = T[iT];
		}
		return new String(result);
	}
	
	private static int[] generateT(char[] L, char[] F){
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
		String s;
		int start;
		public BWRotation(String s, int start) {
			this.s = s;
			this.start = start;
		}
		
		public char getLast(){
			return s.charAt((start+s.length()-1) % s.length());
		}
		public int getStart(){
			return start;
		}
		
		public char charAt(int index){
			return s.charAt((start+index+s.length()) % s.length());
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<s.length();i++){
				sb.append(charAt(i));
			}
			return sb.toString();
		}
		
		@Override
		public int compareTo(BWRotation o) {
	        int len = s.length();

	        int k = 0;
	        while (k < len) {
	            char c1 = charAt(k);
	            char c2 = o.charAt(k);
	            if (c1 != c2) {
	                return c1 - c2;
	            }
	            k++;
	        }
	        return 0;
		}
		
	}
}
