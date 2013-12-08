package pl.bzip2;

public class MoveToFront {

	/**
	 * Wykonuje transformatę MoveToFront na ciągu wejściowym.
	 * @param input ciąg wejściowy
	 * @return ciąg po MTF
	 */
	public static byte[] encode(byte[] input){
		//tablica przechowująca kody wszystkich symboli
		byte[] L = new byte[256];
		for(int i=0;i<256;i++){
			L[i] = (byte) i;
		}
		byte[] result = new byte[input.length];
		for(int i = 0;i<input.length;i++){
			result[i] = L[input[i]];
	        for (int j=0; j<256; j++)
	            if (L[j] < L[input[i]])
	                L[j]++;
	        L[input[i]] = 0;
		}
		return result;
	}
	
	/**
	 * Odtwarza postać pierwotną ciągu wejściowego.
	 * @param input ciąg wejściowy
	 * @return postać oryginalna
	 */
	public static byte[] decode(byte[] input){
		//tablica przechowująca kody wszystkich symboli
		byte[] L = new byte[256];
		for(int i=0;i<256;i++){
			L[i] = (byte) i;
		}
		byte[] result = new byte[input.length];
		byte tmp;
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
