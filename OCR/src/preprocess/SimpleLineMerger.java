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
	
}
