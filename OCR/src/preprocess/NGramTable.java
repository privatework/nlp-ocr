package preprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class NGramTable {
	HashMap<String, Integer> data;
	public NGramTable(String inputFile){
		System.out.println("load ngrams...");
		data = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "ISO-8859-15"));
			String line = reader.readLine();
			while (line != null) {
				String[] elements = line.split("\t");
				int counts = new Integer(elements[0]);
				String nGram = concatStringArray(elements, 1);
				data.put(nGram, counts);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String concatStringArray(String[] array, int beginIndex) {
		StringBuffer result = new StringBuffer();
		result.append(array[beginIndex]);
		for (int i = beginIndex + 1; i < array.length; i++) {
			result.append(" " + array[i]);
		}
		return result.toString();
	}
	
	public int getCount(String nGram) {
		if (data.containsKey(nGram)) {
			return data.get(nGram);
		} else
			return 0;
	}
}
