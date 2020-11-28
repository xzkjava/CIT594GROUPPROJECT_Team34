package edu.upenn.cit594.data;

public class Property {
	private String marketValue;
	private String totalLivableArea;
	private String zipcode;

	public Property(String marketValue, String totalLivableArea, String zipcode) {
		this.marketValue = marketValue;
		this.totalLivableArea = totalLivableArea;
		this.zipcode = zipcode;
	}
	
	public String getMarketValue() {
		return marketValue;
	}
	
	public String getTotalLivableArea() {
		return totalLivableArea;
	}

	public String getZipCode() {
		return zipcode;
	}
	
    public boolean validateString(String value) {
	    	
	    	if(value == null || value.isEmpty()) {
	    		return false;
	    	}
	    	
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
