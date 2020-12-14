package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.Property;

public class ValueCalculator implements PropertyCalculator{
	 
	/*
	 * This method calculates average market value per property for a given zipcode,
	 * insert an entry mapping the zipcode to its total market value in a memoized HashMap object,
	 * insert an entry mapping the zipcode to its total number of properties in a memoized HashMap object.
	 */
	@Override
	public int calculateValuePerProperty(String zipcode, List<Property> properties, HashMap<String, Double> zipTotalMap, HashMap<String, Integer> zipCountMap) {
		
        if(zipcode == null || zipcode.isEmpty() || zipTotalMap == null || zipCountMap == null) {
			throw new IllegalArgumentException("The Zipcode passed to calculateValuePerProperty is invalid");
		}
		
		int count = 0; 			// number of properties in this zipcode
		double totalPrice = 0; 	
				
		for (Property p: properties) {
					
			String valueStr = p.getMarketValue();
					
			if( valueStr == null || valueStr.isEmpty() ||!valueStr.matches("^[\\d+.]+$")) {
				continue;
			}
					
			try {	
				// add this property's price to running total and update count
				double marketValue = Double.parseDouble(valueStr);
				totalPrice = totalPrice + marketValue;
				count++;
						
			}catch(NumberFormatException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		zipTotalMap.put(zipcode, totalPrice);
		zipCountMap.put(zipcode, count);
						
		if(count == 0) {
			return 0;
		}
		// calculate average and return to user
		return (int) totalPrice / count;	
		
	}

	

}
