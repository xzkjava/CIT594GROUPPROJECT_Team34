package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.Property;

public class AreaCalculator implements PropertyCalculator{
	
	@Override
	public int calculateValuePerProperty(String zipcode, List<Property> properties, HashMap<String, Double> zipTotalMap, HashMap<String, Integer> zipCountMap) {
		if(zipcode == null || zipcode.isEmpty() || properties == null || zipTotalMap == null || zipCountMap == null) {
			throw new IllegalArgumentException("The zipcode passed into calculateValuePerProperty inside AreaCalculator is not invalid.");
		}
		int count = -1; 			// number of properties in this zipcode
		
		double totalArea = -1; 
		
        for (Property p: properties) {
				
			String areaStr = p.getTotalLivableArea();
			if( areaStr == null || areaStr.isEmpty() || !areaStr.matches("^[\\d.]+$")) {
				continue;
			}
				
			try {
			    // add the area to running total, update count
				double area = Double.parseDouble(areaStr);
				totalArea = totalArea + area;
				count++;
					
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
			
			
            zipTotalMap.put(zipcode, totalArea);
			zipCountMap.put(zipcode, count);
		
		    if (count == 0) {
			    return 0;	// no properties with valid livableArea
		    }
		
	
		    // calculate average and return result
		   return (int) totalArea / count;	
		
		
	}

	
}
