package pl.bzip2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Klasa implementująca transformatę Burrowsa-Wheelera.
 * Obiekty tej klasy reprezentują jedynie gotową transformatę.
 * @author krzysiek
 *
 */
public class BWTransform {
	
	private byte[] vector;
	private int start;
	
	BWTransform(byte[] vector, int start){
		this.vector = vector;
		this.start = start;
	}
	
	/**
	 * Zwraca zakodowany ciąg
	 * @return
	 */
	public byte[] getVector() {
		return vector;
	}

	/**
	 * Zwraca pozycję pierwszego znaku ciągu oryginalnego
	 * @return
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Koduje ciąg wejściowy transformatą BW
	 * @param bytes ciąg wejściowy
	 * @return wynikowa transformata
	 */
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
	 * Dekoduje ostatnią kolumnę macierzy rotacji w oryginalny ciąg
	 * @param data zakodowany ciag
	 * @param start indeks pierwszego znaku ciągu oryginalnego
	 * @return oryginalna postać ciągu
	 */
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
	
	/**
	 * Generuje wektor przejść taki, że L[i] oraz L[T[i]] to kolejne symbole oryginalnego ciagu.
	 * @param L ostatnia kolumna macierzy rotacji
	 * @param F pierwsza kolumna macierzy rotacji
	 * @return wektor przejść T
	 */
	private static int[] generateT(byte[] L, byte[] F){
		int[] T = new int[L.length];
		//zlicz ile razy występuje każdy symbol
		int[]freqs = new int[256];
		for(int i = 0;i<L.length;i++){
			freqs[L[i]&0xFF] ++;
		}
		//utwórz tablice dla kazdego symbolu przechowująca jego pozycje w L
		int[][] indices = new int[256][];
		for(int i = 0;i<indices.length;i++){
			indices[i] = new int[freqs[i]];
		}
		//wypełnij tablice symboli ich pozycjami w L
		for(int i = 0;i<L.length;i++){
			//pobierz tablice dla danego symbolu
			int[] positions = indices[L[i]&0xFF];
			//dodaj jego pozycje w L do tablicy
			positions[positions.length - freqs[L[i]&0xFF]--] = i;
		}
		for(int i = 0;i<F.length;i++){
			//pobierz tablice dla danego symbolu
			int[] positions = indices[F[i]&0xFF];
			//wypisz kolejna pozycje symbolu w L do tablicy T
			T[i] = positions[(positions.length+freqs[F[i]&0xFF]++)%positions.length]; 
		}
		return T;
	}

	/**
	 * Reprezentuje rotację ciągu. Sam ciąg pozostaje w niezmienionej postaci, modyfikacji
	 * podlega indeks startowy. Na jego podstawie np. pierwszy znak rotacji to 7 znak ciągu rzeczywistego. 
	 * @author krzysiek
	 */
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
