package pl.bzip2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import pl.bzip2.BZip2.CompressionInfo;

public class EfficiencyTest {
	public static void main(String[] args) throws IOException {
		String input = "mapping.txt";
		String compressed = "mapping.txt.bzip";
		String decompressed = "mapping_dec.txt";
		String report = "report.csv";
		int[] blockSizes = new int[]{5, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900};
		FileWriter fWriter = new FileWriter(report);
		for(int s : blockSizes){
			CompressionInfo info = BZip2.compress(input, null, s);
			fWriter.write(String.format(Locale.US, "%d,%f,%d,", s, info.ratio, info.time));
			info = BZip2.decompress(compressed, decompressed);
			fWriter.write(String.format("%d\n", info.time));
		}
		fWriter.close();
	}
}
