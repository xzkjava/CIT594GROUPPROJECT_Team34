package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.upenn.cit594.data.MemoizedRepo;
import edu.upenn.cit594.data.ParkingViolation;

public class ViolationCalculator {
	MemoizedRepo repo;
	
	public ViolationCalculator() {
		repo = MemoizedRepo.getInstance();
	}
	
	public SortedMap<String, Double> computeFinePerCapita() {
		HashMap<String, List<ParkingViolation>> zipViolationMap = repo.getZipViolationMap();
		HashMap<String, Integer> zipPopulationMap = repo.getZipPopulationMap();
		
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
				
				// if total fines is 0 (or negative, skip)
				if (totalFines == 0) {
					repo.getZipTotalFines().put(currentZip, 0);
					continue;
				}
				
				// calculate fines per capita and add to final set
				double finesPerCapita = ((double) totalFines) / currentPopulation; 
				
				finesByZip.put(currentZip, finesPerCapita);
				
				repo.getZipTotalFines().put(currentZip,totalFines);
				
			}
			
			repo.setFinesPerCapita(finesByZip);
			
			return finesByZip;
		}
}
