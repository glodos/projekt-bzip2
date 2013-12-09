package pl.bzip2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pl.bzip2.io.BitReader;
import pl.bzip2.io.BitWriter;

public class BZip2 {

	public void compress(InputStream in, OutputStream out, int blockSize) throws IOException{
		byte[] buffer = new byte[blockSize];
		int read;
		Huffman huffman = new Huffman();
		BitWriter bw = new BitWriter(out);
		while((read = in.read(buffer))>0){
			if(read < blockSize){
				//trim the buffer
				byte[] tmp = new byte[read];
				System.arraycopy(buffer, 0, tmp, 0, read);
				buffer = tmp;
			}
			BWTransform bwTransform = BWTransform.encode(buffer);
			bw.writeInt(bwTransform.getStart());
			byte[] mtf = MoveToFront.encode(bwTransform.getVector());
			huffman.encode(mtf, bw);
		}
	}
	
	public void decompress(InputStream in, OutputStream out) throws IOException{
		BitReader br = new BitReader(in);
		Huffman huffman = new Huffman();
		byte[] decoded;
		int bwtStart;
		while(!br.eof()){
			bwtStart = br.readInt();
			decoded = huffman.decode(br);
			decoded = MoveToFront.decode(decoded);
			decoded = BWTransform.decode(decoded, bwtStart);
			out.write(decoded);
		}
	}
	
	public static void main(String[] args) throws IOException {
		boolean encode = true;
		BZip2 bZip2 = new BZip2();
		if(encode){
			InputStream in = new FileInputStream(new File("Huffman.java"));
			OutputStream out = new FileOutputStream("Huffman.bz2");
			bZip2.compress(in, out, 900*1024);
		}else{
			InputStream in = new FileInputStream("Huffman.bz2");
			OutputStream out = new FileOutputStream("Huffman_dec.java");
			bZip2.decompress(in, out);
		}
	}
}
