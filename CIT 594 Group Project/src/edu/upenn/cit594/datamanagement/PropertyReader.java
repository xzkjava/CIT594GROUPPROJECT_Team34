package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

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
	
	
	public List<Property> parseProperties(){
		//TODO
		return null;
	}
}
