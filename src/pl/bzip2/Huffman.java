package pl.bzip2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import pl.bzip2.io.BitReader;
import pl.bzip2.io.BitWriter;

public class Huffman {
	public static final int SYMBOL_COUNT = 256;
    private byte[][] huffmanCodes = new byte[SYMBOL_COUNT][];

    /**
     * Koduje ciąg bajtów. Drzewo i zakodowany blok są zapisywane w strumieniu wyjściowym.
     * @param vector dane wejściowe
     * @param output strumień wyjściowy
     * @throws IOException
     */
    public void encode(byte [] vector, OutputStream output) throws IOException {
        // 0. Calculate chars frequencies
        int[] symFreqs = calcFrequencies(vector);
        // 1. Create a leaf node for each symbol and add it to the priority queue
        PriorityQueue<Node> pq = fillQueueWithLeafNodes(symFreqs);
        Node treeRoot = buildTree(pq);
        byte[] codeBuffer = new byte[SYMBOL_COUNT];
        fillHuffmanCodes(treeRoot, codeBuffer, 0);
        writeFreqs(output, symFreqs);
        writeEncodedBlock(output, vector);
    }
    
    /**
     * Tworzy tablicę prawdopodobieństwa zbioru wejściowego.<br>
     * Indeksem w tablicy jest wartość symbolu a wartością tablicy jego częstość występowania<br>
     * @return tablica prawdopodobieństwa
     */
    private int[] calcFrequencies(byte[] vector){
    	int [] symFreqs = new int[SYMBOL_COUNT];
    	for(int i = 0; i < vector.length; i++)
            symFreqs[vector[i] & 0xFF]++;
    	return symFreqs;
    }
    
    /**
     * Tworzy kolejkę priorytetową z wierzchołkami dla każdego symbolu
     * @param symFreqs tablica prawdopodobieństwa
     * @return kolejka priorytetowa z wierzchołkami
     */
    private PriorityQueue<Node> fillQueueWithLeafNodes(int[] symFreqs){
    	PriorityQueue<Node> pq = new PriorityQueue<>();
    	for(int i = 0; i < SYMBOL_COUNT; i++) {
            if(symFreqs[i] > 0){
                pq.add(new Node((byte)i, symFreqs[i], null, null));
            }
        }
    	return pq;
    }
    
    /**
     * Tworzy drzewo Huffmana
     * @param pq kolejka priorytetowa zawierająca pojedyncze wierzchołki
     * @return korzeń utworzonego drzewa
     */
    private Node buildTree(PriorityQueue<Node> pq){
    	// 2. While there is more than one node in the queue:
        while(pq.size() > 1) {
            // 2.1 Remove the two nodes of highest priority (lowest probability) from the queue
            Node left = pq.poll();
            Node right = pq.poll();

            // 2.2 Create a new internal node with these two nodes as children and with probability equal to the sum of the two nodes' probabilities.
            Node merged = new Node((byte)1, left.freq+right.freq, left, right);

            // 2.3 Add the new node to the queue.
            pq.add(merged);
        }
        return pq.peek();
    }
    
    /**
     * Rekurencyjnie wyznacza kody Huffmana dla każdego symbolu w danych wejściowych
     * @param node wierzchołek drzewa
     * @param buf bufor, w którym tworzony jest kod
     * @param codeLength aktualna długość kodu w buforze
     */
    private void fillHuffmanCodes(Node node, byte[] buf, int codeLength){
		if (node.isLeaf()) {
			byte[] code = new byte[codeLength];
			System.arraycopy(buf, 0, code, 0, codeLength);
			huffmanCodes[node.symbol & 0xFF] = code;
		} else {
			buf[codeLength] = 0;
			fillHuffmanCodes(node.left, buf, codeLength+1);
			buf[codeLength] = 1;
			fillHuffmanCodes(node.right, buf, codeLength+1);
		}
    }
    
    /**
     * Zapisuje prawdopodobieństwa symboli
     * @param out strumień wyjściowy
     * @throws IOException
     */
    void writeFreqs(OutputStream out, int[] freqs) throws IOException{
    	int bytesLength = freqs.length*4;
    	BZip2.writeInt(freqs.length, out);
    	byte[] buf = new byte[bytesLength];
    	for(int i = 0;i<bytesLength;i+=4){
    		buf[i]  = (byte) (freqs[i/4] >>> 24); 
    		buf[i+1]= (byte) (freqs[i/4] >>> 16); 
    		buf[i+2]= (byte) (freqs[i/4] >>> 8); 
    		buf[i+3]= (byte) freqs[i/4]; 
    	}
    	out.write(buf);
    	out.flush();
    }
    
