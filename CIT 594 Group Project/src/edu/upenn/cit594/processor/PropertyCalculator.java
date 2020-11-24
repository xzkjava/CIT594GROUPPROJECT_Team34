package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public interface PropertyCalculator {
	
	public int calculateDataPerProperty(List<Property> properties);

}
