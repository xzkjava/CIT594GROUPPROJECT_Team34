package edu.upenn.cit594.data;

public class ParkingViolation {
	private int fine;
	private String state;
	private String zipcode;
	

	public ParkingViolation(int fine, String state, String zipcode) {
		this.fine = fine;
		this.state = state;
		this.zipcode = zipcode;
		
	}
	
	public int getFine() {
		
		return fine;
		
	}
	
	public String getZipCode() {
		
		return zipcode;
		
	}
	
	public String getState() {
		
		return state;
		
	}
	

}
