package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.upenn.cit594.data.MemoizedRepo;
import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.data.Property;

public class RankingNormalizer {
	private MemoizedRepo repo;
	private HashMap<String, List<ParkingViolation>> zipViolationMap;
	private HashMap<String, List<Property>> zipPropertyMap;
	private HashMap<String, Integer> zipPopulationMap;	

	public RankingNormalizer() {
		repo = MemoizedRepo.getInstance();
		zipViolationMap = repo.getZipViolationMap();
		zipPropertyMap = repo.getZipPropertyMap();
		zipPopulationMap = repo.getZipPopulationMap();	
	}
	
	public TreeMap<String, Integer> normalizeRatings() {
			
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
		
		TreeMap<String, Double> normalizedFine = repo.getNormalizedFine();
		
		TreeMap<String, Double> normalizedLiveableArea = repo.getNormalizedLiveableArea();
		
		TreeMap<String, Double> normalizedPropertyValues = repo.getNormalizedPropertyValues();
		
		TreeMap<String, Double> normalizedPopulation = repo.getNormalizedPopulation();
		
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
		
		HashMap<String, Integer> zipTotalFines = repo.getZipTotalFines();
		
		HashMap<String, Integer> zipNumOfViolations = repo.getZipNumOfViolations();
		
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
			
			if(zipTotalFines != null && zipTotalFines.containsKey(currentZip)) {
				totalFine = zipTotalFines.get(currentZip);
			}
			
			if(zipNumOfViolations != null && zipNumOfViolations.containsKey(currentZip)) {
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
		
		HashMap<String, Double> zipTotalArea = repo.getZipTotalArea();
		
		HashMap<String, Integer> zipNumOfPropForArea = repo.getZipPropNumForArea();
		
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
			
			if(zipTotalArea != null && zipTotalArea.containsKey(currentZip)) {
				totalArea = zipTotalArea.get(currentZip);
			}
			if(zipNumOfPropForArea != null && zipNumOfPropForArea.containsKey(currentZip)) {
				count = zipNumOfPropForArea.get(currentZip);
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
			    zipNumOfPropForArea.put(currentZip, count);
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
		
		HashMap<String, Double> zipTotalValue = repo.getZipTotalValue();
		
		HashMap<String, Integer> zipNumOfPropForValue = repo.getZipPropNumForValue();
		
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
			
			if(zipTotalValue != null && zipTotalValue.containsKey(currentZip)) {
				totalPrice = zipTotalValue.get(currentZip);
			}
			if(zipNumOfPropForValue != null && zipNumOfPropForValue.containsKey(currentZip)) {
				count = zipNumOfPropForValue.get(currentZip);
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
			    zipNumOfPropForValue.put(currentZip, count);
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
			
			// remove the key-average pairing (and replace with key-normalized average pairing)
			normalizedPropertyValues.remove(currentZip);
			
			double normalizedPropertyValue = ((double)(averagePropertyValue - minPropertyValue)) / (maxPropertyValue - minPropertyValue) ;
			normalizedPropertyValues.put(currentZip, normalizedPropertyValue);
			
			
		}
		
		return normalizedPropertyValues;
	}
}
