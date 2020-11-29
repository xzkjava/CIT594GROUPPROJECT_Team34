package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.ParkingCSVReader;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.logging.Logger;

public class CSVProcessor extends Processor{

	public CSVProcessor(String parkingFileType, String parkingFileName, String propertyFileName,
			String populationFileName, Logger logger) {
		
		super(parkingFileType, parkingFileName, propertyFileName, populationFileName, logger);
		
	}


	@Override
	public ParkingViolationReader createParkingReader(String parkingFileName, Logger logger) {
		
		return new ParkingCSVReader(parkingFileName, logger);
	}
}
