package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.datamanagement.PopulationReader;
import edu.upenn.cit594.datamanagement.PropertyReader;
import edu.upenn.cit594.logging.Logger;

public abstract class Processor {
	
	private ParkingViolationReader parkingReader;
	
	private PropertyReader propertyReader;
	
	private PopulationReader populationReader;
	
	private HashMap<String, List<ParkingViolation>> zipViolationMap = new HashMap<>();
	
	private HashMap<String, Integer> zipPopulationMap= new HashMap<>();
	
	private HashMap<String, List<Property>> zipPropertyMap = new HashMap<>();
	
	public Processor (String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName, Logger logger) {
		
		parkingReader = createReader(parkingFileType, parkingFileName, logger);
		
		propertyReader = new PropertyReader(propertyFileName, logger);
		
		populationReader = new PopulationReader(populationFileName, logger);
		
		zipViolationMap = parkingReader.parseParkingViolations();
		
		zipPopulationMap = populationReader.parsePopulation();
		
		zipPropertyMap = propertyReader.parseProperties();
	}
	
	public abstract ParkingViolationReader createReader(String parkignFileType, String parkingFilename, Logger logger);
	
	//when user types 1, run this method
	public int calculateTotalPopulation() {
		// todo
		
		return 0;
	}
	
	// when user types 2, run this method
	//need to modify this return type
	public SortedMap<String, Double> calculateFinePerCapita() {
		// todo
		return null;
	}
	
	// when user types 3, run this method
	public int calculateMarketValuePerProperty(String zipcode) {
		
		List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
		return calculateDataPerProperty(propertiesForZip, new AverageValueCalculator());
		
	}
	
	// when user types 4, run this method
	public int calculateLivableAreaPerProperty(String zipcode) {
		
		List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
		return calculateDataPerProperty(propertiesForZip, new AverageAreaCalculator());
		
	}
	
	// when user types 5, run this method
	public int calculateMarketValuePerCapita(String zipcode) {
		
		
		return 0;
	}
	
	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(List<Property> properties, PropertyCalculator cal) {
		
		return cal.calculateDataPerProperty(properties);
	
	}
	
//	// this utility method returns a list of all the properties with a given a zip code  (used in calculateDataPerProperty)
//	private List<Property> getPropertiesByZip(String zipcode) {
//		List<Property> matchingProperties = new ArrayList<Property>(); 
//		
//		for (Property p : properties) {
//			
//			if (p.getZipCode().equals(zipcode)) {
//				matchingProperties.add(p);
//			}
//			
//		}
//		
//		return matchingProperties;
//		
//	}
	
	
	

}
