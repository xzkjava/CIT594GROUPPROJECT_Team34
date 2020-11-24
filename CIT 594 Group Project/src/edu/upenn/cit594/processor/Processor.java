package edu.upenn.cit594.processor;


import java.util.ArrayList;
import java.util.List;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.data.Population;
import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.datamanagement.PopulationReader;
import edu.upenn.cit594.datamanagement.PropertyReader;
import edu.upenn.cit594.logging.Logger;

public abstract class Processor {
	
	private ParkingViolationReader parkingReader;
	
	private PropertyReader propertyReader;
	
	private PopulationReader populationReader;
	
	private List<ParkingViolation> violations = new ArrayList<>();
	
	private List<Property> properties = new ArrayList<>();
	
	private List<Population> populations = new ArrayList<>();
	
	public Processor (String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName, Logger logger) {
		
		parkingReader = createReader(parkingFileType, parkingFileName, logger);
		
		propertyReader = new PropertyReader(propertyFileName, logger);
		
		populationReader = new PopulationReader(populationFileName, logger);
		
		violations = parkingReader.parseParkingViolations();
		
		properties = propertyReader.parseProperties();
		
		populations = populationReader.parsePopulation();
	}
	
	public abstract ParkingViolationReader createReader(String parkignFileType, String parkingFilename, Logger logger);
	
	//when user types 1, run this method
	public int calculateTotalPopulation() {
		//todo
		
		return 0;
	}
	
	//when user tyes 2, run this method
	public double calculateFinePerCapita() {
		//todo
		return 0.0000;
	}
	
	//when user types 3, run this method
	public int calculateMarketValuePerProperty(String zipcode) {
		List<Property> propertiesForZip = new ArrayList<>();
		//TODO: populate propertiesForZip list
		
		return calculateDataPerProperty(propertiesForZip, new AverageValueCalculator());
	}
	
	//when user types 4, run this method
	public int calculateLivableAreaPerProperty(String zipcode) {
		List<Property> propertiesForZip = new ArrayList<>();
		//todo: populate propertiesForZip list
		
		return calculateDataPerProperty(propertiesForZip, new AverageAreaCalculator());
	}
	
	//when user types 5, run this method
	public int calculateMarketValuePerCapita(String zipcode) {
		//todo
		
		return 0;
	}
	
	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(List<Property> properties, PropertyCalculator cal) {
		
		return cal.calculateDataPerProperty(properties);
	
	}
	
	
	

}
