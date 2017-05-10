package wordgenerator;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
	Grammar grammar;
	Random rng;
	
	int begsize;
	int endsize;
	int groupsize;
	
	public Generator(Grammar grammar) {
		this.grammar = grammar;
		rng = new Random();
		
		begsize = grammar.beginnings.get(0).length();
		endsize = grammar.endings.get(0).length();
		groupsize = grammar.groups.get(0).length();
	}
	
	public String generate(int length) {
		// Pick a random beginning
		String word = grammar.beginnings.get(rng.nextInt(grammar.beginnings.size()));
		System.out.println("Starting with " + word);
		
		for (int i = begsize; true; i++) {
			try {
				// Add new character to word if it's possible
				String hint = word.substring(i-groupsize+1);
				word = word + PickGroup(hint);
			} catch (Exception e) {
				// Start over if no available group exist
				word = grammar.beginnings.get(rng.nextInt(grammar.beginnings.size()));
				i = begsize-1;
			}
			System.out.println(" -> " + word);
			
			if (i >= length-1) {
				try {
					// Add final character to word if it's possible
					String hint = word.substring(i-endsize+1);
					word = word + PickEnding(hint);
					return word;
				} catch (Exception e) {
					// Nothing to do, just add new characters
				}
				System.out.println("--> " + word);
			}
				
		}
	}

	private String PickGroup(String starting) throws Exception {
		ArrayList<String> available = new ArrayList<String>();
		for (String group : grammar.groups) {
			if (group.substring(0, starting.length()).equals(starting))
				available.add(group);
		}
		
		if (available.size() == 0)
			throw new Exception();
		
		return available.get(rng.nextInt(available.size())).substring(starting.length());
	}
	
	private String PickEnding(String starting) throws Exception {
		ArrayList<String> available = new ArrayList<String>();
		for (String end : grammar.endings) {
			if (end.substring(0, starting.length()).equals(starting))
				available.add(end);
		}
		
		if (available.size() == 0)
			throw new Exception();
		
		return available.get(rng.nextInt(available.size())).substring(starting.length());
	}

	public static void main(String[] args) {
		Grammar grammar = new Grammar(args[0]);
		Generator generator = new Generator(grammar);
		generator.generate(10);
	}

}
