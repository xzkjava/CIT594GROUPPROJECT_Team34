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

public abstract class Processor {
	
	private ParkingViolationReader parkingReader;
	
	private PropertyReader propertyReader;
	
	private PopulationReader populationReader;
	
	private HashMap<String, List<ParkingViolation>> zipViolationMap = new HashMap<>();
	
	private HashMap<String, Integer> zipPopulationMap= new HashMap<>();
	
	private HashMap<String, List<Property>> zipPropertyMap = new HashMap<>();
	
	
	// to save memoized values
	private int totalPopulation;
	
	private SortedMap<String, Double> finesPerCapita;
	
	private HashMap<String, Integer> areaPerProperty;
	
	private HashMap<String, Integer> marketValPerProperty;
	
	private HashMap<String, Integer> marketValPerCapita;
	
	public Processor (String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName) {
		
		parkingReader = createParkingReader(parkingFileName);
		
		propertyReader = new PropertyReader(propertyFileName);
		
		populationReader = new PopulationReader(populationFileName);
		
		zipViolationMap = parkingReader.parseParkingViolations();
		
		zipPopulationMap = populationReader.parsePopulation();
		
		zipPropertyMap = propertyReader.parseProperties();
		
		// initialize containers to save memoized values
		finesPerCapita = new TreeMap<>();
		
		areaPerProperty = new HashMap<>();
		
		marketValPerProperty = new HashMap<>();
		
		marketValPerCapita = new HashMap<>();
		
		totalPopulation = -1;
	}
	
	public abstract ParkingViolationReader createParkingReader(String parkingFilename);
	
	//when user types 1, run this method
	public int calculatePopulation() {
		if (totalPopulation == -1) {
			// value already calculated previously
			totalPopulation = computePopulation();
		}

				
		// otherwise calculate value, add it to saved values, and return its value
		return totalPopulation;
	}
	
	private int computePopulation() {
		
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
		
		return population;
		
	}
	
	// when user types 2, run this method
	public SortedMap<String, Double> calculateFinePerCapita() {
		if (finesPerCapita == null) {
			// value already calculated previously
			finesPerCapita = computeFinePerCapita();
		}

				
		// otherwise calculate value, add it to saved values, and return its value
		return finesPerCapita;
	}
	
	// helper function to compute fines per capita (only needs to run once)
	private SortedMap<String, Double> computeFinePerCapita() {
		
		if (zipViolationMap.size() < 1) {
			return null;
		}
		
		SortedMap<String, Double> finesByZip = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<ParkingViolation>>> it = zipViolationMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<ParkingViolation>> next = it.next();
			String currentZip = next.getKey();	
			
			// if the population of the zip code doesn't contain this zipcode or is unknown, skip
			if (!zipPopulationMap.containsKey(currentZip) || zipPopulationMap.get(currentZip) == null) {
				continue;
			}
			
			List<ParkingViolation> violationsInZip = next.getValue();
			
			
			// if this zipcode has no population, skip
			int currentPopulation = zipPopulationMap.get(currentZip);
			//need to clarify this following logic with instructor
			//the instruction says "not display", doesn't say "not consider when population = 0
			if (currentPopulation < 1) {
				continue;
			}
			
			int totalFines = 0;
			for (ParkingViolation p : violationsInZip) {
				if (!p.getState().equals("PA") || p.getZipCode() == null) {
					// skip over non-PA plates and unknown zip codes
					continue;
				}
				// otherwise, add the fine amount to total for zip code
				totalFines = totalFines + p.getFine();
			}
			
			// if total fines is 0 (or negative, skip)
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
	
	public int calculateValuePerProperty(String zipcode) {
		if (marketValPerProperty.containsKey(zipcode)) {
			// value already calculated previously
			return marketValPerProperty.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		int marketVal = marketValuePerPropertyHelper(zipcode);
		marketValPerProperty.put(zipcode, marketVal);
		return marketVal;
	}
	
	private int marketValuePerPropertyHelper(String zipcode) {
		
		List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
		if(propertiesForZip == null) {
			return 0;
		}
		return calculateDataPerProperty(propertiesForZip, new AverageValueCalculator());
		
	}
	
	// when user types 4, run this method
	public int calculateAreaPerProperty(String zipcode) {
		if (areaPerProperty.containsKey(zipcode)) {
			// value already calculated previously
			return areaPerProperty.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		int liveableArea = computeAreaPerProperty(zipcode);
		areaPerProperty.put(zipcode, liveableArea);
		return liveableArea;
		
	}
	
	
	private int computeAreaPerProperty(String zipcode) {
		
		List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
		if (propertiesForZip == null) {
			return 0;
		}
		return calculateDataPerProperty(propertiesForZip, new AverageAreaCalculator());
		
	}
	
	// when user types 5, run this method
	public int calculateValuePerCapita(String zipcode) {
		if (marketValPerCapita.containsKey(zipcode)) {
			// value already calculated previously
			return marketValPerCapita.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		int marketVal = computeValuePerCapita(zipcode);
		marketValPerCapita.put(zipcode, marketVal);
		return marketVal;
	}
	
	private int computeValuePerCapita(String zipcode) {
		// zipcode is not in the population file
		if(!zipPopulationMap.containsKey(zipcode)) {
			return 0;
		}
		
		int populationOfZip = zipPopulationMap.get(zipcode);

		if (populationOfZip == 0) {
			// population not found 
			return 0;
		}
		
		List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
		
		if(propertiesForZip == null) {
			return 0;
		}
		
		int totalPropertyVal = 0;	// total property value of zipcode
		
		for (Property p : propertiesForZip) {
			try {
				String marketValue = p.getMarketValue();
				
				if(!p.validateString(marketValue)) {
					continue;
				}
				
				// add property value of this property to the total
				int propertyVal = Integer.parseInt(marketValue);
				totalPropertyVal = totalPropertyVal + propertyVal;
			
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		// calculate the average per capita and return value
		return (int) totalPropertyVal / populationOfZip;
		
	}
	
	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(List<Property> properties, PropertyCalculator cal) {
		
		return cal.calculateDataPerProperty(properties);
	
	}
	

}
