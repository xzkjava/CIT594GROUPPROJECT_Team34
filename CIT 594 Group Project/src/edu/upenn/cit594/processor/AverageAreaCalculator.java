package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public class AverageAreaCalculator implements PropertyCalculator{


	@Override
	public int calculateDataPerProperty(List<Property> properties) {
		
		if(properties == null) {
			throw new IllegalArgumentException("The argument to calculateDataPerProperty should not be null");
		}
		
		if (properties.size() == 0) {
			return 0;			// no properties
		}
		
		int count = 0; 			// number of properties in this zip code
		
		long totalArea = 0; 	// total price of houses in this zip code
		
		for (Property p: properties) {
			
			if (!p.validateString(p.getTotalLivableArea())) {
				continue;
			}
			
			try {
				
				int area = Integer.parseInt(p.getTotalLivableArea());
			
				totalArea = totalArea + area;
			
				count++;
			}catch(NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		
		// TO-DO: need to truncate not round this value
		return (int) totalArea / count;	
	}

	
}
