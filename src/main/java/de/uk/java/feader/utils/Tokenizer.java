package de.uk.java.feader.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer implements ITokenizer {
	//Code: Florian Hensel RegEx: Max Latz
	@Override
	public List<String> tokenize(String text) {
		//delete html tags
		text = text.replaceAll("\\<.*?\\>", "");
		
		//delete everyting thats not text
		text = text.replaceAll("[^a-zA-ZäöüßÄÖÜ ]", "");
		//split words in a list
		List<String> words = new ArrayList<String>(); 
		for(String s : Arrays.asList(text.split(" "))) {
			//filter empty entries 
			if(s.trim().length() > 0) {
				words.add(s);
			}
		}
		return words;
	}
}