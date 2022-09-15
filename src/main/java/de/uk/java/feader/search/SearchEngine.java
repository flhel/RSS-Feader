package de.uk.java.feader.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;

/**
*@author Florian Hensel
*/
public class SearchEngine implements ISearchEngine {
	
	ITokenizer tokenizer;
	private Map<String, List<Entry>> searchIndex = new HashMap<String, List<Entry>>();

	@Override
	public List<Entry> search(String searchTerm) {
		List<Entry> entryList = new ArrayList<Entry>();
		HashSet<Entry> entrySet = new HashSet<Entry>();

		if(searchTerm.contains("?") || searchTerm.contains("*")) {
			/* Convert wildcard chars to regex
			 * 0-9 or +,_ etc. wont be matched even if "w" or "." is used 
			 * because they are not present in the searchIndex keys.
			 * look in the Tokenizer class
			 */
			
			searchTerm = searchTerm.replace("?", "\\w");
			searchTerm = searchTerm.replace("*", "\\w*"); 

			Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);

			for(Map.Entry<String, List<Entry>> mapEntry : searchIndex.entrySet()) {
			    Matcher matcher = pattern.matcher(mapEntry.getKey());

			    if(matcher.matches()) {
					// add all the entries of the matching word to the list
			    	for(Entry e : mapEntry.getValue()) {	
			    		//add entry to list if not duplicate
						entrySet.add(e);
			    	}
			    }
			}
			// Set into List (the HashSet is way faster in the loop)
			entryList.addAll(entrySet);
		} else {
			// Normal search (faster/no wildcard)
			entryList = searchIndex.get(searchTerm.toLowerCase());
		}
		
		return entryList;
	}

	@Override
	public void createSearchIndex(List<Feed> feeds) {
		for(Feed f : feeds) {
			addToSearchIndex(f);
		}
	}

	@Override
	public void addToSearchIndex(Feed feed) {
		for(Entry entry : feed.getEntries()) {
			
			List<String> words = tokenizer.tokenize(entry.html());
			
			for(String w : words) {
				// duplicates will be filtered this way
				w = w.toLowerCase();
				List<Entry> entryList = searchIndex.get(w);
				
				// check if the word is already in the searchIndex
				if(entryList != null) {
					boolean entryExists = false;
					
					// check if the entry for the feed already exists in the searchIndex
					for(int i = 0; i < entryList.size() && !entryExists; i++) {
						if(entry.equals(entryList.get(i))) {
							entryExists = true;
						}
					}
					if(!entryExists) {
						entryList.add(entry);
					}
				} else {
					// create new word entry in the search Index with its own feed-entry list
					entryList = new ArrayList<Entry>();
					entryList.add(entry);
					searchIndex.put(w, entryList);
				}
			}
		}
	}

	@Override
	public void setTokenizer(ITokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	@Override
	public void saveSearchIndex(File indexFile) {
		// write the searchIndex to the given indexFile
		try {
			FileOutputStream file = new FileOutputStream(indexFile);
			ObjectOutputStream out = new ObjectOutputStream(file);
				
			out.writeObject(searchIndex);   
			
            out.close();
            
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadSearchIndex(File indexFile) {
		//load the searchIndex from the given indexFile
		try {
			FileInputStream file = new FileInputStream(indexFile);
            ObjectInputStream in = new ObjectInputStream(file);
            
			searchIndex = (Map<String, List<Entry>>) in.readObject();
            
			in.close();
            
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean indexExists() {
		if(searchIndex.isEmpty()) {
			return false;
		}
		return true;
	}

}