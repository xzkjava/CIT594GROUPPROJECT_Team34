package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.ParkingCSVReader;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.logging.Logger;

public class CSVProcessor extends Processor{

	public CSVProcessor(String parkingFileName, String propertyFileName,
			String populationFileName) {
		
		super(parkingFileName, propertyFileName, populationFileName);
		
	}


	@Override
	public ParkingViolationReader createParkingReader(String parkingFileName) {
		
		return new ParkingCSVReader(parkingFileName);
	}
}
