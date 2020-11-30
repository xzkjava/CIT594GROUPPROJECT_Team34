package edu.upenn.cit594.processor;

public class ProcessorFactory {
	
	public static Processor createProcessor(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName) {
		
		if(parkingFileType.equals("csv")){
			return new CSVProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName);
		}
		else if(parkingFileType.equals("json")){
			return new JSONProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName);
		}
		
		return null;
		
	}
	
}
