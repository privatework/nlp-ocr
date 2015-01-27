package preprocess;

public class Line {
	boolean beforeEmptyLine;
	boolean afterEmptyLine;
	int length;
	boolean hasPunctuation;
	
	boolean fullSentence;
	boolean inDiaglog;
	
	String rawLine;
	String tokenizedLine;
	String taggedLine;
	
	// boolean last last sentence is in a paragraph
	// boolean last sentence is in a paragraph
	
	// last two words
	// or last two pos-es
	
	// first word
	
	// TODO
	boolean wellFormed;
	boolean capitalized;
	
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
		
	}
	
}
