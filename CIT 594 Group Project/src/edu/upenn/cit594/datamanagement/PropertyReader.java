package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.logging.Logger;

public class PropertyReader {
	private BufferedReader buffReader;
	
	public PropertyReader(String fileName) {
		
		try {
			
			buffReader = new BufferedReader(new FileReader(fileName));
			
			Logger logger = Logger.getInstance();
			
			logger.log(String.valueOf(System.currentTimeMillis()) + " " + fileName + " is opened to read.\n");
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	//this returns a map that associates a zipcode with a list of properties
	public HashMap<String, List<Property>> parseProperties(){
		
		HashMap<String, List<Property>> ret = new HashMap<>();
		
		try {
			String line = buffReader.readLine();
			
			String[ ] wordsInLine = line.split(",", -1);
			
			int indexOfZip = -1;
			
			int indexOfMarketValue = -1;
			
			int indexOfArea = -1;
			
			
			for(int i = 0; i < wordsInLine.length; i++) {
				
				if(wordsInLine[i].equals("zip_code")){
					
					indexOfZip = i;
				}
				else if(wordsInLine[i].equals("market_value")) {
					
					indexOfMarketValue = i;
				}
				else if(wordsInLine[i].equals("total_livable_area")) {
					
					indexOfArea = i;
				}
			}
			
			if(indexOfZip == -1 || indexOfMarketValue == -1 || indexOfArea == -1) {
				System.err.println("One or more fields are missing from the Properties files: indexOfZip, indexOfMarketValue, indexOfArea");
				return null;
			}
			
			while ((line = buffReader.readLine()) != null) {
				//remove comma inside quote
				line = removeCommaInsideQuote(line);
	       
				String[] words = line.split(",", -1);
				
				String marketValue = words[indexOfMarketValue];
				
				String area = words[indexOfArea];
				
				String zipcode = words[indexOfZip];
				
				if(zipcode.isEmpty()) {
					continue;
				}
				else if (!zipcode.matches("^[\\d-]+$")) {
					continue;
				}

				if(zipcode.length() < 5) {
					continue;
				}
				
				zipcode = zipcode.substring(0, 5);
				
				Property propertyToAdd = new Property(marketValue, area, zipcode);
				
				if(!ret.containsKey(zipcode)) {
					
					ArrayList<Property> properties = new ArrayList<>();
					
					properties.add(propertyToAdd);
					
					ret.put(zipcode, properties);
				}
				else {
					ret.get(zipcode).add(propertyToAdd);
				}
				
				
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		return ret;
	}
	
	private String removeCommaInsideQuote(String text) {
		
		StringBuilder newText = new StringBuilder();
		
		//the queue indices will store all indices for quotation marks
		Queue<Integer> indices = new LinkedList<>();
		
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '\"') {
				indices.add(i);
			}
		}
		
		int size = indices.size();
		
		//if the text doesn't contain quotes or quotation marks don't come in pairs
		if(size <= 1) {
			return text;
		}
	   
		int start = 0;
		int startQuote = -1;
		int endQuote = -1;
		
		do {
			startQuote = indices.poll();
			
			if(start >= startQuote) {
				break;
			}
			
			if(Character.isDigit(text.charAt(startQuote - 1))  || (text.charAt(startQuote - 1) != '\"' && text.charAt(startQuote - 1 ) != ',')){   //skip location data like 1/5", this " is not a start quotation mark
				continue;
			}
			
			newText.append(text.substring(start, startQuote));
			
			if(indices.isEmpty()) {
				break;
			}
			
			endQuote = indices.poll();
			
			//append quote and remove comma inside quote
			newText.append(text.substring(startQuote, endQuote).replace(",", " "));
			
			start = endQuote;
			

		} while(!indices.isEmpty() && start < text.length());
		
	   //no quotation in this row
	   if(endQuote == -1) {
		   return text;
	   }
	   
	   newText.append(text.substring(endQuote));
	   
	   String ret = newText.toString();
	   
	   return ret;
		
	}
		
}
