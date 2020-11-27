package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.logging.Logger;

public class PropertyReader {
	private BufferedReader buffReader;
	
	public PropertyReader(String fileName, Logger logger) {
		
		try {
			buffReader = new BufferedReader(new FileReader(fileName));
			
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
			
			String[ ] wordsInLine = line.split(",");
			
			int indexOfZip = 0;
			
			int indexOfMarketValue = 0;
			
			int indexOfArea = 0;
			
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
			
			while ((line = buffReader.readLine()) != null) {
				
				Pattern pattern = Pattern.compile("\".+\"");
				Matcher matcher = pattern.matcher(line);
				
				if(matcher.matches()) {
					matcher.replaceAll("\" \"");
				}
				
				String[] words = line.split(",");
				
				String marketValue = words[indexOfMarketValue];
				
				String area = words[indexOfArea];
				
				String zipcode = words[indexOfZip];
				
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return ret;
	}
}
