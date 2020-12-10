package edu.upenn.cit594.processor;

import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.MemoizedRepo;
import edu.upenn.cit594.data.Property;

public class ValueCalculator implements PropertyCalculator{
	MemoizedRepo repo;
	
	public ValueCalculator() {
		repo = MemoizedRepo.getInstance();
	}
	 
	@Override
	public int calculateValuePerProperty(String zipcode) {
		
        if(zipcode == null || zipcode.isEmpty()) {
			throw new IllegalArgumentException("The Zipcode passed to calculateValuePerProperty is invalid");
		}
		
		int count = -1; 			// number of properties in this zipcode
		double totalPrice = -1; 
		int averagePrice = 0; // total price of houses in this zipcode
		
		HashMap<String, Integer> zipPopulationMap = repo.getZipPopulationMap();
		HashMap<String, Double> zipTotalValue = repo.getZipTotalValue();
		HashMap<String, Integer> zipTotalPropNum = repo.getZipPropNumForValue();
		HashMap<String, Integer> valuePerProperty = repo.getMarketValPerProperty();
		
		if(zipPopulationMap != null && !zipPopulationMap.containsKey(zipcode)) {
			valuePerProperty.put(zipcode, 0);
			return 0;
		}
		if(zipTotalValue != null && zipTotalValue.containsKey(zipcode)) {
			totalPrice = zipTotalValue.get(zipcode);
		}
		if(zipTotalPropNum != null && zipTotalPropNum.containsKey(zipcode)) {
			count = zipTotalPropNum.get(zipcode);
		}
		
		if(totalPrice != -1 && count != -1) {
			if (count == 0 || totalPrice == 0) {
				valuePerProperty.put(zipcode, 0);
				return 0;
			}
			else {
				averagePrice = (int) totalPrice / count;
				valuePerProperty.put(zipcode, averagePrice);
				return averagePrice;
			}
		}
		else {
			HashMap<String, List<Property>> zipPropertyMap = repo.getZipPropertyMap();
			
			if(zipPropertyMap != null && zipPropertyMap.containsKey(zipcode)) {
				List<Property> properties = zipPropertyMap.get(zipcode);
				
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
					}
				}
				
				zipTotalValue.put(zipcode, totalPrice);
				zipTotalPropNum.put(zipcode, count);
			}
		}
			
		if(count == 0) {
			// no properties with valid marketValue, return 0
			valuePerProperty.put(zipcode, 0);
			return 0;
		}
		// calculate average and return to user
		averagePrice = (int) totalPrice / count;	
		valuePerProperty.put(zipcode, averagePrice);
		
		return averagePrice;
	}

	public int computeValuePerCapita(String zipcode) {
		HashMap<String, Integer> zipPopulationMap = repo.getZipPopulationMap();
	    
		// zipcode is not in the population file
		if(zipPopulationMap != null && !zipPopulationMap.containsKey(zipcode)) {
			return 0;
		}
		
		int populationOfZip = zipPopulationMap.get(zipcode);

		if (populationOfZip == 0) {
			// population not found 
			return 0;
		}
		
		HashMap<String, Integer> valuePerCapita = repo.getMarketValPerCapita();
		HashMap<String, Double> zipTotalValue = repo.getZipTotalValue();
		double totalPropertyVal = -1;	
		int averageVal = -1;// total property value of zipcode
		
		if(zipTotalValue != null && zipTotalValue.containsKey(zipcode)) {
			totalPropertyVal = zipTotalValue.get(zipcode);
		}
		
		if(totalPropertyVal != -1) {
			averageVal = (int) totalPropertyVal / populationOfZip;
			valuePerCapita.put(zipcode, averageVal);
			return averageVal;
		}
		
		else {
			HashMap<String, List<Property>> zipPropertyMap = repo.getZipPropertyMap();
			List<Property> propertiesForZip = zipPropertyMap.get(zipcode);
			
			if(propertiesForZip == null || propertiesForZip.size() == 0) {
				return 0;
			}
			
			int numOfProp = 0;
			totalPropertyVal = 0;
			
			for (Property p : propertiesForZip) {
				
					String marketValue = p.getMarketValue();
					
					
					if(marketValue == null || marketValue.isEmpty() ||!marketValue.matches("^[+-]?[\\d.]+$")) {
						continue;
					}
					
					// add property value of this property to the total
					
					double propertyVal = 0;
					
					
					try {
						
						propertyVal = Double.parseDouble(marketValue);
					
					    totalPropertyVal = totalPropertyVal + propertyVal;
					   
					    numOfProp++;
				
					} catch(NumberFormatException e) {
						
						e.printStackTrace();
				}
			}
			
			zipTotalValue.put(zipcode, totalPropertyVal);
			
			repo.getZipPropNumForValue().put(zipcode, numOfProp);
		}
		
		
		// calculate the average per capita and return value
		averageVal = (int) totalPropertyVal / populationOfZip;
		
		valuePerCapita.put(zipcode, averageVal);
		
		return averageVal;
		
		
		
	}
		

}
