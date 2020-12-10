package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.Property;

public interface PropertyCalculator {
	
	public abstract int calculateValuePerProperty(String zipcode, List<Property> properties, HashMap<String, Double> zipTotalMap, HashMap<String, Integer> zipCountMap);

	
	
}
