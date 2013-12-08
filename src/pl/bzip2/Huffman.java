package pl.bzip2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;

import pl.bzip2.io.BitReader;
import pl.bzip2.io.BitWriter;

public class Huffman {
	public static final int SYMBOL_COUNT = 256;
    private byte[][] huffmanCodes = new byte[SYMBOL_COUNT][];
    private int nodeCount;

    /**
     * Koduje ciąg bajtów. Drzewo i zakodowany blok są zapisywane w strumieniu wyjściowym.
     * @param vector dane wejściowe
     * @param writer strumień wyjściowy
     * @throws IOException
     */
    public void encode(byte [] vector, BitWriter writer) throws IOException {
        nodeCount = 0;
        // 0. Calculate chars frequencies
        int[] symFreqs = calcFrequencies(vector);
        // 1. Create a leaf node for each symbol and add it to the priority queue
        PriorityQueue<Node> pq = fillQueueWithLeafNodes(symFreqs);
        Node treeRoot = buildTree(pq);
        byte[] codeBuffer = new byte[SYMBOL_COUNT];
        fillHuffmanCodes(treeRoot, codeBuffer, 0);
        writeTree(writer, treeRoot);
        writeEncodedBlock(writer, vector);
    }
    
    /**
     * Tworzy tablicę prawdopodobieństwa zbioru wejściowego.<br>
     * Indeksem w tablicy jest wartość symbolu a wartością tablicy jego częstość występowania<br>
     * @return tablica prawdopodobieństwa
     */
    private int[] calcFrequencies(byte[] vector){
    	int [] symFreqs = new int[SYMBOL_COUNT];
    	for(int i = 0; i < vector.length; i++)
            symFreqs[vector[i]]++;
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
                nodeCount++;
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
            Node merged = new Node((byte)0, left.freq+right.freq, left, right);

            // 2.3 Add the new node to the queue.
            pq.add(merged);
            nodeCount++;
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
			huffmanCodes[node.symbol] = code;
		} else {
			buf[codeLength] = 0;
			fillHuffmanCodes(node.left, buf, codeLength+1);
			buf[codeLength] = 1;
			fillHuffmanCodes(node.right, buf, codeLength+1);
		}
    }
    
    /**
     * Zapisuje drzewo do strumienia danych.
     * @param w strumień wyjściowy
     * @throws IOException
     */
    private void writeTree(BitWriter w, Node treeRoot) throws IOException{
    	byte[] tree = flattenTree(treeRoot, nodeCount);
    	OutputStream out = w.getOutputStream();
    	out.write(ByteBuffer.allocate(4).putInt(nodeCount).array());
    	out.write(tree, 1, tree.length-1);
    	out.flush();
    }
    
    /**
     * Przekształca drzewo w tablicę bajtów
     * @param root korzeń drzewa
     * @param nodeCount liczba wierzchołków drzewa
     * @return spłaszczona reprezentacja
     */
    private static byte[] flattenTree(Node root, int nodeCount){
    	ByteBuffer buffer = ByteBuffer.allocate(nodeCount+1);
    	root.writeValue(buffer, 1);
    	return buffer.array();
    }
    
    /**
     * Zapisuje zakodowany blok do strumienia wyjściowego
     * @param w strumień wyjściowy
     * @throws IOException
     */
    private void writeEncodedBlock(BitWriter w, byte[] vector) throws IOException{
    	int blockSize = calcBlockSize();
    	OutputStream out = w.getOutputStream();
    	//zapisz wielkość oryginalnego bloku
    	out.write(ByteBuffer.allocate(4).putInt(vector.length).array());
    	//zapisz wielkość zakodowanego bloku
    	out.write(ByteBuffer.allocate(4).putInt(blockSize).array());
    	for(int i = 0;i<vector.length;i++){
    		w.write(huffmanCodes[i]);
    	}
    	w.flush();
    }
    
    /**
     * Oblicza rozmiar zakodowanego bloku
     * @return rozmiar bloku w bajtach
     */
    private int calcBlockSize(){
    	int result = 0;
    	for(int i =0;i<huffmanCodes.length;i++){
    		result+=huffmanCodes[i].length;
    	}
    	if(result % 8 != 0){
    		result += 8;
    	}
    	return result / 8;
    }
    
    /**
     * Dekoduje ciąg danych poprzez odtworzenie drzewa Huffmana i odbudowę oryginalnego wektora.
     * @param reader strumień wejściowy
     * @return oryginalny ciąg danych
     * @throws IOException
     */
    public byte[] decode(BitReader reader) throws IOException{
    	Node treeRoot = readTree(reader);
    	return decodeHuffmanCode(reader, treeRoot);
    }
    
    /**
     * Odczytuje drzewo Huffmana z danych wejściowych i odtwarza jego strukturę.
     * @param reader strumień wejściowy
     * @return korzeń odtworzonego drzewa
     * @throws IOException
     */
    private Node readTree(BitReader reader) throws IOException{
    	nodeCount = readInt(reader);
    	byte[] treeBuffer = new byte[nodeCount];
    	InputStream in = reader.getInputStream();
    	int read = in.read(treeBuffer);
    	if(read!=nodeCount)
    		throw new IOException("Unable to read the tree, bytes missing.");
    	return Node.readNode(treeBuffer, 1);
    }
    
    /**
     * Dekoduje cały blok złożony z kodów Huffmana w postać pierwotną
     * @param r strumień wejściowy
     * @param treeRoot korzeń drzewa Huffmana
     * @return postać pierwotna ciągu
     * @throws IOException
     */
    private static byte[] decodeHuffmanCode(BitReader r, Node treeRoot) throws IOException{
    	int originalBlockSize = readInt(r);
    	byte[] vector = new byte[originalBlockSize];
    	int dataSize = readInt(r);
    	BitCounter bitCounter = new BitCounter(dataSize);
    	for(int i=0;i<originalBlockSize;i++){
    		Node leaf = findLeaf(treeRoot, r, bitCounter);
    		vector[i] = leaf.symbol;
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
    private static Node findLeaf(Node n, BitReader r, BitCounter bitCounter) throws IOException{
    	if(n.isLeaf())
    		return n;
    	byte bit = r.read(Math.min(bitCounter.getByteCount(), 1024));
    	bitCounter.decrement();
    	if(bit==0){
    		return findLeaf(n.left, r, bitCounter);
    	}else{
    		return findLeaf(n.right, r, bitCounter);
    	}
    }
    
    /**
     * Pomocnicza funkcja do czytania liczby int ze strumienia wejściowego.
     * @param r strumień wejściowy
     * @return następne 4 bajty z wejścia zamienione na int
     * @throws IOException
     */
    private static int readInt(BitReader r) throws IOException{
    	InputStream in = r.getInputStream();
    	ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    	byte[] intBuf = byteBuffer.array();
    	int read = in.read(intBuf);
    	if(read!=4)
    		throw new IOException("Unable to read the int");
    	return byteBuffer.getInt(0);
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
            return this.freq - o.freq;
        }
        
        public void writeValue(ByteBuffer b, int position){
        	b.put(position, symbol);
        	if(!isLeaf()){
        		left.writeValue(b, position*2);
        		right.writeValue(b, position*2+1);
        	}
        }
        
        public static Node readNode(byte[] buf, int position){
        	if(position>buf.length)
        		return null;
        	else
        		return new Node(buf[position-1], 0, readNode(buf, position*2), readNode(buf, position*2+1));
        }
    }
    
    private static class BitCounter{
    	int bitCount;
    	int byteCount;
    	
    	public BitCounter(int byteCount){
    		this.byteCount = byteCount;
    	}
    	
    	public void decrement(){
    		bitCount++;
    		if(bitCount == 8){
    			bitCount = 0;
    			byteCount--;
    		}
    	}
    	
    	public int getByteCount(){
    		return byteCount;
    	}
    }
}
