package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.MemoizedRepo;
import edu.upenn.cit594.data.Property;

public class AreaCalculator implements PropertyCalculator{
	MemoizedRepo repo;
	
	public AreaCalculator() {
		repo = MemoizedRepo.getInstance();
	}
	
	@Override
	public int calculateValuePerProperty(String zipcode) {
		if(zipcode == null || zipcode.isEmpty()) {
			throw new IllegalArgumentException("The zipcode passed into calculateValuePerProperty inside AreaCalculator is not invalid.");
		}
		int count = -1; 			// number of properties in this zipcode
		
		double totalArea = -1; 
		
		int averageArea = 0; // total price of houses in this zipcode
		
		HashMap<String, Double> zipTotalArea = repo.getZipTotalArea();
		
		HashMap<String, Integer> zipPropNum = repo.getZipPropNumForArea();
		
		HashMap<String, Integer> areaPerProperty = repo.getAreaPerProperty();
		
		
		if(zipTotalArea != null && zipTotalArea.containsKey(zipcode)) {
			totalArea = zipTotalArea.get(zipcode);
		}
		
		if(zipPropNum != null && zipPropNum.containsKey(zipcode)) {
			count = zipPropNum.get(zipcode);
		}
		
		if(count != -1 && totalArea != -1) {
			if(count == 0 || totalArea == 0) {
				areaPerProperty.put(zipcode, 0);
				return 0;
			}
			else {
				averageArea = (int) totalArea / count;
				areaPerProperty.put(zipcode, averageArea);
				return averageArea;
			}
		}
		
		else {
			List<Property> properties = null;
			
			HashMap<String, List<Property>> zipPropertyMap = repo.getZipPropertyMap();
			
			if(zipPropertyMap != null && zipPropertyMap.containsKey(zipcode)) {
				properties = zipPropertyMap.get(zipcode);
			}
			if(properties == null || properties.size( )== 0) {
				areaPerProperty.put(zipcode, 0);
				return 0;
			}
			
					
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
			
			zipTotalArea.put(zipcode, totalArea);
			zipPropNum.put(zipcode, count);
			
		}
		
		
		if (count == 0) {
			areaPerProperty.put(zipcode, 0);
			return 0;	// no properties with valid livableArea
		}
		
	
		// calculate average and return result
		averageArea = (int) totalArea / count;	
		
		areaPerProperty.put(zipcode, averageArea);
		
		return averageArea;
		
	}

	
}
