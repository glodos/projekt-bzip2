package pl.bzip2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;

public class BZip2 {
	
	public static final String HELP = "Usage: bzip2.jar";
	
	public enum Option{COMPRESS, DECOMPRESS, OUTPUT, BLOCK_SIZE, HELP}

	private static void compress(String in, String out, int blockSize) throws IOException{
		System.out.println("Compressing "+in);
		InputStream input = new FileInputStream(in);
		OutputStream output = new FileOutputStream(prepareOutput(in, out));
		if(blockSize > 0){
			blockSize*= 1024;
		}else{
			blockSize = 900*1024;
		}
		byte[] buffer = new byte[blockSize];
		try{
			Huffman huffman = new Huffman();
			int read;
			int blocks = 0;
			while((read = input.read(buffer))>0){
				if(read < blockSize){
					//trim the buffer
					byte[] tmp = new byte[read];
					System.arraycopy(buffer, 0, tmp, 0, read);
					buffer = tmp;
				}
				BWTransform bwTransform = BWTransform.encode(buffer);
				writeInt(bwTransform.getStart(), output);
				byte[] mtf = MoveToFront.encode(bwTransform.getVector());
				huffman.encode(mtf, output);
				System.out.printf("Block %d done\n", ++blocks);
			}
		}finally{
			input.close();
			output.close();
		}
	}
	
	private static void decompress(String in, String out) throws IOException{
		System.out.println("Decompressing "+in);
		InputStream input = new FileInputStream(in);
		OutputStream output = new FileOutputStream(prepareOutput(in, out));
		try{
			Huffman huffman = new Huffman();
			byte[] decoded;
			int bwtStart;
			int blocks = 0;
			while(input.available()>0){
				bwtStart = readInt(input);
				decoded = huffman.decode(input);
				decoded = MoveToFront.decode(decoded);
				decoded = BWTransform.decode(decoded, bwtStart);
				output.write(decoded);
				System.out.printf("Block %d done\n", ++blocks);
			}
		}finally{
			input.close();
			output.close();
		}
	}
	
	private static File prepareOutput(String input, String output){
		File inFile = new File(input).getAbsoluteFile();
		String inName = inFile.getName();
		String outName;
		File outFile;
		if(output == null){
			outFile = new File(inFile.getParent());
			if(inName.endsWith(".bzip")){
				outName = inName.substring(0, inName.length()-5);
			}else {
				outName = inName + ".bzip";
			}
		}else{
			outFile = new File(output);
			outName = outFile.getName();
		}
		if(outFile.isDirectory()){
			outFile = new File(outFile, outName);
		}else{
			outFile = new File(outFile.getParent(), outName);
		}
		return outFile;
	}
	
    /**
     * Pomocnicza funkcja do czytania liczby int ze strumienia wejściowego.
     * @param r strumień wejściowy
     * @return następne 4 bajty z wejścia zamienione na int
     * @throws IOException
     */
    public static int readInt(InputStream input) throws IOException{
    	ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    	byte[] intBuf = byteBuffer.array();
    	int read = input.read(intBuf);
    	if(read!=4)
    		throw new IOException("Unable to read the int");
    	return byteBuffer.getInt(0);
    }
    
    public static void writeInt(int integer, OutputStream out) throws IOException{
		out.write(ByteBuffer.allocate(4).putInt(integer).array());
	}
	
	public static void main(String[] args){
		try {
			OptParser parser = new OptParser(args);
			switch (parser.getMainOption()) {
			case COMPRESS:
				compress(parser.getInput(), parser.getOutput(), parser.getBlockSize());
				break;
			case DECOMPRESS:
				decompress(parser.getInput(), parser.getOutput());
				break;
			case HELP:
			default:
				printHelp();
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printHelp(){
		System.out.println(HELP);
	}
	
	public static class OptParser{
		private Option main = Option.HELP;
		private Option current;
		private String output;
		private String input;
		private int blockSize;
		public OptParser(String[] args) throws ParseException{
			String opt;
			for(int i = 0;i<args.length;i++){
				opt = args[i];
				switch (opt) {
				case "-c":
				case "--compress":
					current = Option.COMPRESS;
					main = current;
					checkSecondArg(args, i, current);
					break;
				case "-d":
				case "--decompress":
					current = Option.DECOMPRESS;
					main = current;
					checkSecondArg(args, i, current);
					break;
				case "-o":
				case "--output":
					current = Option.OUTPUT;
					checkSecondArg(args, i, current);
					break;
				case "-b":
				case "--block-size":
					current = Option.BLOCK_SIZE;
					checkSecondArg(args, i, current);
					break;
				case "-h":
				case "--help":
					current = Option.HELP;
					main = current;
					break;
				default:
					if(current == Option.COMPRESS || current == Option.DECOMPRESS){
						input = opt;
					}else if(current == Option.OUTPUT){
						output = opt;
					}else if(current == Option.BLOCK_SIZE){
						try{
							blockSize = Integer.parseInt(opt);
						}catch (NumberFormatException e) {
							throw new ParseException("Invalid size format: "+opt, i);
						}
					}else{
						throw new ParseException("Unrecognized option: "+opt, i);
					}
					break;
				}
			}
		}
		
		private void checkSecondArg(String[] args, int position, Option current) throws ParseException{
			if(position+1 >= args.length)
				throw new ParseException("Missing argument for option "+current, position);
		}
		
		public Option getMainOption(){
			return main;
		}
		
		public String getInput(){
			return input;
		}
		
		public String getOutput(){
			return output;
		}
		
		public int getBlockSize(){
			return blockSize;
		}
	}
}
