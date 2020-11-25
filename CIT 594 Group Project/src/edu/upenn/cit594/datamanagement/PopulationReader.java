package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.Population;
import edu.upenn.cit594.logging.Logger;

public class PopulationReader {
	private BufferedReader buffReader;
	
	public PopulationReader(String fileName, Logger logger) {
		
		try {
			buffReader = new BufferedReader (new FileReader(fileName));
			
			logger.log(String.valueOf(System.currentTimeMillis()) + " " + fileName + " is opened to read.\n");
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}
	
	//this method returns a map that associates a zipcode with the population size
	public HashMap<String, Integer> parsePopulation(){
		//TODO
		return null;
	}

}
