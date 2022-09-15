package de.uk.java.feader.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;

public class AppIO implements IAppIO{
	/**
	*@author Florian Hensel
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<Feed> loadSubscribedFeeds(File feedsFile) {
		List<Feed> feeds = new ArrayList<Feed>();
		try {
			FileInputStream file = new FileInputStream(feedsFile);
            ObjectInputStream in = new ObjectInputStream(file);
            
			feeds = (List<Feed>) in.readObject();

			in.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return feeds;
	}

	/**
	*@author Florian Hensel
	*/
	@Override
	public void saveSubscribedFeeds(List<Feed> feeds, File feedsFile) {
		try {
			FileOutputStream file = new FileOutputStream(feedsFile);
			ObjectOutputStream out = new ObjectOutputStream(file);

			out.writeObject(feeds);
            
            out.close();
            
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	*@author Max Latz
	*/
	@Override
	public Properties loadConfig(File configFile) {
		Properties p = new Properties();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile)); 
			String line;
			
			// Load all properties from feader.config 
			while((line = br.readLine()) != null) {
				if(line.charAt(0) != '#') {
					//Split keys and values at = 
					String[] split = line.split("=");
					p.setProperty(split[0],split[1]);
				}
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return p;
	}
	
	/**
	*@author Max Latz
	*/
	@Override
	public void saveConfig(Properties config, File configFile) {	
		try {
			BufferedWriter bw = new java.io.BufferedWriter(new FileWriter(configFile));
			bw.write("#Feader config\n");
			
			// eg: Thu Jul 04 14:47:42 CEST 2019, takes system language and timezone into account
			SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			Date d = new Date(System.currentTimeMillis());
			bw.write("#" + f.format(d));
			
			//Get key and property names
			Enumeration<?> e = config.propertyNames();
			
			//Write all properties into feader.config 
			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				bw.newLine();
				bw.write(key + "=" + config.getProperty(key));	
			}
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	*@author Max Latz
	*/
	@Override
	public void exportAsHtml(List<Entry> entries, File htmlFile) {
		try {
			BufferedWriter bw = new java.io.BufferedWriter(new FileWriter(htmlFile));
			
			//Entries shouldn't be empty
			String title =  entries.get(0).getParentFeedTitle();
			
			//Write HTML Header
			bw.write("<!DOCTYPE html>\n");
			bw.write("<html lang=\"en\">\n");
			bw.write("<head><title>" + title + "</title></head>\n");
			
			//Write entries
			for(Entry e : entries) {
				bw.write(e.html());
				bw.newLine();
			}
			
			bw.write("</html>");
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}