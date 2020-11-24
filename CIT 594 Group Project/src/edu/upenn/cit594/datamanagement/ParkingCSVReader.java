package edu.upenn.cit594.datamanagement;

import java.util.List;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.logging.Logger;

public class ParkingCSVReader extends ParkingViolationReader {

	public ParkingCSVReader(String fileName, Logger logger) {
		
		super(fileName, logger);
		
	}

	@Override
	public List<ParkingViolation> parseParkingViolations() {
		// TODO Auto-generated method stub
		return null;
	
	}

}
