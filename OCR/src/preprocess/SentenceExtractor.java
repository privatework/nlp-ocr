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
		maxCharacterCount = 80;
		String rawLine = reader.readLine();
		int emptyLineCount = 0;
		Line currentLine = null;
		while (rawLine != null) {
			if (!"".equals(rawLine)) {
				currentLine = new Line(rawLine);
				if (emptyLineCount > 0) {
					currentLine.succeedingEmptyLineNum = emptyLineCount;
					lines.add(currentLine);
					emptyLineCount = 0;
				}
				int characterCount = rawLine.length();
				if (characterCount > maxCharacterCount) {
					maxCharacterCount = characterCount;
					System.out.println("new max: " + characterCount);
					System.out.println(rawLine);
				}
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
		writer.close();
	}
	
	public void extractSentences(String inputFile, String outputFile) throws IOException {
		readLines(inputFile);
		for (int iter = 0; iter < 2; iter++) {
			mergeLines();
		}
		printLines(outputFile);
	}
}
