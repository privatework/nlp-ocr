package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleLineMerger {
	int maxCount = 80;
	
	public boolean merge(Line currentLine, Line nextLine) {
		if (currentLine == null || nextLine == null) return false;
		if (currentLine.length < maxCount / 2) {
			return false;
		} else {
			if (currentLine.endWithPunctuation()) return false;
			return true;
		}
	}
	
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
//	public static void main(String args[]) throws IOException {
//		//String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
//		String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
//		String outFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageSentences_3.txt";
//		SimpleLineMerger merger = new SimpleLineMerger(inFile);
//		merger.extractSentences(inFile, outFile);
//		
//	}
}
