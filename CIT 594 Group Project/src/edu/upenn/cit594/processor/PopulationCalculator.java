package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import edu.upenn.cit594.data.MemoizedRepo;

public class PopulationCalculator {
	MemoizedRepo repo;
	
	public PopulationCalculator() {
		repo = MemoizedRepo.getInstance();
	}
	
	public int computePopulation() {
		HashMap<String, Integer> zipPopulationMap = repo.getZipPopulationMap();
		
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
		
		//save total population in repo
		repo.setTotalPopulation(population);
		
		return population;
		
	}
	
}
