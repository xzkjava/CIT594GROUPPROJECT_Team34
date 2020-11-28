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
			return 0;			// no properties, return 0
		}
		
		int count = 0; 			// number of properties in this zipcode
		long totalArea = 0; 	// total price of houses in this zipcode
		
		for (Property p: properties) {
			
			if (!p.validateString(p.getTotalLivableArea())) {
				// invalid format for livableArea, skip this property 
				continue;
			}
			
			try {
				
				// add the area to running total, update count
				int area = Integer.parseInt(p.getTotalLivableArea());
				totalArea = totalArea + area;
				count++;
				
			}catch(NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		
		if (count == 0) {
			return 0;	// no properties with valid livableArea
		}
		
		// calculate average and return result
		return (int) totalArea / count;	
		
	}

	
}
