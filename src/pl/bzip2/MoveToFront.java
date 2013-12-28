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
		int index;
		for(int i = 0;i<input.length;i++){
			index = find(L, input[i]);
			moveToFront(L, index);
			result[i] = (byte) index;
		}
		return result;
	}
	
	/**
	 * Przesuwa symbol na danej pozycji na początek tablicy
	 * @param array
	 * @param index
	 */
	private static void moveToFront(byte[] array, int index){
		byte tmp = array[index];
		for(int i = index;i>0;i--){
			array[i] = array[i-1];
		}
		array[0] = tmp;
	}
	/**
	 * Znajduje indeks symbolu w tablicy
	 * @param array tablica
	 * @param b symbol do znalezienia
	 * @return indeks symbolu
	 */
	private static int find(byte[] array, byte b){
		if(array[b & 0xFF] == b)
			return b & 0xFF;
		for(int i = 0;i<array.length;i++){
			if(array[i] == b)
				return i;
		}
		//nigdy tu nie wejdzie
		return 0;
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
		byte symbol;
		int index;
	    for (int i = 0; i<input.length; i++) {
	    	index = input[i] & 0xFF;
	    	symbol = L[input[i] & 0xFF];
	    	moveToFront(L, index);
	    	result[i] = symbol;
	    }
	    return result;
	}
}
