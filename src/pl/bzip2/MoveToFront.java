package pl.bzip2;

public class MoveToFront {

	public static char[] encode(char[] input){
		char[] L = new char[256];
		for(int i=0;i<256;i++){
			L[i] = (char) i;
		}
		char[] result = new char[input.length];
		for(int i = 0;i<input.length;i++){
			result[i] = L[input[i]];
	        for (int j=0; j<256; j++)
	            if (L[j] < L[input[i]])
	                L[j]++;
	        L[input[i]] = 0;
		}
		return result;
	}
	
	public static char[] decode(char[] input){
		char[] L = new char[256];
		for(int i=0;i<256;i++){
			L[i] = (char) i;
		}
		char[] result = new char[input.length];
		char tmp;
	    for (int i = 0; i<input.length; i++) {
	    	result[i] = L[input[i]];
	        tmp = L[input[i]];
	        for (int j = input[i]; j>0; j--)
	            L[j] = L[j-1];
	        L[0] = tmp;
	    }
	    return result;
	}
}
