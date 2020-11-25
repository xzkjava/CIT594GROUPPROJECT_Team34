package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public abstract class PropertyCalculator {
	
	public abstract int calculateDataPerProperty(List<Property> properties);
	
    protected boolean validateString(String value) {
    	
    	if(value.charAt(0) == '-' || value.charAt(0) == '+') {
    		
    		for(int i = 1; i < value.length(); i++) {
    			
    			if(!Character.isDigit(value.charAt(i))) {
    				return false;
    			}
    		}
    	}
    	else {
    		
    		for(int i = 0; i < value.length(); i++) {
			
    			if(!Character.isDigit(value.charAt(i))) {
				
    				return false;
    			}
			
    		}
    	}
			
		return true;
	}

}
