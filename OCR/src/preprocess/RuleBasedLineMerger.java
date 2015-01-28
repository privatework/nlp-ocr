package preprocess;

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
	public static int HIGH_TRIGRAM_THREAD_HOLD = 50;
	
	HashSet<String> forbiddenTagSet;
	HashSet<String> forbiddenTokenSet;
	public RuleBasedLineMerger() {
		super();
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
		// Avoiding tagging error for I
		return forbiddenTokenSet.contains(lastToken) || (forbiddenTagSet.contains(lastTag) && !lastToken.equals("I"));
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
	
	public boolean formVeryGoodTrigram(String[] lastTokens, String[] firstTokens) {
		String triGram1 = lastTokens[0] + " " + lastTokens[1] + " " + firstTokens[0];
		String triGram2 = lastTokens[1] + " " + firstTokens[0] + " " + firstTokens[1];
		int count1 = triGramTable.getCount(triGram1);
		int count2 = triGramTable.getCount(triGram2);
		if (count1 >= HIGH_TRIGRAM_THREAD_HOLD || count2 >= HIGH_TRIGRAM_THREAD_HOLD) return true;
		else return false;
	}
	
	public boolean goodConcatenation(Line currLine, Line nextLine) {
		String[] lastTokens = currLine.lastTwoTokens;
		String[] firstTokens = nextLine.firstTwoTokens;
		if (lastTokens == null || firstTokens == null) return false;
		return formGoodBigram(lastTokens, firstTokens) || formGoodTrigram(lastTokens, firstTokens);
	}
	
	public boolean veryGoodConcatenation(Line currLine, Line nextLine) {
		String[] lastTokens = currLine.lastTwoTokens;
		String[] firstTokens = nextLine.firstTwoTokens;
		if (lastTokens == null || firstTokens == null) return false;
		return formVeryGoodTrigram(lastTokens, firstTokens);
	}
	
	// checking whether two continuous lines (with or without empty lines in
	// between should be merged
	@Override
	public boolean merge(Line currentLine, Line nextLine) {
		// 
		if (currentLine == null || nextLine == null) return false;
		
		// Don't care about line with all uppercase  letter
		if (Line.isAllUpperCase(currentLine.rawLine)) return false;
		
		// if 2 lines are more than 2 empty lines away, don't merge
		if (nextLine.succeedingEmptyLineNum > 1) return false;
		
		// if 2 lines has 1 empty line in between, if concatenating them form a very common trigram
		// merge them, if not don't merge
		if (nextLine.succeedingEmptyLineNum > 0) {
			if (veryGoodConcatenation(currentLine, nextLine) && currentLine.length >= maxCount / 2) return true;
			else return false;
		}
		
		// if the first line is too short, don't merge
		if (currentLine.length < maxCount / 2) {
			return false;
			// if concatenating them form a good trigram or bigram, merge them
		} else if (goodConcatenation(currentLine, nextLine)) { 
			return true;
		} else {
			// don't merge if the first line already end with a .
			if (currentLine.endWithPunctuation()) {
				return false;
			// if the first line ends with unacceptable part of speech
		    // for example a preposition (or, in) or conjunction (and)
		    // merge them
			} else if (hasBadEnding(currentLine)) {
				return true;
			} else {
				if (nextLine.hasPunctuation || nextLine.inParagraph || nextLine.hasVerbOrPronoun())  {
					// next line has punctuation 
					if (LOWER_CASE.matcher(nextLine.rawLine).matches())
						// lower case -> good
						return true;
					else {
						if (currentLine.inParagraph || currentLine.hasVerbOrPronoun()) return true;
						// check if case the previous sentence is in dialog
						// if not check something whether the concatenate make sense
						
						// greedy function, be careful
						return (currentLine.length > maxCount * 4 / 5.0 || currentLine.hasPunctuation);
						// use some 
					}
				} else {
					
					if (currentLine.inParagraph || currentLine.hasVerbOrPronoun()) {
						return true;
					} else {
						
					}
					return false;
				}
			}
		}
	}
	
	public static void main(String args[]) {
		// Testing special cases
//		String string1 = "Note I - Nature of Business and Significant Accounting Policies";
//		String string2 = "(Continued)";
//		String string1 = "SECTION I.I6. A CERTIFIED AUDIT WILL BE PROVIDED UPON THE REQUEST OF";
//		String string2 = "THE HOLDERS OF A MAJORITY OF THE UNITS OF PARTICIPATION IN THE POOL";
		String string1 = "Income Taxes - No provision for income taxes has been made in these ﬁnancial";
		String string2 = "statements because each partner is individually responsible for reporting income or loss";
		Line line1 = new Line(string1);
		Line line2 = new Line(string2);
		RuleBasedLineMerger merger = new RuleBasedLineMerger();
		merger.setMaxCount(123);
		System.out.println(merger.merge(line1, line2));
		System.out.println(biGramTable.getCount("financial statements"));
	}
	
}
