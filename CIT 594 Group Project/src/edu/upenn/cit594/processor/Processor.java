package edu.upenn.cit594.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
	
	// for memoizing rateZipCodes
	private TreeMap<String, Double> normalizedFine;
	
	private TreeMap<String, Double> normalizedLiveableArea;
	
	private TreeMap<String, Double> normalizedPropertyValues;
	
	private TreeMap<String, Double> normalizedPopulation;
	
	private TreeMap<String, Integer> normalizedRatings;
	
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
	
	public static Processor createProcessor(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName) {
		
		if(parkingFileType.equals("csv")){
			return new CSVProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName);
		}
		else if(parkingFileType.equals("json")){
			return new JSONProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName);
		}
		
		return null;
		
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
	
	// when user types in 6, return the normalized rankings of each neighborhood
	// these are calculated by calculating the normalized property value, fine amount, and ranking
	// which are all between 0 and 1 (the fine amount is 1 - the normalized value) since more fines is
	// less desirable. the results are averaged and converted to an integer out of 100.
	
	public SortedMap<String, Integer> rateZipCodes() {
		if (normalizedRatings.equals(null)) {
			normalizedRatings = normalizeRatings();
		}
		
		return normalizedRatings;
	}
	
	private TreeMap<String, Integer> normalizeRatings() {
		// error check for empty values first
		if (zipViolationMap.equals(null) || zipViolationMap.size() < 1) {
			return null;
		}
		
		if (zipPropertyMap.equals(null) || zipPropertyMap.size() < 1) {
			return null;
		}
		
		if (zipPopulationMap.equals(null) || zipPopulationMap.size() < 1) {
			return null;
		}
		
		HashSet<String> mutualKeys = getMutualKeys(zipPropertyMap.keySet(), zipPropertyMap.keySet());
		mutualKeys = getMutualKeys(mutualKeys, zipViolationMap.keySet());
		
		if (normalizedFine.equals(null)) {
			normalizedFine = normalizeFines(mutualKeys);
		}
		
		if (normalizedLiveableArea.equals(null)) {
			normalizedLiveableArea = normalizeLiveableArea(mutualKeys);
		}
		
		if (normalizedPropertyValues.equals(null)) {
			normalizedPropertyValues = normalizePropertyValues(mutualKeys);
		}
		
		if (normalizedPopulation.equals(null)) {
			normalizedPopulation = normalizePopulation(mutualKeys);
		}
		
		TreeMap<String, Integer> zipCodeRankings = new TreeMap<String, Integer>();
		
		Iterator<Entry<String, Double>> it = normalizedPropertyValues.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> next = it.next();
			String currentZip = next.getKey();
			double normPropVal = next.getValue();
			
			if (!normalizedFine.containsKey(currentZip) || !normalizedLiveableArea.containsKey(currentZip) || !normalizedPopulation.containsKey(currentZip)) {
				// missing data on this zipcode (in case mutualKeys has an error)
				continue;
			}
			
			double normArea = normalizedLiveableArea.get(currentZip);
			double normPop = normalizedPopulation.get(currentZip);
			double normFine = normalizedFine.get(currentZip);
			
			int ranking = (int) (100 * (normArea + normFine + normPop + normPropVal) / 4);
			
			zipCodeRankings.put(currentZip, ranking);
			
		}

		return zipCodeRankings;
	}
	
	private HashSet<String> getMutualKeys(Set<String> keySet1, Set<String> keySet2) {
		HashSet<String> mutualKeys = new HashSet<String>();
		Iterator<String> it = keySet1.iterator();
		while(it.hasNext()) {
			String nextKey = it.next();
			if (keySet2.contains(nextKey)) {
				mutualKeys.add(nextKey);
			}
		}
		
		return mutualKeys;
	}
	
	private TreeMap<String, Double> normalizeFines(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		double minFine = Double.MAX_VALUE;
		double maxFine = Double.MIN_VALUE;
		
		TreeMap<String, Double> normalizedFine = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<ParkingViolation>>> it = zipViolationMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<ParkingViolation>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			
			List<ParkingViolation> violations = next.getValue();
			
			int totalFine = 0;
			int count = 0;
			
			for (ParkingViolation p: violations) {
				
				try {
					
					// add this property's price to running total and update count
					int currentFine = p.getFine();
					totalFine = totalFine + currentFine;
					count++;
					
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
			
			double averageFine = totalFine / count;
			
			minFine = Math.min(minFine, averageFine);
			maxFine = Math.max(maxFine, averageFine);
			
			normalizedFine.put(currentZip, averageFine);
			
		}
		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Double>> it2 = normalizedFine.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			double averageFine = next.getValue();
			
			// remove the key-average pairing (and replace with key-normalized average pairing)
			normalizedFine.remove(currentZip);
			
			// normalize fines (note that less fines are better)
			double normalizedFineCurrent = 1 - (averageFine - minFine) / (maxFine - minFine) ;
			normalizedFine.put(currentZip, normalizedFineCurrent);
			
			
		}
		
		return normalizedFine;
	}
	
	private TreeMap<String, Double> normalizeLiveableArea(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		double minArea = Double.MAX_VALUE;
		double maxArea= Double.MIN_VALUE;
		
		TreeMap<String, Double> normalizedLiveableArea = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<Property>>> it = zipPropertyMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<Property>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			
			List<Property> properties = next.getValue();
			
			double totalArea = 0;
			int count = 0;
			
			for (Property p: properties) {
				
				if (!p.validateString(p.getTotalLivableArea())) {
					// invalid format for marketValue, skip this 
					continue;
				}
				
				try {
					
					// add this property's price to running total and update count
					int marketValue = Integer.parseInt(p.getTotalLivableArea());
					totalArea = totalArea + marketValue;
					count++;
					
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
			
			double averageArea = totalArea / count;
			
			minArea = Math.min(minArea, averageArea);
			maxArea = Math.max(maxArea, averageArea);
			
			normalizedLiveableArea.put(currentZip, averageArea);
			
		}
		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Double>> it2 = normalizedLiveableArea.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			double averageArea = next.getValue();
			
			// remove the key-average pairing (and replace with key-normalized average pairing)
			normalizedLiveableArea.remove(currentZip);
			
			double normalizedPropertyValue = (averageArea - minArea) / (maxArea - minArea) ;
			normalizedLiveableArea.put(currentZip, normalizedPropertyValue);
			
			
		}
		
		return normalizedLiveableArea;
	}
	
	
	
	private TreeMap<String, Double> normalizePopulation(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		int minPopulation = Integer.MAX_VALUE;
		int maxPopulation = Integer.MIN_VALUE;
		

		
		Iterator<Entry<String, Integer>> it = zipPopulationMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, Integer> next = it.next();
			int currentPopulation = next.getValue();
			
			minPopulation = Math.min(minPopulation , currentPopulation);
			maxPopulation = Math.max(maxPopulation , currentPopulation);
			
			
		}
		
		TreeMap<String, Double> normalizedPopulation = new TreeMap<String, Double>();

		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Integer>> it2 = zipPopulationMap.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Integer> next = it2.next();
			
			String currentZip = next.getKey();	
			int currentPopulation = next.getValue();
			
			double normalizedCurrentPopulation = (currentPopulation - minPopulation) / (maxPopulation - minPopulation) ;
			normalizedPropertyValues.put(currentZip, normalizedCurrentPopulation);
			
			
		}
		
		return normalizedPopulation;
	}
	
	private TreeMap<String, Double> normalizePropertyValues(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		double minPropertyValue = Double.MAX_VALUE;
		double maxPropertyValue = Double.MIN_VALUE;
		
		TreeMap<String, Double> normalizedPropertyValues = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<Property>>> it = zipPropertyMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<Property>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			
			List<Property> properties = next.getValue();
			
			double totalPrice = 0;
			int count = 0;
			
			for (Property p: properties) {
				
				if (!p.validateString(p.getMarketValue())) {
					// invalid format for marketValue, skip this 
					continue;
				}
				
				try {
					
					// add this property's price to running total and update count
					int marketValue = Integer.parseInt(p.getMarketValue());
					totalPrice = totalPrice + marketValue;
					count++;
					
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
			
			double averagePropertyValue = totalPrice / count;
			
			minPropertyValue = Math.min(minPropertyValue, averagePropertyValue);
			maxPropertyValue = Math.max(maxPropertyValue, averagePropertyValue);
			
			normalizedPropertyValues.put(currentZip, averagePropertyValue);
			
		}
		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Double>> it2 = normalizedPropertyValues.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			double averagePropertyValue = next.getValue();
			
			// remove the key-average pairing (and replace with key-normalized average pairing)
			normalizedPropertyValues.remove(currentZip);
			
			double normalizedPropertyValue = (averagePropertyValue - minPropertyValue) / (maxPropertyValue - minPropertyValue) ;
			normalizedPropertyValues.put(currentZip, normalizedPropertyValue);
			
			
		}
		
		return normalizedPropertyValues;
	}
	
	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(List<Property> properties, PropertyCalculator cal) {
		
		return cal.calculateDataPerProperty(properties);
	
	}
	

}
