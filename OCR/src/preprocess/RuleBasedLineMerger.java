package preprocess;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;

public class RuleBasedLineMerger extends SimpleLineMerger{
	public static Pattern PAGE_NUM_PATTERN = Pattern.compile("^[0-9]+$");
	public static Pattern SECTION_PATTERN  = Pattern.compile("^[0-9]+(\\s\\.)?(\\s[a-zA-Z]+)+.*");
	public static Pattern LOWER_CASE	   = Pattern.compile("^[a-z\\(].*");
	public static String[] FORBIDDEN_TAGS   = {"CC", "DT", "EX", "IN", "MD", 
		"PDT", "POS", "PRP", "PRP$","TO","WP$", "WP", "WRB", "WDT"};
	public static String[] FORBIDDEN_TOKENS = {","};
	public static String biGramFile = "/home/thenghiapham/work/odesk/nlp_ocr/w2_.txt";
	public static String triGramFile = "/home/thenghiapham/work/odesk/nlp_ocr/w3_.txt";
	public static NGramTable biGramTable = new NGramTable(biGramFile);
	public static NGramTable triGramTable = new NGramTable(triGramFile);
	public static int BIGRAM_THREAD_HOLD = 100;
	public static int TRIGRAM_THREAD_HOLD = 25;
	
	HashSet<String> forbiddenTagSet;
	HashSet<String> forbiddenTokenSet;
	public RuleBasedLineMerger(String inputFile) {
		super(inputFile);
		forbiddenTagSet = new HashSet<>();
		forbiddenTokenSet = new HashSet<>();
		for (String tag: FORBIDDEN_TAGS) {
			forbiddenTagSet.add(tag);
		}
		for (String token: FORBIDDEN_TOKENS) {
			forbiddenTokenSet.add(token);
		}
	}
	
	public boolean hasBadEnding(Line line) {
		String tagString = line.getTagString();
		String[] taggedWords = tagString.split(" ");
		String lastTaggedWord = taggedWords[taggedWords.length - 1];
		String lastTag = lastTaggedWord.split("_")[1];
		
		String[] tokens = line.tokenizedLine.split(" ");
		String lastToken = tokens[tokens.length - 1];
		return forbiddenTokenSet.contains(lastToken) || forbiddenTagSet.contains(lastTag);
	}
	
	public boolean formGoodBigram(String[] lastTokens, String[] firstTokens) {
		String biGram = lastTokens[1] + " " + firstTokens[0];
		int count = biGramTable.getCount(biGram);
		if (count >= BIGRAM_THREAD_HOLD) return true;
		else return false;
	}
	
	public boolean formGoodTrigram(String[] lastTokens, String[] firstTokens) {
		String triGram1 = lastTokens[0] + " " + lastTokens[1] + " " + firstTokens[0];
		String triGram2 = lastTokens[1] + " " + firstTokens[0] + " " + firstTokens[1];
		int count1 = triGramTable.getCount(triGram1);
		int count2 = triGramTable.getCount(triGram2);
		if (count1 >= TRIGRAM_THREAD_HOLD || count2 >= TRIGRAM_THREAD_HOLD) return true;
		else return false;
	}
	
	public boolean goodConcatenation(Line currLine, Line nextLine) {
		String[] lastTokens = currLine.lastTwoTokens;
		String[] firstTokens = nextLine.firstTwoTokens;
		if (lastTokens == null || firstTokens == null) return false;
		return formGoodBigram(lastTokens, firstTokens) || formGoodTrigram(lastTokens, firstTokens);
	}
	
	@Override
	public boolean merge(Line currentLine, Line nextLine) {
		if (currentLine == null || nextLine == null) return false;
		// can ask some weight here for the prob
		if (nextLine.afterEmptyLine) return false;
		// don't take into account short lines
		if (currentLine.length < maxCount / 2) {
			return false;
		} else if (goodConcatenation(currentLine, nextLine)) { 
			return true;
		} else {
			if (currentLine.fullSentence) {
				return false;
			} else if (hasBadEnding(currentLine)) {
				return true;
			} else {
				
				if (nextLine.hasPunctuation)  {
					// next line has punctuation 
					if (LOWER_CASE.matcher(nextLine.rawLine).matches())
						// lower case -> good
						return true;
					else {
						if (currentLine.inParagraph) return true;
						// check if case the previous sentence is in dialog
						// if not check something whether the concatenate make sense
						return false;
						// use some 
					}
				} else {
					
					if (currentLine.inParagraph) {
						return true;
					} else {
						
					}
					return false;
				}
			}
		}
	}
	
	public static void main(String args[]) throws IOException {
//		System.out.println(LOWER_CASE.matcher("(ab").matches());
		String inFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageRaw_3_1.txt";
		String outFile = "/home/thenghiapham/work/odesk/nlp_ocr/imageSentences_3.txt";
		RuleBasedLineMerger merger = new RuleBasedLineMerger(inFile);
		merger.extractSentences(inFile, outFile);
		
	}
	
}
