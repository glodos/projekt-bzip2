package pl.bzip2;

import java.util.PriorityQueue;

public class Huffman {
    private static char[] vector;

    public Huffman(char [] vector) {
        this.vector = vector;
    }

    public static void encode() {
        int [] charFreqs = new int[256];
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // 0. Calculate chars frequencies
        for(int i = 0; i < vector.length; i++)
            charFreqs[vector[i]]++;

        // 1. Create a leaf node for each symbol and add it to the priority queue
        for(int i = 0; i < 256; i++) {
            if(charFreqs[i] > 0)
                pq.add(new Node(i, freq[i], null, null));
        }

        // 2. While there is more than one node in the queue:
        while(pq.size() > 1) {
            // 2.1 Remove the two nodes of highest priority (lowest probability) from the queue
            Node left = pq.poll();
            Node right = pq.poll();

            // 2.2 Create a new internal node with these two nodes as children and with probability equal to the sum of the two nodes' probabilities.
            Node merged = new Node('\0', left.freq+right.freq, left, right);

            // 2.3 Add the new node to the queue.
            pq.add(merged);
        }



    }

    private static class Node implements Comparable<Node> {
        char c;
        private final int freq;
        private Node left;
        private Node right;

        private Node(char c, int freq, Node left, Node right) {
            this.c = c;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        private Boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        @Override
        public int compareTo(Node o) {
            return this.freq - o.freq;
        }
    }
}
