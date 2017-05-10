package wordgenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Grammar {
	protected TreeMap<String, Integer> beginnings_map;
	protected TreeMap<String, Integer> endings_map;
	protected TreeMap<String, Integer> groups_map;
	private final static int beglength = 5;
	private final static int endlength = 3;
	private final static int grouplength = 5;

	protected ArrayList<String> beginnings;
	protected ArrayList<String> endings;
	protected ArrayList<String> groups;
	
	public Grammar(String fname)
	{		
		beginnings_map = new TreeMap<String, Integer>();
		endings_map    = new TreeMap<String, Integer>();
		groups_map     = new TreeMap<String, Integer>();
		beginnings = new ArrayList<String>();
		endings    = new ArrayList<String>();
		groups     = new ArrayList<String>();
		
		// Open dictionary file
		FileInputStream dictionary;
		
		try {
			dictionary = new FileInputStream(fname);
		} catch (FileNotFoundException e)
		{
			System.out.println("Dictionary file " + fname + " was not found");
			return;
		}
		
		// Process dictionary file
		ReadDictionary(dictionary);
		
		// Close file
		try {
			dictionary.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void ReadDictionary(FileInputStream dictionary) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(dictionary));
		String line;
		
		try {
			// Read first line
			line = reader.readLine();
			
			while (line != null) {
				// Truncate to slash
				int idx = line.indexOf("/");
				if (idx < 0)
					idx = line.length();
				
				ProcessWord(line.substring(0, idx));

				// Read next line
				line = reader.readLine();
			}	
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Now the maps are filled.
		// Find the most represented 100 beginnings and ends,
		// and the most represented 10000 groups
		List<Integer> beg_occurrences = new ArrayList<Integer>(beginnings_map.values());
		Collections.sort(beg_occurrences);
		int beg_min = beg_occurrences.get(beg_occurrences.size()-100).intValue();
		for (Map.Entry<String, Integer> entry : beginnings_map.entrySet()) {
			if (entry.getValue() >= beg_min)
				beginnings.add(entry.getKey());
		}

		List<Integer> end_occurrences = new ArrayList<Integer>(endings_map.values());
		Collections.sort(end_occurrences);
		int end_min = end_occurrences.get(end_occurrences.size()-100).intValue();
		for (Map.Entry<String, Integer> entry : endings_map.entrySet()) {
			if (entry.getValue() >= end_min)
				endings.add(entry.getKey());
		}

		List<Integer> group_occurrences = new ArrayList<Integer>(groups_map.values());
		Collections.sort(group_occurrences);
		int group_min = group_occurrences.get(group_occurrences.size()-10000).intValue();
		for (Map.Entry<String, Integer> entry : groups_map.entrySet()) {
			if (entry.getValue() >= group_min)
				groups.add(entry.getKey());
		}
	}
	
	private void ProcessWord(String word) {
		// If the word is too short, throw it away
		int l = word.length();
		if (l < grouplength+1)
			return;
		word = word.toLowerCase();
		
		// Add beginning
		String beg = word.substring(0, beglength);
		if (beginnings_map.containsKey(beg))
			beginnings_map.put(beg, beginnings_map.get(beg)+1);
		else
			beginnings_map.put(beg, 1);
		
		// Add ending
		String end = word.substring(l-endlength, l);
		if (endings_map.containsKey(end))
			endings_map.put(end, endings_map.get(end)+1);
		else
			endings_map.put(end, 1);
		
		// Add all groups
		for (int i = 1; i < l-grouplength; ++i) {
			String group = word.substring(i, i+grouplength);
			if (groups_map.containsKey(group))
				groups_map.put(group, groups_map.get(group)+1);
			else
				groups_map.put(group, 1);
			
		}
	}
	
	
	static public void main(String[] args) {
		Grammar grammar = new Grammar(args[0]);
		//grammar.beginnings.values()
		//System.out.println(grammar.beginnings_map.values());
		System.out.println(grammar.groups);
	}
}
