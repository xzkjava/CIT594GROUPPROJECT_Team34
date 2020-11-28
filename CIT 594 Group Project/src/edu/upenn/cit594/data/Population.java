package edu.upenn.cit594.data;

public class Population {
	private String zipcode;
	private int size;
	
	public Population(String zipcode, int size) {
		this.zipcode = zipcode;
		this.size = size;
	}
	
	public String getZipcode() {
		
		return zipcode;
	}
	
	public int getSize() {
		
		return size;
	}

}
