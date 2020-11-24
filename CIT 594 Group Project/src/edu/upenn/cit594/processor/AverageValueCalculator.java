package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public class AverageValueCalculator implements PropertyCalculator{

	@Override
	public int calculateDataPerProperty(List<Property> properties) {
		
		if (properties.size() == 0) {
			return 0;			// no properties
		}
		
		int count = 0; 			// number of properties in this zip code
		double totalPrice = 0; 	// total price of houses in this zip code
		
		for (Property p: properties) {
			totalPrice = totalPrice + p.getMarketValue();
			count++;
		}
		
		// TO-DO: need to truncate not round this value
		return (int) totalPrice / count;	
	}


}
