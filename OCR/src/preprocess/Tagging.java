package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagging {
	public static final String MODEL_FIlE="/home/thenghiapham/Downloads/stanford-postagger-2014-01-04/models/english-bidirectional-distsim.tagger";
	public static final MaxentTagger tagger = new MaxentTagger(MODEL_FIlE);
	
	public static String getTokenizedString(String sentence) {
		  PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new StringReader(sentence),
	              new CoreLabelTokenFactory(), "");
		  StringBuffer tokenizedSentence = new StringBuffer();
		  if (ptbt.hasNext()) {
			  tokenizedSentence.append(ptbt.next().toString());
		  }
	      for (CoreLabel label; ptbt.hasNext(); ) {
	        label = ptbt.next();
	        tokenizedSentence.append(" ");
	        tokenizedSentence.append(label.toString());
	      }
	      String resultString = tokenizedSentence.toString();
	      resultString = resultString.replaceAll("-RRB-",")");
	      resultString = resultString.replaceAll("-LRB-","(");
	      return resultString;
	}
	
	public static String getTagString(String sentence) {
		sentence = getTokenizedString(sentence);
		sentence = tagger.tagTokenizedString(sentence);
		return sentence;
	}
	
	public static void tokenize(String inputFile, String outputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String line = reader.readLine();
		while (line != null) {
			if (!"".equals(line)) {
				line = getTokenizedString(line);
			}
			writer.write(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		writer.close();
	}
	
	public static void tag(String inputFile, String outputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String line = reader.readLine();
		while (line != null) {
			if (!"".equals(line)) {
				line = getTokenizedString(line);
				line = tagger.tagTokenizedString(line);
				// line = tagger.tagString(line);
			}
			writer.write(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		writer.close();
	}
	
	public static void main(String args[]) throws IOException {
		String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
		String outFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageTagged_3.txt";
		Tagging.tag(inFile, outFile);
	}

}
