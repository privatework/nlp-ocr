package preprocess;

public class Line {
	boolean beforeEmptyLine;
	boolean afterEmptyLine;
	int length;
	boolean hasPunctuation;
	
	boolean fullSentence;
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
		fullSentence = tokenizedLine.endsWith(" .");
		
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
	
}
