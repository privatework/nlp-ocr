package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleLineMerger {
	int maxCount;
	public static int getMaxCharacterCount(String inputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line = reader.readLine();
		int maxCount = 80;
		while (line != null) {
			if (!"".equals(line)) {
				int count = line.length();
				if (count > maxCount) {
					maxCount = count;
					System.out.println("new max: " + count);
					System.out.println(line);
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return maxCount;
		
	}
	
	public SimpleLineMerger(String inputFile) {
		try {
			maxCount = getMaxCharacterCount(inputFile);
		} catch (IOException e) {
			maxCount = 80;
		}
		System.out.println("max Count: " + maxCount);
	}
	
	public boolean merge(Line currentLine, Line nextLine) {
		if (currentLine == null || nextLine == null) return false;
		if (currentLine.length < maxCount / 2) {
			return false;
		} else {
			if (currentLine.fullSentence) return false;
			return true;
		}
	}
	
	public void extractSentences(String inputFile, String outputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String rawLine = reader.readLine();
		boolean emptyLine = false;
		Line currentLine = null;
		Line previousLine = null;
		Line nextLine = null;
		while (rawLine != null) {
			if (!"".equals(rawLine)) {
				nextLine = new Line(rawLine);
				if (emptyLine) {
					nextLine.afterEmptyLine = true;
					emptyLine = false;
				}
				
				// process here
				boolean merged = merge(currentLine, nextLine);
				if (!merged) {
					if (nextLine.afterEmptyLine) {
						writer.write("\n");
					}
					if (currentLine != null)
						writer.write("\n");
				} else {
					currentLine.inDiaglog = true;
					writer.write(" ");
				}
//				writer.write(nextLine.tokenizedLine);
				writer.write(nextLine.rawLine);
				previousLine = currentLine;
				currentLine = nextLine;
				
			} else {
				if (emptyLine) {
					writer.write("\n");
					currentLine.beforeEmptyLine = true;
				}
				emptyLine = true;
			}
//			writer.write(rawLine + "\n");
			
			rawLine = reader.readLine();
			
			
			
		}
		reader.close();
		writer.close();
	}
	
	public static void main(String args[]) throws IOException {
		//String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
		String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
		String outFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageSentences_3.txt";
		SimpleLineMerger merger = new SimpleLineMerger(inFile);
		merger.extractSentences(inFile, outFile);
		
	}
}
