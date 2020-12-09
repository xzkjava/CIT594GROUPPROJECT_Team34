package edu.upenn.cit594.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import edu.upenn.cit594.processor.CSVProcessor;
import edu.upenn.cit594.processor.Processor;

class CommandLineUserInterfaceTest {

	@Test
	void testIsAllDigits() {
		//arrange
		CommandLineUserInterface test = new CommandLineUserInterface(new CSVProcessor("parking.csv", "properties.csv", "population.txt"));
		String str1 = "129382";
		//act and assert
		
	}

}
