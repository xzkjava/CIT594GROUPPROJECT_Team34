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
	
    //the three files to store the parsed data from three input files
	private HashMap<String, List<ParkingViolation>> zipViolationMap;
	
	private HashMap<String, Integer> zipPopulationMap;
	
	private HashMap<String, List<Property>> zipPropertyMap;
	
	
	
	// to save memoized values
	
	//option 1
	private int totalPopulation = -1;
	
	private SortedMap<String, Double> finesPerCapita;
	
	private HashMap<String, Integer> areaPerProperty;
	
	private HashMap<String, Integer> marketValPerProperty;
	
	private HashMap<String, Integer> marketValPerCapita;
	
	private HashMap<String, Double> zipTotalValue;
	
	private HashMap<String, Integer> zipPropNumForValue;
	
	private HashMap<String, Integer> zipPropNumForArea;
	
	private HashMap<String, Double> zipTotalArea;
	
	private HashMap<String, Integer> zipTotalFines;
	
	private HashMap<String, Integer> zipNumOfViolations;
	
	// for memoizing rateZipCodes
	private TreeMap<String, Double> normalizedFine;
	
	private TreeMap<String, Double> normalizedLiveableArea;
	
	private TreeMap<String, Double> normalizedPropertyValues;
	
	private TreeMap<String, Double> normalizedPopulation;
	
	private TreeMap<String, Integer> normalizedRatings;
	

	
	protected Processor (String parkingFileName, String propertyFileName, String populationFileName) {
		
		parkingReader = createParkingReader(parkingFileName);
		
		propertyReader = new PropertyReader(propertyFileName);
		
		populationReader = new PopulationReader(populationFileName);
		
		
		zipViolationMap = parkingReader.parseParkingViolations();
		
		zipPopulationMap = populationReader.parsePopulation();
		
		zipPropertyMap = propertyReader.parseProperties();

	}
	
	
	
	public abstract ParkingViolationReader createParkingReader(String parkingFilename);
	
	//when user types 1, run this method
	public int calculatePopulation() {
		
		// otherwise calculate value, add it to saved values, and return its value
		if (totalPopulation == -1) {
			
			totalPopulation = computePopulation();

		}	
		
		return totalPopulation;
	}
	


	// when user types 2, run this method
	public SortedMap<String, Double> calculateFinePerCapita() {

		// otherwise calculate value, add it to saved values, and return its value
		if (finesPerCapita == null) {
			
			finesPerCapita = computeFinePerCapita();
		}
			
		
		return finesPerCapita;
	}

	
	// when user types 3, run this method
	
	public int calculateValuePerProperty(String zipcode) {

		if (marketValPerProperty != null && marketValPerProperty.containsKey(zipcode)) {
			// value already calculated previously
			return marketValPerProperty.get(zipcode);
		}
		
		if(marketValPerProperty == null) {
			marketValPerProperty = new HashMap<>();
		}
		
		if(!zipPopulationMap.containsKey(zipcode) || !zipPropertyMap.containsKey(zipcode)) {
		    
			marketValPerProperty.put(zipcode, 0);
		    
			return 0;
		}
		
		double totalValue = -1;
		int numOfProp = -1;
		int averageVal = 0;
		
		if(zipPropNumForValue == null) {
			zipPropNumForValue = new HashMap<>();
		}
		if(zipPropNumForValue.containsKey(zipcode)) {
			numOfProp = zipPropNumForValue.get(zipcode);
		}
		
		if( numOfProp == 0) {
			marketValPerProperty.put(zipcode, 0);
			return 0;
		}
		
		if (zipTotalValue == null) {
			zipTotalValue = new HashMap<>();
		}
		
		if(zipTotalValue.containsKey(zipcode)) {
			totalValue = zipTotalValue.get(zipcode);
		}		
		
		if(totalValue != -1 && numOfProp != -1) {
			averageVal = (int) totalValue / numOfProp;
			marketValPerProperty.put(zipcode, averageVal);
			return averageVal;
		}
		
		List<Property> properties = zipPropertyMap.get(zipcode);
		
		if (properties == null) {
			
			marketValPerProperty.put(zipcode, 0);
			
			return 0;
		}
	    // otherwise calculate value, add it to saved values, and return its value 
		averageVal = calculateDataPerProperty(zipcode, properties, zipTotalValue, zipPropNumForValue, new ValueCalculator());
				
				
		marketValPerProperty.put(zipcode, averageVal);
		
		return  averageVal;
	}
	

	
	// when user types 4, run this method
	public int calculateAreaPerProperty(String zipcode) {
		
		if (areaPerProperty != null && areaPerProperty.containsKey(zipcode)) {

			// value already calculated previously
			return areaPerProperty.get(zipcode);
		}
		
		if(areaPerProperty == null) {
			areaPerProperty = new HashMap<>();
		}
		
		if( !zipPopulationMap.containsKey(zipcode) || !zipPropertyMap.containsKey(zipcode)) {
			
			areaPerProperty.put(zipcode, 0);
			
			return 0;
		}
		
		double totalArea = -1;
		int count = -1;
		int averageArea = 0;
		
		if(zipPropNumForArea == null) {
			zipPropNumForArea = new HashMap<>();
		}
		
		if(zipPropNumForArea.containsKey(zipcode)) {
			count = zipPropNumForArea.get(zipcode);
		}
		
		if(count == 0) {
			areaPerProperty.put(zipcode, 0);
			
			return 0;
		}
		
		if(zipTotalArea == null) {
			zipTotalArea = new HashMap<>();
		}
		if(zipTotalArea.containsKey(zipcode)) {
			totalArea = zipTotalArea.get(zipcode);
		}
		
		if(totalArea != -1 && count != -1) {
			averageArea = (int) totalArea / count;
			areaPerProperty.put(zipcode, averageArea);
			return averageArea;
		}
		// otherwise calculate value, add it to saved values, and return its value
		
		List<Property> properties = zipPropertyMap.get(zipcode);
		
		if(properties == null) {
			areaPerProperty.put(zipcode, 0);
			return 0;
		}
		
		averageArea = calculateDataPerProperty(zipcode, properties, zipTotalArea, zipPropNumForArea, new AreaCalculator());	
		
		areaPerProperty.put(zipcode, averageArea);
		
		return averageArea;

	}
	
	
	// when user types 5, run this method
	public int calculateValuePerCapita(String zipcode) {
		
		if (marketValPerCapita != null && marketValPerCapita.containsKey(zipcode)) {

			// value already calculated previously
			return marketValPerCapita.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		int averageVal = computeValuePerCapita(zipcode);
		
		marketValPerCapita.put(zipcode, averageVal);
		
		return averageVal;
	}
	

	
	// when user types in 6, return the normalized rankings of each neighborhood
	// These are calculated by calculating the normalized property value, livable area, fine amount, and
	// population resulting in a value between 0 and 1 (the fine amount is 1 - the normalized value 
	// since more fines is less desirable. the results are averaged and converted to an integer out of 100.
	
	public SortedMap<String, Integer> rateZipCodes() {

		if (normalizedRatings == null) {
			// value already exists, print this out
			normalizedRatings = normalizeRatings();
		}
		
		return normalizedRatings;
	}
	

	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(String zipcode, List<Property> properties, HashMap<String, Double> zipTotalMap, HashMap<String, Integer> zipCountMap, PropertyCalculator cal) {
		
		return cal.calculateValuePerProperty(zipcode, properties, zipTotalMap, zipCountMap);
	
	}
	
	public static Processor createProcessor(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName) {
	   if(parkingFileType.equals("csv")){
		   return new CSVProcessor(parkingFileName, propertyFileName, populationFileName);
	   }
	   return new JSONProcessor(parkingFileName, propertyFileName, populationFileName);
   }
   
   //the following methods are private utility methods.
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
		
		//save total population 
		totalPopulation = population;
		
		return population;
		
	}
   
   private SortedMap<String, Double> computeFinePerCapita() {
		
		if (zipViolationMap == null || zipViolationMap.size() < 1) {
				return null;
			}
			
		SortedMap<String, Double> finesByZip = new TreeMap<String, Double>();
			
		Iterator<Entry<String, List<ParkingViolation>>> it = zipViolationMap.entrySet().iterator();
		while (it.hasNext()) {
				
				Entry<String, List<ParkingViolation>> next = it.next();
				String currentZip = next.getKey();	
				
				// if the population of the zip code doesn't contain this zipcode or is unknown, skip
				if(!zipPopulationMap.containsKey(currentZip)) {
					continue;
				}
				
				int currentPopulation = zipPopulationMap.get(currentZip);
				
				
				if (currentPopulation == 0) {
					continue;
				}
				
				List<ParkingViolation> violationsInZip = next.getValue();
				
				int totalFines = 0;
				for (ParkingViolation p : violationsInZip) {
					if (p.getZipCode() == null || !p.getState().equals("PA") ) {
						// skip over non-PA plates and unknown zip codes
						continue;
					}
					// otherwise, add the fine amount to total for zip code
					totalFines = totalFines + p.getFine();
				}
				
				if(zipTotalFines == null) {
					zipTotalFines = new HashMap<>();
				}
				// if total fines is 0 (or negative, skip)
				if (totalFines == 0) {
					zipTotalFines.put(currentZip, 0);
					continue;
				}
				
				// calculate fines per capita and add to final set
				double finesPerCapita = ((double) totalFines) / currentPopulation; 
				
				finesByZip.put(currentZip, finesPerCapita);
				
				zipTotalFines.put(currentZip,totalFines);
				
			}
			
			finesPerCapita = finesByZip;
			
			return finesByZip;
		}
   
   private int computeValuePerCapita(String zipcode) {
	    
		// zipcode is not in the population file
		if(zipPopulationMap != null && !zipPopulationMap.containsKey(zipcode)) {
			return 0;
		}
		
		int populationOfZip = zipPopulationMap.get(zipcode);

		if (populationOfZip == 0) {
			// population not found 
			return 0;
		}
		
		if(zipPropertyMap != null && !zipPropertyMap.containsKey(zipcode)) {
			return 0; 
		}
		
		
		double totalPropertyVal = -1;	
		int averageVal = -1;// total property value of zipcode
		
		if (zipTotalValue == null) {
			zipTotalValue = new HashMap<>();
		}
		
		if(zipTotalValue.containsKey(zipcode)) {
			totalPropertyVal = zipTotalValue.get(zipcode);
		}
		
		if(marketValPerCapita == null) {
			
			marketValPerCapita = new HashMap<>();
		}
		
		if(totalPropertyVal != -1) {
			averageVal = (int) totalPropertyVal / populationOfZip;
			marketValPerCapita.put(zipcode, averageVal);
			return averageVal;
		}
		
		else {
			
			List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
			
			int numOfProp = 0;
			totalPropertyVal = 0;
			
			for (Property p : propertiesForZip) {
				
					String marketValue = p.getMarketValue();
					
					
					if(marketValue == null || marketValue.isEmpty() ||!marketValue.matches("^[+-]?[\\d.]+$")) {
						continue;
					}
					
					// add property value of this property to the total
					
					double propertyVal = 0;
					
					
					try {
						
						propertyVal = Double.parseDouble(marketValue);
					
					    totalPropertyVal = totalPropertyVal + propertyVal;
					   
					    numOfProp++;
				
					} catch(NumberFormatException e) {
						
						e.printStackTrace();
				}
			}
			
			zipTotalValue.put(zipcode, totalPropertyVal);
			
			if(zipPropNumForValue == null) {
				zipPropNumForValue = new HashMap<>();
			}
			
			zipPropNumForValue.put(zipcode, numOfProp);
		}
		
		
		// calculate the average per capita and return value
		averageVal = (int) totalPropertyVal / populationOfZip;
		
		marketValPerCapita.put(zipcode, averageVal);
		
		return averageVal;	
		
	}
   
   private TreeMap<String, Integer> normalizeRatings() {
		
		// error check for empty values first
		if (zipViolationMap == null || zipViolationMap.size() < 1) {
			return null;
		}
		
		if (zipPropertyMap == null || zipPropertyMap.size() < 1) {
			return null;
		}
		
		if (zipPopulationMap == null || zipPopulationMap.size() < 1) {
			return null;
		}
		
		HashSet<String> mutualKeys = getMutualKeys(zipPropertyMap.keySet(), zipPropertyMap.keySet());
		mutualKeys = getMutualKeys(mutualKeys, zipViolationMap.keySet());
		
		if (normalizedFine == null) {
			normalizedFine = normalizeFines(mutualKeys);
		}
		
		if (normalizedLiveableArea == null) {
			normalizedLiveableArea = normalizeLiveableArea(mutualKeys);
		}
		
		if (normalizedPropertyValues == null) {
			normalizedPropertyValues = normalizePropertyValues(mutualKeys);
		}
		
		if (normalizedPopulation == null) {
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
		
		TreeMap<String, Double> averageFine = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<ParkingViolation>>> it = zipViolationMap.entrySet().iterator();
		while (it.hasNext()) {
			
			
			Entry<String, List<ParkingViolation>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			int totalFine = -1;
			int count = -1;
			
			if (zipTotalFines == null) {
				zipTotalFines = new HashMap<>();
			}
			
			if(zipTotalFines.containsKey(currentZip)) {
				totalFine = zipTotalFines.get(currentZip);
			}
			
			
			if(zipNumOfViolations == null) {
				zipNumOfViolations = new HashMap<>();
			}
			
			if(zipNumOfViolations.containsKey(currentZip)) {
				count = zipNumOfViolations.get(currentZip);
			}
			
			if (totalFine == -1 && count == -1) {
				
				List<ParkingViolation> violations = next.getValue();
				
				 totalFine = 0;
				 count = 0;
				
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
				
				zipTotalFines.put(currentZip, totalFine);
				zipNumOfViolations.put(currentZip, count);
			}
			
			
			double avgFine = totalFine / count;
			
			minFine = Math.min(minFine, avgFine);
			maxFine = Math.max(maxFine, avgFine);
			
		   averageFine.put(currentZip, avgFine);
			
		}
		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		TreeMap<String, Double> normalizedFine = new TreeMap<String, Double>();
		
		Iterator<Entry<String, Double>> it2 = averageFine.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			double avgFine = next.getValue();
			
			// normalize fines (note that less fines are better)
			double normalizedFineCurrent = 1 - ((double)(avgFine - minFine)) / (maxFine - minFine) ;
			normalizedFine.put(currentZip, normalizedFineCurrent);
			
			
		}
		
		return normalizedFine;
	}
	
	private TreeMap<String, Double> normalizeLiveableArea(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		double minArea = Double.MAX_VALUE;
		double maxArea= Double.MIN_VALUE;
		
		TreeMap<String, Double> averageLiveableArea = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<Property>>> it = zipPropertyMap.entrySet().iterator();
		while (it.hasNext()) {
			
			Entry<String, List<Property>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			double totalArea = -1;
			int count = -1;
			double averageArea = 0;
			
			if(zipTotalArea == null) {
				zipTotalArea = new HashMap<>();
			}
			
			if(zipTotalArea.containsKey(currentZip)) {
				totalArea = zipTotalArea.get(currentZip);
			}
			
			if(zipPropNumForArea == null) {
				zipPropNumForArea = new HashMap<>();
			}
			
			if(zipPropNumForArea.containsKey(currentZip)) {
				count = zipPropNumForArea.get(currentZip);
			}
			
			if(totalArea == -1 && count == -1) {
				totalArea = 0;
				count = 0;
				List<Property> properties = next.getValue();
						
				for (Property p: properties) {
					String areaStr = p.getTotalLivableArea();
					
					if(areaStr == null ||areaStr.isEmpty() || !areaStr.matches("^\\d+$")) {
						continue;
					}
					
					
					try {
						
						// add this property's price to running total and update count
						int marketValue = Integer.parseInt(areaStr);
						totalArea = totalArea + marketValue;
						count++;
						
					}catch(NumberFormatException e) {
						e.printStackTrace();
					}
					
				}
				
               zipTotalArea.put(currentZip, totalArea);
			    zipPropNumForArea.put(currentZip, count);
			}
			
			if(count == 0) {
				averageArea = 0;
			}
			else {
				averageArea = (double) totalArea / count;
			}
			
			
			minArea = Math.min(minArea, averageArea);
			maxArea = Math.max(maxArea, averageArea);
			
			averageLiveableArea.put(currentZip, averageArea);
			
			
		}
		
		TreeMap<String, Double> normalizedLiveableArea = new TreeMap<String, Double>();
		
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Double>> it2 = averageLiveableArea.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			double averageArea = next.getValue();
			
			
			double normalizedPropertyValue = ((double)(averageArea - minArea)) / (maxArea - minArea) ;
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
			
			double normalizedCurrentPopulation = ((double)(currentPopulation - minPopulation)) / (maxPopulation - minPopulation) ;
			normalizedPopulation.put(currentZip, normalizedCurrentPopulation);
			
			
		}
		
		return normalizedPopulation;
	}
	
	private TreeMap<String, Double> normalizePropertyValues(HashSet<String> mutualKeys) {
		
		// first pass, compute average property value for each property
		// and also keep track of minimum and maximum
		double minPropertyValue = Double.MAX_VALUE;
		double maxPropertyValue = Double.MIN_VALUE;
		
		TreeMap<String, Double> averagePropertyValues = new TreeMap<String, Double>();
		
		Iterator<Entry<String, List<Property>>> it = zipPropertyMap.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Entry<String, List<Property>> next = it.next();
			String currentZip = next.getKey();	
			
			if (!mutualKeys.contains(currentZip)) {
				// this zipcode is not in all 3 hashmaps, skip it.
				continue;
			}
			
			double totalPrice = -1;
			int count = -1;
			double averagePrice = 0;
			
			if(zipTotalValue == null) {
				zipTotalValue = new HashMap<>();
			}
			
			if(zipTotalValue.containsKey(currentZip)) {
				totalPrice = zipTotalValue.get(currentZip);
			}
			
			if(zipPropNumForValue == null) {
				zipPropNumForValue = new HashMap<>();
			}
			
			if(zipPropNumForValue.containsKey(currentZip)) {
				count = zipPropNumForValue.get(currentZip);
			}
			
			if(totalPrice == -1 && count == -1) {
				List<Property> properties = next.getValue();	
				totalPrice = 0;
				count = 0;
				
				for (Property p: properties) {
					String valueStr  = p.getMarketValue();
					
					if(valueStr == null || valueStr.isEmpty() || !valueStr.matches("^\\d+$")) {
						continue;
					}
				
					try {
						
						// add this property's price to running total and update count
						int marketValue = Integer.parseInt(valueStr);
						totalPrice = totalPrice + marketValue;
						count++;
						
					}catch(NumberFormatException e) {
						e.printStackTrace();
					}
					
				}
				
			    zipTotalValue.put(currentZip, totalPrice);
			    zipPropNumForValue.put(currentZip, count);
			}
			
			if(count == 0) {
				averagePrice = 0;
			}
			else {
				averagePrice = (double) totalPrice / count;
			}
			
			
			minPropertyValue = Math.min(minPropertyValue, averagePrice);
			maxPropertyValue = Math.max(maxPropertyValue, averagePrice);
			
			averagePropertyValues.put(currentZip, averagePrice);
			
			
		}
		
		
		TreeMap<String, Double> normalizedPropertyValues = new TreeMap<String, Double>();
		// second pass (on valid values), normalize the averages based on minimum and maximum
		Iterator<Entry<String, Double>> it2 = averagePropertyValues.entrySet().iterator();
		while (it2.hasNext()) {
		
			Entry<String, Double> next = it2.next();
			
			String currentZip = next.getKey();
			
			double averagePropertyValue = next.getValue();
			
			double normalizedPropertyValue = ((double)(averagePropertyValue - minPropertyValue)) / (maxPropertyValue - minPropertyValue) ;
			
			normalizedPropertyValues.put(currentZip, normalizedPropertyValue);
			
			
		}
		
		return normalizedPropertyValues;
	}
		
}
