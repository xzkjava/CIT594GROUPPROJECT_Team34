package edu.upenn.cit594.datamanagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.logging.Logger;

public abstract class ParkingViolationReader {
	public FileReader fileReader;
	
	public ParkingViolationReader(String fileName, Logger logger) {
		
		try {
			fileReader = new FileReader(fileName);
			logger.log(String.valueOf(System.currentTimeMillis()) + " " + fileName + " is opened to read.\n");
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	
	}
	
	public abstract List<ParkingViolation> parseParkingViolations();
}
