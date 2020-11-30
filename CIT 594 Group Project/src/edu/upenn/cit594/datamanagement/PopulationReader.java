package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.upenn.cit594.logging.Logger;

public class PopulationReader {
	private BufferedReader buffReader;
	
	public PopulationReader(String fileName) {
		
		try {
				
			//File file = new File("src" + File.separator + fileName);
			
			buffReader = new BufferedReader (new FileReader(fileName));
		
			Logger logger = Logger.getInstance();
			
			logger.log(String.valueOf(System.currentTimeMillis()) + " " + fileName + " is opened to read.\n");
		}
		catch(FileNotFoundException e) {
			
			e.printStackTrace();
		}
	
	}
	
	//this method returns a map that associates a zipcode with the population size
	public HashMap<String, Integer> parsePopulation(){
		
		HashMap<String, Integer> ret = new HashMap<>();
		
		String line = "";
		
		try {
			while ((line = buffReader.readLine()) != null) {
				String[] wordsInLine = line.split(" ");
				
				String zipcode = wordsInLine[0];
				
				int size = 0;
				
				try {
					size = Integer.parseInt(wordsInLine[1]);
				}
				catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
				ret.put(zipcode, size);
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

}
