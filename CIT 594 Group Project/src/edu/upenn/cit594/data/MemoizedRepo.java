package edu.upenn.cit594.data;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MemoizedRepo {
	
	
	private HashMap<String, List<ParkingViolation>> zipViolationMap = new HashMap<>();
	
	private HashMap<String, Integer> zipPopulationMap = new HashMap<>();
	
	private HashMap<String, List<Property>> zipPropertyMap = new HashMap<>();
	
	
	
	// to save memoized values
	
	//option 1
	private int totalPopulation = -1;
	
	private SortedMap<String, Double> finesPerCapita;
	
	private HashMap<String, Integer> areaPerProperty = new HashMap<>();
	
	private HashMap<String, Integer> marketValPerProperty = new HashMap<>();
	
	private HashMap<String, Integer> marketValPerCapita = new HashMap<>();
	
	private HashMap<String, Double> zipTotalValue = new HashMap<>();
	
	private HashMap<String, Integer> zipPropNumForValue = new HashMap<>();
	
	private HashMap<String, Integer> zipPropNumForArea = new HashMap<>();
	
	private HashMap<String, Double> zipTotalArea = new HashMap<>();
	
	private HashMap<String, Integer> zipTotalFines = new HashMap<>();
	
	private HashMap<String, Integer> zipNumOfViolations = new HashMap<>();
	
	// for memoizing rateZipCodes
	private TreeMap<String, Double> normalizedFine;
	
	private TreeMap<String, Double> normalizedLiveableArea;
	
	private TreeMap<String, Double> normalizedPropertyValues;
	
	private TreeMap<String, Double> normalizedPopulation;
	
	private TreeMap<String, Integer> normalizedRatings;
	
	private static MemoizedRepo instance;
	
	
	private MemoizedRepo() {
		
	}
	public static MemoizedRepo getInstance() {
		if(instance == null) {
			instance = new MemoizedRepo();
		}
		
		return instance;
	}

	public HashMap<String, List<ParkingViolation>> getZipViolationMap() {
		return zipViolationMap;
	}


	public HashMap<String, Integer> getZipPopulationMap() {
		return zipPopulationMap;
	}


	public HashMap<String, List<Property>> getZipPropertyMap() {
		return zipPropertyMap;
	}


	public int getTotalPopulation() {
		return totalPopulation;
	}


	public SortedMap<String, Double> getFinesPerCapita() {
		return finesPerCapita;
	}


	public HashMap<String, Integer> getAreaPerProperty() {
		return areaPerProperty;
	}


	public HashMap<String, Integer> getMarketValPerProperty() {
		return marketValPerProperty;
	}


	public HashMap<String, Integer> getMarketValPerCapita() {
		return marketValPerCapita;
	}

	public HashMap<String, Double> getZipTotalValue() {
		return zipTotalValue;
	}


	public HashMap<String, Integer> getZipPropNumForValue() {
		return zipPropNumForValue;
	}


	public TreeMap<String, Double> getNormalizedFine() {
		return normalizedFine;
	}


	public TreeMap<String, Double> getNormalizedLiveableArea() {
		return normalizedLiveableArea;
	}


	public TreeMap<String, Double> getNormalizedPropertyValues() {
		return normalizedPropertyValues;
	}


	public TreeMap<String, Double> getNormalizedPopulation() {
		return normalizedPopulation;
	}

	
	public void setZipViolationMap(HashMap<String, List<ParkingViolation>> zipViolationMap) {
		this.zipViolationMap = zipViolationMap;
	}


	public void setZipPopulationMap(HashMap<String, Integer> zipPopulationMap) {
		this.zipPopulationMap = zipPopulationMap;
	}


	public void setZipPropertyMap(HashMap<String, List<Property>> zipPropertyMap) {
		this.zipPropertyMap = zipPropertyMap;
	}


	
	public void setTotalPopulation(int totalPopulation) {
		this.totalPopulation = totalPopulation;
	}


	
	public void setFinesPerCapita(SortedMap<String, Double> finesPerCapita) {
		this.finesPerCapita = finesPerCapita;
	}


	
	public HashMap<String, Integer> getZipTotalFines() {
		return zipTotalFines;
	}


	public void setZipTotalFines(HashMap<String, Integer> zipTotalFines) {
		this.zipTotalFines = zipTotalFines;
	}

	

	public HashMap<String, Integer> getZipNumOfViolations() {
		return zipNumOfViolations;
	}


	public void setZipNumOfViolations(HashMap<String, Integer> zipNumOfViolations) {
		this.zipNumOfViolations = zipNumOfViolations;
	}


	public void setNormalizedPopulation(TreeMap<String, Double> normalizedPopulation) {
		this.normalizedPopulation = normalizedPopulation;
	}

	public TreeMap<String, Integer> getNormalizedRatings() {
		return normalizedRatings;
	}

	public void setNormalizedRatings(TreeMap<String, Integer> normalizedRatings) {
		this.normalizedRatings = normalizedRatings;
	}

	public HashMap<String, Double> getZipTotalArea() {
		return zipTotalArea;
	}

	public void setZipTotalArea(HashMap<String, Double> zipTotalArea) {
		this.zipTotalArea = zipTotalArea;
	}

	public HashMap<String, Integer> getZipPropNumForArea() {
		return zipPropNumForArea;
	}

	public void setZipPropNumForArea(HashMap<String, Integer> zipPropNumForArea) {
		this.zipPropNumForArea = zipPropNumForArea;
	}

	public void setZipPropNumForValue(HashMap<String, Integer> zipPropNumForValue) {
		this.zipPropNumForValue = zipPropNumForValue;
	}
	

	
}
