package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.ParkingJSONReader;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.logging.Logger;

public class JSONProcessor extends Processor{

	public JSONProcessor(String parkingFileType, String parkingFileName, String propertyFileName,
			String populationFileName, Logger logger) {
		
		super(parkingFileType, parkingFileName, propertyFileName, populationFileName,logger);
		
	}

	@Override
	public ParkingViolationReader createReader(String parkingFileName, Logger logger) {
		
		return new ParkingJSONReader(parkingFileName, logger);
	}


}
