package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.upenn.cit594.data.MemoizedRepo;
import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.datamanagement.ParkingViolationReader;
import edu.upenn.cit594.datamanagement.PopulationReader;
import edu.upenn.cit594.datamanagement.PropertyReader;

public abstract class Processor {
	
	private ParkingViolationReader parkingReader;
	
	private PropertyReader propertyReader;
	
	private PopulationReader populationReader;
	
	private MemoizedRepo repo;
	
//	private HashMap<String, List<ParkingViolation>> zipViolationMap;
//	
//	private HashMap<String, Integer> zipPopulationMap;
//	
//	private HashMap<String, List<Property>> zipPropertyMap;
//	
//	
//	
//	// to save memoized values
//	
//	//option 1
//	private int totalPopulation;
//	
//	private SortedMap<String, Double> finesPerCapita;
//	
//	private HashMap<String, Integer> areaPerProperty;
//	
//	private HashMap<String, Integer> marketValPerProperty;
//	
//	private HashMap<String, Integer> marketValPerCapita;
//	
//	private HashMap<String, Long> zipTotalValue;
//	
//	private HashMap<String, Integer> zipTotalPropNum;
//	
//	// for memoizing rateZipCodes
//	private TreeMap<String, Double> normalizedFine;
//	
//	private TreeMap<String, Double> normalizedLiveableArea;
//	
//	private TreeMap<String, Double> normalizedPropertyValues;
//	
//	private TreeMap<String, Double> normalizedPopulation;
//	
//	private TreeMap<String, Integer> normalizedRatings;
//	

	
	protected Processor (String parkingFileName, String propertyFileName, String populationFileName) {
		
		parkingReader = createParkingReader(parkingFileName);
		
		propertyReader = new PropertyReader(propertyFileName);
		
		populationReader = new PopulationReader(populationFileName);
		
		repo = MemoizedRepo.getInstance();
		
		repo.setZipViolationMap(parkingReader.parseParkingViolations());
		
		repo.setZipPopulationMap(populationReader.parsePopulation());
		
		repo.setZipPropertyMap(propertyReader.parseProperties());
		
//		// initialize containers to save memoized values
//		finesPerCapita = new TreeMap<>();
//		
//		areaPerProperty = new HashMap<>();
//		
//		marketValPerProperty = new HashMap<>();
//		
//		marketValPerCapita = new HashMap<>();
//		
//		totalPopulation = -1;
	}
	
	
	
	public abstract ParkingViolationReader createParkingReader(String parkingFilename);
	
	//when user types 1, run this method
	public int calculatePopulation() {
		
		int totalPopulation = repo.getTotalPopulation();
		
		// otherwise calculate value, add it to saved values, and return its value
		if (totalPopulation == -1) {
			
			PopulationCalculator cal = new PopulationCalculator();
			
			totalPopulation = cal.computePopulation();
		}

				
		
		return totalPopulation;
	}
	
	
	// when user types 2, run this method
	public SortedMap<String, Double> calculateFinePerCapita() {
		SortedMap<String, Double> finesPerCapita = repo.getFinesPerCapita();
		// otherwise calculate value, add it to saved values, and return its value
		if (finesPerCapita == null) {
			ViolationCalculator cal = new ViolationCalculator();
			finesPerCapita = cal.computeFinePerCapita();
		}
			
		
		return finesPerCapita;
	}
	
	
	
	// when user types 3, run this method
	
	public int calculateValuePerProperty(String zipcode) {
		HashMap<String, Integer> marketValPerProperty = repo.getMarketValPerProperty();
		if (marketValPerProperty != null && marketValPerProperty.containsKey(zipcode)) {
			// value already calculated previously
			return marketValPerProperty.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		ValueCalculator cal = new ValueCalculator();
		
		return calculateDataPerProperty(zipcode, cal);
	}
	

	
	// when user types 4, run this method
	public int calculateAreaPerProperty(String zipcode) {
		
		HashMap<String, Integer> areaPerProperty = repo.getAreaPerProperty();
		if (areaPerProperty != null && areaPerProperty.containsKey(zipcode)) {
			// value already calculated previously
			return areaPerProperty.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		AreaCalculator cal = new AreaCalculator();
		
		return calculateDataPerProperty(zipcode, cal);
		
	}
	
	// when user types 5, run this method
	public int calculateValuePerCapita(String zipcode) {
		HashMap<String, Integer> marketValPerCapita = repo.getMarketValPerCapita();
		if (marketValPerCapita != null && marketValPerCapita.containsKey(zipcode)) {
			// value already calculated previously
			return marketValPerCapita.get(zipcode);
		}
		
		// otherwise calculate value, add it to saved values, and return its value
		ValueCalculator cal = new ValueCalculator();
		
		return cal.computeValuePerCapita(zipcode);
	}
	

	
	// when user types in 6, return the normalized rankings of each neighborhood
	// These are calculated by calculating the normalized property value, livable area, fine amount, and
	// population resulting in a value between 0 and 1 (the fine amount is 1 - the normalized value 
	// since more fines is less desirable. the results are averaged and converted to an integer out of 100.
	
	public SortedMap<String, Integer> rateZipCodes() {
		TreeMap<String, Integer> normalizedRatings = repo.getNormalizedRatings();
		if (normalizedRatings == null) {
			// value already exists, print this out
			RankingNormalizer normalizer = new RankingNormalizer();
			normalizedRatings = normalizer.normalizeRatings();
		}
		
		return normalizedRatings;
	}
	
	
	
	//this utility method will be called in calculateMarketValuePerHome and calculateLivableAreaPerHome methods
	private int calculateDataPerProperty(String zipcode, PropertyCalculator cal) {
		
		return cal.calculateValuePerProperty(zipcode);
	
	}
	
   public static Processor createProcessor(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName) {
	   if(parkingFileType.equals("csv")){
		   return new CSVProcessor(parkingFileName, propertyFileName, populationFileName);
	   }
	   return new JSONProcessor(parkingFileName, propertyFileName, populationFileName);
   }
}
