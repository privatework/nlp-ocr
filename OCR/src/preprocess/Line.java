package preprocess;

import java.util.regex.Pattern;

public class Line {
	public static Pattern START_WITH_LOWER_CASE	       = Pattern.compile("^[a-z\\(].*");
	public static Pattern START_WITH_UPPER_CASE_WORD   = Pattern.compile("^[A-Za-z]+ .*");
	
	int succeedingEmptyLineNum;
	int length;
	boolean hasPunctuation;
	
//	boolean fullSentence;
	boolean inParagraph;
	
	String rawLine;
	String tokenizedLine;
	private String taggedLine;
	
	// boolean last last sentence is in a paragraph
	// boolean last sentence is in a paragraph
	
	// last two words
	// or last two pos-es
	
	// first word
	
	// TODO
	boolean wellFormed;
	boolean capitalized;
	String[] lastTwoTokens;
	String[] firstTwoTokens;
	
	public static boolean isAllUpperCase(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return false;
		}
		int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isUpperCase(cs.charAt(i)) == false) {
		        return false;
			}
		}
		return true;
	}
	
	public Line(String line) {
		this.rawLine = line;
		length = line.length();
		
		tokenizedLine = Tagging.getTokenizedString(line);
		hasPunctuation = tokenizedLine.contains(" . ") || tokenizedLine.endsWith(" .");
		
		capitalized = isAllUpperCase(line);
		
		String[] tokens = tokenizedLine.toLowerCase().split(" ");
		if (tokens.length > 2) {
			int tokenNum = tokens.length;
			lastTwoTokens = new String[2];
			lastTwoTokens[0] = tokens[tokenNum - 2];
			lastTwoTokens[1] = tokens[tokenNum - 1];
			firstTwoTokens = new String[2];
			firstTwoTokens[0] = tokens[0];
			firstTwoTokens[1] = tokens[1];
		}
		
		
	}
	
	public String getTagString(){
		if (taggedLine == null)
			taggedLine = Tagging.getTagString(rawLine);
		return taggedLine;
	}
	
	public boolean hasVerbOrPronoun() {
		if (taggedLine == null)
			taggedLine = Tagging.getTagString(rawLine);
		String[] taggedWords = taggedLine.split(" ");
		for (String taggedWord: taggedWords) {
			String[] elements = taggedWord.split("_");
			String tag = elements[elements.length - 1];
			if ("PRP".equals(tag) || "PRP$".equals("tag")) return true;
			// can check verb form but there might be tagger error
			String word = elements[0];
			if (word.equals("is") || word.equals("are")) return true;
		}
		return false;
	}
	
	public boolean goodStandAloneLine() {
		return startWithCapitalizedWord() && endWithPunctuation();
	}
	
	public boolean endWithPunctuation() {
		return tokenizedLine.endsWith(" .");
	}
	
	public boolean startWithCapitalizedWord() {
		return START_WITH_UPPER_CASE_WORD.matcher(rawLine).matches();
	}
}