    /**
     * Zapisuje zakodowany blok do strumienia wyjściowego
     * @param out strumień wyjściowy
     * @throws IOException
     */
    private void writeEncodedBlock(OutputStream out, byte[] vector) throws IOException{
    	int blockSize = calcBlockSize(vector);
    	//zapisz wielkość oryginalnego bloku
    	BZip2.writeInt(vector.length, out);
    	//zapisz wielkość zakodowanego bloku
    	BZip2.writeInt(blockSize, out);
    	//zapisz blok
    	BitWriter w = new BitWriter(blockSize);
    	for(int i = 0;i<vector.length;i++){
    		w.write(huffmanCodes[vector[i] & 0xFF]);
    	}
    	out.write(w.array());
    	out.flush();
    }
    
    /**
     * Oblicza rozmiar zakodowanego bloku
     * @return rozmiar bloku w bajtach
     */
    private int calcBlockSize( byte[] vector){
    	int result = 0;
    	for(int i = 0;i<vector.length;i++){
    		result+=huffmanCodes[vector[i] & 0xFF].length;
    	}
    	if(result % 8 != 0){
    		result += 8;
    	}
    	return result / 8;
    }
    
    /**
     * Dekoduje ciąg danych poprzez odtworzenie drzewa Huffmana i odbudowę oryginalnego wektora.
     * @param in strumień wejściowy
     * @return oryginalny ciąg danych
     * @throws IOException
     */
    public byte[] decode(InputStream in) throws IOException{
    	int[]symFreqs = readFreqs(in); 
    	PriorityQueue<Node> pq = fillQueueWithLeafNodes(symFreqs);
        Node treeRoot = buildTree(pq);
    	return decodeHuffmanCode(in, treeRoot);
    }
    
    /**
     * Odczytuje drzewo Huffmana z danych wejściowych i odtwarza jego strukturę.
     * @param in strumień wejściowy
     * @return korzeń odtworzonego drzewa
     * @throws IOException
     */
    int[] readFreqs(InputStream in) throws IOException{
    	int length = BZip2.readInt(in)*4;
    	byte[] buffer = new byte[length];
    	int read = in.read(buffer);
    	if(read!=length)
    		throw new IOException("Unable to read the frequencies, bytes missing.");
    	int[] freqs = new int[length/4];
    	for(int i = 0;i<length;i+=4){
    		freqs[i/4] = (buffer[i+3] & 0xFF | (buffer[i+2] & 0xFF) << 8 | 
    				(buffer[i+1] & 0xFF) << 16 | (buffer[i] & 0xFF) << 24);
    	}
    	return freqs;
    }
    
    /**
     * Dekoduje cały blok złożony z kodów Huffmana w postać pierwotną
     * @param r strumień wejściowy
     * @param treeRoot korzeń drzewa Huffmana
     * @return postać pierwotna ciągu
     * @throws IOException
     */
    private static byte[] decodeHuffmanCode(InputStream in, Node treeRoot) throws IOException{
    	int originalBlockSize = BZip2.readInt(in);
    	byte[] vector = new byte[originalBlockSize];
    	int dataSize = BZip2.readInt(in);
    	try{
    		byte[] buffer = new byte[dataSize];
    		in.read(buffer);
    		BitReader r = new BitReader(buffer);
	    	for(int i=0;i<originalBlockSize;i++){
	    		Node leaf = findLeaf(treeRoot, r, dataSize);
	    		vector[i] = leaf.symbol;
	    	}
    	}catch(EOFException e){
    		Logger.getGlobal().info("End of file!");
    		//koniec pliku
    	}
    	return vector;
    }
    
    /**
     * Rekurencyjnie przeszukuje drzewo i zwraca wartość liścia dla odczytanego kodu z wejścia.
     * @param n aktualny wierzchołek drzewa
     * @param r strumień wejściowy
     * @param bitCounter licznik przeczytanych bajtów - zapobiega wczytaniu danych, których nie chcemy czytać jako bity
     * @return liść powiązany z wejściowym kodem Huffmana
     * @throws IOException
     */
    private static Node findLeaf(Node n, BitReader r, int dataSize) throws IOException{
    	if(n.isLeaf())
    		return n;
    	byte bit = r.read();
    	if(bit==0){
    		return findLeaf(n.left, r, dataSize);
    	}else{
    		return findLeaf(n.right, r, dataSize);
    	}
    }
    

    private static class Node implements Comparable<Node> {
    	byte symbol;
        private final int freq;
        private Node left;
        private Node right;

        public Node(byte symbol, int freq, Node left, Node right) {
            this.symbol = symbol;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        @Override
        public int compareTo(Node o) {
        	if(this.freq == o.freq){
        		return symbol - o.symbol;
        	}else{
        		return this.freq - o.freq;
        	}
        }
        
    }
    
}
