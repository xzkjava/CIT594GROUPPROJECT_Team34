package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public class AverageValueCalculator implements PropertyCalculator{

	@Override
	public int calculateDataPerProperty(List<Property> properties) {
		
		if (properties == null) {
			throw new IllegalArgumentException("The argument to calculateDataPerProperty() should not be null");
		}
		if (properties.size() == 0) {
			return 0;			// no properties
		}
		
		int count = 0; 			// number of properties in this zip code
		long totalPrice = 0; 	// total price of houses in this zip code
		
		for (Property p: properties) {
			
			if (!p.validateString(p.getMarketValue())) {
				continue;
			}
			try {
				int marketValue = Integer.parseInt(p.getMarketValue());
				
				totalPrice = totalPrice + marketValue;
				
				count++;
				
			}catch(NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		
		if(count == 0) {
			return 0;
		}
		// TO-DO: need to truncate not round this value
		return (int) totalPrice / count;	
	}



}
