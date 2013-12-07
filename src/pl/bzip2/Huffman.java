package pl.bzip2;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;

public class Huffman {
    private byte[] vector;
    private int nodeCount;
    private Node treeRoot;

    public Huffman(byte [] vector) {
        this.vector = vector;
    }

    public void encode(BitWriter writer) {
        nodeCount = 0;
        // 0. Calculate chars frequencies
        int[] symFreqs = calcFrequencies();
        // 1. Create a leaf node for each symbol and add it to the priority queue
        PriorityQueue<Node> pq = fillQueueWithLeafNodes(symFreqs);
        treeRoot = buildTree(pq);
        
    }
    
    private int[] calcFrequencies(){
    	int [] symFreqs = new int[256];
    	for(int i = 0; i < vector.length; i++)
            symFreqs[vector[i]]++;
    	return symFreqs;
    }
    
    private PriorityQueue<Node> fillQueueWithLeafNodes(int[] symFreqs){
    	PriorityQueue<Node> pq = new PriorityQueue<>();
    	for(int i = 0; i < 256; i++) {
            if(symFreqs[i] > 0){
                pq.add(new Node((byte)i, symFreqs[i], null, null));
                nodeCount++;
            }
        }
    	return pq;
    }
    
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
    
    public byte[] getTree(){
    	return flattenTree(treeRoot, nodeCount);
    }
    
    private static byte[] flattenTree(Node root, int nodeCount){
    	ByteBuffer buffer = ByteBuffer.allocate(nodeCount+1);
    	root.writeValue(buffer, 1);
    	return buffer.array();
    }
    
    private static Node readTree(byte[] bytes){
    	return Node.readNode(bytes, 1);
    }
    

    private static class Node implements Comparable<Node> {
    	byte symbol;
        private final int freq;
        private Node left;
        private Node right;

        private Node(byte symbol, int freq, Node left, Node right) {
            this.symbol = symbol;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {
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
}
