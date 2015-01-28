package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SentenceExtractor {
	ArrayList<Line> lines;
	boolean[] isMergedArray;
	int maxCharacterCount;
	
	RuleBasedLineMerger merger;
	public SentenceExtractor() {
		merger = new RuleBasedLineMerger();
	}
	
	private void readLines(String inputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		lines = new ArrayList<>();
		maxCharacterCount = 80;
		String rawLine = reader.readLine();
		int emptyLineCount = 0;
		Line currentLine = null;
		while (rawLine != null) {
			if (!"".equals(rawLine)) {
				currentLine = new Line(rawLine);
				if (emptyLineCount > 0) {
					currentLine.succeedingEmptyLineNum = emptyLineCount;
					emptyLineCount = 0;
				}
				int characterCount = rawLine.length();
				if (characterCount > maxCharacterCount) {
					maxCharacterCount = characterCount;
//					System.out.println("new max: " + characterCount);
//					System.out.println(rawLine);
				}
				lines.add(currentLine);
			} else {
				emptyLineCount++;
			}
			rawLine = reader.readLine();
		}
		reader.close();
		
		isMergedArray = new boolean[lines.size() - 1];
	}
	
	private void mergeLines() {
		merger.setMaxCount(maxCharacterCount);
		Line currentLine = lines.get(0);
		Line nextLine = null;
		for (int i = 0; i < isMergedArray.length; i++) {
			nextLine = lines.get(i + 1);
			if (!isMergedArray[i]) {
				boolean shouldBeMerged = merger.merge(currentLine, nextLine);
				if (shouldBeMerged) {
					currentLine.inParagraph = true;
					nextLine.inParagraph = true;
					isMergedArray[i] = true;
				}
			}
			currentLine = nextLine;
		}
	}
	
	private void printLines(String outputFile) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		boolean mergedWithPreviousLine = true;
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			if (i > 0) {
				mergedWithPreviousLine = isMergedArray[i - 1];
			}
			if (!mergedWithPreviousLine) {
				for (int j = 0; j < line.succeedingEmptyLineNum + 1; j++) {
					writer.write("\n");
				}
			}
			writer.write(line.rawLine);
		}
		writer.close();
	}
	
	private void printSentences(String outputFile) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String paragraph = "";
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			if (line.inParagraph) {
				paragraph = line.tokenizedLine;
				while (isMergedArray[i]) {
					i++;
					line = lines.get(i);
					paragraph += " " + line.tokenizedLine;
				}
//				System.out.println(paragraph);
				printParagraph(paragraph, writer);
			} else {
				if (line.goodStandAloneLine() && (line.length > maxCharacterCount / 2) && !Line.isAlmostAllUpperCase(line.rawLine)) {
					writer.write(line.tokenizedLine);
					writer.write("\n");
				}
			}
		}
		writer.close();
	}
	
	private void printParagraph(String tokenizedParagraph, BufferedWriter writer) throws IOException{
		String[] sentences = tokenizedParagraph.split(" \\. ");
		int length = sentences.length;
		for (int i = 0; i < length - 1; i++) {
			writer.write(sentences[i]);
			writer.write(" .");
			char nextChar = sentences[i + 1].charAt(0);
			if (Character.isLowerCase(nextChar) || nextChar == '(') {
				writer.write(" ");
			} else {
				writer.write("\n");
			}
		}
		if (sentences[length - 1].endsWith(".")) {
			writer.write(sentences[length - 1]);
			writer.write("\n");
		}
	}
	
	public void extractSentences(String inputFile, String paragraphFile, String sentenceFile) throws IOException {
		readLines(inputFile);
		for (int iter = 0; iter < 2; iter++) {
			mergeLines();
		}
		printLines(paragraphFile);
		printSentences(sentenceFile);
	}
	
	public static void main(String args[]) throws IOException {
		//System.out.println(LOWER_CASE.matcher("(ab").matches());
		String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_7_1.txt";
		String paragraphFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageParagraph_7.txt";
		String sentenceFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageSentences_7.txt";
		SentenceExtractor extractor = new SentenceExtractor();
		extractor.extractSentences(inFile, paragraphFile, sentenceFile);

	}
}
