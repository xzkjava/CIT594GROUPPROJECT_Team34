package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.ParkingJSONReader;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.logging.Logger;

public class JSONProcessor extends Processor{

	public JSONProcessor(String parkingFileName, String propertyFileName,
			String populationFileName) {
		
		super(parkingFileName, propertyFileName, populationFileName);
		
	}

	@Override
	public ParkingViolationReader createParkingReader(String parkingFileName) {
		
		return new ParkingJSONReader(parkingFileName);
	}


}
