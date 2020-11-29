package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.Property;

public class AverageValueCalculator implements PropertyCalculator{

	@Override
	public int calculateDataPerProperty(List<Property> properties) {
		
		if (properties == null) {
			throw new IllegalArgumentException("The argument to calculateDataPerProperty() should not be null");
		}
		if (properties.size() == 0) {
			return 0;			// no properties, return 0
		}
		
		int count = 0; 			// number of properties in this zipcode
		long totalPrice = 0; 	// total price of houses in this zipcode
		
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
		
		if(count == 0) {
			// no properties with valid marketValue, return 0
			return 0;
		}
		
		// calculate average and return to user
		return (int) totalPrice / count;	
	}



}
