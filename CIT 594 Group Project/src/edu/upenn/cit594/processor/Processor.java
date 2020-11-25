package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

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
		
		if (zipPopulationMap.size() < 1) {
			// check for empty set
			return 0;
		}
		
		int population = 0;
		
		// iterate over set and sum up populations
		Iterator<Entry<String, Integer>> it = zipPopulationMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> next = it.next();
			population = population + next.getValue();
		}
		
		
		return 0;
	}
	
	// when user types 2, run this method
	//need to modify this return type
	public SortedMap<String, Double> calculateFinePerCapita() {
		
		if (zipViolationMap.size() < 1 || zipPopulationMap.size() < 1) {
			return null;
		}
		
		SortedMap<String, Double> finesByZip = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<ParkingViolation>>> it = zipViolationMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<ParkingViolation>> next = it.next();
			String currentZip = next.getKey();	
			List<ParkingViolation> violationsInZip = next.getValue();
			
			// if the population of the zip code is unknown, skip
			if (zipPopulationMap.get(currentZip) == null) {
				continue;
			}
			// if this zip code has no population, skip
			int currentPopulation = zipPopulationMap.get(currentZip);
			if (currentPopulation < 1) {
				continue;
			}
			
			int totalFines = 0;
			for (ParkingViolation p : violationsInZip) {
				if (p.getState() != "PA" || p.getZipCode() == null) {
					// skip over non-PA plates and unknown zip codes
					continue;
				}
				// otherwise, add the fine amount to total for zip code
				totalFines = totalFines + p.getFine();
			}
			
			// if total fines is 0 (or negative, skip
			if (totalFines < 1) {
				continue;
			}
			
			// calculate fines per capita and add to final set
			double finesPerCapita = ((double) totalFines) / currentPopulation; 
			finesByZip.put(currentZip, finesPerCapita);
			
		}
		
		return finesByZip;
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
